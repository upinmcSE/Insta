import Navbar from "@/components/navbar";
import type { UserProfile } from "@/types/types";
import { Settings, Grid } from 'lucide-react';
import { useState } from "react";
import { useNavigate } from "react-router";

const Profile: React.FC = () => {
    const [user, setUser] = useState<UserProfile | null>({
        id: "user123",
        email: "duongtrungthanh@example.com",
        avtUrl: "/assets/unknown.png",
        fullName: "Upin",
        bio: "Just a coder with a love for cats and tech. Hopefully...",
        followers: [],
        following: ["1231323"],
    });
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [loading, setLoading] = useState(true);
    const [followLoading, setFollowLoading] = useState(false);
    const isCurrentUserProfile = true;

    const navigate = useNavigate();

    const handleFollow = async () => {
        setFollowLoading(true);
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 1000));
        setFollowLoading(false);
    };

    // Mock posts data
    const posts = [
        { id: 1, image: "https://i.pinimg.com/736x/72/80/55/728055c1ab422e4af017ff52864c8143.jpg" },
        { id: 2, image: "https://i.pinimg.com/736x/0c/94/f1/0c94f15fc457d9bdbc76240c13dd9edc.jpg" },
        { id: 3, image: "https://i.pinimg.com/736x/c3/ca/7e/c3ca7e33f5bb79b5d7837c0a8a19ea52.jpg" },
        { id: 4, image: "https://i.pinimg.com/736x/ab/d2/8b/abd28b579d36c1e5c171efcd80f6832c.jpg" },
    ];

    return (
        <div className="flex min-h-screen">
            {/* Navbar: 1 part */}
            <div className="w-1/5">
                <Navbar onCreateClick={() => setIsDialogOpen(true)} />
            </div>

            {/* Profile Section */}
            <div className="max-w-6xl mx-auto">
                {/* Profile Header */}
                <div className="flex flex-col md:flex-row items-center md:items-start mb-10 p-4">
                    <div className="w-24 h-24 md:w-32 md:h-32 rounded-full overflow-hidden flex-shrink-0 mb-4 md:mb-0 md:mr-10">
                        <img 
                            src={user?.avtUrl || "/assets/unknow.png"} 
                            alt={user?.fullName} 
                            className="w-full h-full object-cover"
                        />
                    </div>
                    
                    <div className="flex flex-col items-center md:items-start">
                        <div className="flex items-center mb-4">
                            <h1 className="text-xl md:text-2xl font-light mr-4">{user?.fullName}</h1>
                            {isCurrentUserProfile ? (
                                <button 
                                    className="bg-instagram-lightGray text-black font-semibold px-4 py-1.5 rounded text-sm flex items-center"
                                    onClick={() => navigate('/edit-profile')}
                                >
                                    <Settings size={16} className="mr-2" /> Edit Profile
                                </button>
                            ) : (
                                <button 
                                    className={`px-6 py-1.5 rounded text-sm font-semibold ${
                                        true
                                            ? 'bg-instagram-lightGray text-black' 
                                            : 'bg-instagram-blue text-white'
                                    }`}
                                    onClick={handleFollow}
                                    disabled={followLoading}
                                >
                                    {false ? 'Following' : 'Follow'}
                                </button>
                            )}
                        </div>
                        
                        <div className="flex space-x-6 mb-4 text-sm">
                            <div><span className="font-semibold">{posts.length}</span> posts</div>
                            <div><span className="font-semibold">{user?.followers.length || 0}</span> followers</div>
                            <div><span className="font-semibold">{user?.following.length || 0}</span> following</div>
                        </div>
                        
                        <div className="text-sm">
                            <p className="whitespace-pre-line">{user?.bio}</p>
                        </div>
                    </div>
                </div>
                
                {/* Profile Tabs */}
                <div className="border-t border-instagram-border">
                    <div className="flex justify-center">
                        <button className="flex items-center py-3 px-4 border-t border-black text-sm font-semibold">
                            <Grid size={12} className="mr-2" /> POSTS
                        </button>
                    </div>
                </div>
                
                {/* Posts Grid */}
                <div className="grid grid-cols-3 gap-1 md:gap-6">
                    {posts.map(post => (
                        <div key={post.id} className="aspect-square relative cursor-pointer">
                            <img 
                                src={post.image} 
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
        </div>
    );
};

export default Profile;