import React, { useEffect } from 'react';
import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router';
import { isAuthenticated, login } from '@/services/auth';
import { setToken, setUser } from '@/services/storage';
import { OAuthConfig } from '@/config/configuration';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  let navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated()) {
      navigate("/")
    }
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
     try {
      const response = await login(email, password);
      setToken(response.result.accessToken)
      setUser(response.result.userInfo)
      navigate("/");
    } catch (error) {
      console.log(error)
    }

  };
  
  const handleClick = async () => {
    const callbackUrl = OAuthConfig.redirectUri;
    const authUrl = OAuthConfig.authUri;
    const googleClientId = OAuthConfig.clientId

    const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(
      callbackUrl
    )}&response_type=code&client_id=${googleClientId}&scope=openid%20email%20profile`;
    
    console.log(targetUrl);

    window.location.href = targetUrl;
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-white">
      <div className="w-full max-w-sm p-6 bg-white shadow-md rounded-lg">
        <div className="text-center mb-6">
          <h1 className="text-4xl font-script text-gray-800">Insta</h1>
        </div>
        <form 
            className="space-y-4"
            onSubmit={handleSubmit}
        >
          <input
            type="text"
            placeholder="Email của bạn"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
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
            Đăng nhập
          </button>
        </form>
        <div className="text-center my-4 text-gray-500">HOẶC</div>
        <button 
          className="w-full flex items-center justify-center gap-2 bg-white text-gray-700 py-2 rounded-lg border border-gray-300 hover:bg-gray-50"
          onClick={handleClick}
        >
          <svg
            className="w-5 h-5"
            viewBox="0 0 48 48"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path fill="#4285F4" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l8.98 6.98C13.77 14.63 18.47 9.5 24 9.5z"/>
            <path fill="#34A853" d="M46.98 24.55c0-1.7-.15-3.33-.43-4.9H24v9.3h12.84c-.56 2.98-2.24 5.5-4.78 7.18l7.36 5.73c4.31-3.98 7.56-10.03 7.56-17.31z"/>
            <path fill="#FBBC05" d="M11.54 28.28c-1.03-1.7-1.63-3.65-1.63-5.78s.6-4.08 1.63-5.78l-8.98-6.98C.86 13.5 0 18.58 0 24s.86 10.5 2.56 14.26l8.98-6.98z"/>
            <path fill="#EA4335" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.36-5.73c-2.24 1.5-5.09 2.38-8.53 2.38-5.53 0-10.23-5.13-11.46-12.01l-8.98 6.98C6.51 42.62 14.62 48 24 48z"/>
          </svg>
          Đăng nhập bằng Google
        </button>
        <div className="text-center mt-4 text-sm text-gray-600">
          Quên mật khẩu?
        </div>
        <div className="text-center mt-6 text-sm text-gray-600">
          Bạn chưa có tài khoản? 
          <NavLink to="/register" className="text-blue-500">Đăng ký</NavLink>
        </div>
      </div>
    </div>
  );
};

export default Login;