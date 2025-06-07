import React, { useState, useEffect } from 'react';
import newsApi from '../api/newsApi'; // To fetch categories and sources

const FilterControls = ({
  keyword, setKeyword,
  selectedCategoryId, setSelectedCategoryId,
  selectedSourceId, setSelectedSourceId,
  startDate, setStartDate,
  endDate, setEndDate,
  sortBy, setSortBy,
  sortDir, setSortDir,
  onApplyFilters, // Callback to trigger search in parent
}) => {
  const [categories, setCategories] = useState([]);
  const [sources, setSources] = useState([]);
  const [fetchingOptions, setFetchingOptions] = useState(true);

  // Fetch categories and sources when component mounts
  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [categoriesRes, sourcesRes] = await Promise.all([
          newsApi.get('/categories'),
          newsApi.get('/sources')
        ]);
        setCategories(categoriesRes.data);
        setSources(sourcesRes.data);
      } catch (err) {
        console.error('Error fetching categories or sources:', err);
        // Handle error appropriately, e.g., display a message
      } finally {
        setFetchingOptions(false);
      }
    };
    fetchOptions();
  }, []);

  return (
    <div className="bg-white p-6 rounded-lg shadow-md mb-8 font-['Inter', sans-serif]">
      <h2 className="text-xl font-bold text-[#1F2937] mb-4">Filter News</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Keyword Search */}
        <div>
          <label htmlFor="keyword" className="block text-sm font-medium text-[#4B5563] mb-1">Keyword</label>
          <input
            type="text"
            id="keyword"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Search title or description..."
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          />
        </div>

        {/* Category Filter */}
        <div>
          <label htmlFor="category" className="block text-sm font-medium text-[#4B5563] mb-1">Category</label>
          <select
            id="category"
            value={selectedCategoryId}
            onChange={(e) => setSelectedCategoryId(e.target.value)}
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          >
            <option value="">All Categories</option>
            {fetchingOptions ? (
              <option disabled>Loading categories...</option>
            ) : (
              categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))
            )}
          </select>
        </div>

        {/* Source Filter */}
        <div>
          <label htmlFor="source" className="block text-sm font-medium text-[#4B5563] mb-1">Source</label>
          <select
            id="source"
            value={selectedSourceId}
            onChange={(e) => setSelectedSourceId(e.target.value)}
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          >
            <option value="">All Sources</option>
            {fetchingOptions ? (
              <option disabled>Loading sources...</option>
            ) : (
              sources.map((src) => (
                <option key={src.id} value={src.id}>{src.name}</option>
              ))
            )}
          </select>
        </div>

        {/* Sort By */}
        <div>
          <label htmlFor="sortBy" className="block text-sm font-medium text-[#4B5563] mb-1">Sort By</label>
          <select
            id="sortBy"
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          >
            <option value="publishedAt">Published Date</option>
            <option value="title">Title</option>
            {/* Add other sortable fields if applicable in your backend */}
          </select>
        </div>

        {/* Sort Direction */}
        <div>
          <label htmlFor="sortDir" className="block text-sm font-medium text-[#4B5563] mb-1">Sort Direction</label>
          <select
            id="sortDir"
            value={sortDir}
            onChange={(e) => setSortDir(e.target.value)}
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          >
            <option value="desc">Descending</option>
            <option value="asc">Ascending</option>
          </select>
        </div>

        {/* Start Date */}
        <div>
          <label htmlFor="startDate" className="block text-sm font-medium text-[#4B5563] mb-1">From Date</label>
          <input
            type="date"
            id="startDate"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          />
        </div>

        {/* End Date */}
        <div>
          <label htmlFor="endDate" className="block text-sm font-medium text-[#4B5563] mb-1">To Date</label>
          <input
            type="date"
            id="endDate"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="w-full px-3 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none"
          />
        </div>

        {/* Apply Filters Button */}
        <div className="md:col-span-2 lg:col-span-1 flex items-end"> {/* Use items-end to align button */}
          <button
            onClick={onApplyFilters}
            className="w-full px-4 py-2 bg-[#4F46E5] text-white rounded-md hover:bg-[#6B7280] transition duration-300 font-['Inter', sans-serif]"
          >
            Apply Filters
          </button>
        </div>
      </div>
    </div>
  );
};

export default FilterControls;