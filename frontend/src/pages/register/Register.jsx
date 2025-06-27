import React, { useState } from 'react'
import styles from './register.module.css'
import VStack from '../../components/ui/vstack/VStack'
import HStack from '../../components/ui/hstack/HStack'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const Register = () => {

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')

  const { register } = useAuth();

  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register(fullName, email, password);
      navigate('/login'); // Chuyển hướng sau khi đăng ký thành công
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className={styles.registerContainer}>
      <VStack gap={20} align="center" justify="center" className={styles.registerBox}> 
        <h1 className={styles.logo}>Insta</h1>
        <h3 style={{margin: 0}}>Đăng ký để xem ảnh và video từ bạn bè.</h3>
        <VStack gap={16} className={styles.registerForm}>
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
          <input
            type="text"
            placeholder="Tên người dùng"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            className={styles.inputField}
          />
          {error && <div className={styles.error}>{error}</div>}
          <button type="submit" className={styles.registerButton} onClick={handleSubmit}>Đăng ký</button>
        </VStack>
        <HStack gap={5} justify="center">
          <div className={styles.signupText}>Bạn đã có tài khoản?</div>
          <span className={styles.signupLink}onClick={() => navigate('/login')}>Đăng nhập</span>
        </HStack>
      </VStack>
    </div>
  )
}

export default Register
