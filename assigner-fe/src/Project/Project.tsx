import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './Project.css';
import Helpers from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';
import { Stack } from '@mui/material';
import Forum from '../Forum/Forum';

function Project() {
  const { course_name, edition, group_name, project_id } = useParams();

  const navigate = useNavigate();

  const [projectResponse, setProjectResponse] =
    useState<IProjectResponse | null>(null);

  interface IProjectResponse {
    id: Number;
    name: string;
    teamLimit: Number;
    projectManager: string;
    description: string;
  }

  useEffect(() => {
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
      .json((json) => {
        setProjectResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, project_id]);

  // TODO: maybe forbidden should be handled differently on this page?
  // if (isForbidden) {
  //   return <Forbidden />;
  // }

  if (projectResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <Stack spacing='20px'>
            <p>
              {course_name} / {edition} / {group_name} / {projectResponse.name}
            </p>
            <p>Opiekun projektu: {projectResponse.projectManager}</p>
            <p>Limit miejsc: {projectResponse.teamLimit.toString()}</p>
            <p>{projectResponse.description}</p>
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
