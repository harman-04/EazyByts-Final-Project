import React, { createContext, useState, useEffect, useContext } from 'react';
import authApi from '../api/authApi';
import newsApi from '../api/newsApi'; // Also import newsApi to add interceptor

// Create the AuthContext
export const AuthContext = createContext(null);

// Create a custom hook for easy access to auth context
export const useAuth = () => {
  return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Stores user details (username, email)
  const [token, setToken] = useState(localStorage.getItem('jwt_token')); // Stores JWT token
  const [loading, setLoading] = useState(true); // To check if initial authentication state is loaded

  // Function to save user and token
  const saveAuthData = (userData, jwtToken) => {
    setUser(userData);
    setToken(jwtToken);
    if (jwtToken) {
      localStorage.setItem('jwt_token', jwtToken); // Store token in local storage
    } else {
      localStorage.removeItem('jwt_token');
    }
  };

  // Login function
  const login = async (username, password) => {
    try {
      const response = await authApi.post('/login', { username, password });
      // Assuming your backend returns { username, email, token }
      const { username: userUsername, email, token: jwtToken } = response.data;
      saveAuthData({ username: userUsername, email }, jwtToken);
      return { success: true };
    } catch (error) {
      console.error('Login failed:', error.response?.data || error.message);
      saveAuthData(null, null); // Clear any partial data
      return { success: false, message: error.response?.data?.message || 'Invalid username or password' };
    }
  };

  // Register function
  const register = async (username, email, password) => {
    try {
      const response = await authApi.post('/register', { username, email, password });
      // Assuming your backend returns { username, email, token } upon successful registration
      const { username: userUsername, email: userEmail, token: jwtToken } = response.data;
      saveAuthData({ username: userUsername, email: userEmail }, jwtToken);
      return { success: true };
    } catch (error) {
      console.error('Registration failed:', error.response?.data || error.message);
      saveAuthData(null, null);
      // Backend throws DuplicateResourceException, map it to a user-friendly message
      const errorMessage = error.response?.data?.message || 'Registration failed. Please try again.';
      if (errorMessage.includes("username already exists")) {
          return { success: false, message: 'Username already exists.' };
      }
      if (errorMessage.includes("email already registered")) {
          return { success: false, message: 'Email already registered.' };
      }
      return { success: false, message: errorMessage };
    }
  };

  // Logout function
  const logout = () => {
    saveAuthData(null, null);
  };

  // Axios Interceptor for attaching token to requests
  useEffect(() => {
    // Attach token to authApi and newsApi requests
    const requestInterceptor = newsApi.interceptors.request.use(
      (config) => {
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Clean up interceptor on unmount or token change
    return () => {
      newsApi.interceptors.request.eject(requestInterceptor);
    };
  }, [token]); // Re-run effect if token changes

  // Initial load check (e.g., if token exists from previous session)
  // In a real app, you might validate the token with a /validate or /me endpoint
  useEffect(() => {
    // For now, we just assume if token exists, user is logged in
    // A more robust solution would involve a /me endpoint that validates the token
    // and returns user details.
    if (token) {
      // Potentially make a call to a /me endpoint to get user details
      // If the backend had a /api/users/me endpoint that returns UserDTO,
      // you'd call it here:
      /*
      const fetchUserDetails = async () => {
        try {
          const response = await newsApi.get('/users/me');
          setUser(response.data);
        } catch (err) {
          console.error('Failed to fetch user details:', err);
          logout(); // Token might be invalid/expired
        } finally {
          setLoading(false);
        }
      };
      fetchUserDetails();
      */
      // For now, if token exists, we just set a placeholder user
      setUser({ username: 'Logged In User', email: 'user@example.com' }); // Placeholder
      setLoading(false);
    } else {
      setLoading(false);
    }
  }, [token]);


  const authContextValue = {
    user,
    token,
    loading,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={authContextValue}>
      {!loading && children} {/* Render children only after auth state is loaded */}
    </AuthContext.Provider>
  );
};