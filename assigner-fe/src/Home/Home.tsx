import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import LoadingButton from '@mui/lab/LoadingButton';
import wretch from 'wretch';
import { toast, ToastContainer } from 'react-toastify';
import './Home.css';

function Home() {
  const [loading, setLoading] = useState(true);
  const [url, setUrl] = useState('');
  const navigate = useNavigate();

  function fetchAuth() {
    setLoading(true);
    wretch('/api/auth')
      .post({ callbackUrl: window.location.href + 'callback' })
      .json((json) => {
        setLoading(false);
        setUrl(json.authorizeUrl);
      })
      .catch((error) => {
        console.log(error);
        setTimeout(fetchAuth, 3000);
      });
  }

  useEffect(() => {
    fetchAuth();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function usosLogin() {
    const authWindow = window.open(url, '_blank');

    const receiveVerifier = (event: any) => {
      if (event.data.verifier) {
        authWindow?.close();
        window.removeEventListener('message', receiveVerifier);
        wretch('/api/verify')
          .post({ verifier: event.data.verifier })
          .res((_res) => {
            navigate('/dashboard');
          })
          .catch((error) => {
            toast.error('Logowanie nie powiodło się, spróbuj ponownie.', {
              position: 'top-right',
              autoClose: 3000,
              hideProgressBar: false,
              theme: 'dark',
            });
            fetchAuth();
          });
      }
    };
    window.addEventListener('message', receiveVerifier);
  }
  return (
    <div className='Assigner-center-container'>
      <div className='Assigner-center'>
        <ToastContainer />
        <LoadingButton
          variant='contained'
          loading={loading}
          onClick={usosLogin}
        >
          USOS Login
        </LoadingButton>
      </div>
    </div>
  );
}

export default Home;
