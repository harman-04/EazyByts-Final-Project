import React, { useState, useEffect } from 'react';
import newsApi from '../api/newsApi'; // Re-use newsApi for saved articles as well
import { useAuth } from '../auth/AuthContext';
import NewsArticleCard from '../components/NewsArticleCard'; // Re-use the card component

const SavedArticlesPage = () => {
  const { user } = useAuth();
  const [savedArticles, setSavedArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // TEMPORARY: As before, replace this with actual user.id from AuthContext
  const currentUserId = user ? 1 : null; // IMPORTANT: Update this with the real user ID

  const fetchSavedArticles = async () => {
    if (!user || !currentUserId) {
      setLoading(false);
      setError('Please log in to view your saved articles.');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const response = await newsApi.get(`/users/${currentUserId}/saved-articles`);
      setSavedArticles(response.data);
    } catch (err) {
      console.error('Error fetching saved articles:', err);
      setError(err.response?.data?.message || 'Failed to load saved articles.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSavedArticles();
  }, [user, currentUserId]); // Re-fetch if user or userId changes

  // Callback to update state when an article is unsaved from the card
  const handleArticleToggle = (isArticleSaved) => {
    // If an article is unsaved, we can re-fetch or optimistically remove it
    if (!isArticleSaved) {
      fetchSavedArticles(); // Simple approach: re-fetch all saved articles
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#4B5563] font-['Inter', sans-serif]">
        Loading saved articles...
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#EF4444] font-['Inter', sans-serif]">
        Error: {error}
      </div>
    );
  }

  if (!user) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#4B5563] font-['Inter', sans-serif]">
        You must be logged in to view your saved articles. Please <Link to="/login" className="text-[#4F46E5] hover:underline">login</Link>.
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 font-['Inter', sans-serif]">
      <h1 className="text-3xl font-bold text-[#1F2937] mb-8 text-center">Your Saved Articles</h1>
      {savedArticles.length === 0 ? (
        <p className="text-center text-lg text-[#4B5563] mt-10">
          You haven't saved any articles yet. Go to the <Link to="/" className="text-[#4F46E5] hover:underline">home page</Link> to find articles to save!
        </p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {savedArticles.map((article) => (
            // Re-using NewsArticleCard, but passing onSaveToggle to update state
            <NewsArticleCard key={article.id} article={article} onSaveToggle={handleArticleToggle} />
          ))}
        </div>
      )}
    </div>
  );
};

export default SavedArticlesPage;