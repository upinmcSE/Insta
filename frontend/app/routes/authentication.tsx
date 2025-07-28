import { useNavigate } from "react-router";
import { useState, useEffect } from "react";
import { setToken, setUser } from "@/services/storage";


const Authenticate: React.FC = () => {
  const navigate = useNavigate();
  const [isLoggedin, setIsLoggedin] = useState<boolean>(false);

  useEffect(() => {
    console.log(window.location.href)

    // const accessTokenRegex = /access_token=([^&]+)/;
    const authCodeRegex = /code=([^&]+)/;
    const isMatch = window.location.href.match(authCodeRegex);

    if (isMatch) {
      const authCode = isMatch[1];

      fetch(
        `http://localhost:8181/api/v1/auth/outbound/authentication?code=${authCode}`,
        {
          method: "POST",
        }
      )
        .then((response) => {
          return response.json();
        })
        .then((data) => {
          console.log(data);

          setToken(data.result?.accessToken || null);
          setUser(data.result?.userInfo || null)
          setIsLoggedin(true);
        });
    }
  }, []);

  useEffect(() => {
    if (isLoggedin) {
      navigate("/");
    }
  }, [isLoggedin, navigate]);

  return (
    <div className="flex flex-col gap-8 justify-center items-center h-screen">
      <div className="animate-spin rounded-full h-12 w-12 border-4 border-blue-500 border-t-transparent"></div>
      <p className="text-lg text-gray-700">Authenticating...</p>
    </div>
  );
};

export default Authenticate;