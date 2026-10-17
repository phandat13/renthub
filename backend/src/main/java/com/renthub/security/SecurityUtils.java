package com.renthub.security;

import com.renthub.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public final class SecurityUtils {
    private SecurityUtils() {}

    // The current logged-in user or empty if the user is a guest
    // Used for public endpoints that need to know who is viewing
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();;
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }
}