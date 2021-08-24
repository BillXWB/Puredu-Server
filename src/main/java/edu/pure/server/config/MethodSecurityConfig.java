package edu.pure.server.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Override
    protected AffirmativeBased accessDecisionManager() {
        final var affirmativeBased = (AffirmativeBased) super.accessDecisionManager();
        final var decisionVoters = affirmativeBased.getDecisionVoters();
        decisionVoters.add(MethodSecurityConfig.roleHierarchyVoter());
        return new AffirmativeBased(decisionVoters);
    }

    @Contract(" -> new")
    private static @NotNull RoleHierarchyVoter roleHierarchyVoter() {
        return new RoleHierarchyVoter(MethodSecurityConfig.roleHierarchy());
    }

    private static @NotNull RoleHierarchyImpl roleHierarchy() {
        final var roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }
}
