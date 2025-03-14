import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("login", "routes/login/Login.tsx"),
    route("localgame", "routes/localgame/localgame.tsx"),
    route("aigame", "routes/aigame/aigame.tsx"),
    route("onlinegame", "routes/onlinegame/onlinegame.tsx")
] satisfies RouteConfig;
