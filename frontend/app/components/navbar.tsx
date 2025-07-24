import React from 'react';
import { NavLink, useNavigate } from 'react-router';
import 'material-icons/iconfont/material-icons.css';
import { LogOut } from 'lucide-react'
import { logout } from '@/services/auth';

interface NavbarProps {
  onCreateClick: () => void;
}

const Navbar: React.FC<NavbarProps> = ({ onCreateClick }) => {

  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };


  return (
    <div className="fixed w-64 h-screen bg-white border-r border-gray-200 p-6 flex flex-col justify-between">
      <div>
        <div className="mb-8">
          <h1 className="text-4xl font-script text-gray-800">Insta</h1>
        </div>
        <nav className="space-y-6">
          <NavLink to="/" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">home</span>
            <span className="text-lg">Trang chủ</span>
          </NavLink>
          <NavLink to="/search" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">search</span>
            <span className="text-lg">Tim kiếm</span>
          </NavLink>
          <NavLink to="/" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">explore</span>
            <span className="text-lg">Khám phá</span>
          </NavLink>
          <NavLink to="/" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">movie</span>
            <span className="text-lg">Reels</span>
          </NavLink>
          <NavLink to="/messages" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">message</span>
            <span className="text-lg">Tin nhắn</span>
          </NavLink>
          <NavLink to="/notifications" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">notifications</span>
            <span className="text-lg">Thông báo</span>
          </NavLink>
          <button onClick={onCreateClick} className="flex items-center space-x-4 cursor-pointer text-gray-600 hover:text-black">
            <span className="material-icons">add_circle_outline</span>
            <span className="text-lg">Tạo</span>
          </button>
          <NavLink to="/profile" className="flex items-center space-x-4 text-gray-600 hover:text-black">
            <span className="material-icons">person</span>
            <span className="text-lg">Trang cá nhân</span>
          </NavLink>
        </nav>
      </div>
      <div>
        <button
          onClick={handleLogout}
          className="flex items-center p-3 rounded-md hover:bg-gray-100 transition-colors w-full"
        >
          <LogOut className="mr-3" size={24} />
          <span>Logout</span>
        </button>
      </div>
    </div>
  );
};

export default Navbar;