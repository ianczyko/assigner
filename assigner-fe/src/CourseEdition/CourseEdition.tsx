import { useParams } from 'react-router-dom';
import './CourseEdition.css';

function CourseEdition() {
  const { course_name, edition } = useParams();
  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <p>{course_name}, edition: {edition}</p>
      </header>
    </div>
  );
}

export default CourseEdition;
