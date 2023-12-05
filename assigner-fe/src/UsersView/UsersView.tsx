import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
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
import {
  FormControl,
  MenuItem,
  Paper,
  Select,
  SelectChangeEvent,
} from '@mui/material';

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

  const handleRoleAssignmentChange =
    (user: IUser) => (event: SelectChangeEvent) => {
      const w = wretch().addon(QueryStringAddon);
      w.url(`/api/users/${user.usosId}/role`)
        .query({
          'new-role': event.target.value,
        })
        .put()
        .unauthorized((error) => {
          Helpers.handleUnathorised(navigate);
        })
        .forbidden((error) => {
          Helpers.handleForbidden();
        })
        .res((res) => {
          fetchUsers();
        })
        .catch((error) => console.log(error));
    };

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

          <TableContainer sx={{ width: 750 }} component={Paper}>
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
                    <TableCell>
                      <FormControl
                        variant='standard'
                        sx={{ minWidth: 120, paddingTop: '2px' }}
                      >
                        <Select
                          value={String(user.userType)}
                          onChange={handleRoleAssignmentChange(user)}
                          label='Przypisany zespół'
                          sx={{
                            '& .MuiSelect-select': {
                              paddingLeft: 2,
                            },
                          }}
                        >
                          <MenuItem value={0}>
                            <em>Student</em>
                          </MenuItem>
                          <MenuItem value={1}>
                            <em>Nauczyciel</em>
                          </MenuItem>
                          <MenuItem value={2}>
                            <em>Koordynator</em>
                          </MenuItem>
                        </Select>
                      </FormControl>
                    </TableCell>
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
