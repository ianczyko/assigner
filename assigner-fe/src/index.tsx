import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './Home/Home';
import Dashboard from './Dashboard/Dashboard';
import Callback from './Callback/Callback';
import Courses from './Courses/Courses';
import CourseEditionGroup from './CourseEditionGroup/CourseEditionGroup';
import NotFound from './NotFound/NotFound';
import reportWebVitals from './reportWebVitals';
import './index.css';
import 'react-toastify/dist/ReactToastify.css';
import Team from './Team/Team';
import Project from './Project/Project';
import AssignmentView from './AssignmentView/AssignmentView';
import { ThemeProvider, createTheme } from '@mui/material';

const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#1565c0',
      contrastText: '#fff',
    },
  },
});

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <ThemeProvider theme={darkTheme}>
      <Router>
        <Routes>
          <Route path='/' element={<Home />} />
          <Route path='/callback' element={<Callback />} />
          <Route path='/dashboard' element={<Dashboard />} />
          <Route path='/courses' element={<Courses />} />
          <Route
            path='/courses/:course_name/:edition/:group_name'
            element={<CourseEditionGroup />}
          />
          <Route
            path='/courses/:course_name/:edition/:group_name/assignment-view'
            element={<AssignmentView />}
          />
          <Route
            path='/courses/:course_name/:edition/:group_name/teams/:team_id'
            element={<Team />}
          />
          <Route
            path='/courses/:course_name/:edition/:group_name/projects/:project_id'
            element={<Project />}
          />
          <Route path='*' element={<NotFound />} />
        </Routes>
      </Router>
    </ThemeProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
