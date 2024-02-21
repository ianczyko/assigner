import './NewCourseEditionGroup.css';

import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

import 'react-toastify/dist/ReactToastify.css';

interface NewCourseEditionGroupParams {
  onFinish: Function;
  edition: string;
  courseName: string;
}

function NewCourseEditionGroup({
  edition,
  courseName,
  onFinish,
}: NewCourseEditionGroupParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);

  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    const w = wretch().addon(QueryStringAddon);
    w.url(`/api/courses/${courseName}/editions/${edition}/groups`)
      .query({ group: data.group })
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
    <form ref={form} onSubmit={handleSubmit(onSubmit)}>
      <h2>
        Nowy grupa dla edycji {edition} kursu {courseName}{' '}
      </h2>
      <label htmlFor='group'>Nazwa grupy</label>
      <input
        placeholder='np. 101 lub pt 16:15'
        {...register('group', { required: true })}
      />
      <input type='submit' />
    </form>
  );
}

export default NewCourseEditionGroup;
