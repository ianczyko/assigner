import './JoinCourseEditionGroup.css';

import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import { useEffect, useState } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

import 'react-toastify/dist/ReactToastify.css';
import {
  Button,
  FormControl,
  MenuItem,
  Select,
  SelectChangeEvent,
} from '@mui/material';

interface JoinCourseEditionGroupParams {
  onFinish: Function;
  edition: string;
  courseName: string;
}

interface ICourseEditionGroup {
  id: string;
  groupName: string;
}

function JoinCourseEditionGroup({
  edition,
  courseName,
  onFinish,
}: JoinCourseEditionGroupParams) {
  const navigate = useNavigate();
  const [groups, setGroups] = useState<Array<ICourseEditionGroup> | null>(null);
  const [group, setGroup] = useState<string>('');

  useEffect(() => {
    wretch(`/api/courses/${courseName}/editions/${edition}/groups`)
      .get()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((response) => {
        return response.json();
      })
      .then((json) => {
        setGroups(json);
      })
      .catch((error) => console.log(error));
  }, [courseName, edition, navigate]);

  useEffect(() => {
    if (groups == null || groups.length === 0) {
      return;
    }
    setGroup(groups![0].groupName);
  }, [groups]);

  const onSubmit = async () => {
    const w = wretch().addon(QueryStringAddon);
    w.url(`/api/courses/${courseName}/editions/${edition}/groups/initial`)
      .query({ group: group })
      .post()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((res) => {
        onFinish();
      })
      .catch((error) => console.log(error));
  };

  return (
    <>
      <h2>
        Wybierz grupę w edycji {edition} kursu {courseName}
        {groups == null ? (
          <p>Ładowanie grup...</p>
        ) : (
          <>
            <FormControl
              variant='standard'
              sx={{ minWidth: 120, paddingTop: '2px', marginTop: '15px' }}
            >
              <Select
                value={group}
                onChange={(event: SelectChangeEvent) => {
                  setGroup(event.target.value);
                }}
                label='Przypisany zespół'
                MenuProps={{
                  disablePortal: true,
                  onClick: (e) => {
                    e.preventDefault();
                  },
                }}
                sx={{
                  '& .MuiSelect-select': {
                    paddingLeft: 2,
                  },
                }}
              >
                {groups!.map((group) => {
                  return (
                    <MenuItem key={group.id} value={group.groupName}>
                      {group.groupName}
                    </MenuItem>
                  );
                })}
              </Select>
            </FormControl>
            <Button
              variant='contained'
              onClick={onSubmit}
              style={{ margin: '20px 0 20px 0' }}
            >
              Dołącz do grupy*
            </Button>
            <p className='Assigner-font-small'>
              * Studenci przoszeni są o dołączanie jedynie do przedmiotów,
              edycji i grupy której są członkami. <br /> Zmiana grupy w obrębie
              edycji wymaga kontaktu z koordynatorem.
            </p>
          </>
        )}
      </h2>
    </>
  );
}

export default JoinCourseEditionGroup;
