import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import Navbar from "@/components/navbar";
import type { UserProfile, Post } from "@/types/types";
import { Settings, Grid } from "lucide-react";
import { getUser } from "@/services/storage";
import { getMyProfile, getUserProfile, follow } from "@/services/user";
import { getPostsByUser } from "@/services/post";
import type { FeedResponse } from "@/types/feed";

const Profile: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [user, setUser] = useState<UserProfile | null>(null);
  const [posts, setPosts] = useState<Post[]>([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [followLoading, setFollowLoading] = useState(false);
  const [isCurrentUserProfile, setIsCurrentUserProfile] = useState(false);
  const navigate = useNavigate();

  // Lấy thông tin người dùng hiện tại từ storage
  const currentUserRaw = getUser();
  const currentUser = currentUserRaw ? JSON.parse(currentUserRaw) : null;

  useEffect(() => {
    // Nếu không có currentUser, chuyển hướng đến login ngay lập tức
    if (!currentUser) {
      navigate("/login", { replace: true });
      return;
    }

    // Nếu không có id, sử dụng id của currentUser
    const profileId = id || currentUser.id;

    const fetchProfileData = async () => {
      try {
        setLoading(true);

        // Kiểm tra xem đây có phải là hồ sơ của người dùng hiện tại không
        const isCurrentUser = currentUser.id === profileId;
        setIsCurrentUserProfile(isCurrentUser);

        // Gọi API phù hợp
        const profileResponse = isCurrentUser
          ? await getMyProfile()
          : await getUserProfile(profileId);

        setUser({
          ...profileResponse.result,
          bio: profileResponse.result.bio || "",
          followers: profileResponse.result.followers || [],
          following: profileResponse.result.following || [],
        });

        // Gọi API để lấy bài đăng
        const postsResponse: FeedResponse = await getPostsByUser(profileId);
        setPosts(postsResponse.result.data || []);
      } catch (error) {
        console.error("Error fetching profile data:", error);
        setUser(null);
        setPosts([]);
      } finally {
        setLoading(false);
      }
    };

    fetchProfileData();
  }, [id, currentUser?.id, navigate]);

  const handleFollow = async () => {
    if (!id || !currentUser) {
      navigate("/login", { replace: true });
      return;
    }

    try {
      setFollowLoading(true);
      await follow(id);
      const updatedProfile = await getUserProfile(id);
      setUser({
        ...updatedProfile.result,
        bio: updatedProfile.result.bio || "",
        followers: updatedProfile.result.followers || [],
        following: updatedProfile.result.following || [],
      });
    } catch (error) {
      console.error("Error following user:", error);
    } finally {
      setFollowLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen justify-center items-center">
        <p>Loading...</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex min-h-screen justify-center items-center">
        <p>User not found</p>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen">
      <div className="w-1/5">
        <Navbar onCreateClick={() => setIsDialogOpen(true)} />
      </div>
      <div className="max-w-6xl mx-auto">
        <div className="flex flex-col md:flex-row items-center md:items-start mb-10 p-4">
          <div className="w-24 h-24 md:w-32 md:h-32 rounded-full overflow-hidden flex-shrink-0 mb-4 md:mb-0 md:mr-10">
            <img
              src={user.avtUrl || "/assets/unknown.png"}
              alt={user.fullName}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="flex flex-col items-center md:items-start">
            <div className="flex items-center mb-4">
              <h1 className="text-xl md:text-2xl font-light mr-4">{user.fullName}</h1>
              {isCurrentUserProfile ? (
                <button
                  className="bg-instagram-lightGray cursor-pointer text-black font-semibold px-4 py-1.5 rounded text-sm flex items-center"
                  onClick={() => navigate("/edit-profile")}
                >
                  <Settings size={16} className="mr-2" /> Edit Profile
                </button>
              ) : (
                <button
                  className={`px-6 py-1.5 rounded text-sm cursor-pointer font-semibold ${
                    currentUser && user.followers.includes(currentUser.id)
                      ? "bg-instagram-lightGray text-black"
                      : "bg-instagram-blue text-white"
                  }`}
                  onClick={handleFollow}
                  disabled={followLoading || !currentUser}
                >
                  {currentUser && user.followers.includes(currentUser.id) ? "Following" : "Follow"}
                </button>
              )}
            </div>
            <div className="flex space-x-6 mb-4 text-sm cursor-pointer">
              <div>
                <span className="font-semibold">{posts.length}</span> posts
              </div>
              <div>
                <span className="font-semibold">{user.followers.length}</span> followers
              </div>
              <div>
                <span className="font-semibold">{user.following.length}</span> following
              </div>
            </div>
            <div className="text-sm">
              <p className="whitespace-pre-line">{user.bio}</p>
            </div>
          </div>
        </div>
        <div className="border-t border-instagram-border">
          <div className="flex justify-center">
            <button className="flex items-center py-3 px-4 border-t border-black text-sm font-semibold">
              <Grid size={12} className="mr-2" /> POSTS
            </button>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-1 md:gap-6">
          {posts.map((post) => (
            <div key={post.postId} className="aspect-square relative cursor-pointer">
              <img
                src={post.fileUrls[0]}
                alt=""
                className="absolute inset-0 w-full h-full object-cover"
              />
            </div>
          ))}
        </div>
        
        {posts.length === 0 && (
          <div className="text-center py-10">
            <h2 className="text-xl font-semibold mb-2">No Posts Yet</h2>
            <p className="text-instagram-darkGray">
              {isCurrentUserProfile
                ? "When you share photos, they'll appear on your profile."
                : "This user hasn't posted any photos yet."}
            </p>
          </div>
        )}
      </div>
      {isDialogOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-4 rounded-lg">
            <h3>Dialog</h3>
            <button onClick={() => setIsDialogOpen(false)}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Profile;