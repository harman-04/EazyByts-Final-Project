import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext'; // New import

const Header = () => {
  const { user, logout } = useAuth(); // Get user and logout function from context
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login'); // Redirect to login page after logout
  };

  return (
    <header className="bg-[#4F46E5] text-white p-4 shadow-md font-['Inter', sans-serif]">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/" className="text-2xl font-bold tracking-tight">
          NewsAggregator
        </Link>
        <nav>
          <ul className="flex items-center space-x-6">
            <li>
              <Link to="/" className="hover:text-[#D1D5DB] transition duration-300">Home</Link>
            </li>
            {user ? (
              <>
                <li>
                  <Link to="/profile" className="hover:text-[#D1D5DB] transition duration-300">Profile</Link>
                </li>
                <li>
                  <Link to="/saved-articles" className="hover:text-[#D1D5DB] transition duration-300">Saved</Link>
                </li>
                <li className="text-sm">Welcome, <span className="font-semibold">{user.username || user.email}</span></li>
                <li>
                  <button
                    onClick={handleLogout}
                    className="bg-[#EF4444] text-white px-3 py-1 rounded-md hover:bg-[#B91C1C] transition duration-300 font-semibold"
                  >
                    Logout
                  </button>
                </li>
              </>
            ) : (
              <>
                <li>
                  <Link to="/login" className="hover:text-[#D1D5DB] transition duration-300">Login</Link>
                </li>
                <li>
                  <Link to="/register" className="bg-white text-[#4F46E5] px-3 py-1 rounded-md hover:bg-[#E5E7EB] transition duration-300 font-semibold">
                    Register
                  </Link>
                </li>
              </>
            )}
          </ul>
        </nav>
      </div>
    </header>
  );
};

export default Header;