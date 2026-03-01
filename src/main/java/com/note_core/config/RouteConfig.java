package com.note_core.config;

import org.springframework.http.HttpMethod;

public final class RouteConfig {

    private RouteConfig() {
    }

    public static final Route[] PUBLIC_ROUTES = {
            Route.of(HttpMethod.POST, "/api/auth/**"),
    };

    public record Route(HttpMethod method, String pattern) {
        public static Route of(HttpMethod method, String pattern) {
            return new Route(method, pattern);
        }

        public static Route of(String pattern) {
            return new Route(null, pattern);
        }
    }
}
