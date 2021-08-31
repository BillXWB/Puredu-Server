package edu.pure.server.controller;

import edu.pure.server.model.Role;
import edu.pure.server.model.RoleName;
import edu.pure.server.model.User;
import edu.pure.server.payload.ApiResponse;
import edu.pure.server.payload.JwtAuthenticationResponse;
import edu.pure.server.payload.LoginRequest;
import edu.pure.server.payload.SignupRequest;
import edu.pure.server.repository.RoleRepository;
import edu.pure.server.repository.UserRepository;
import edu.pure.server.security.JwtProvider;
import edu.pure.server.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse>
    authenticateUser(@RequestBody final @Valid @NotNull LoginRequest request) {
        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = this.jwtProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>>
    registerUser(@RequestBody final @Valid @NotNull SignupRequest request) {
        if (this.userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                                 .body(ApiResponse.failure("昵称已存在！"));
        }
        if (this.userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                                 .body(ApiResponse.failure("邮箱已被使用！"));
        }
        User user = new User(request.getName(), request.getUsername(),
                             request.getEmail(), request.getPassword());
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        final Role role = this.roleRepository.findByName(RoleName.ROLE_USER).get();
        user.setRoles(Collections.singleton(role));
        user = this.userRepository.save(user);
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class)
                                        .getCurrentUser(UserPrincipal.from(user)))
                                 .toUri())
                .body(ApiResponse.success());
    }
}
