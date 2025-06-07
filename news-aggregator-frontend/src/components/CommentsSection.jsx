import React, { useState, useEffect } from 'react';
import newsApi from '../api/newsApi'; // Re-use newsApi for comments as well
import { useAuth } from '../auth/AuthContext'; // To get current user and check authentication status
import { Link } from 'react-router-dom'; // <--- ADD THIS IMPORT STATEMENT

const CommentsSection = ({ articleId }) => {
  const [comments, setComments] = useState([]);
  const [loadingComments, setLoadingComments] = useState(true);
  const [commentsError, setCommentsError] = useState(null);
  const [newCommentContent, setNewCommentContent] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [submitError, setSubmitError] = useState(null);

  const { user } = useAuth(); // Get current user from AuthContext

  // IMPORTANT: For now, we need a placeholder userId.
  // In a production app with JWT, your backend would extract userId from the token.
  // You might need to update your AuthContext to store the user's ID.
  // For demonstration, let's use a dummy ID or retrieve it from a more complete user object.
  // Assuming 'user' in AuthContext has an 'id' field if you fetch full user details.
  // For now, if user object doesn't have ID, you will need to manually set it for testing.
  const currentUserId = user ? 1 : null; // TEMPORARY: Replace with actual user.id from AuthContext.
                                         // This means you need a user with ID 1 in your DB for testing comments.

  // Fetch comments for the article
  const fetchComments = async () => {
    setLoadingComments(true);
    setCommentsError(null);
    try {
      const response = await newsApi.get(`/articles/${articleId}/comments`);
      setComments(response.data);
    } catch (err) {
      console.error('Error fetching comments:', err);
      setCommentsError('Failed to load comments.');
    } finally {
      setLoadingComments(false);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [articleId]); // Re-fetch comments if articleId changes

  const handleAddComment = async (e) => {
    e.preventDefault();
    setSubmitError(null);
    if (!user) {
      setSubmitError('You must be logged in to add a comment.');
      return;
    }
    if (!newCommentContent.trim()) {
      setSubmitError('Comment cannot be empty.');
      return;
    }
    if (!currentUserId) {
        setSubmitError('User ID not available. Cannot post comment. Please log in properly.');
        return;
    }

    setSubmittingComment(true);
    try {
      // Backend expects: POST /api/articles/{articleId}/comments?userId={userId}
      // And a JSON body: { "content": "..." }
      const response = await newsApi.post(
        `/articles/${articleId}/comments?userId=${currentUserId}`, // Pass userId as query param
        { content: newCommentContent }
      );
      setComments([response.data, ...comments]); // Add new comment to the top of the list
      setNewCommentContent(''); // Clear the input field
    } catch (err) {
      console.error('Error adding comment:', err);
      setSubmitError(err.response?.data?.message || 'Failed to add comment. Please try again.');
    } finally {
      setSubmittingComment(false);
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!user || !currentUserId) {
      alert('You must be logged in to delete comments.');
      return;
    }
    if (!window.confirm('Are you sure you want to delete this comment?')) {
      return;
    }

    try {
      // Backend expects: DELETE /api/articles/{articleId}/comments/{commentId}?userId={userId}
      await newsApi.delete(`/articles/${articleId}/comments/${commentId}?userId=${currentUserId}`);
      setComments(comments.filter((comment) => comment.id !== commentId)); // Remove deleted comment from list
    } catch (err) {
      console.error('Error deleting comment:', err);
      alert(err.response?.data?.message || 'Failed to delete comment. You might not have permission.');
    }
  };

  const formatCommentDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <div className="mt-10 pt-8 border-t border-[#E5E7EB]">
      <h2 className="text-2xl font-bold text-[#1F2937] mb-6">Comments</h2>

      {/* Add New Comment Form */}
      {user ? (
        <form onSubmit={handleAddComment} className="mb-8 p-4 bg-[#F9FAFB] rounded-lg shadow-sm">
          <textarea
            className="w-full px-4 py-2 border border-[#D1D5DB] rounded-md focus:ring-[#4F46E5] focus:border-[#4F46E5] outline-none resize-y min-h-[80px] text-[#1F2937]"
            placeholder="Add a comment..."
            value={newCommentContent}
            onChange={(e) => setNewCommentContent(e.target.value)}
            rows="3"
            required
          ></textarea>
          {submitError && <p className="text-[#EF4444] text-sm mt-2 mb-2">{submitError}</p>}
          <button
            type="submit"
            className="bg-[#4F46E5] text-white px-5 py-2 rounded-md hover:bg-[#6B7280] transition duration-300 font-semibold mt-3"
            disabled={submittingComment}
          >
            {submittingComment ? 'Posting...' : 'Post Comment'}
          </button>
        </form>
      ) : (
        <p className="text-[#4B5563] mb-8 p-4 bg-[#F9FAFB] rounded-lg shadow-sm">
          <Link to="/login" className="text-[#4F46E5] hover:underline font-semibold">Login</Link> to add a comment.
        </p>
      )}

      {/* Display Comments */}
      {loadingComments ? (
        <p className="text-center text-[#4B5563]">Loading comments...</p>
      ) : commentsError ? (
        <p className="text-center text-[#EF4444]">{commentsError}</p>
      ) : comments.length === 0 ? (
        <p className="text-center text-[#4B5563]">No comments yet. Be the first to comment!</p>
      ) : (
        <div className="space-y-6">
          {comments.map((comment) => (
            <div key={comment.id} className="bg-white p-5 rounded-lg shadow-md border border-[#E5E7EB]">
              <div className="flex justify-between items-start mb-2">
                <div>
                  <p className="font-semibold text-[#1F2937] text-lg">{comment.username}</p>
                  <p className="text-sm text-[#4B5563]">{formatCommentDate(comment.createdAt)}</p>
                </div>
                {user && user.username === comment.username && ( // Only show delete if logged-in user owns comment
                  <button
                    onClick={() => handleDeleteComment(comment.id)}
                    className="text-[#EF4444] hover:text-[#B91C1C] transition duration-300 text-sm font-medium"
                    title="Delete Comment"
                  >
                    Delete
                  </button>
                )}
              </div>
              <p className="text-[#1F2937] leading-relaxed">{comment.content}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CommentsSection;