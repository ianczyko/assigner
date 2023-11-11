import './NewTeam.css';

import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

interface NewTeamParams {
  courseName: string;
  courseEdition: string;
  groupName: string;
  addCreator: boolean;
  onFinish: Function;
}

function NewTeam({
  courseName,
  courseEdition,
  groupName,
  addCreator,
  onFinish,
}: NewTeamParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);
  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    const w = wretch().addon(QueryStringAddon);
    w.url(
      `/api/courses/${courseName}/editions/${courseEdition}/groups/${groupName}/teams`
    )
      .query({ 'add-creator': addCreator })
      .post({ name: data.name })
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
      <h2>Nowy zespół *</h2>
      <label htmlFor='name'>Nazwa zespołu</label>
      <input
        placeholder='np. The Lambda Team'
        {...register('name', { required: true })}
      />
      <input type='submit' />
      <p className='Assigner-font-small'>
        * jeśli tworzony przez studenta, student zostanie automatycznie
        członkiem zespołu, w przeciwnym wypadku zostanie utworzony pusty zespół.
      </p>
    </form>
  );
}

export default NewTeam;
