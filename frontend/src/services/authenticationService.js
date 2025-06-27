import { getToken, removeToken, setToken } from "./localStorageService";
import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";

export const logIn = async (username, password) => {
  const response = await httpClient.post(API.LOGIN, {
    username: username,
    password: password,
  });

  setToken(response.data?.result?.token);

  return response;
};

export const register = async (username, password, rePassword) => {
  const response = await httpClient.post(API.REGISTER, {
    username: username,
    password: password,
    rePassword: rePassword,
  });

  setToken(response.data?.result?.token);

  return response;
}

export const logOut = () => {
  removeToken();
};

export const isAuthenticated = () => {
  return getToken();
};