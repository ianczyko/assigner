import { NavigateFunction } from 'react-router-dom';
import { WretchResponse } from 'wretch/types';
import { toast } from 'react-toastify';

const Helpers = {
  handleUnathorised: function (navigate: NavigateFunction) {
    navigate('/');
  },

  handleForbidden: function () {
    toast.error('Brak uprawnień do wykonania akcji!', {
      position: 'top-right',
      autoClose: 3000,
      hideProgressBar: false,
      theme: 'dark',
    });
  },

  extractUserType: function (
    response: WretchResponse,
    setUserType: React.Dispatch<React.SetStateAction<UserType>>
  ) {
    const userType = response.headers.get('user-type');
    if (userType != null) {
      const userTypeOrdinal = parseInt(userType);
      setUserType(userTypeOrdinal);
    }
  },
};

export enum UserType {
  STUDENT = 0,
  TEACHER,
  COORDINATOR,
}

export default Helpers;
