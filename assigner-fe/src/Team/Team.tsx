import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './Team.css';
import Forbidden from '../Forbidden/Forbidden';

function Team() {
  const { course_name, edition, team_id } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [teamResponse, setTeamResponse] = useState<ITeamResponse | null>(null);

  interface ITeamResponse {
    id: Number;
    name: string;
    assignedProject: any; // TODO: define when needed
    members: Array<IUser>;
  }

  interface IUser {
    id: Number;
    name: string;
    secondName: string | null;
    surname: string;
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

  if (isForbidden) {
    return <Forbidden />;
  }

  if (teamResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <p>Zespół: {teamResponse.name}</p>
          <ul>
            Członkowie zespołu:
            {teamResponse.members.map((user) => {
              return (
                <li key={user.id.toString()}>
                  {user.name} {user.surname}
                </li>
              );
            })}
          </ul>
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
