import { Link, useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import './AssignmentView.css';
import Forbidden from '../Forbidden/Forbidden';
import LoadingButton from '@mui/lab/LoadingButton';
import { Checkbox, IconButton, Stack, Tooltip } from '@mui/material';
import Helpers from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import CustomNavigator from '../CustomNavigator/CustomNavigator';

function AssignmentView() {
  const { course_name, edition, group_name } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);

  const [teamsResponse, setTeamsResponse] =
    useState<Array<ITeamResponse> | null>(null);

  const navigate = useNavigate();

  interface ITeamResponse {
    id: number;
    name: string;
    assignedProject: IProjectResponse;
    happiness: number;
    isAssignmentFinal: boolean;
  }

  interface IProjectResponse {
    id: number;
    name: string;
    description: string;
  }

  async function submitAssign() {
    setSubmitLoading(true);
    await wretch()
      .url(
        `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/team-project-assignment`
      )
      .post()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((res) => {})
      .catch((error) => console.log(error));

    await fetchTeams();
    setSubmitLoading(false);
  }

  const handleTeamIsAssignmentFinalChange = (team: ITeamResponse) => () => {
    const w = wretch().addon(QueryStringAddon);
    return w
      .url(
        `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team.id}/assignment-final`
      )
      .query({ 'is-final': !team.isAssignmentFinal })
      .put()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        fetchTeams();
      })
      .catch((error) => console.log(error));
  };

  function getColor(val: number) {
    const red = Math.round(255 * (1 - val));
    const green = Math.round(255 * val);
    return `rgb(${red}, ${green}, 0)`;
  }

  function fetchTeams() {
    return wretch(
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
  }

  useEffect(() => {
    fetchTeams();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (isForbidden) {
    return <Forbidden />;
  }

  if (teamsResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />

          <CustomNavigator
            course_name={course_name}
            edition={edition}
            group_name={group_name}
          />

          <h4>Obecne przypisania zespołów do projektów:</h4>
          <ul>
            <TableContainer component={Paper}>
              <Table sx={{ minWidth: 630 }} aria-label='simple table'>
                <TableHead>
                  <TableRow>
                    <TableCell>Zespół</TableCell>
                    <TableCell align='right'>Przypisany temat</TableCell>
                    <TableCell align='right'>Zadowolenie</TableCell>
                    <TableCell align='right'>
                      <Stack
                        direction='row'
                        justifyContent='right'
                        alignItems='center'
                        spacing='1px'
                      >
                        <p>Zatwierdź przypisanie </p>
                        <Tooltip title='Zatwierdzone przypisania nie są aktualizowane przez model optymalizacyjny.'>
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
                  </TableRow>
                </TableHead>
                <TableBody>
                  {teamsResponse.map((team) => (
                    <TableRow
                      key={team.id}
                      sx={{
                        '&:last-child td, &:last-child th': { border: 0 },
                      }}
                    >
                      <TableCell component='th' scope='row'>
                        <Link
                          className='Assigner-link'
                          to={`/courses/${course_name}/${edition}/${group_name}/teams/${team.id}`}
                        >
                          {team.name}
                        </Link>
                      </TableCell>
                      <TableCell align='right'>
                        {team.assignedProject == null ? (
                          '-'
                        ) : (
                          <Link
                            className='Assigner-link'
                            to={`/courses/${course_name}/${edition}/${group_name}/projects/${team.assignedProject.id}`}
                          >
                            {team.assignedProject.name}
                          </Link>
                        )}
                      </TableCell>
                      <TableCell align='right'>
                        {team.happiness ? (
                          <Stack
                            direction='row'
                            justifyContent='right'
                            alignItems='center'
                            spacing='10px'
                          >
                            <div>{team.happiness}/5</div>
                            <div
                              style={{
                                width: '1em',
                                height: '1em',
                                backgroundColor: getColor(team.happiness / 5.0),
                              }}
                            ></div>
                          </Stack>
                        ) : (
                          '-'
                        )}
                      </TableCell>
                      <TableCell align='right'>
                        {team.assignedProject == null ? (
                          '-'
                        ) : (
                          <Checkbox
                            checked={team.isAssignmentFinal}
                            onChange={handleTeamIsAssignmentFinalChange(team)}
                            sx={{
                              color: 'white',
                              padding: 0,
                            }}
                          />
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </ul>
          <LoadingButton
            variant='contained'
            loading={submitLoading}
            onClick={submitAssign}
          >
            Przypisz tematy za pomocą <br /> modelu optymalizacyjnego
          </LoadingButton>
        </header>
      </div>
    );
  }

  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <ToastContainer />
        <p>Trwa Ładowanie przypisań projektów do zespołów...</p>
      </header>
    </div>
  );
}

export default AssignmentView;
