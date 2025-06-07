import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import newsApi from '../api/newsApi';
import CommentsSection from '../components/CommentsSection';
import SaveArticleButton from '../components/SaveArticleButton'; // New import

const NewsArticleDetailPage = () => {
  const { id } = useParams();
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchArticle = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await newsApi.get(`/articles/${id}`);
        setArticle(response.data);
      } catch (err) {
        console.error(`Error fetching article with ID ${id}:`, err);
        setError('Failed to load article. It might not exist or there was a network error.');
        setArticle(null);
      } finally {
        setLoading(false);
      }
    };

    fetchArticle();
  }, [id]);

  if (loading) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#4B5563] font-['Inter', sans-serif]">
        Loading article details...
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

  if (!article) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#4B5563] font-['Inter', sans-serif]">
        Article not found.
      </div>
    );
  }

  // Helper to format date
  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    try {
      const date = new Date(dateTimeString);
      return date.toLocaleString();
    } catch (e) {
      return dateTimeString;
    }
  };

  return (
    <div className="container mx-auto p-6 bg-white shadow-lg rounded-lg my-8 font-['Inter', sans-serif]">
      {article.imageUrl ? (
        <img
          src={article.imageUrl}
          alt={article.title}
          className="w-full h-80 object-cover rounded-lg mb-6 shadow-md"
          onError={(e) => { e.target.onerror = null; e.target.src = 'https://picsum.photos/800/450?random=' + article.id; }}
        />
      ) : (
        <img
          src={'https://picsum.photos/800/450?random=' + article.id}
          alt="No Image Available"
          className="w-full h-80 object-cover rounded-lg mb-6 shadow-md"
        />
      )}

      <div className="flex justify-between items-start mb-4"> {/* Flex for title and save button */}
        <h1 className="text-4xl font-bold text-[#1F2937] flex-grow pr-4">{article.title}</h1>
        {article.id && <SaveArticleButton articleId={article.id} />} {/* Save button */}
      </div>

      <div className="flex flex-wrap items-center text-sm text-[#4B5563] mb-6 space-x-4">
        {article.sourceName && (
          <span>
            <strong className="text-[#1F2937]">Source:</strong> {article.sourceName}
          </span>
        )}
        {article.categoryName && (
          <span>
            <strong className="text-[#1F2937]">Category:</strong> {article.categoryName}
          </span>
        )}
        {article.author && (
          <span>
            <strong className="text-[#1F2937]">Author:</strong> {article.author}
          </span>
        )}
        {article.publishedAt && (
          <span>
            <strong className="text-[#1F2937]">Published:</strong> {formatDateTime(article.publishedAt)}
          </span>
        )}
      </div>

      <p className="text-lg text-[#1F2937] leading-relaxed mb-6 whitespace-pre-wrap">
        {article.content || article.description || 'No content available for this article.'}
      </p>

      {article.articleUrl && (
        <div className="mb-8">
          <a
            href={article.articleUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-block bg-[#4F46E5] text-white py-3 px-6 rounded-md hover:bg-[#6B7280] transition duration-300 font-semibold text-lg"
          >
            Read Full Article at {article.sourceName || 'Source'}
          </a>
        </div>
      )}

      {/* Comments Section */}
      <CommentsSection articleId={id} />
    </div>
  );
};

export default NewsArticleDetailPage;