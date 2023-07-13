import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './CourseEdition.css';

function CourseEdition() {
  const { course_name, edition } = useParams();

  const [editionResponse, setEditionResponse] =
    useState<IEditionResponse | null>(null);

  interface IEditionResponse {
    id: Number;
    edition: string;
    users: Array<IUser>;
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
      .json((json) => {
        setEditionResponse(json);
      })
      .catch((error) => console.log(error));
  }, [course_name, edition]);

  if (editionResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <p>
            {course_name}, edition: {editionResponse.edition}
          </p>
          <ul>
            Lista studentów:
            {editionResponse.users.map((user) => {
              return (
                <li key={user.id.toString()}>
                  {user.name} {user.surname} {user.usosId.toString()}
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
        <p>Loading course edition...</p>
      </header>
    </div>
  );
}

export default CourseEdition;
