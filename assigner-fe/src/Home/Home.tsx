import React, { useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import LoadingButton from '@mui/lab/LoadingButton';
import './Home.css';

function Home() {
  const [loading, setLoading] = React.useState(true);
  const [url, setUrl] = React.useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetch('/auth', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ callbackUrl: "http://localhost:3000/callback" }),
    })
      .then((response) => response.json())
      .then(function (data) {
        setLoading(false);
        setUrl(data.authorizeUrl);
      });
  }, []);

  function usosLogin() {
    console.log('You clicked submit, url=' + url);
    const authWindow = window.open(url, '_blank');

    const receiveVerifier = (event: any) => {
      if (event.data.verifier) {
        authWindow?.close();
        fetch('/verify', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ verifier: event.data.verifier }),
        })
          .then((response) => {
            if (response.status == 200) {
              navigate("/dashboard");
            } else {
              // TODO: better fetch error handling
              console.log(response.status)
              console.log(response.body)
              console.log(response)
            }
          })
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
