import './Courses.css';

import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Popup from 'reactjs-popup';
import wretch from 'wretch';
import { Button } from '@mui/material';
import NewCourseEdition from '../NewCourseEdition/NewCourseEdition';
import NewCourse from '../NewCourse/NewCourse';

function Courses() {
  const [courses, setCourses] = useState<Array<ICourse> | null>(null);
  const [isOpenCourse, setIsOpenCourse] = useState(false);
  const [isOpen, setIsOpen] = useState(false);

  interface ICourse {
    id: string;
    name: string;
    courseEditions: Array<ICourseEdition>;
  }

  interface ICourseEdition {
    id: string;
    edition: string;
  }

  function fetchCourses() {
    wretch('/api/courses')
      .get()
      .json((json) => {
        setIsOpen(false);
        setCourses(json);
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    fetchCourses();
  }, []);

  if (courses != null && courses.length > 0) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          {newCoursePopup()}
          <ul>
            {courses.map((course) => {
              return (
                <li key={course.id}>
                  <div className='Assigner-row-parent'>
                    <div className='Assigner-row-child'>{course.name}</div>
                    <div className='Assigner-row-child'>
                      <Popup
                        trigger={(open) => (
                          <Button variant='contained'>Nowa edycja</Button>
                        )}
                        position='right center'
                        closeOnDocumentClick
                        open={isOpen}
                        onOpen={() => setIsOpen(!isOpen)}
                      >
                        <NewCourseEdition
                          courseName={course.name}
                          onFinish={fetchCourses}
                        />
                      </Popup>
                    </div>
                  </div>
                  <ul>
                    {course.courseEditions.map((courseEdition) => {
                      return (
                        <li key={courseEdition.id}>
                          <Link
                            className='Assigner-link'
                            to={
                              '/courses/' +
                              course.name +
                              '/' +
                              courseEdition.edition
                            }
                          >
                            {courseEdition.edition}
                          </Link>
                        </li>
                      );
                    })}
                  </ul>
                </li>
              );
            })}
          </ul>
        </header>
      </div>
    );
  } else if (courses != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <p>Brak kursów.</p>
          {newCoursePopup()}
        </header>
      </div>
    );
  }

  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <p>Trwa ładowanie kursów...</p>
      </header>
    </div>
  );

  function newCoursePopup() {
    return (
      <Popup
        trigger={(open) => <Button variant='contained'>Nowy kurs</Button>}
        position='right center'
        closeOnDocumentClick
        open={isOpenCourse}
        onOpen={() => setIsOpenCourse(!isOpenCourse)}
      >
        <NewCourse onFinish={fetchCourses} />
      </Popup>
    );
  }
}

export default Courses;
