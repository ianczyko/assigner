import { NavigateFunction } from 'react-router-dom';

const Helpers = {
  handleUnathorised: function (navigate: NavigateFunction) {
    navigate('/');
  },
};

export default Helpers;
