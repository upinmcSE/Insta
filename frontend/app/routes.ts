import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("login", "routes/login.tsx"),
    route("register", "routes/register.tsx"),
    route("authentication", "routes/authentication.tsx"),
    route("profile", "routes/profile.tsx"),
    route("user", "routes/user.tsx"),
    route("search", "routes/search.tsx"),
    route("messages", "routes/chat.tsx"),
] satisfies RouteConfig;
