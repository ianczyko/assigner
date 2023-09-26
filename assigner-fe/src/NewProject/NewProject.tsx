import './NewProject.css';

import wretch from 'wretch';
import { FieldValues, useForm } from 'react-hook-form';
import { useRef } from 'react';

interface NewProjectParams {
  courseName: string;
  courseEdition: string;
  onFinish: Function;
}

function NewProject({ courseName, courseEdition, onFinish }: NewProjectParams) {
  const { register, handleSubmit } = useForm();

  const form = useRef(null);

  const onSubmit = async (data: FieldValues) => {
    wretch()
      .url(`/api/courses/${courseName}/editions/${courseEdition}/projects`)
      .post({ name: data.name, description: data.description })
      .res((res) => {
        console.log(res); // TODO: remove me
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
      <input
        placeholder='System ma wyznaczać wstępny przydział projektów na podstawie całkowitoliczbowego modelu optymalizacyjnego'
        {...register('description', { required: true })}
      />
      <input type='submit' />
    </form>
  );
}

export default NewProject;
