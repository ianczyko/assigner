import { Link, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import './Team.css';
import Forbidden from '../Forbidden/Forbidden';
import { Slider } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';

function Team() {
  const { course_name, edition, team_id } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [submitLoading, setSubmitLoading] = useState(false);

  const [sliderValueDict, setSliderValueDict] = useState<
    Record<number, number | number[]>
  >({});

  const [teamResponse, setTeamResponse] = useState<ITeamResponse | null>(null);
  const [preferenceResponse, setPreferenceResponse] =
    useState<Array<IPreferenceResponse> | null>(null);

  interface IPreferenceResponse {
    rating: number;
    project: IProjectResponse;
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

  useEffect(() => {
    wretch(`/api/courses/${course_name}/editions/${edition}/teams/${team_id}`)
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .json((json) => {
        setTeamResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
  }, [course_name, edition, team_id]);

  useEffect(() => {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/teams/${team_id}/project-ratings/view`
    )
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .json((json) => {
        setPreferenceResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
  }, [course_name, edition, team_id]);

  if (isForbidden) {
    return <Forbidden />;
  }

  if (teamResponse != null && preferenceResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <div className='Assigner-align-left'>
            <p>Zespół: {teamResponse.name}</p>
            <p>
              Przypisany temat: {teamResponse.assignedProject.name ?? 'brak'}
            </p>
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
