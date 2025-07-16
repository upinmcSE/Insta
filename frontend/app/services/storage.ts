import type { User } from "@/types/types";

export const KEY_TOKEN = "accessToken";
export const KEY_USER = "user"

export const setToken = (token: string) => {
  localStorage.setItem(KEY_TOKEN, token);
};

export const setUser = (user: User) => {
  localStorage.setItem(KEY_USER, JSON.stringify(user));
}

export const getToken = () => {
  return localStorage.getItem(KEY_TOKEN);
};

export const getUser = () => {
  return localStorage.getItem(KEY_USER);
}

export const removeToken = () => {
  return localStorage.removeItem(KEY_TOKEN);
};

export const removeUser = () => {
  return localStorage.removeItem(KEY_USER);
}