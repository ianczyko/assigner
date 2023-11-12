import { useParams, Link, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import './CourseEditionGroup.css';
import Forbidden from '../Forbidden/Forbidden';
import Popup from 'reactjs-popup';
import {
  Button,
  FormControl,
  IconButton,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  Tooltip,
} from '@mui/material';
import NewTeam from '../NewTeam/NewTeam';
import NewProject from '../NewProject/NewProject';
import JoinTeam from '../JoinTeam/JoinTeam';
import Helpers, { UserType } from '../Common/Helpers';
import { ToastContainer, toast } from 'react-toastify';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faQuestionCircle, faXmark } from '@fortawesome/free-solid-svg-icons';
import CustomNavigator from '../CustomNavigator/CustomNavigator';

function CourseEditionGroup() {
  const { course_name, edition, group_name } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [userType, setUserType] = useState<UserType>(UserType.STUDENT);

  const [editionResponse, setEditionResponse] =
    useState<IEditionResponse | null>(null);
  const [assignedTeam, setAssignedTeam] = useState<ITeamResponse | null>(null);
  const [teamsResponse, setTeamsResponse] =
    useState<Array<ITeamResponse> | null>(null);
  const [groupsResponse, setGroupsResponse] =
    useState<Array<IEditionResponse> | null>(null);
  const [projectsResponse, setProjectsResponse] =
    useState<Array<IProjectResponse> | null>(null);

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

  interface IProjectResponse {
    id: number;
    name: string;
    teamLimit: number;
    finalAssignedTeamsCount: number;
    projectManager: string;
    description: string;
  }

  interface IUser {
    id: number;
    name: string;
    secondName: string | null;
    surname: string;
    usosId: number;
    userType: UserType;
  }

  function fetchGroups() {
    wretch(`/api/courses/${course_name}/editions/${edition}/groups`)
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
        setGroupsResponse(json);
      })
      .catch((error) => console.log(error));
    getAssignedTeam();
  }

  useEffect(() => {
    fetchGroups();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function getGroup() {
    wretch(
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

  function removeTeam(team: ITeamResponse) {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team.id}`
    )
      .delete()
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .res((response) => {
        fetchTeams();
      })
      .catch((error) => console.log(error));
  }

  function removeProject(project: IProjectResponse) {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/projects/${project.id}`
    )
      .delete()
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .badRequest((error) => {
        toast.error('Nie można usunąć projektu!', {
          position: 'top-right',
          autoClose: 3000,
          hideProgressBar: false,
          theme: 'dark',
        });
      })
      .res((response) => {
        fetchProjects();
      })
      .catch((error) => console.log(error));
  }

  const handleTeamAssignmentChange =
    (user: IUser) => (event: SelectChangeEvent) => {
      const w = wretch().addon(QueryStringAddon);
      w.url(
        `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/manual-reassignment`
      )
        .query({
          'team-id': event.target.value,
          'previous-team-id': getAssignedTeamOf(user.id)?.id,
          'usos-id': user.usosId,
        })
        .post()
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

  const handleGroupAssignmentChange =
    (user: IUser) => (event: SelectChangeEvent) => {
      const w = wretch().addon(QueryStringAddon);
      w.url(
        `/api/courses/${course_name}/editions/${edition}/groups/user-reassignment`
      )
        .query({
          'group-from': group_name,
          'group-to': event.target.value,
          usosId: user.usosId,
        })
        .post()
        .unauthorized((error) => {
          Helpers.handleUnathorised(navigate);
        })
        .forbidden((error) => {
          Helpers.handleForbidden();
        })
        .res((res) => {
          getGroup();
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
          <CustomNavigator
            course_name={course_name}
            edition={edition}
            group_name={group_name}
          />

          <Stack direction='row'>
            <Stack>
              <ul>
                {!assignedTeam?.id && (
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
                )}
                <p>Lista zespołów:</p>
                <div
                  style={{
                    marginTop: '20px',
                    marginRight: '20px',
                  }}
                >
                  <TableContainer component={Paper}>
                    <Table aria-label='simple table'>
                      <TableHead>
                        <TableRow>
                          <TableCell></TableCell>
                          <TableCell>Zespół</TableCell>
                          <TableCell align='right'>Członkowie</TableCell>
                          {userType === UserType.COORDINATOR && (
                            <TableCell align='right'>Usuń</TableCell>
                          )}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {teamsResponse.map((team, index) => (
                          <TableRow
                            key={team.id}
                            sx={{
                              '&:last-child td, &:last-child th': { border: 0 },
                            }}
                          >
                            <TableCell>{index + 1}</TableCell>
                            <TableCell component='th' scope='row'>
                              <Link
                                className='Assigner-link'
                                to={`/courses/${course_name}/${edition}/${group_name}/teams/${team.id}`}
                              >
                                {team.name}
                              </Link>
                            </TableCell>
                            <TableCell component='th' scope='row'>
                              {team.members.length}
                            </TableCell>
                            {userType === UserType.COORDINATOR && (
                              <TableCell component='th' scope='row'>
                                <IconButton
                                  onClick={() => {
                                    removeTeam(team);
                                  }}
                                  color='inherit'
                                  size='small'
                                >
                                  <FontAwesomeIcon icon={faXmark} />
                                </IconButton>
                              </TableCell>
                            )}
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </div>
              </ul>

              {userType === UserType.STUDENT && (
                <Stack direction='row' spacing='6px' alignItems='center'>
                  <p>Twój zespół: </p>
                  {assignedTeam?.id ? (
                    <Link
                      className='Assigner-link'
                      to={`/courses/${course_name}/${edition}/${group_name}/teams/${
                        assignedTeam!.id
                      }`}
                    >
                      {assignedTeam!.name}
                    </Link>
                  ) : (
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
                  )}
                </Stack>
              )}
            </Stack>
            <ul>
              {userType !== UserType.STUDENT && (
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
              )}

              <p>Lista tematów:</p>
              <div
                style={{
                  marginTop: '20px',
                  marginRight: '20px',
                }}
              >
                <TableContainer component={Paper}>
                  <Table aria-label='simple table'>
                    <TableHead>
                      <TableRow>
                        <TableCell></TableCell>
                        <TableCell>Temat</TableCell>
                        <TableCell align='right'>Limit miejsc</TableCell>
                        <TableCell align='right'>
                          Ilość zatw. przypisań
                        </TableCell>
                        {userType === UserType.COORDINATOR && (
                          <TableCell align='right'>Usuń</TableCell>
                        )}
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {projectsResponse.map((project, index) => (
                        <TableRow
                          key={project.id}
                          sx={{
                            '&:last-child td, &:last-child th': { border: 0 },
                          }}
                        >
                          <TableCell>{index + 1}</TableCell>
                          <TableCell component='th' scope='row'>
                            <Link
                              className='Assigner-link'
                              to={`/courses/${course_name}/${edition}/${group_name}/projects/${project.id}`}
                            >
                              {project.name}
                            </Link>
                          </TableCell>
                          <TableCell align='right'>
                            {project.teamLimit}
                          </TableCell>
                          <TableCell align='right'>
                            {project.finalAssignedTeamsCount}
                          </TableCell>
                          {userType === UserType.COORDINATOR && (
                            <TableCell component='th' scope='row'>
                              <IconButton
                                onClick={() => {
                                  removeProject(project);
                                }}
                                color='inherit'
                                size='small'
                              >
                                <FontAwesomeIcon icon={faXmark} />
                              </IconButton>
                            </TableCell>
                          )}
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </div>
            </ul>
          </Stack>
          <h4>Lista studentów:</h4>
          <ul>
            <TableContainer component={Paper}>
              <Table sx={{ minWidth: 650 }} aria-label='simple table'>
                <TableHead>
                  <TableRow>
                    <TableCell></TableCell>
                    <TableCell>Imię</TableCell>
                    <TableCell>Nazwisko</TableCell>
                    <TableCell>Zespół</TableCell>
                    {userType !== UserType.STUDENT && (
                      <TableCell>
                        <Stack
                          direction='row'
                          justifyContent='left'
                          alignItems='center'
                          spacing='1px'
                        >
                          <p>Grupa</p>
                          <Tooltip title='Jest to funkcjonalność przepisywania studentów do innych grup. Jeśli zmiana grupy jest zablokowana, należy w pierwszej kolejności usunąć osobę z zespołu w kolumnie Zespół.'>
                            <IconButton
                              onClick={() => {}}
                              color='inherit'
                              size='small'
                            >
                              <FontAwesomeIcon icon={faQuestionCircle} />
                            </IconButton>
                          </Tooltip>
                        </Stack>
                      </TableCell>
                    )}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {editionResponse.users
                    .filter((user) => user.userType === UserType.STUDENT)
                    .map((user, index) => {
                      return (
                        <TableRow
                          key={user.id.toString()}
                          sx={{
                            '&:last-child td, &:last-child th': { border: 0 },
                          }}
                        >
                          <TableCell>{index + 1}</TableCell>
                          <TableCell>{user.name}</TableCell>
                          <TableCell>{user.surname}</TableCell>
                          <TableCell>
                            {userType === UserType.STUDENT ? (
                              getAssignedTeamOf(user.id)?.name ?? '-'
                            ) : (
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
                            )}
                          </TableCell>
                          {userType !== UserType.STUDENT && (
                            <TableCell>
                              <FormControl
                                variant='standard'
                                sx={{ minWidth: 120, paddingTop: '2px' }}
                              >
                                <Select
                                  value={group_name}
                                  onChange={handleGroupAssignmentChange(user)}
                                  label='Przypisana grupa'
                                  disabled={assignedTeams[user.id] !== ''}
                                  sx={{
                                    '& .MuiSelect-select': {
                                      paddingLeft: 2,
                                    },
                                  }}
                                >
                                  {groupsResponse!.map((group) => {
                                    return (
                                      <MenuItem
                                        key={group.groupName}
                                        value={group.groupName}
                                      >
                                        {group.groupName}
                                      </MenuItem>
                                    );
                                  })}
                                </Select>
                              </FormControl>
                            </TableCell>
                          )}
                        </TableRow>
                      );
                    })}
                </TableBody>
              </Table>
            </TableContainer>
          </ul>

          <br />
          {userType !== UserType.STUDENT && (
            <Link
              className='Assigner-link'
              to={`/courses/${course_name}/${edition}/${group_name}/assignment-view`}
            >
              Przypisania tematów do zespołów
            </Link>
          )}
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

export default CourseEditionGroup;
