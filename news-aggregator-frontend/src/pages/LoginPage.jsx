import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await login(username, password);

    if (result.success) {
      navigate('/'); // Redirect to home page on successful login
    } else {
      setError(result.message || 'Login failed. Please check your credentials.');
    }
    setLoading(false);
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-100px)] bg-[#F9FAFB] font-['Inter', sans-serif]">
      <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-md">
        <h2 className="text-3xl font-bold text-[#1F2937] mb-6 text-center">Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="username" className="block text-sm font-medium text-[#4B5563] mb-1">Username or Email</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-4 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
              required
            />
          </div>
          <div className="mb-6">
            <label htmlFor="password" className="block text-sm font-medium text-[#4B5563] mb-1">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
              required
            />
          </div>
          {error && <p className="text-[#EF4444] text-sm mb-4 text-center">{error}</p>}
          <button
            type="submit"
            className="w-full bg-[#4F46E5] text-white py-2 px-4 rounded-md hover:bg-[#6B7280] transition duration-300 font-semibold"
            disabled={loading}
          >
            {loading ? 'Logging In...' : 'Login'}
          </button>
        </form>
        <p className="text-center text-sm text-[#4B5563] mt-6">
          Don't have an account?{' '}
          <Link to="/register" className="text-[#4F46E5] hover:underline">Register here</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;