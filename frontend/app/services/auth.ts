import type { LoginResponse, RegisterResponse } from "@/types/auth";
import { Api } from "./api";
import { removeToken, getToken, removeUser } from "./storage";


async function login(email: string, password: string): Promise<LoginResponse> {
  return Api.post("/auth/login", { email, password });
}

async function register(email: string, password: string, fullName: string): Promise<RegisterResponse> {
  return Api.post("/auth/register", { email, password, fullName });
}

async function logout() {
  removeToken();
  removeUser();
};

function isAuthenticated(): boolean {
  const token = getToken();
  return token != null ? true : false;
}

export {
  login,
  register,
  logout,
  isAuthenticated
}