import React from 'react';

interface PostProps {
  username: string;
  imageUrl: string;
  caption: string;
  likes: number;
  comments: number;
  timeAgo: string;
}

const Post: React.FC<PostProps> = ({ username, imageUrl, caption, likes, comments, timeAgo }) => {
  return (
    <div className="border-t border-gray-200 p-4">
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center space-x-2">
          <img src={imageUrl} alt={username} className="w-10 h-10 rounded-full" />
          <div>
            <p className="font-semibold">{username}</p>
            <p className="text-sm text-gray-500">{timeAgo}</p>
          </div>
        </div>
        <button className="text-gray-500">...</button>
      </div>
      <img src={imageUrl} alt={caption} className="w-full h-64 object-cover mb-2" />
      <div className="flex items-center justify-between">
        <div className="flex space-x-2">
          <button className="text-red-500">❤️</button>
          <button className="text-gray-500">💬</button>
          <button className="text-gray-500">📤</button>
        </div>
        <button className="text-gray-500">🔖</button>
      </div>
      <p className="text-sm font-semibold">{likes} lượt thích</p>
      <p className="text-sm">{caption}</p>
      <p className="text-sm text-gray-500">{comments} bình luận</p>
    </div>
  );
};

export default Post;