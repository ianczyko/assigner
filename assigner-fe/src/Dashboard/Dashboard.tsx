import React, { useEffect, useState } from 'react';
import './Dashboard.css';

function Dashboard() {
  interface IProfileResponse {
    id: string;
    first_name: string;
    last_name: string;
  }
  const [profile, setProfile] = useState<IProfileResponse | null>(null);


  useEffect(() => {
    fetch('/profile')
      .then((response) => response.json())
      .then(function (data) {
        setProfile(data);
      });
  }, []);

  if (profile != null) {
    return (
      <div className="Assigner-center-container">
        <header className="Assigner-center Assigner-header">
          <p>
            {profile.id} - {profile.first_name} - {profile.last_name}
          </p>
        </header>
      </div>
    );
  }

  return (
    <div className="Assigner-center-container">
      <header className="Assigner-center Assigner-header">
        <p>
          Loading profile...
        </p>
      </header>
    </div>
  );
}

export default Dashboard;
