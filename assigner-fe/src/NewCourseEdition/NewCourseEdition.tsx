import './NewCourseEdition.css';

import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

interface NewCourseEditionParams {
  courseName: string;
  onFinish: Function;
}

function NewCourseEdition({ courseName, onFinish }: NewCourseEditionParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);

  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    const w = wretch().addon(QueryStringAddon);
    w.url(`/api/courses/${courseName}/editions`)
      .query({
        edition: data.edition,
      })
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
      <h2>Nowa edycja dla kursu {courseName}</h2>
      <label htmlFor='edition'>Nazwa edycji</label>
      <input
        placeholder='np. 22z'
        {...register('edition', { required: true })}
      />
      <input type='submit' />
    </form>
  );
}

export default NewCourseEdition;
