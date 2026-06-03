import axios from "axios";

const baseURL = (import.meta as ImportMeta).env?.VITE_API_BASE_URL || "http://localhost:8080";

export const api = axios.create({
  baseURL,
  timeout: 15000
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("aicr_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
