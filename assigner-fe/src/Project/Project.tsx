import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import './Project.css';
import Helpers, { UserType } from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';
import { Button, IconButton, Stack, Tooltip, Typography } from '@mui/material';
import Forum from '../Forum/Forum';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import CustomNavigator from '../CustomNavigator/CustomNavigator';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';
import Popup from 'reactjs-popup';
import UpdateProjectDescription from '../UpdateProjectDescription/UpdateProjectDescription';

function Project() {
  const { course_name, edition, group_name, project_id } = useParams();

  const navigate = useNavigate();

  const [projectResponse, setProjectResponse] =
    useState<IProjectResponse | null>(null);

  const [userType, setUserType] = useState<UserType>(UserType.STUDENT);

  const [isOpenDesc, setIsOpenDesc] = useState(false);

  interface IProjectResponse {
    id: number;
    name: string;
    teamLimit: number;
    finalAssignedTeamsCount: number;
    projectManager: string;
    description: string;
  }

  function changeLimitBy(difference: number) {
    const w = wretch().addon(QueryStringAddon);
    w.url(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/projects/${project_id}/limit`
    )
      .query({ new_limit: projectResponse!.teamLimit + difference })
      .put()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setProjectResponse(json);
      })
      .catch((error) => console.log(error));
  }

  function fetchProject() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/projects/${project_id}`
    )
      .get()
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
        setProjectResponse(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    fetchProject();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, project_id]);

  if (projectResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <CustomNavigator
            course_name={course_name}
            edition={edition}
            group_name={group_name}
            project_name={projectResponse.name}
            project_id={projectResponse.id}
          />
          <Stack spacing='20px'>
            <TableContainer component={Paper}>
              <Table sx={{ minWidth: 550 }} aria-label='simple table'>
                <TableHead>
                  <TableRow>
                    <TableCell>Nazwa tematu</TableCell>
                    <TableCell>Opiekun Tematu</TableCell>
                    <TableCell align='right'>Limit miejsc</TableCell>
                    <TableCell align='right'>
                      Ilość zatwierdzonych przypisań
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow
                    key={projectResponse.name}
                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                  >
                    <TableCell>{projectResponse.name}</TableCell>
                    <TableCell>{projectResponse.projectManager}</TableCell>
                    <TableCell>
                      <Stack
                        direction='row'
                        justifyContent='right'
                        alignItems='center'
                        spacing='1px'
                      >
                        {userType === UserType.COORDINATOR && (
                          <Tooltip title='Zwiększ limit miejsc o 1.'>
                            <IconButton
                              onClick={() => {
                                changeLimitBy(1);
                              }}
                              color='inherit'
                              size='small'
                            >
                              <FontAwesomeIcon icon={faPlus} />
                            </IconButton>
                          </Tooltip>
                        )}

                        <p>{projectResponse.teamLimit}</p>
                        {userType === UserType.COORDINATOR && (
                          <Tooltip title='Zmniejsz limit miejsc o 1.'>
                            <span>
                              <IconButton
                                onClick={() => {
                                  changeLimitBy(-1);
                                }}
                                color='inherit'
                                size='small'
                                disabled={projectResponse.teamLimit < 1}
                              >
                                <FontAwesomeIcon icon={faMinus} />
                              </IconButton>
                            </span>
                          </Tooltip>
                        )}
                      </Stack>
                    </TableCell>
                    <TableCell align='right'>
                      {projectResponse.finalAssignedTeamsCount}
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>
            <Paper
              style={{
                maxHeight: 300,
                overflow: 'auto',
                padding: 16,
                width: 600,
              }}
            >
              <Typography>{projectResponse.description}</Typography>
            </Paper>
            {userType === UserType.COORDINATOR && (
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <Popup
                  trigger={(open) => (
                    <Button variant='contained' sx={{ width: 150 }}>
                      Zmień opis
                    </Button>
                  )}
                  position='right center'
                  closeOnDocumentClick
                  open={isOpenDesc}
                  onOpen={() => setIsOpenDesc(!isOpenDesc)}
                >
                  <UpdateProjectDescription
                    courseEdition={edition!}
                    courseName={course_name!}
                    groupName={group_name!}
                    projectId={project_id!}
                    onFinish={fetchProject}
                  />
                </Popup>
              </div>
            )}
            <Forum />
          </Stack>
        </header>
      </div>
    );
  }

  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <ToastContainer />
        <p>Trwa Ładowanie zespołu...</p>
      </header>
    </div>
  );
}

export default Project;
