import { Link, useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './AssignmentView.css';
import Forbidden from '../Forbidden/Forbidden';
import LoadingButton from '@mui/lab/LoadingButton';
import { Stack } from '@mui/material';
import Helpers from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';

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
      .res((res) => {
        console.log(res); // TODO: remove me
      })
      .catch((error) => console.log(error));

    await fetchTeams();
    setSubmitLoading(false);
  }

  function getColor(val: number) {
    const red = Math.round(255 * (1 - val));
    const green = Math.round(255 * val);
    return `rgb(${red}, ${green}, 0)`;
  }

  function fetchTeams() {
    return wretch(`/api/courses/${course_name}/editions/${edition}/groups/${group_name}/teams`)
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
  }

  useEffect(() => {
    fetchTeams();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (isForbidden) {
    return <Forbidden />;
  }

  console.log(teamsResponse);
  if (teamsResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <ul>
            Obecne przypisania <br />
            zespołów do projektów:
            {teamsResponse.map((team) => {
              return (
                <li key={team.id.toString()}>
                  <Stack direction='row' alignItems='center' spacing='10px'>
                    <div>
                      zespół:{' '}
                      <Link
                        className='Assigner-link'
                        to={`/courses/${course_name}/${edition}/${group_name}/teams/${team.id}`}
                      >
                        {team.name}
                      </Link>{' '}
                      - temat:{' '}
                      {team.assignedProject == null ? (
                        'brak'
                      ) : (
                        <Link
                          className='Assigner-link'
                          to={`/courses/${course_name}/${edition}/${group_name}/projects/${team.assignedProject.id}`}
                        >
                          {team.assignedProject.name}
                        </Link>
                      )}{' '}
                    </div>
                    {team.happiness ? (
                      <Stack direction='row' alignItems='center' spacing='10px'>
                        <div>- zadowolenie: {team.happiness}/5</div>
                        <div
                          style={{
                            width: '1em',
                            height: '1em',
                            backgroundColor: getColor(team.happiness / 5.0),
                          }}
                        ></div>
                      </Stack>
                    ) : (
                      <div></div>
                    )}
                  </Stack>
                </li>
              );
            })}
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
