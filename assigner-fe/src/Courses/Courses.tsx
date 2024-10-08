import './Courses.css';

import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Popup from 'reactjs-popup';
import wretch from 'wretch';
import { Button, Stack } from '@mui/material';
import NewCourseEdition from '../NewCourseEdition/NewCourseEdition';
import NewCourse from '../NewCourse/NewCourse';
import Helpers, { UserType } from '../Common/Helpers';
import CustomNavigator from '../CustomNavigator/CustomNavigator';
import NewCourseEditionGroup from '../NewCourseEditionGroup/NewCourseEditionGroup';
import JoinCourseEditionGroup from '../JoinCourseEditionGroup/JoinCourseEditionGroup';

import { ToastContainer } from 'react-toastify';

function Courses() {
  const [courses, setCourses] = useState<Array<ICourse> | null>(null);
  const [isOpenCourse, setIsOpenCourse] = useState(false);
  const [isOpenDict, setIsOpenDict] = useState<Record<number, boolean>>({});
  const [isOpenGroupDict, setIsOpenGroupDict] = useState<
    Record<number, boolean>
  >({});
  const [isOpenJoinDict, setIsOpenJoinDict] = useState<Record<number, boolean>>(
    {}
  );
  const [userType, setUserType] = useState<UserType>(UserType.STUDENT);

  const navigate = useNavigate();

  interface ICourse {
    id: number;
    name: string;
    courseEditions: Array<ICourseEditionGroup>;
  }

  interface ICourseEditionGroup {
    id: number;
    edition: string;
    archived: boolean;
    courseEditionGroups: Array<ICourseEditionGroupGroup>;
  }

  interface ICourseEditionGroupGroup {
    id: string;
    groupName: string;
  }

  function fetchCourses() {
    wretch('/api/courses/filtered')
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
          <CustomNavigator />
          {newCoursePopup()}
          <ul>
            {courses.map((course) => {
              return (
                <li key={course.id} style={{ marginTop: 10 }}>
                  <Stack direction='row' spacing='20px'>
                    <p>{course.name}</p>
                    {userType === UserType.COORDINATOR && (
                      <Popup
                        trigger={(open) => (
                          <Button variant='contained' size='small'>
                            Nowa edycja
                          </Button>
                        )}
                        position='right center'
                        closeOnDocumentClick
                        open={isOpenDict[course.id]}
                        onOpen={() => {
                          setIsOpenDict({
                            ...isOpenDict,
                            [course.id]: true,
                          });
                        }}
                        onClose={() => {
                          setIsOpenDict({
                            ...isOpenDict,
                            [course.id]: false,
                          });
                        }}
                      >
                        <NewCourseEdition
                          courseName={course.name}
                          onFinish={fetchCourses}
                        />
                      </Popup>
                    )}
                  </Stack>
                  <ul>
                    {course.courseEditions.map((courseEdition) => {
                      return (
                        <li key={courseEdition.id} style={{ marginTop: 10 }}>
                          <Stack direction='row' spacing='20px'>
                            <p>{courseEdition.edition}</p>
                            {courseEdition.archived && <p>(Archiwum)</p>}
                            {userType === UserType.COORDINATOR && (
                              <Popup
                                trigger={(open) => (
                                  <Button variant='contained' size='small'>
                                    Nowa grupa
                                  </Button>
                                )}
                                position='right center'
                                closeOnDocumentClick
                                open={isOpenGroupDict[courseEdition.id]}
                                onOpen={() => {
                                  setIsOpenGroupDict({
                                    ...isOpenGroupDict,
                                    [courseEdition.id]: true,
                                  });
                                }}
                                onClose={() => {
                                  setIsOpenGroupDict({
                                    ...isOpenGroupDict,
                                    [courseEdition.id]: false,
                                  });
                                }}
                              >
                                <NewCourseEditionGroup
                                  edition={courseEdition.edition}
                                  courseName={course.name}
                                  onFinish={fetchCourses}
                                />
                              </Popup>
                            )}
                            {userType === UserType.STUDENT &&
                              !courseEdition.archived &&
                              courseEdition.courseEditionGroups.length ===
                                0 && (
                                <Popup
                                  trigger={(open) => (
                                    <Button variant='contained' size='small'>
                                      Dołącz do edycji
                                    </Button>
                                  )}
                                  position='right center'
                                  open={isOpenJoinDict[courseEdition.id]}
                                  onOpen={() => {
                                    setIsOpenJoinDict({
                                      ...isOpenJoinDict,
                                      [courseEdition.id]: true,
                                    });
                                  }}
                                  onClose={() => {
                                    setIsOpenJoinDict({
                                      ...isOpenJoinDict,
                                      [courseEdition.id]: false,
                                    });
                                  }}
                                >
                                  <JoinCourseEditionGroup
                                    edition={courseEdition.edition}
                                    courseName={course.name}
                                    onFinish={fetchCourses}
                                  />
                                </Popup>
                              )}
                          </Stack>

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
    if (userType !== UserType.COORDINATOR) {
      return;
    }
    return (
      <div>
        <Popup
          trigger={(open) => <Button variant='contained'>Nowy kurs</Button>}
          position='right center'
          closeOnDocumentClick
          open={isOpenCourse}
          onOpen={() => setIsOpenCourse(true)}
          onClose={() => setIsOpenCourse(false)}
        >
          <NewCourse onFinish={fetchCourses} />
        </Popup>
      </div>
    );
  }
}

export default Courses;
