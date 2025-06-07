import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import ProtectedRoute from './auth/ProtectedRoute';

import Header from './components/Header';
import Footer from './components/Footer';
import NewsFeedPage from './pages/NewsFeedPage';
import NewsArticleDetailPage from './pages/NewsArticleDetailPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import SavedArticlesPage from './pages/SavedArticlesPage'; // New import

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen flex flex-col bg-[#F9FAFB]">
          <Header />
          <main className="flex-grow">
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/articles/:id" element={<NewsArticleDetailPage />} />

              {/* Protected Routes - only accessible if logged in */}
              <Route element={<ProtectedRoute />}>
                <Route path="/" element={<NewsFeedPage />} />
                <Route path="/profile" element={<h1 className="text-center text-3xl font-bold mt-10 text-[#1F2937] font-['Inter', sans-serif]">User Profile (Coming Soon!)</h1>} />
                <Route path="/saved-articles" element={<SavedArticlesPage />} /> {/* New Protected Route */}
              </Route>

              {/* Fallback Route for 404 */}
              <Route path="*" element={<h1 className="text-center text-3xl font-bold mt-10 text-[#1F2937] font-['Inter', sans-serif]">404 - Page Not Found</h1>} />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;