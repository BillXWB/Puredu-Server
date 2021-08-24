package edu.pure.server.controller;

import edu.pure.server.assembler.UserAssembler;
import edu.pure.server.exception.AppException;
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
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserAssembler userAssembler;
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
    public ResponseEntity<ApiResponse>
    registerUser(@RequestBody final @Valid @NotNull SignupRequest request) {
        if (this.userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                                 .body(new ApiResponse(false,
                                                       "Username is already taken!"));
        }
        if (this.userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                                 .body(new ApiResponse(false,
                                                       "Email address is already in use!"));
        }
        User user = new User(request.getName(), request.getUsername(),
                             request.getEmail(), request.getPassword());
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        final Role role = this.roleRepository.findByName(RoleName.ROLE_USER)
                                             .orElseThrow(() -> new AppException(
                                                     "User Role was not set."));
        user.setRoles(Collections.singleton(role));
        user = this.userRepository.save(user);
        final EntityModel<User> entityModel = this.userAssembler.toModel(user);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(new ApiResponse(true, "User registered successfully."));
    }
}
