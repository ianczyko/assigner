import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './Project.css';
import Helpers from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';
import { Stack } from '@mui/material';
import Forum from '../Forum/Forum';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

function Project() {
  const { course_name, edition, group_name, project_id } = useParams();

  const navigate = useNavigate();

  const [projectResponse, setProjectResponse] =
    useState<IProjectResponse | null>(null);

  interface IProjectResponse {
    id: number;
    name: string;
    teamLimit: number;
    finalAssignedTeamsCount: number;
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
            <TableContainer component={Paper}>
              <Table sx={{ minWidth: 550 }} aria-label='simple table'>
                <TableHead>
                  <TableRow>
                    <TableCell>Nazwa tematu</TableCell>
                    <TableCell>Opiekun Tematu</TableCell>
                    <TableCell align='right'>Limit miejsc</TableCell>
                    <TableCell align='right'>Ilość przypisań</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow
                    key={projectResponse.name}
                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                  >
                    <TableCell>{projectResponse.name}</TableCell>
                    <TableCell>{projectResponse.projectManager}</TableCell>
                    <TableCell align='right'>
                      {projectResponse.teamLimit}
                    </TableCell>
                    <TableCell align='right'>
                      {projectResponse.finalAssignedTeamsCount}
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>

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
