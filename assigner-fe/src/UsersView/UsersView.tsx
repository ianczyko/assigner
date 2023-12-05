import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import './UsersView.css';
import Forbidden from '../Forbidden/Forbidden';
import Helpers, { UserType } from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';
import CustomNavigator from '../CustomNavigator/CustomNavigator';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { Paper } from '@mui/material';

function UsersView() {
  const [isForbidden, setIsForbidden] = useState(false);

  const [usersResponse, setUsersResponse] = useState<Array<IUser> | null>(null);

  const navigate = useNavigate();

  interface IUser {
    id: number;
    name: string;
    secondName: string | null;
    surname: string;
    usosId: number;
    userType: UserType;
  }

  function fetchUsers() {
    return wretch(`/api/users`)
      .get()
      .forbidden((error) => {
        setIsForbidden(true);
      })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setUsersResponse(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (isForbidden) {
    return <Forbidden />;
  }

  if (usersResponse != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <CustomNavigator
            showCourses={false}
            custom_text='Zarządzanie Użytkownikami'
          />

          <TableContainer sx={{ width: 650 }} component={Paper}>
            <Table aria-label='simple table'>
              <TableHead>
                <TableRow>
                  <TableCell></TableCell>
                  <TableCell>USOS Id</TableCell>
                  <TableCell>Imię</TableCell>
                  <TableCell>Drugie imię</TableCell>
                  <TableCell>Nazwisko</TableCell>
                  <TableCell>Rola</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {usersResponse.map((user, index) => (
                  <TableRow
                    key={user.id}
                    sx={{
                      '&:last-child td, &:last-child th': { border: 0 },
                    }}
                  >
                    <TableCell>{index + 1}</TableCell>
                    <TableCell>{user.usosId}</TableCell>
                    <TableCell>{user.name}</TableCell>
                    <TableCell>{user.secondName}</TableCell>
                    <TableCell>{user.surname}</TableCell>
                    <TableCell>{user.userType}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
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

export default UsersView;
