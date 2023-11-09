import { IconButton, Stack } from '@mui/material';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHouseChimney } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';

interface NavigatorParams {
  showCourses?: boolean;
  course_name?: string;
  edition?: string;
  group_name?: string;
  project_name?: string;
  project_id?: number;
  team_name?: string;
  team_id?: number;
}

export default function CustomNavigator({
  showCourses = true,
  course_name,
  edition,
  group_name,
  project_name,
  project_id,
  team_name,
  team_id,
}: NavigatorParams) {
  return (
    <Stack direction='row' spacing='10px' paddingBottom='40px'>
      <IconButton component={Link} to='/dashboard' color='inherit'>
        <FontAwesomeIcon icon={faHouseChimney} />
      </IconButton>
      {showCourses && (
        <>
          <p>/</p>
          <Link className='Assigner-link' to={`/courses/`}>
            Kursy
          </Link>
        </>
      )}
      {course_name && (
        <>
          <p>/</p>
          <p>{course_name}</p>
        </>
      )}

      {edition && (
        <>
          <p>/</p>
          <p>{edition}</p>
        </>
      )}

      {group_name && (
        <>
          <p>/</p>
          <Link
            className='Assigner-link'
            to={`/courses/${course_name}/${edition}/${group_name}`}
          >
            {group_name}
          </Link>
        </>
      )}

      {project_name && project_id && (
        <>
          <p>/</p>
          <Link
            className='Assigner-link'
            to={`/courses/${course_name}/${edition}/${group_name}/projects/${project_id}`}
          >
            {project_name}
          </Link>
        </>
      )}
      
      {team_name && team_id && (
        <>
          <p>/</p>
          <Link
            className='Assigner-link'
            to={`/courses/${course_name}/${edition}/${group_name}/teams/${team_id}`}
          >
            {team_name}
          </Link>
        </>
      )}
    </Stack>
  );
}
