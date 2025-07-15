import axios, { AxiosError, type AxiosInstance, type AxiosResponse } from "axios";
import { getToken } from "./storage";

const url = "http://localhost:8181/poops";


const Api: AxiosInstance = axios.create({ baseURL: url + "/api/v1" });

Api.interceptors.request.use(async config => {
    const token = getToken()

    if (token) config.headers.set("Authorization", `Bearer ${token}`);

    return config;
});

Api.interceptors.response.use(
    async (res: AxiosResponse) => res.data,
    async (err: AxiosError) => Promise.reject(err)
);

export { Api };