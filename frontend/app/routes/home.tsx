import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import Navbar from '../components/navbar';
import { NavLink } from 'react-router';
import CreatPost from '@/components/createPost';
import type { Post as PostType, User, UserProfile, Comment } from '@/types/types';
import Post from '@/components/post';
import { isAuthenticated } from '@/services/auth';
import { getUser, getToken } from '@/services/storage';

// Mock User
const mockUser1: User = {
  id: 'user1',
  fullName: 'John Doe',
  avatarUrl: undefined,
};

const mockUser2: User = {
  id: 'user2',
  fullName: 'Jane Smith',
  avatarUrl: undefined,
};

// Mock Comment
const mockComments: Comment[] = [
  {
    id: 'comment1',
    post_id: 'post1',
    user_id: 'user2',
    content: 'Wow, amazing photo! 😍',
    reply_comment: [
      {
        id: 'reply1',
        comment_id: 'comment1',
        user_id: 'user1',
        content: 'Thanks for the love!',
        created_at: Date.now() - 1000 * 60 * 5,
        user: mockUser1,
      },
    ],
    created_at: Date.now() - 1000 * 60 * 10,
    user: {
      id: 'user2',
      fullName: 'Jane Smith',
      avatarUrl: 'https://via.placeholder.com/40',
    },
  },
  {
    id: 'comment2',
    post_id: 'post1',
    user_id: 'user3',
    content: 'Where was this taken?',
    reply_comment: [],
    created_at: Date.now() - 1000 * 60 * 8,
    user: {
      id: 'user3',
      fullName: 'Alex Johnson',
      avatarUrl: 'https://via.placeholder.com/40',
    },
  },
  {
    id: 'comment3',
    post_id: 'post1',
    user_id: 'user4',
    content: 'Love the colors!',
    reply_comment: [],
    created_at: Date.now() - 1000 * 60 * 6,
    user: {
      id: 'user4',
      fullName: 'Emma Brown',
      avatarUrl: 'https://via.placeholder.com/40',
    },
  },
];

// Mock PostWithDetails
const mockPosts: PostType[] = [
  {
    id: 'post1',
    user_id: 'user1',
    images: [
      'https://static.vecteezy.com/system/resources/thumbnails/045/132/934/small_2x/a-beautiful-picture-of-the-eiffel-tower-in-paris-the-capital-of-france-with-a-wonderful-background-in-wonderful-natural-colors-photo.jpg',
      'https://via.placeholder.com/800x600',
      'https://via.placeholder.com/500x500',
    ],
    caption: 'Chasing sunsets 🌅 #nature #photography',
    like_count: 150,
    liked_by_current_user: false,
    comments: mockComments,
    created_at: Date.now() - 1000 * 60 * 60 * 2,
    user: mockUser1,
  },
  {
    id: 'post2',
    user_id: 'user2',
    images: [
      'https://images.unsplash.com/photo-1603984973710-e915353b35fa?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YW1hemluZyUyMHBpY3R1cmV8ZW58MHx8MHx8fDA%3D',
      'https://via.placeholder.com/700x500',
    ],
    caption: 'Exploring the mountains 🏔️ #adventure',
    like_count: 230,
    liked_by_current_user: false,
    comments: [
      {
        id: 'comment4',
        post_id: 'post2',
        user_id: 'user1',
        content: 'Looks like an epic trip!',
        reply_comment: [],
        created_at: Date.now() - 1000 * 60 * 15,
        user: mockUser2,
      },
    ],
    created_at: Date.now() - 1000 * 60 * 60 * 5,
    user: {
      id: 'user2',
      fullName: 'Jane Smith',
      avatarUrl: undefined,
    },
  },
];

const Home: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const navigate = useNavigate();
  const hasPosts = mockPosts.length > 0;

  useEffect(() => {
    const token = getToken();
    if (!token || !isAuthenticated()) {
      navigate('/login');
    } else {
      const userData = getUser();
      console.log(userData)
      if (userData) {
        setUser(JSON.parse(userData));
      }
    }
  }, [navigate]);

  const handleComment = async (postId: string, comment: string): Promise<void> => {
    console.log(`Comment added to post ${postId}: ${comment}`);
    await new Promise((resolve) => setTimeout(resolve, 1000));
  };

  const handleUpload = (images: File[] | null, caption: string) => {
    if (images) {
      images.forEach((img) => {
        console.log('Uploading image:', img.name, 'Caption:', caption);
      });
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
          {hasPosts ? (
            mockPosts.map((post) => (
              <Post 
                key={post.id} 
                post={post} 
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
                  src={user?.avatarUrl || '/assets/unknow.png'}
                  alt="User"
                  className="w-10 h-10 rounded-full"
                />
                <div>
                  <p className="font-semibold">{user?.fullName}</p>
                </div>
              </div>
              <button className="text-blue-500">Chuyển</button>
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