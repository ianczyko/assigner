import './JoinTeam.css';

import wretch from 'wretch';
import { FieldValues, useForm } from 'react-hook-form';
import QueryStringAddon from 'wretch/addons/queryString';
import { useRef } from 'react';
import Helpers from '../Common/Helpers';
import { useNavigate } from 'react-router-dom';

interface JoinTeamParams {
  courseName: string;
  courseEdition: string;
  onFinish: Function;
}

function JoinTeam({ courseName, courseEdition, onFinish }: JoinTeamParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);

  const navigate = useNavigate();

  const onSubmit = async (data: FieldValues) => {
    let token: string = data.token;
    if (token.split(':').length === 0) return;
    let teamId = token.split(':')[0];
    let actualtoken = token.split(':')[1];
    const w = wretch().addon(QueryStringAddon);
    w.url(
      `/api/courses/${courseName}/editions/${courseEdition}/teams/${teamId}/members`
    )
      .query({ 'access-token': actualtoken })
      .post()
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
      <h2>Dołącz do zespołu</h2>
      <label htmlFor='token'>Kod dostępu</label>
      <input
        placeholder='np. 11:123456'
        {...register('token', { required: true })}
      />
      <input type='submit' />
    </form>
  );
}

export default JoinTeam;
