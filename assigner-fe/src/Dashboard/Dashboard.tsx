import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import wretch from 'wretch';
import './Dashboard.css';
import Helpers from '../Common/Helpers';
import { ToastContainer } from 'react-toastify';

function Dashboard() {
  interface IProfileResponse {
    id: string;
    first_name: string;
    last_name: string;
  }
  const [profile, setProfile] = useState<IProfileResponse | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    wretch('/api/profile')
      .get()
      .unauthorized((error) => {
        Helpers.handleUnathorised(navigate);
      })
      .forbidden((error) => {
        Helpers.handleForbidden();
      })
      .json((json) => {
        setProfile(json);
      })
      .catch((error) => console.log(error));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (profile != null) {
    return (
      <div className='Assigner-center-container'>
        <header className='Assigner-center Assigner-header'>
          <ToastContainer />
          <p>
            {profile.id} - {profile.first_name} - {profile.last_name}
          </p>
          <Link className='Assigner-link' to='/courses'>
            Kursy
          </Link>
        </header>
      </div>
    );
  }

  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <ToastContainer />
        <p>Trwa ładowanie profilu...</p>
      </header>
    </div>
  );
}

export default Dashboard;
