import Countdown, { zeroPad } from 'react-countdown';

function Callback() {
  function goBackToOpener() {
    window.opener?.postMessage(
      { verifier: window.location.search.split('oauth_verifier=')[1] },
      '*'
    );
    window.close();
  }

  interface TimeSegments {
    seconds: number;
  }

  const renderer = ({ seconds }: TimeSegments) => (
    <span>Kontynuowanie w: {zeroPad(seconds, 1)}s</span>
  );

  return (
    <div className='Assigner-center-container'>
      <header className='Assigner-center Assigner-header'>
        <p>Logowanie pomyślne!</p>
        <br />
        <Countdown
          date={Date.now() + 3000}
          onComplete={goBackToOpener}
          renderer={renderer}
        />
      </header>
    </div>
  );
}

export default Callback;
