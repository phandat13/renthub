package com.renthub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class RestAuthenticationEntryPointHelper {
    private RestAuthenticationEntryPointHelper() {}

    public static void forbidden(HttpServletRequest req, HttpServletResponse res) throws IOException {
        RestAuthenticationEntryPoint.writeJson(res, 403, "Forbidden",
                "Bạn không có quyền truy cập", req.getRequestURI());
    }
}