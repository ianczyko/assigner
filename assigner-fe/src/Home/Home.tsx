import React, { useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import LoadingButton from '@mui/lab/LoadingButton';
import wretch from 'wretch';
import './Home.css';

function Home() {
  const [loading, setLoading] = React.useState(true);
  const [url, setUrl] = React.useState("");
  const navigate = useNavigate();

  useEffect(() => {
    wretch('/auth')
      .post({ callbackUrl: window.location.href + "callback" })
      .json(json => {
        setLoading(false);
        setUrl(json.authorizeUrl);
      })
      .catch(error => console.log(error));
  }, []);

  function usosLogin() {
    const authWindow = window.open(url, '_blank');

    const receiveVerifier = (event: any) => {
      if (event.data.verifier) {
        authWindow?.close();
        window.removeEventListener('message', receiveVerifier);
        wretch('/verify')
          .post({ verifier: event.data.verifier })
          .res((_res) => {
            navigate("/dashboard");
          })
          .catch(error => console.log(error));
      }
    };
    window.addEventListener('message', receiveVerifier);
  }
  return (
    <div className="Assigner-center-container">
      <div className="Assigner-center">
        <LoadingButton variant="contained" loading={loading} onClick={usosLogin}>
          USOS Login
        </LoadingButton>
      </div>
    </div>
  );
}

export default Home;
