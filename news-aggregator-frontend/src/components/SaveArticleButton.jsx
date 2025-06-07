import React, { useState, useEffect } from 'react';
import newsApi from '../api/newsApi';
import { useAuth } from '../auth/AuthContext';

// Import heroicons for save/unsave icons
// If you haven't installed heroicons, run: npm install @heroicons/react
// Then configure your tailwind.config.js to purge heroicons if needed:
// content: ['./src/**/*.{js,jsx,ts,tsx}', './node_modules/@heroicons/react/**/*.js'],
import { BookmarkIcon as BookmarkOutline } from '@heroicons/react/24/outline';
import { BookmarkIcon as BookmarkSolid } from '@heroicons/react/24/solid';

const SaveArticleButton = ({ articleId, onSaveToggle }) => {
  const { user } = useAuth();
  const [isSaved, setIsSaved] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // TEMPORARY: As before, replace this with actual user.id from AuthContext
  const currentUserId = user ? 1 : null; // IMPORTANT: Update this with the real user ID

  useEffect(() => {
    // Only check saved status if user is logged in
    if (user && currentUserId) {
      const checkSavedStatus = async () => {
        setLoading(true);
        setError(null);
        try {
          // Fetch all saved articles for the user and check if this article is among them
          // In a real app, you might have a more optimized endpoint like /api/users/{userId}/saved-articles/status/{articleId}
          const response = await newsApi.get(`/users/${currentUserId}/saved-articles`);
          const savedArticles = response.data;
          const found = savedArticles.some(saved => saved.id === articleId); // 'id' from SavedArticleDTO refers to NewsArticle ID
          setIsSaved(found);
        } catch (err) {
          console.error('Error checking saved status:', err);
          setError('Failed to check saved status.');
        } finally {
          setLoading(false);
        }
      };
      checkSavedStatus();
    } else {
      setIsSaved(false); // Not saved if not logged in
    }
  }, [user, currentUserId, articleId]); // Re-run if user, userId, or articleId changes

  const handleSaveToggle = async () => {
    if (!user || !currentUserId) {
      alert('You must be logged in to save articles.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      if (isSaved) {
        // Unsave the article
        await newsApi.delete(`/users/${currentUserId}/saved-articles/${articleId}`);
        setIsSaved(false);
        alert('Article unsaved!');
      } else {
        // Save the article
        await newsApi.post(`/users/${currentUserId}/saved-articles/${articleId}`);
        setIsSaved(true);
        alert('Article saved!');
      }
      // Notify parent component if a callback is provided
      if (onSaveToggle) {
        onSaveToggle(!isSaved); // Pass the new saved status
      }
    } catch (err) {
      console.error('Error saving/unsaving article:', err);
      // More specific error messages for duplicate or not found might be good
      const errorMessage = err.response?.data?.message || 'Failed to toggle saved status.';
      setError(errorMessage);
      alert(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Render nothing if not logged in or loading and no initial status
  if (!user && !loading) {
    return null; // Or a disabled button, or a "Login to Save" message
  }

  return (
    <button
      onClick={handleSaveToggle}
      className={`p-2 rounded-full transition-colors duration-200 ${
        isSaved ? 'bg-[#EF4444] text-white hover:bg-[#B91C1C]' : 'bg-[#E5E7EB] text-[#4B5563] hover:bg-[#D1D5DB]'
      } disabled:opacity-50 disabled:cursor-not-allowed`}
      disabled={loading || !user}
      title={isSaved ? 'Unsave Article' : 'Save Article'}
    >
      {loading ? (
        <svg className="animate-spin h-5 w-5 text-current" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      ) : isSaved ? (
        <BookmarkSolid className="h-5 w-5" />
      ) : (
        <BookmarkOutline className="h-5 w-5" />
      )}
    </button>
  );
};

export default SaveArticleButton;