import './NewCourse.css';

import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

import 'react-toastify/dist/ReactToastify.css';

interface NewCourseParams {
  onFinish: Function;
}

function NewCourse({ onFinish }: NewCourseParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);

  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    const w = wretch().addon(QueryStringAddon);
    w.url(`/api/courses`)
      .query({ name: data.name })
      .post()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((res) => {
        console.log(res); // TODO: remove me
        onFinish();
      })
      .catch((error) => console.log(error));
  };

  return (
    <form ref={form} onSubmit={handleSubmit(onSubmit)}>
      <h2>Nowy kurs</h2>
      <label htmlFor='name'>Nazwa kursu</label>
      <input
        placeholder='np. PZSP2'
        {...register('name', { required: true })}
      />
      <input type='submit' />
    </form>
  );
}

export default NewCourse;
