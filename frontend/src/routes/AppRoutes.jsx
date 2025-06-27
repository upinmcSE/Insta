import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "../pages/login/Login";
import Home from "../pages/home/Home";
import Profile from "../pages/profile/Profile";
import Chat from "../pages/chat/Chat";
import Register from "../pages/register/Register";
import { AuthProvider } from "../context/AuthContext";
import RouteGurad from "../components/RouteGurad";

const AppRoutes = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<RouteGurad requireAuth={false}><Login /></RouteGurad>} />
          <Route path="/register" element={<RouteGurad requireAuth={false}><Register /></RouteGurad>} />
          <Route path="/" element={<RouteGurad><Home /></RouteGurad>} />
          <Route path="/profile" element={<RouteGurad><Profile /></RouteGurad>} />
          <Route path="/chat" element={<RouteGurad><Chat /></RouteGurad>} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default AppRoutes;