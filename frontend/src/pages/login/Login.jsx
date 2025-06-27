import React, { useState } from 'react';
import styles from './login.module.css';
import VStack from '../../components/ui/vstack/VStack';
import HStack from '../../components/ui/hstack/HStack';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const { login } = useAuth();

  const navigate = useNavigate();
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await login(email, password);
      navigate('/'); // Chuyển hướng sau khi đăng nhập thành công (có thể thay bằng route khác)
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className={styles.loginContainer}>
      <VStack gap={20} align="center" justify="center" className={styles.loginBox}>
        <h1 className={styles.logo}>Insta</h1>
        <VStack gap={16} className={styles.loginForm}>
          <input
            type="text"
            placeholder="Số điện thoại, tên người dùng hoặc email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className={styles.inputField}
          />
          <input
            type="password"
            placeholder="Mật khẩu"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={styles.inputField}
          />
          {error && <div className={styles.error}>{error}</div>}
          <button type="submit" className={styles.loginButton} onClick={handleSubmit}>Đăng nhập</button>
        </VStack>
        <div className={styles.forgotPassword}>Quên mật khẩu?</div>
        <HStack gap={5} justify="center">
          <div className={styles.signupText}>Bạn chưa có tài khoản?</div>
          <span className={styles.signupLink} onClick={() => navigate('/register')}>Đăng ký</span>
        </HStack>
      </VStack>
    </div>
  );
};

export default Login;