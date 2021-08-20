package edu.pure.server.security;

import edu.pure.server.exception.UserNotFoundException;
import edu.pure.server.model.User;
import edu.pure.server.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = this.repository
                .findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withUsername(username));
        return UserPrincipal.from(user);
    }

    @Transactional
    UserDetails loadUserById(final Long id) {
        final User user = this.repository
                .findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));
        return UserPrincipal.from(user);
    }
}
