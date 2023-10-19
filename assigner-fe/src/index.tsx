import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './Home/Home';
import Dashboard from './Dashboard/Dashboard';
import Callback from './Callback/Callback';
import Courses from './Courses/Courses';
import CourseEdition from './CourseEdition/CourseEdition';
import NotFound from './NotFound/NotFound';
import reportWebVitals from './reportWebVitals';
import './index.css';
import 'react-toastify/dist/ReactToastify.css';
import Team from './Team/Team';
import Project from './Project/Project';
import AssignmentView from './AssignmentView/AssignmentView';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path='/' element={<Home />} />
        <Route path='/callback' element={<Callback />} />
        <Route path='/dashboard' element={<Dashboard />} />
        <Route path='/courses' element={<Courses />} />
        <Route
          path='/courses/:course_name/:edition'
          element={<CourseEdition />}
        />
        <Route
          path='/courses/:course_name/:edition/assignment-view'
          element={<AssignmentView />}
        />
        <Route
          path='/courses/:course_name/:edition/teams/:team_id'
          element={<Team />}
        />
        <Route
          path='/courses/:course_name/:edition/projects/:project_id'
          element={<Project />}
        />
        <Route path='*' element={<NotFound />} />
      </Routes>
    </Router>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
