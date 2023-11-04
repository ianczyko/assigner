import { useParams, Link, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import './CourseEdition.css';
import Forbidden from '../Forbidden/Forbidden';
import Popup from 'reactjs-popup';
import {
  Button,
  FormControl,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
} from '@mui/material';
import NewTeam from '../NewTeam/NewTeam';
import NewProject from '../NewProject/NewProject';
import JoinTeam from '../JoinTeam/JoinTeam';
import Helpers, { UserType } from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

function CourseEdition() {
  const { course_name, edition, group_name } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [userType, setUserType] = useState<UserType>(UserType.STUDENT);

  const [editionResponse, setEditionResponse] =
    useState<IEditionResponse | null>(null);
  const [assignedTeam, setAssignedTeam] = useState<ITeamResponse | null>(null);
  const [teamsResponse, setTeamsResponse] =
    useState<Array<ITeamResponse> | null>(null);
  const [projectsResponse, setProjectsResponse] =
    useState<Array<ITeamResponse> | null>(null);

  const [assignedTeams, setAssignedTeams] = useState<any>({});

  const [isOpen, setIsOpen] = useState(false);
  const [isOpenJoin, setIsOpenJoin] = useState(false);
  const [isOpenProject, setIsOpenProject] = useState(false);

  const navigate = useNavigate();

  interface IEditionResponse {
    id: number;
    groupName: string;
    users: Array<IUser>;
  }

  interface ITeamResponse {
    id: number;
    name: string;
    members: Array<IUser>;
  }

  interface IUser {
    id: number;
    name: string;
    secondName: string | null;
    surname: string;
    usosId: number;
  }

  function getGroup() {
    return wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}`
    )
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((response) => {
        Helpers.extractUserType(response, setUserType);
        return response.json();
      })
      .then((json) => {
        setEditionResponse(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    getGroup();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition]);

  useEffect(() => {
    if (editionResponse == null) return;
    if (teamsResponse == null) return;
    editionResponse.users.forEach((user: IUser) => {
      let userTeam = getAssignedTeamOf(user.id)?.id ?? '';
      setAssignedTeams((prev: any) => ({ ...prev, [user.id]: userTeam }));
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [editionResponse, teamsResponse]);

  function getAssignedTeam() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/assigned-team`
    )
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setAssignedTeam(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    getAssignedTeam();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition]);

  function fetchTeams() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams`
    )
      .get()
      .forbidden((error) => {
        console.log(error); // TODO: better error handling
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setTeamsResponse(json);
      })
      .catch((error) => console.log(error));
    getAssignedTeam();
  }

  useEffect(() => {
    fetchTeams();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function fetchProjects() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/projects`
    )
      .get()
      .forbidden((error) => {
        console.log(error); // TODO: better error handling
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setProjectsResponse(json);
      })
      .catch((error) => console.log(error));
  }

  function getAssignedTeamOf(userId: number) {
    let userTeams = teamsResponse?.filter(
      (t) => t.members.filter((m) => m.id === userId).length
    );
    if (userTeams == null || userTeams.length < 1) return null;
    return userTeams[0];
  }

  useEffect(() => {
    fetchProjects();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleTeamAssignmentChange =
    (user: IUser) => (event: SelectChangeEvent) => {
      const w = wretch().addon(QueryStringAddon);
      w.url(
        `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/manual-team-assign`
      )
        .query({
          'team-id': event.target.value,
          'previous-team-id': getAssignedTeamOf(user.id)?.id,
          usosId: user.usosId,
        })
        .put()
        .unauthorized((error) => {
          Helpers.handleUnathorised(navigate);
        })
        .forbidden((error) => {
          Helpers.handleForbidden();
        })
        .res((res) => {
          fetchTeams();
        })
        .catch((error) => console.log(error));
    };

  if (isForbidden) {
    return <Forbidden />;
  }

  if (
    editionResponse != null &&
    teamsResponse != null &&
    projectsResponse != null
  ) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <p>
            {course_name} / {edition} / {group_name}
          </p>

          <Stack direction='row'>
            <Stack>
              <ul>
                <Popup
                  trigger={(open) => (
                    <Button variant='contained'>Nowy zespół</Button>
                  )}
                  position='right center'
                  closeOnDocumentClick
                  open={isOpen}
                  onOpen={() => setIsOpen(!isOpen)}
                >
                  <NewTeam
                    courseEdition={edition!}
                    courseName={course_name!}
                    groupName={group_name!}
                    addCreator={userType === UserType.STUDENT}
                    onFinish={fetchTeams}
                  />
                </Popup>
                <br />
                Lista zespołów:
                {teamsResponse.map((team) => {
                  return (
                    <li key={team.id.toString()}>
                      <Link
                        className='Assigner-link'
                        to={`/courses/${course_name}/${edition}/${group_name}/teams/${team.id}`}
                      >
                        {team.name}
                      </Link>
                    </li>
                  );
                })}
              </ul>
              {(() => {
                if (userType !== UserType.STUDENT) {
                  return <div></div>;
                }
                return (
                  <Stack direction='row' spacing='6px' alignItems='center'>
                    <p>Twój zespół: </p>
                    {(() => {
                      if (assignedTeam?.id) {
                        return (
                          <Link
                            className='Assigner-link'
                            to={`/courses/${course_name}/${edition}/${group_name}/teams/${
                              assignedTeam!.id
                            }`}
                          >
                            {assignedTeam!.name}
                          </Link>
                        );
                      }
                      return (
                        <Popup
                          trigger={(open) => (
                            <Button variant='contained'>
                              Dołącz do <br /> zespołu
                            </Button>
                          )}
                          position='right center'
                          closeOnDocumentClick
                          open={isOpenJoin}
                          onOpen={() => setIsOpenJoin(!isOpenJoin)}
                        >
                          <JoinTeam
                            courseEdition={edition!}
                            courseName={course_name!}
                            groupName={group_name!}
                            onFinish={getAssignedTeam}
                          />
                        </Popup>
                      );
                    })()}
                  </Stack>
                );
              })()}
            </Stack>
            <ul>
              {(() => {
                if (userType === UserType.STUDENT) {
                  return <div></div>;
                }
                return (
                  <Popup
                    trigger={(open) => (
                      <Button variant='contained'>Nowy temat</Button>
                    )}
                    position='right center'
                    closeOnDocumentClick
                    open={isOpenProject}
                    onOpen={() => setIsOpenProject(!isOpenProject)}
                  >
                    <NewProject
                      courseEdition={edition!}
                      courseName={course_name!}
                      groupName={group_name!}
                      onFinish={fetchProjects}
                    />
                  </Popup>
                );
              })()}
              <br />
              Lista tematów:
              {projectsResponse.map((project) => {
                return (
                  <li key={project.id.toString()}>
                    <Link
                      className='Assigner-link'
                      to={`/courses/${course_name}/${edition}/${group_name}/projects/${project.id}`}
                    >
                      {project.name}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </Stack>
          <h4>Lista studentów:</h4>
          <ul>
            <TableContainer component={Paper}>
              <Table sx={{ minWidth: 550 }} aria-label='simple table'>
                <TableHead>
                  <TableRow>
                    <TableCell>Imię</TableCell>
                    <TableCell>Nazwisko</TableCell>
                    <TableCell>Zespół</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {editionResponse.users.map((user) => {
                    return (
                      <TableRow
                        key={user.id.toString()}
                        sx={{
                          '&:last-child td, &:last-child th': { border: 0 },
                        }}
                      >
                        <TableCell>{user.name}</TableCell>
                        <TableCell>{user.surname}</TableCell>
                        <TableCell>
                          {(() => {
                            if (userType === UserType.STUDENT) {
                              return getAssignedTeamOf(user.id)?.name ?? '-';
                            }
                            return (
                              <FormControl
                                variant='standard'
                                sx={{ minWidth: 120, paddingTop: '2px' }}
                              >
                                <Select
                                  value={assignedTeams[user.id] ?? ''}
                                  onChange={handleTeamAssignmentChange(user)}
                                  label='Przypisany zespół'
                                  sx={{
                                    '& .MuiSelect-select': {
                                      paddingLeft: 2,
                                    },
                                  }}
                                >
                                  <MenuItem value=''>
                                    <em>Brak</em>
                                  </MenuItem>
                                  {teamsResponse!.map((team) => {
                                    return (
                                      <MenuItem key={team.id} value={team.id}>
                                        {team.name}
                                      </MenuItem>
                                    );
                                  })}
                                </Select>
                              </FormControl>
                            );
                          })()}
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </TableContainer>
          </ul>

          <br />
          {(() => {
            if (userType === UserType.STUDENT) {
              return <div></div>;
            }
            return (
              <Link
                className='Assigner-link'
                to={`/courses/${course_name}/${edition}/${group_name}/assignment-view`}
              >
                Przypisania tematów do zespołów
              </Link>
            );
          })()}
        </header>
      </div>
    );
  }

  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <ToastContainer />
        <p>Trwa Ładowanie edycji kursu...</p>
      </header>
    </div>
  );
}

export default CourseEdition;
