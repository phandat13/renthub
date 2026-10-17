package com.renthub.user;

import com.renthub.security.UserPrincipal;
import com.renthub.user.dto.UpdateProfileRequest;
import com.renthub.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal){
        return userService.getProfile(principal.getId());
    }

    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal UserPrincipal principal, @Validated @RequestBody UpdateProfileRequest request){
        return userService.updateProfile(principal.getId(), request);
    }
}