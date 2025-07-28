import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import Navbar from '../components/navbar';
import { NavLink } from 'react-router';
import CreatPost from '@/components/createPost';
import { type Post as PostType, type User, type UserProfile, type Comment } from '@/types/types';
import { isAuthenticated } from '@/services/auth';
import { getUser, getToken } from '@/services/storage';
import Post from '@/components/post';
import { dynamicFeed } from '@/services/feed';
import type { FeedResponse } from '@/types/feed';
import { createPost } from '@/services/post';

const Home: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [posts, setPosts] = useState<PostType[] | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate('/login');
    } else {
      const userData = getUser();
      console.log(userData)
      if (userData) {
        setUser(JSON.parse(userData));
      }
    }
  }, [navigate]);

  useEffect(() => {
    const fetchPosts = async () => {
      if (!isAuthenticated()) {
        return;
      }

      try {
        const response: FeedResponse = await dynamicFeed();

        if (response.result) {
          setPosts(response.result.data);
        }else {
          setPosts([]);
        }
      } catch (err) {
        console.error('Error fetching posts:', err);
        setPosts([]);
      }
    };

    fetchPosts();
  }, [user]);


  const handleComment = async (postId: number, comment: string): Promise<void> => {
    console.log(`Comment added to post ${postId}: ${comment}`);
    await new Promise((resolve) => setTimeout(resolve, 1000));
  };

  const handleUpload = async (images: File[] | null, caption: string) => {
    if (!images || images.length === 0) {
        console.error('No images provided for upload');
        return;
    }

    try {
        const response = await createPost(images, caption);
        console.log('Upload successful:', response);
        
        images.forEach((img) => {
            console.log('Uploaded image:', img.name, 'Caption:', caption);
        });
    } catch (error) {
        console.error('Upload failed:', error);
        throw error;
    }

  };

  return (
    <div className="flex min-h-screen">
      {/* Navbar: 1 part */}
      <div className="w-1/5 min-w-[200px]">
        <Navbar onCreateClick={() => setIsDialogOpen(true)} />
      </div>

      {/* Main Content: 3 parts */}
      <div className="flex flex-1">
        {/* Posts: 2 parts */}
        <div className="w-2/3 max-w-[600px] mx-auto p-4">
          {posts?.length ? (
            posts.map((post) => (
              <Post 
                key={post.postId} 
                post={post} 
                user={user}
                onComment={handleComment} 
              />
            ))
          ) : (
            <div className="text-center text-gray-500 mt-10">
              <p>Chưa có bài viết nào từ những người bạn theo dõi.</p>
            </div>
          )}
        </div>

        {/* Suggestions Sidebar: 1 part */}
        <div className="w-1/3 max-w-[320px] p-4 border-l border-gray-200">
          <div className="space-y-4 mb-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <img
                  src={user?.avatarUrl || '/assets/unknown.png'}
                  alt="User"
                  className="w-10 h-10 rounded-full"
                />
                <div>
                  <p className="font-semibold">{user?.fullName}</p>
                </div>
              </div>
              <NavLink 
                className="text-blue-500"
                to="/profile"
              >
                Chuyển
              </NavLink>
            </div>
          </div>
          <div className="flex justify-between items-center mb-4">
            <h2 className="font-semibold">Gợi ý cho bạn</h2>
            <NavLink to="/see-all" className="text-blue-500 text-sm">
              Xem tất cả
            </NavLink>
          </div>
          {/* Add more suggested users as needed */}
        </div>
      </div>

      {/* Create Post Dialog */}
      <CreatPost
        isOpen={isDialogOpen}
        onClose={() => setIsDialogOpen(false)}
        onSubmit={handleUpload}
      />
    </div>
  );
};

export default Home;