import './NewCourseEdition.css';

import wretch from 'wretch';
import FormDataAddon from 'wretch/addons/formData';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';

interface NewCourseEditionParams {
  courseName: string;
  onFinish: Function;
}

function NewCourseEdition({ courseName, onFinish }: NewCourseEditionParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);
  const fileUpload = useRef<HTMLInputElement>(null);

  const onSubmit = async (data: FieldValues) => {
    const w = wretch().addon(FormDataAddon);
    var file = fileUpload.current!.files![0];
    w.url(`/api/courses/${courseName}/editions`)
      .formData({
        file,
        edition: data.edition,
      })
      .post()
      .res((res) => {
        onFinish();
        console.log(res); // TODO: change this
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
      <label htmlFor='file'>Lista studentów (.csv)</label>
      <input
        {...register('file', { required: false })}
        ref={fileUpload}
        type='file'
        accept='text/csv'
      />
      <input type='submit' />
    </form>
  );
}

export default NewCourseEdition;
