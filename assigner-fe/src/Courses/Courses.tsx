import './Courses.css';

import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Popup from 'reactjs-popup';
import wretch from 'wretch';
import { Button } from '@mui/material';
import NewCourseEdition from '../NewCourseEdition/NewCourseEdition';
import NewCourse from '../NewCourse/NewCourse';
import Helpers, { UserType } from '../Common/Helpers';

import { ToastContainer } from 'react-toastify';

function Courses() {
  const [courses, setCourses] = useState<Array<ICourse> | null>(null);
  const [isOpenCourse, setIsOpenCourse] = useState(false);
  const [isOpenDict, setIsOpenDict] = useState<Record<number, boolean>>({});
  const [userType, setUserType] = useState<UserType>(UserType.STUDENT);

  const navigate = useNavigate();

  interface ICourse {
    id: number;
    name: string;
    courseEditions: Array<ICourseEdition>;
  }

  interface ICourseEdition {
    id: string;
    edition: string;
    courseEditionGroups: Array<ICourseEditionGroup>;
  }

  interface ICourseEditionGroup {
    id: string;
    groupName: string;
  }

  function fetchCourses() {
    wretch('/api/courses')
      .get()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((response) => {
        Helpers.extractUserType(response, setUserType);
        return response.json();
      })
      .then((json) => {
        setCourses(json);
        if (courses != null) {
          let dictionary = Object.assign(
            {},
            ...courses.map((x) => ({ [x.id]: false }))
          );
          setIsOpenDict(dictionary);
        }
      })
      .catch((error) => console.log(error));
  }

  useEffect(() => {
    fetchCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (courses != null && courses.length > 0) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          {newCoursePopup()}
          <ul>
            {courses.map((course) => {
              return (
                <li key={course.id}>
                  <div className='Assigner-row-parent'>
                    <div className='Assigner-row-child'>{course.name}</div>
                    <div className='Assigner-row-child'>
                      {(() => {
                        if (userType === UserType.STUDENT) {
                          return <div></div>;
                        }
                        return (
                          <Popup
                            trigger={(open) => (
                              <Button variant='contained'>Nowa edycja</Button>
                            )}
                            position='right center'
                            closeOnDocumentClick
                            open={isOpenDict[course.id]}
                            onOpen={() => {
                              setIsOpenDict({
                                ...isOpenDict,
                                [course.id]: !isOpenDict[course.id],
                              });
                            }}
                          >
                            <NewCourseEdition
                              courseName={course.name}
                              onFinish={fetchCourses}
                            />
                          </Popup>
                        );
                      })()}
                    </div>
                  </div>
                  <ul>
                    {course.courseEditions.map((courseEdition) => {
                      return (
                        <li key={courseEdition.id}>
                          {courseEdition.edition}
                          <ul>
                            {courseEdition.courseEditionGroups.map(
                              (courseEditionGroup) => {
                                return (
                                  <li key={courseEditionGroup.id}>
                                    <Link
                                      className='Assigner-link'
                                      to={
                                        '/courses/' +
                                        course.name +
                                        '/' +
                                        courseEdition.edition +
                                        '/' +
                                        courseEditionGroup.groupName
                                      }
                                    >
                                      {courseEditionGroup.groupName}
                                    </Link>
                                  </li>
                                );
                              }
                            )}
                          </ul>
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
          <ToastContainer />
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
    if (userType === UserType.STUDENT) {
      return;
    }
    return (
      <div>
        <Popup
          trigger={(open) => <Button variant='contained'>Nowy kurs</Button>}
          position='right center'
          closeOnDocumentClick
          open={isOpenCourse}
          onOpen={() => setIsOpenCourse(!isOpenCourse)}
        >
          <NewCourse onFinish={fetchCourses} />
        </Popup>
      </div>
    );
  }
}

export default Courses;
