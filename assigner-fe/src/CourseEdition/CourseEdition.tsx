import { useParams, Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './CourseEdition.css';
import Forbidden from '../Forbidden/Forbidden';
import Popup from 'reactjs-popup';
import { Button, Stack } from '@mui/material';
import NewTeam from '../NewTeam/NewTeam';
import NewProject from '../NewProject/NewProject';

function CourseEdition() {
  const { course_name, edition } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [editionResponse, setEditionResponse] =
    useState<IEditionResponse | null>(null);
  const [teamsResponse, setTeamsResponse] =
    useState<Array<ITeamResponse> | null>(null);
  const [projectsResponse, setProjectsResponse] =
    useState<Array<ITeamResponse> | null>(null);

  const [isOpen, setIsOpen] = useState(false);
  const [isOpenProject, setIsOpenProject] = useState(false);

  interface IEditionResponse {
    id: Number;
    edition: string;
    users: Array<IUser>;
  }

  interface ITeamResponse {
    id: Number;
    name: string;
    assignedProject: any; // TODO: define when needed
  }

  interface IUser {
    id: Number;
    name: string;
    secondName: string | null;
    surname: string;
    usosId: Number;
  }

  useEffect(() => {
    wretch(`/api/courses/${course_name}/editions/${edition}`)
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .json((json) => {
        setEditionResponse(json);
      })
      .catch((error) => console.log(error));
  }, [course_name, edition]);

  function fetchTeams() {
    wretch(`/api/courses/${course_name}/editions/${edition}/teams`)
      .get()
      .forbidden((error) => {
        console.log(error); // TODO: better error handling
        setIsForbidden(true);
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

  function fetchProjects() {
    wretch(`/api/courses/${course_name}/editions/${edition}/projects`)
      .get()
      .forbidden((error) => {
        console.log(error); // TODO: better error handling
        setIsForbidden(true);
      })
      .json((json) => {
        setProjectsResponse(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    fetchProjects();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
          <p>
            Kurs {course_name}, edycja: {editionResponse.edition}
          </p>

          <Stack direction='row'>
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
                  courseEdition={editionResponse.edition}
                  courseName={course_name!}
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
                      to={`/courses/${course_name}/${edition}/teams/${team.id}`}
                    >
                      {team.name}
                    </Link>
                  </li>
                );
              })}
            </ul>
            <ul>
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
                  courseEdition={editionResponse.edition}
                  courseName={course_name!}
                  onFinish={fetchProjects}
                />
              </Popup>
              <br />
              Lista tematów:
              {projectsResponse.map((project) => {
                return (
                  <li key={project.id.toString()}>
                    <Link
                      className='Assigner-link'
                      to={`/courses/${course_name}/${edition}/projects/${project.id}`}
                    >
                      {project.name}
                    </Link>
                  </li>
                );
              })}
            </ul>
          </Stack>

          <ul>
            Lista studentów:
            {editionResponse.users.map((user) => {
              return (
                <li key={user.id.toString()}>
                  <p className='Assigner-font-medium Assigner-no-margin'>
                    {user.name} {user.surname} {user.usosId.toString()}
                  </p>
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
        <p>Trwa Ładowanie edycji kursu...</p>
      </header>
    </div>
  );
}

export default CourseEdition;
