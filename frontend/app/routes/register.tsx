import React, { useEffect } from 'react';
import { useState } from 'react';
import { NavLink } from 'react-router';
import { redirect } from "react-router";
import { isAuthenticated, register } from '@/services/auth';


const Register: React.FC = () => {
  const [email, setEmail] = useState('');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');

  useEffect(() => {
    if(isAuthenticated != null){
        redirect("/")
    }
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Email:', email, 'Full Name:', fullName, 'Password:', password);
    // Add your signup logic here
    try{
        const response = await register(email, password, fullName);
        console.log("Response body:", response);
    }catch(error){
        console.log(error)
    }

  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-white">
      <div className="w-full max-w-sm p-6 bg-white shadow-md rounded-lg">
        <div className="text-center mb-6">
          <h1 className="text-4xl font-script text-gray-800">Insta</h1>
          <p className="text-sm text-gray-600 mt-2">Đăng ký để xem ảnh và video từ bạn bè.</p>
        </div>
        <button className="w-full flex items-center justify-center gap-2 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 mb-4">
          <svg
            className="w-5 h-5"
            fill="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M22.675 0H1.325C.593 0 0 .593 0 1.325v21.351C0 23.407.593 24 1.325 24H12.82v-9.294H9.692v-3.622h3.128V8.413c0-3.1 1.893-4.788 4.659-4.788 1.325 0 2.463.099 2.795.143v3.24l-1.918.001c-1.504 0-1.795.715-1.795 1.763v2.313h3.587l-.467 3.622h-3.12V24h6.116c.73 0 1.323-.593 1.323-1.325V1.325C24 .593 23.407 0 22.675 0z"/>
          </svg>
          Đăng nhập bằng Facebook
        </button>
        <div className="text-center my-4 text-gray-500">HOẶC</div>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Email của bạn"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
          <input
            type="text"
            placeholder="Tên đầy đủ"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
          <input
            type="password"
            placeholder="Mật khẩu"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
          />
          <button
            type="submit"
            className="w-full bg-purple-500 text-white py-2 rounded-lg hover:bg-purple-600"
          >
            Đăng ký
          </button>
        </form>
        <div className="text-center mt-6 text-sm text-gray-600">
          Bạn có tài khoản? 
          <NavLink to="/login" className="text-blue-500">Đăng nhập</NavLink>
        </div>
      </div>
    </div>
  );
};

export default Register;