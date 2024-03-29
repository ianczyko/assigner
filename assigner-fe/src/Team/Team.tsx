import { Link, useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import Snackbar from '@mui/material/Snackbar';
import QueryStringAddon from 'wretch/addons/queryString';
import './Team.css';
import {
  Button,
  Checkbox,
  IconButton,
  List,
  Slider,
  Stack,
  Tooltip,
} from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faCopy,
  faQuestionCircle,
  faRefresh,
} from '@fortawesome/free-solid-svg-icons';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select, { SelectChangeEvent } from '@mui/material/Select';
import Helpers, { UserType } from '../Common/Helpers';
import { ToastContainer, toast } from 'react-toastify';
import moment from 'moment';
import 'moment/locale/pl';
import CustomNavigator from '../CustomNavigator/CustomNavigator';
moment.locale('pl');

function Team() {
  const { course_name, edition, group_name, team_id } = useParams();

  const [isCopied, setIsCopied] = useState(false);

  const [submitLoading, setSubmitLoading] = useState(false);
  const [submitLoadingAccessToken, setSubmitLoadingAccessToken] =
    useState(false);

  const [sliderValueDict, setSliderValueDict] = useState<
    Record<number, number | number[]>
  >({});

  const [teamResponse, setTeamResponse] = useState<ITeamResponse | null>(null);
  const [userType, setUserType] = useState<UserType>(UserType.STUDENT);
  const [accessTokenResponse, setAccessTokenResponse] =
    useState<IAccessTokenResponse | null>(null);
  const [projectsResponse, setProjectsResponse] =
    useState<Array<ITeamResponse> | null>(null);
  const [preferenceResponse, setPreferenceResponse] =
    useState<Array<IPreferenceResponse> | null>(null);

  const [assignedProject, setAssignedProject] = useState('');

  const navigate = useNavigate();

  interface IPreferenceResponse {
    rating: number;
    project: IProjectResponse;
  }

  interface IAccessTokenResponse {
    accessToken: number;
    accessTokenExpirationDate: Date;
  }

  interface IProjectResponse {
    id: number;
    name: string;
    description: string;
  }

  interface ITeamResponse {
    id: number;
    name: string;
    assignedProject: IProjectResponse;
    isAssignmentFinal: boolean;
    members: Array<IUser>;
    readonly: boolean;
  }

  interface IUser {
    id: number;
    name: string;
    secondName: string | null;
    surname: string;
  }

  function submitPreference() {
    if (preferenceResponse == null) {
      return;
    }
    setSubmitLoading(true);
    preferenceResponse.forEach((pref) => {
      if (
        pref.project.id in sliderValueDict &&
        sliderValueDict[pref.project.id] !== pref.rating
      ) {
        const w = wretch().addon(QueryStringAddon);
        w.url(
          `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/project-ratings`
        )
          .query({
            'project-id': pref.project.id,
            rating: sliderValueDict[pref.project.id],
          })
          .put()
          .unauthorized((error) => {
            Helpers.handleUnathorised(navigate);
          })
          .badRequest((error) => {
            let preferenceError = JSON.parse(error.message).message;
            toast.error(`Nieprawidłowa preferencja: (${preferenceError})`, {
              position: 'top-right',
              autoClose: 3000,
              hideProgressBar: false,
              theme: 'dark',
            });
          })
          .res((res) => {})
          .catch((error) => console.log(error));
      }
    });
    setSubmitLoading(false);
  }

  function updateSliderValues(id: number) {
    return function (event: Event, newVal: number | number[]) {
      setSliderValueDict({ ...sliderValueDict, [id]: newVal });
    };
  }

  function changeAssignmentFinal() {
    const w = wretch().addon(QueryStringAddon);
    return w
      .url(
        `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/assignment-final`
      )
      .query({ 'is-final': !teamResponse!.isAssignmentFinal })
      .put()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        fetchTeamResponse();
      })
      .catch((error) => console.log(error));
  }

  function getAccessToken() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/access-token`
    )
      .get()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        // do nothing, case: coordinator previewing team
      })
      .json((json) => {
        setAccessTokenResponse(json);
      })
      .catch((error) => console.log(error));
  }

  async function generateAccessToken() {
    setSubmitLoadingAccessToken(true);
    await wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/access-token`
    )
      .put()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setAccessTokenResponse(json);
      })
      .catch((error) => console.log(error));
    setSubmitLoadingAccessToken(false);
  }

  function handleCopy() {
    navigator.clipboard.writeText(
      `${team_id}:${accessTokenResponse?.accessToken!.toString()}`
    );
    setIsCopied(true);
  }

  useEffect(() => {
    if (teamResponse != null && !teamResponse.readonly) {
      getAccessToken();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [teamResponse]);

  function fetchTeamResponse() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}`
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
        setAssignedProject(json.assignedProject?.id?.toString() ?? '');
        setTeamResponse(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    fetchTeamResponse();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, team_id]);

  useEffect(() => {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/project-ratings/view`
    )
      .get()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setPreferenceResponse(json);
      })
      .catch((error) => console.log(error));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, team_id]);

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

  useEffect(() => {
    fetchProjects();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition]);

  function handleLeave() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/leave`
    )
      .post()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((response) => {
        fetchTeamResponse();
      })
      .catch((error) => console.log(error));
  }

  const handleAssignmentChange = (event: SelectChangeEvent) => {
    const w = wretch().addon(QueryStringAddon);
    w.url(
      `/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams/${team_id}/assigned-project`
    )
      .query({ 'project-id': event.target.value })
      .put()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setAssignedProject(json.assignedProject?.id?.toString() ?? '');
      })
      .catch((error) => console.log(error));
  };

  if (
    teamResponse != null &&
    preferenceResponse != null &&
    projectsResponse != null
  ) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <div className='Assigner-align-left'>
            <CustomNavigator
              course_name={course_name}
              edition={edition}
              group_name={group_name}
              team_name={teamResponse.name}
              team_id={teamResponse.id}
            />
            <Stack direction='row' alignItems='center' spacing='30px'>
              <div>
                <Stack direction='row' alignItems='center' spacing='15px'>
                  <p>Przypisany temat:</p>
                  {userType !== UserType.COORDINATOR ? (
                    <p>
                      {teamResponse.assignedProject
                        ? teamResponse.assignedProject.name
                        : 'brak'}
                    </p>
                  ) : (
                    <FormControl
                      variant='standard'
                      sx={{ minWidth: 120, paddingTop: '2px' }}
                    >
                      <Select
                        value={assignedProject}
                        onChange={handleAssignmentChange}
                        label='Przypisany projekt'
                        disabled={userType !== UserType.COORDINATOR}
                        sx={{
                          '& .MuiSelect-select': {
                            paddingLeft: 2,
                          },
                        }}
                      >
                        <MenuItem value=''>
                          <em>Brak</em>
                        </MenuItem>
                        {projectsResponse!.map((project) => {
                          return (
                            <MenuItem key={project.id} value={project.id}>
                              {project.name}
                            </MenuItem>
                          );
                        })}
                      </Select>
                    </FormControl>
                  )}
                </Stack>

                {teamResponse.assignedProject && (
                  <Stack direction='row' alignItems='center' spacing='10px'>
                    <p>Zatwierdzenie przypisania:</p>
                    <Checkbox
                      checked={teamResponse.isAssignmentFinal}
                      onChange={changeAssignmentFinal}
                      disabled={userType !== UserType.COORDINATOR}
                      sx={{
                        color: 'white',
                        padding: 0,
                      }}
                    />
                  </Stack>
                )}

                <Stack direction='row' alignItems='center' spacing='10px'>
                  <p>Kod dostępu: </p>
                  {(() => {
                    if (teamResponse.readonly) {
                      return <div>*****</div>;
                    }
                    if (accessTokenResponse?.accessToken) {
                      return (
                        <Stack
                          direction='row'
                          alignItems='center'
                          spacing='6px'
                        >
                          <p>
                            {team_id}:{accessTokenResponse!.accessToken}
                          </p>
                          <Stack direction='row' alignItems='center'>
                            <Tooltip title='Kopiuj kod dostępu'>
                              <IconButton onClick={handleCopy} color='inherit'>
                                <FontAwesomeIcon icon={faCopy} />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title='Wygeneruj nowy kod dostępu'>
                              <IconButton
                                onClick={generateAccessToken}
                                color='inherit'
                              >
                                <FontAwesomeIcon icon={faRefresh} />
                              </IconButton>
                            </Tooltip>
                            <Snackbar
                              open={isCopied}
                              autoHideDuration={2500}
                              onClose={() => setIsCopied(false)}
                              message='Skopiowano do schowka.'
                            />
                            <Tooltip
                              title={`Kod ważny do: ${moment(
                                accessTokenResponse!.accessTokenExpirationDate
                              ).format('LLL')}`}
                            >
                              <IconButton onClick={() => {}} color='inherit'>
                                <FontAwesomeIcon icon={faQuestionCircle} />
                              </IconButton>
                            </Tooltip>
                          </Stack>
                        </Stack>
                      );
                    }
                    return (
                      <LoadingButton
                        variant='contained'
                        loading={submitLoadingAccessToken}
                        onClick={generateAccessToken}
                      >
                        Generuj
                      </LoadingButton>
                    );
                  })()}
                </Stack>
                <p className='Assigner-no-margin'>Członkowie zespołu:</p>
                <ul className='Assigner-align-left Assigner-no-margin'>
                  {teamResponse.members.map((user) => {
                    return (
                      <li key={user.id.toString()}>
                        {user.name} {user.surname}
                      </li>
                    );
                  })}
                </ul>
                {!teamResponse.readonly && (
                  <Button
                    variant='contained'
                    onClick={handleLeave}
                    style={{ marginTop: '20px' }}
                  >
                    Opuść zespół
                  </Button>
                )}
              </div>
              <div>
                <ul className='Assigner-list-type-none Assigner-no-padding'>
                  Preferencje zespołu:
                  <List
                    style={{
                      maxHeight: '30vh',
                      minWidth: '25vw',
                      overflow: 'scroll',
                      overflowX: 'hidden',
                      paddingRight: '25px',
                      marginBottom: '20px',
                    }}
                  >
                    {preferenceResponse.map((pref) => {
                      return (
                        <li
                          key={pref.project.id}
                          className='Assigner-no-padding'
                        >
                          <Link
                            className='Assigner-link'
                            to={`/courses/${course_name}/${edition}/${group_name}/projects/${pref.project.id}`}
                          >
                            {pref.project.name}
                          </Link>

                          <Slider
                            aria-label='Ocena'
                            defaultValue={pref.rating}
                            valueLabelDisplay='auto'
                            step={1}
                            marks
                            onChange={updateSliderValues(pref.project.id)}
                            min={1}
                            max={5}
                            disabled={teamResponse.readonly}
                          />
                        </li>
                      );
                    })}
                  </List>
                  {!teamResponse.readonly && (
                    <LoadingButton
                      variant='contained'
                      loading={submitLoading}
                      onClick={submitPreference}
                    >
                      Zapisz preferencje
                    </LoadingButton>
                  )}
                </ul>
              </div>
            </Stack>
          </div>
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

export default Team;
