import React, { useState } from 'react';
import Navbar from '../components/navbar';
import Post from '../components/post';
import { NavLink } from 'react-router';
import CreatPost from '@/components/createPost';

const Home: React.FC = () => {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const hasPosts = false;

    const handleUpload = (images: File[] | null, caption: string) => {
    if (images) {
            images.forEach((img) => {
                console.log('Uploading image:', img.name, 'Caption:', caption);
                // Upload từng ảnh tại đây
            });
        }
    };

    return (
        <div className="flex z-0">
            <Navbar onCreateClick={() => setIsDialogOpen(true)} />
            <div className="flex-1 p-4">
                {/* <div className="flex items-center justify-between mb-4">
                <div className="flex items-center space-x-2">
                    <div className="flex items-center space-x-1 text-gray-600">
                    <span className="material-icons">sentiment_satisfied_alt</span>
                    <span>Tim bạn bè và tài khoản mà bạn có thể biết</span>
                    </div>
                    <NavLink to="/find-friends" className="text-blue-500">Tìm</NavLink>
                </div>
                </div> */}
                <div className="flex justify-center">
                    <div className="w-full max-w-2xl">
                        {hasPosts ? (
                        <>
                            <Post
                            username="Upin"
                            imageUrl="https://via.placeholder.com/300x400"
                            caption="Bài viết của tôi hay vcc"
                            likes={214}
                            comments={10}
                            timeAgo="2 ngày"
                            />
                            
                        </>
                        ) : (
                        <div className="text-center text-gray-500 mt-10">
                            <p>Chưa có bài viết nào từ những người bạn theo dõi.</p>
                        </div>
                        )}
                    </div>
                <div className="w-80 p-4 ml-4 border-l border-gray-200">
                    <div className="space-y-4 mb-4">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-2">
                                <img src="https://via.placeholder.com/40" alt="User" className="w-10 h-10 rounded-full" />
                                <div>
                                    <p className="font-semibold">Upin</p>
                                </div>
                            </div>
                            <button className="text-blue-500">Chuyển</button>
                        </div>
                    </div>

                    <div className="flex justify-between items-center mb-4">
                        <h2 className="font-semibold">Gợi ý cho bạn</h2>
                        <NavLink to="/see-all" className="text-blue-500 text-sm">Xem tất cả</NavLink>
                    </div>
                    {/* Add more suggested users as needed */}
                </div>
                </div>
            </div>
            <CreatPost 
                    isOpen={isDialogOpen}
                    onClose={() => setIsDialogOpen(false)}
                    onSubmit={handleUpload}
            />
        </div>
    );
};

export default Home;