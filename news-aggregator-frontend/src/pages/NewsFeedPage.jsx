import React, { useState, useEffect, useCallback } from 'react';
import newsApi from '../api/newsApi';
import NewsArticleCard from '../components/NewsArticleCard';
import FilterControls from '../components/FilterControls'; // New import

const NewsFeedPage = () => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(10); // Users can change this later
  const [totalElements, setTotalElements] = useState(0);

  // Filtering state
  const [keyword, setKeyword] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState('');
  const [selectedSourceId, setSelectedSourceId] = useState('');
  const [startDate, setStartDate] = useState(''); // YYYY-MM-DD format for input
  const [endDate, setEndDate] = useState('');     // YYYY-MM-DD format for input

  // Sorting state
  const [sortBy, setSortBy] = useState('publishedAt'); // Default sort
  const [sortDir, setSortDir] = useState('desc');     // Default direction

  // Fetch articles from the backend
  const fetchArticles = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: currentPage,
        size: pageSize,
        sortBy: sortBy,
        sortDir: sortDir,
      };

      // Add filter parameters only if they are set
      if (keyword) params.keyword = keyword;
      if (selectedCategoryId) params.categoryId = selectedCategoryId;
      if (selectedSourceId) params.sourceId = selectedSourceId;
      if (startDate) params.startDate = startDate + 'T00:00:00'; // Append time for backend parsing if needed
      if (endDate) params.endDate = endDate + 'T23:59:59'; // Append time for backend parsing if needed

      const response = await newsApi.get('/articles', { params });

      setArticles(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setCurrentPage(response.data.pageNo); // Ensure consistent page number
    } catch (err) {
      console.error('Error fetching news articles:', err);
      // More specific error message if backend provides it
      setError('Failed to fetch news articles. Please try again later.');
    } finally {
      setLoading(false);
    }
  }, [currentPage, pageSize, sortBy, sortDir, keyword, selectedCategoryId, selectedSourceId, startDate, endDate]);

  // Initial fetch and fetch on filter/sort changes
  useEffect(() => {
    fetchArticles();
  }, [fetchArticles]); // Dependency array includes fetchArticles, which changes if its dependencies change

  // Handle filter application
  const handleApplyFilters = () => {
    setCurrentPage(0); // Reset to first page when applying new filters
    fetchArticles(); // Trigger fetch with new filters
  };

  // Handle sorting changes
  const handleSortChange = (newSortBy) => {
    setCurrentPage(0); // Reset page on sort change
    setSortBy(newSortBy);
  };

  const handleSortDirChange = (newSortDir) => {
    setCurrentPage(0); // Reset page on sort direction change
    setSortDir(newSortDir);
  };

  // Pagination handlers
  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#4B5563]">
        Loading news articles...
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto p-6 text-center text-lg text-[#EF4444]">
        Error: {error}
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 font-['Inter', sans-serif] text-[#1F2937]">
      <h1 className="text-3xl font-bold mb-6">Latest News</h1>

      {/* Filter and Sort Controls */}
      <FilterControls
        keyword={keyword}
        setKeyword={setKeyword}
        selectedCategoryId={selectedCategoryId}
        setSelectedCategoryId={setSelectedCategoryId}
        selectedSourceId={selectedSourceId}
        setSelectedSourceId={setSelectedSourceId}
        startDate={startDate}
        setStartDate={setStartDate}
        endDate={endDate}
        setEndDate={setEndDate}
        sortBy={sortBy}
        setSortBy={handleSortChange}
        sortDir={sortDir}
        setSortDir={handleSortDirChange}
        onApplyFilters={handleApplyFilters}
      />

      {articles.length === 0 ? (
        <div className="text-center text-lg text-[#4B5563] mt-8">
          No news articles found matching your criteria.
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-8">
            {articles.map((article) => (
              <NewsArticleCard key={article.id} article={article} />
            ))}
          </div>

          {/* Pagination Controls */}
          <div className="flex justify-center items-center space-x-4 mt-8">
            <button
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
              className="px-4 py-2 bg-[#4F46E5] text-white rounded-md hover:bg-[#6B7280] disabled:opacity-50 disabled:cursor-not-allowed transition duration-300 font-['Inter', sans-serif]"
            >
              Previous
            </button>

            {/* Page number buttons */}
            <div className="flex space-x-2">
              {[...Array(totalPages).keys()].map((page) => (
                <button
                  key={page}
                  onClick={() => handlePageChange(page)}
                  className={`px-3 py-1 rounded-md text-sm font-medium transition duration-300 font-['Inter', sans-serif] ${
                    currentPage === page
                      ? 'bg-[#4F46E5] text-white'
                      : 'bg-[#E5E7EB] text-[#4B5563] hover:bg-[#D1D5DB]'
                  }`}
                >
                  {page + 1}
                </button>
              ))}
            </div>

            <button
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages - 1}
              className="px-4 py-2 bg-[#4F46E5] text-white rounded-md hover:bg-[#6B7280] disabled:opacity-50 disabled:cursor-not-allowed transition duration-300 font-['Inter', sans-serif]"
            >
              Next
            </button>
          </div>
          <p className="text-center text-sm text-[#4B5563] mt-2">
            Showing {articles.length} of {totalElements} articles
          </p>
        </>
      )}
    </div>
  );
};

export default NewsFeedPage;