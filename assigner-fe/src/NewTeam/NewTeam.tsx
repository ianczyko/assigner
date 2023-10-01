import './NewTeam.css';

import wretch from 'wretch';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

interface NewTeamParams {
  courseName: string;
  courseEdition: string;
  onFinish: Function;
}

function NewTeam({ courseName, courseEdition, onFinish }: NewTeamParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);
  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    wretch()
      .url(`/api/courses/${courseName}/editions/${courseEdition}/teams`)
      .post({ name: data.name })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .res((res) => {
        console.log(res); // TODO: remove me
        onFinish();
      })
      .catch((error) => console.log(error));
  };

  return (
    <form ref={form} onSubmit={handleSubmit(onSubmit)}>
      <h2>Nowy zespół</h2>
      <label htmlFor='name'>Nazwa zespołu</label>
      <input
        placeholder='np. The Lambda Team'
        {...register('name', { required: true })}
      />
      <input type='submit' />
    </form>
  );
}

export default NewTeam;
