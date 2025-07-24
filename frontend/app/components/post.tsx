import React, { useState } from 'react';
import { Link } from 'react-router';
import { Heart, MessageCircle, Send, Bookmark, MoreHorizontal, ChevronLeft, ChevronRight } from 'lucide-react';
import type { Post as PostType, User } from '@/types/types';
import { getUser } from '@/services/storage';
import { like, unlike } from '@/services/post';

interface PostProps {
  post: PostType;
  user: User | null;
  onComment?: (postId: number, comment: string) => Promise<void>;
}

const Post: React.FC<PostProps> = ({ user, post, onComment }) => {
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(post.likedUserIds.length);
  const [commentText, setCommentText] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showAllComments, setShowAllComments] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0); // Track current image

  const timeSince = (timestamp: number | string) => {
    const date = typeof timestamp === 'number' ? new Date(timestamp) : new Date(timestamp);
    const seconds = Math.floor((new Date().getTime() - date.getTime()) / 1000);
    
    let interval = seconds / 31536000;
    if (interval > 1) return Math.floor(interval) + ' years ago';
    interval = seconds / 2592000;
    if (interval > 1) return Math.floor(interval) + ' months ago';
    interval = seconds / 86400;
    if (interval > 1) return Math.floor(interval) + ' days ago';
    interval = seconds / 3600;
    if (interval > 1) return Math.floor(interval) + ' hours ago';
    interval = seconds / 60;
    if (interval > 1) return Math.floor(interval) + ' minutes ago';
    return Math.floor(seconds) + ' seconds ago';
  };

  const handleLike = async () => {
    if(!user) return;

    try {
      if (liked) {
        await unlike(post.postId);
        setLiked(false);
        setLikeCount((prev) => Math.max(0, prev - 1));
      } else {
        await like(post.postId)
        setLiked(true);
        setLikeCount((prev) => prev + 1);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const handleComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentText.trim() || !user || isSubmitting) return;

    setIsSubmitting(true);
    try {
      
      setCommentText('');
    } catch (error) {
      console.log(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleNextImage = () => {
    setCurrentImageIndex((prev) => (prev + 1) % post.fileUrls.length);
  };

  const handlePrevImage = () => {
    setCurrentImageIndex((prev) => (prev - 1 + post.fileUrls.length) % post.fileUrls.length);
  };


  return (
    <div className="instagram-card">
      {/* Post Header */}
      <div className="flex items-center p-3 mb-4">
        <Link to={`/profile/${post.fullName}`} className="flex items-center">
          <img
            src={post.avatarUrl || '/assets/unknow.png'}
            alt={post.fullName}
            className="w-8 h-8 rounded-full object-cover mr-3"
          />
          <span className="font-semibold">{post.fullName}</span>
        </Link>
        <button className="ml-auto text-gray-500">
          <MoreHorizontal size={20} />
        </button>
      </div>

      {/* Post Image Carousel */}
      <div className="relative">
        <img
          src={post.fileUrls[currentImageIndex]}
          alt={`Post image ${currentImageIndex + 1}`}
          className="w-full h-[400px] object-cover"
        />
        {post.fileUrls.length > 1 && (
          <>
            <button
              onClick={handlePrevImage}
              className="absolute left-2 top-1/2 transform -translate-y-1/2 text-white p-2 rounded-full"
            >
              <ChevronLeft size={24} />
            </button>
            <button
              onClick={handleNextImage}
              className="absolute right-2 top-1/2 transform -translate-y-1/2 text-white p-2 rounded-full"
            >
              <ChevronRight size={24} />
            </button>
            {/* Image Indicators */}
            <div className="absolute bottom-2 left-1/2 transform -translate-x-1/2 flex space-x-1">
              {post.fileUrls.map((_, index) => (
                <div
                  key={index}
                  className={`w-2 h-2 rounded-full ${
                    index === currentImageIndex ? 'bg-white' : 'bg-gray-400'
                  }`}
                />
              ))}
            </div>
          </>
        )}
      </div>

      {/* Post Actions */}
      <div className="p-3">
        <div className="flex items-center mb-3">
          <button
            className={`mr-4 cursor-pointer ${liked ? 'text-instagram-red' : ''}`}
            onClick={handleLike}
          >
            <Heart size={24} className={liked ? 'fill-instagram-red text-instagram-red animate-like' : ''} />
          </button>
          <button className="mr-4 cursor-pointer">
            <MessageCircle size={24} />
          </button>
          <button className="mr-4 cursor-pointer">
            <Send size={24} />
          </button>
          <button className="ml-auto cursor-pointer">
            <Bookmark size={24} />
          </button>
        </div>

        {/* Likes count */}
        {likeCount > 0 && (
          <div className="font-semibold mb-2">
            {likeCount} {likeCount === 1 ? 'like' : 'likes'}
          </div>
        )}

        {/* Caption */}
        <div className="mb-2">
          <Link to={`/profile/${post.fullName}`} className="font-semibold mr-2">
            {post.fullName}
          </Link>
          <span>{post.caption}</span>
        </div>

        {/* Comments */}
        {/* {post.comments.length > 0 && (
          <div className="mt-1 mb-3">
            {post.comments.length > 2 && !showAllComments && (
              <button
                className="text-instagram-darkGray text-sm mb-2 cursor-pointer"
                // onClick={() => setShowAllComments(true)}
              >
                View all {post.comments.length} comments
              </button>
            )}
          </div>
        )} */}

        {/* Post Date */}
        <div className="text-xs text-instagram-darkGray mt-1">{timeSince(post.created_at)}</div>
      </div>

      {/* Comment Form */}
      <form onSubmit={handleComment} className="border-t border-instagram-border p-3 flex">
        <input
          type="text"
          placeholder="Add a comment..."
          className="flex-grow bg-transparent focus:outline-none"
          value={commentText}
          onChange={(e) => setCommentText(e.target.value)}
        />
        <button
          type="submit"
          className={`font-semibold ${
            commentText.trim() ? 'text-instagram-blue cursor-pointer' : 'text-instagram-blue opacity-50'
          }`}
          disabled={!commentText.trim() || isSubmitting}
        >
          Gửi
        </button>
      </form>
    </div>
  );
};

export default Post;