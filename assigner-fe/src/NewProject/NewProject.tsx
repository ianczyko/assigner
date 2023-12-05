import './NewProject.css';

import wretch from 'wretch';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

interface NewProjectParams {
  courseName: string;
  courseEdition: string;
  groupName: string;
  onFinish: Function;
}

function NewProject({
  courseName,
  courseEdition,
  groupName,
  onFinish,
}: NewProjectParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);
  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    wretch()
      .url(
        `/api/courses/${courseName}/editions/${courseEdition}/groups/${groupName}/projects`
      )
      .post({
        name: data.name,
        description: data.description,
        teamLimit: data.teamLimit,
      })
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
      <h2>Nowy temat</h2>
      <label htmlFor='name'>Nazwa tematu</label>
      <input
        placeholder='np. System do przydziału projektów '
        {...register('name', { required: true })}
      />
      <label htmlFor='description'>Opis tematu</label>
      <textarea
        rows={9}
        cols={32}
        style={{ resize: 'none' }}
        placeholder='np. System ma wyznaczać wstępny przydział projektów na podstawie całkowitoliczbowego modelu optymalizacyjnego'
        {...register('description', { required: true })}
      />
      <label htmlFor='teamLimit'>Limit przypisanych zespołów</label>
      <input placeholder='1' {...register('teamLimit', { required: true })} />
      <input type='submit' />
    </form>
  );
}

export default NewProject;
