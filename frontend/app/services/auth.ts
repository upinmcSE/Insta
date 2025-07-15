import type { LoginResponse, RegisterResponse } from "@/types/auth";
import { Api } from "./api";
import { removeToken, getToken } from "./storage";


async function login(email: string, password: string): Promise<LoginResponse> {
  return Api.post("/auth/login", { email, password });
}

async function register(email: string, password: string, fullName: string): Promise<RegisterResponse> {
  return Api.post("/auth/register", { email, password, fullName });
}

async function logout(){
  removeToken();
};

async function isAuthenticated() {
  return getToken();
};

export {
  login,
  register,
  logout,
  isAuthenticated
}