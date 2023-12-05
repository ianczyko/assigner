import './UpdateProjectDescription.css';

import wretch from 'wretch';
import QueryStringAddon from 'wretch/addons/queryString';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

interface UpdateProjectDescriptionParams {
  courseName: string;
  courseEdition: string;
  groupName: string;
  projectId: string;
  onFinish: Function;
}

function UpdateProjectDescription({
  courseName,
  courseEdition,
  groupName,
  projectId,
  onFinish,
}: UpdateProjectDescriptionParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);
  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    const w = wretch().addon(QueryStringAddon);
    w.url(
      `/api/courses/${courseName}/editions/${courseEdition}/groups/${groupName}/projects/${projectId}/description`
    )
      .query({
        'new-description': data.description,
      })
      .put()
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
      <h2>Zmiana opisu</h2>
      <label htmlFor='description'>Nowy opis tematu</label>
      <textarea
        rows={9}
        cols={32}
        style={{ resize: 'none' }}
        placeholder='np. System ma wyznaczać wstępny przydział projektów na podstawie całkowitoliczbowego modelu optymalizacyjnego'
        {...register('description', { required: true })}
      />
      <input type='submit' style={{
        marginTop: 20
      }} />
    </form>
  );
}

export default UpdateProjectDescription;
