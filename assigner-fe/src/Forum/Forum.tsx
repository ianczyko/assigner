import './Forum.css';
import wretch from 'wretch';
import Helpers from '../Common/Helpers';
import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { ToastContainer } from 'react-toastify';
import { Stack } from '@mui/material';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';
import moment from 'moment';
import 'moment/locale/pl';
moment.locale('pl');

function Forum() {
  const { course_name, edition, project_id } = useParams();

  const navigate = useNavigate();

  const { register, handleSubmit } = useForm();
  const form = useRef(null);

  const [forumResponse, setForumResponse] = useState<Array<IForumResponse>>([]);

  interface IForumResponse {
    id: Number;
    content: string;
    createdDate: Date;
    author: IUser;
  }

  interface IUser {
    id: Number;
    name: string;
    secondName: string | null;
    surname: string;
  }

  useEffect(() => {
    wretch(
      `/api/courses/${course_name}/editions/${edition}/projects/${project_id}/forum-comments`
    )
      .get()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setForumResponse(json);
        console.log(json); // TODO: remove me
      })
      .catch((error) => console.log(error));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [course_name, edition, project_id]);

  const onSubmit = async (data: FieldValues) => {
    wretch()
      .url(
        `/api/courses/${course_name}/editions/${edition}/projects/${project_id}/forum-comments`
      )
      .post({ content: data.content })
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .res((res) => {
        console.log(res); // TODO: remove me
        // onFinish();
      })
      .catch((error) => console.log(error));
  };

  if (forumResponse != null) {
    return (
      <div>
        <ToastContainer />
        <Stack>
          <h3>Forum</h3>
          <Stack spacing='10px'>
            {forumResponse.map((entry) => {
              return (
                <div key={entry.id.toString()}>
                  <Stack direction='row' alignItems='center' spacing='30px'>
                    <Stack>
                      <p className='Assigner-font-medium'>
                        {entry.author.name} {entry.author.surname}
                      </p>
                      <p className='Assigner-font-medium'>
                        {moment(entry.createdDate).fromNow()}
                      </p>
                    </Stack>
                    <p
                      style={{
                        width: '85%',
                        wordWrap: 'break-word',
                        textAlign: 'left',
                      }}
                      className='Assigner-font-xlarge'
                    >
                      {entry.content}
                    </p>
                  </Stack>
                </div>
              );
            })}
          </Stack>
          <form ref={form} onSubmit={handleSubmit(onSubmit)}>
            <h4>Dodaj wpis do Forum</h4>
            <label htmlFor='content'>Treść komentarza</label>
            <input {...register('content', { required: true })} />
            <input type='submit' />
          </form>
        </Stack>
      </div>
    );
  }

  return (
    <div>
      <ToastContainer />
      <p>Trwa Ładowanie zespołu...</p>
    </div>
  );
}

export default Forum;
