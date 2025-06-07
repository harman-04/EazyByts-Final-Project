import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './AuthContext';

const ProtectedRoute = () => {
  const { user, loading } = useAuth();

  if (loading) {
    // Optionally render a loading spinner or message here
    return (
      <div className="flex justify-center items-center h-screen bg-[#F9FAFB] text-[#4B5563]">
        Loading authentication...
      </div>
    );
  }

  // If user is not logged in, redirect to login page
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // If user is logged in, render the child routes
  return <Outlet />;
};

export default ProtectedRoute;