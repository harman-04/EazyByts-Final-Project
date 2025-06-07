import axios from 'axios';

// Create an Axios instance with a base URL
const newsApi = axios.create({
  baseURL: 'http://localhost:8080/api', // Your Spring Boot backend API base URL
  headers: {
    'Content-Type': 'application/json',
  },
});

export default newsApi;