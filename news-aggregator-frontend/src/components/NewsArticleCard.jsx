import React from 'react';
import { Link } from 'react-router-dom';
import SaveArticleButton from './SaveArticleButton'; // New import

const NewsArticleCard = ({ article }) => {
  // Helper to format date
  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    try {
      const date = new Date(dateTimeString);
      return date.toLocaleString(); // Formats to local date and time
    } catch (e) {
      return dateTimeString; // Return original if parsing fails
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden flex flex-col h-full transform transition-transform duration-300 hover:scale-[1.02] border border-[#E5E7EB] font-['Inter', sans-serif]">
      {/* Clickable Image */}
      <Link to={`/articles/${article.id}`} className="block">
        {article.imageUrl ? (
          <img
            src={article.imageUrl}
            alt={article.title}
            className="w-full h-48 object-cover"
            onError={(e) => { e.target.onerror = null; e.target.src = 'https://picsum.photos/600/400?random=' + article.id; }}
          />
        ) : (
          <img
            src={'https://picsum.photos/600/400?text=No+Image&random=' + article.id}
            alt="No Image Available"
            className="w-full h-48 object-cover"
          />
        )}
      </Link>

      <div className="p-4 flex flex-col flex-grow">
        {/* Clickable Title */}
        <Link to={`/articles/${article.id}`} className="block">
          <h3 className="text-xl font-semibold text-[#1F2937] mb-2 leading-tight hover:text-[#4F46E5] transition-colors duration-300">
            {article.title}
          </h3>
        </Link>

        {article.description && (
          <p className="text-[#4B5563] text-sm mb-3 line-clamp-3">
            {article.description}
          </p>
        )}

        <div className="text-xs text-[#6B7280] mt-auto pt-2 border-t border-[#F3F4F6] flex justify-between items-center"> {/* Added flex for alignment */}
          <div>
            {article.sourceName && <span>Source: <span className="font-medium text-[#1F2937]">{article.sourceName}</span></span>}
            {article.categoryName && <span className="ml-2">Category: <span className="font-medium text-[#1F2937]">{article.categoryName}</span></span>}
            <p className="mt-1">Published: {formatDateTime(article.publishedAt)}</p>
          </div>
          {/* Add SaveArticleButton here */}
          {article.id && <SaveArticleButton articleId={article.id} />} {/* Pass article.id */}
        </div>
      </div>
    </div>
  );
};

export default NewsArticleCard;