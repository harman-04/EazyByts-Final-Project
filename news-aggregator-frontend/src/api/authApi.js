import axios from 'axios';

const authApi = axios.create({
  baseURL: 'http://localhost:8080/api/auth', // Base URL for authentication endpoints
  headers: {
    'Content-Type': 'application/json',
  },
});

export default authApi;