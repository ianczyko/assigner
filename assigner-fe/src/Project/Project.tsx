import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './Project.css';
import Forbidden from '../Forbidden/Forbidden';

function Project() {
  const { course_name, edition, project_id } = useParams();

  const [isForbidden, setIsForbidden] = useState(false);

  const [ProjectResponse, setProjectResponse] =
    useState<IProjectResponse | null>(null);

  interface IProjectResponse {
    id: Number;
    name: string;
    description: string;
  }

  useEffect(() => {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/projects/${project_id}`
    )
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .json((json) => {
        setProjectResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
  }, [course_name, edition, project_id]);

  if (isForbidden) {
    return <Forbidden />;
  }

  if (ProjectResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <p>Temat: {ProjectResponse.name}</p>
          <p>{ProjectResponse.description}</p>
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

export default Project;
