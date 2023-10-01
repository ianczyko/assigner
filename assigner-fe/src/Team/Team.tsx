import { Link, useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import Snackbar from '@mui/material/Snackbar';
import QueryStringAddon from 'wretch/addons/queryString';
import './Team.css';
import Forbidden from '../Forbidden/Forbidden';
import { IconButton, Slider, Stack, Tooltip } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCopy, faQuestionCircle } from '@fortawesome/free-solid-svg-icons';
import Helpers from '../Common/Helpers';

function Team() {
  const { course_name, edition, team_id } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [isCopied, setIsCopied] = useState(false);

  const [submitLoading, setSubmitLoading] = useState(false);
  const [submitLoadingAccessToken, setSubmitLoadingAccessToken] =
    useState(false);

  const [sliderValueDict, setSliderValueDict] = useState<
    Record<number, number | number[]>
  >({});

  const [teamResponse, setTeamResponse] = useState<ITeamResponse | null>(null);
  const [accessTokenResponse, setAccessTokenResponse] =
    useState<IAccessTokenResponse | null>(null);
  const [preferenceResponse, setPreferenceResponse] =
    useState<Array<IPreferenceResponse> | null>(null);

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
    members: Array<IUser>;
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
          `/api/courses/${course_name}/editions/${edition}/teams/${team_id}/project-ratings`
        )
          .query({
            'project-id': pref.project.id,
            rating: sliderValueDict[pref.project.id],
          })
          .put()
          .unauthorized((error) => {
            Helpers.handleUnathorised(navigate);
          })
          .res((res) => {
            console.log(res); // TODO: remove me
          })
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

  function getAccessToken() {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/teams/${team_id}/access-token`
    )
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .json((json) => {
        setAccessTokenResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
  }

  async function generateAccessToken() {
    setSubmitLoadingAccessToken(true);
    await wretch(
      `/api/courses/${course_name}/editions/${edition}/teams/${team_id}/access-token`
    )
      .put()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .json((json) => {
        setAccessTokenResponse(json);
        console.log(json); // TODO: remove me
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
    getAccessToken();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, team_id]);

  useEffect(() => {
    wretch(`/api/courses/${course_name}/editions/${edition}/teams/${team_id}`)
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .json((json) => {
        setTeamResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, team_id]);

  useEffect(() => {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/teams/${team_id}/project-ratings/view`
    )
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .json((json) => {
        setPreferenceResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, team_id]);

  if (isForbidden) {
    return <Forbidden />;
  }

  if (teamResponse != null && preferenceResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <div className='Assigner-align-left'>
            <p style={{ textAlign: 'center' }}>Zespół: {teamResponse.name}</p>
            <Stack direction='row' alignItems='center' spacing='30px'>
              <div>
                <p>
                  Przypisany temat:{' '}
                  {teamResponse.assignedProject?.name ?? 'brak'}
                </p>
                <Stack direction='row' alignItems='center' spacing='10px'>
                  <p>Kod dostępu: </p>
                  {(() => {
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
                            <IconButton onClick={handleCopy} color='inherit'>
                              <FontAwesomeIcon icon={faCopy} />
                            </IconButton>
                            <Snackbar
                              open={isCopied}
                              autoHideDuration={2500}
                              onClose={() => setIsCopied(false)}
                              message='Skopiowano do schowka.'
                            />
                            <Tooltip
                              title={`Kod ważny do: ${
                                accessTokenResponse!.accessTokenExpirationDate // TODO: better date format
                              }`}
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
              </div>
              <div>
                <ul className='Assigner-list-type-none Assigner-no-padding'>
                  Preferencje zespołu:
                  {preferenceResponse.map((pref) => {
                    return (
                      <li key={pref.project.id} className='Assigner-no-padding'>
                        <Link
                          className='Assigner-link'
                          to={`/courses/${course_name}/${edition}/projects/${pref.project.id}`}
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
                        />
                      </li>
                    );
                  })}
                  <LoadingButton
                    variant='contained'
                    loading={submitLoading}
                    onClick={submitPreference}
                  >
                    Zapisz preferencje
                  </LoadingButton>
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
        <p>Trwa Ładowanie zespołu...</p>
      </header>
    </div>
  );
}

export default Team;
