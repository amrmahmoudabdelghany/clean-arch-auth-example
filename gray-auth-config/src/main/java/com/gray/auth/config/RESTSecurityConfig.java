package com.gray.auth.config;


import com.gary.auth.core.domain.IUserContext;
import com.gary.auth.core.domain.jwt.AccessToken;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RESTSecurityConfig {


    @RequiredArgsConstructor
    public static final class DefaultAuthentication implements Authentication {

        private final String token ;
        private final WebAuthenticationDetails authenticationDetails;
        private IUserContext user ;
        private boolean isAuth ;
        private String accountRole ;

         DefaultAuthentication(DefaultAuthentication defaultAuthentication ,  IUserContext user  , String accountRole){
            this.token = defaultAuthentication.token ;
            this.authenticationDetails = defaultAuthentication.authenticationDetails ;
            this.user = user ;
            this.accountRole = accountRole;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {

//            List<SimpleGrantedAuthority> authorities = new ArrayList<>( );
//            if(user.hasRole(RoleFlag.ROLE_ADMIN)) {
//                 authorities.add(new SimpleGrantedAuthority(RoleFlag.ROLE_ADMIN.name())) ;
//            }
//            if(user.hasRole(RoleFlag.ROLE_USER)) {
//                authorities.add(new SimpleGrantedAuthority(RoleFlag.ROLE_USER.name())) ;
//            }
//            return authorities;
            String [] roleArr = this.accountRole.split(",") ;

            return Stream.of(roleArr).map(SimpleGrantedAuthority::new).toList();
        }

        @Override
        public Object getCredentials() {
            return this.token;
        }

        @Override
        public Object getDetails() {
            return this.authenticationDetails;
        }

        @Override
        public Object getPrincipal() {
            return this.user ;
        }

        @Override
        public boolean isAuthenticated() {
            return this.isAuth;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            this.isAuth = isAuthenticated ;
        }

        @Override
        public String getName() {
            return this.user.getUserName();
        }
    }

    @Component
    @RequiredArgsConstructor
    class   DefaultAuthManager implements AuthenticationManager {
        private final DefaultAuthProvider defaultAuthProvider ;

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return defaultAuthProvider.authenticate(authentication);
        }
    }

    @Component
    @RequiredArgsConstructor
    static
    class DefaultAuthProvider implements AuthenticationProvider {

        private final ITokenProcessor tokenProcessor ;


        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            if (authentication instanceof DefaultAuthentication auth) {

                SignedToken token = this.tokenProcessor.transform((String) auth.getCredentials());
                AccessToken accessToken = AccessToken.from(token);

                final IUserContext userContext = UserContext.builder()
                        .withAccountId(accessToken.getAccountId())
                        .withUserName(accessToken.getUserName())
                        .withEmail(accessToken.getEmail())
                        .build();

                System.out.println("Default Auth Provider : " + userContext);

                auth = new DefaultAuthentication(auth, userContext, accessToken.getRole());
                auth.setAuthenticated(true);
                return auth;
            } else
                throw new AuthenticationServiceException("AuthenticationServiceException");
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return DefaultAuthentication.class.equals(authentication);
        }
    }

    @Component
    @RequiredArgsConstructor
    public class JWTAuthenticationFilter  extends OncePerRequestFilter {

        private final DefaultAuthManager authenticationManager ;

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain)
                throws ServletException, IOException {

            final String jwt = request.getHeader("Authorization");

            if (jwt == null || !jwt.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String token = jwt.substring(7);
            System.out.println("JWT Authentication Filter : " + token);
            DefaultAuthentication s = new DefaultAuthentication(token,
                    new WebAuthenticationDetailsSource().buildDetails(request));

            Authentication authentication = authenticationManager.authenticate(s);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }

    }


    @RequiredArgsConstructor
    private final static class UserContext implements IUserContext {

        private final UUID accountId;
        private final String userName;
        private final String email;

        @Override
        public UUID getId() {
            return this.accountId;
        }

        @Override
        public String getUserName() {
            return this.userName;
        }

        @Override
        public String getEmail() {
            return this.email;
        }

        public static AccountIdStep builder() {
            return new Stepper();
        }

        public interface AccountIdStep {
            UserNameStep withAccountId(UUID accountId);
        }

        public interface UserNameStep {
            EmailStep withUserName(String userName);
        }

        public interface EmailStep {
            FinalStep withEmail(String email);
        }

        public interface FinalStep {
            UserContext build();
        }

        private static class Stepper implements AccountIdStep, UserNameStep, EmailStep, FinalStep {
            private UUID accountId;
            private String userName;
            private String email;

            @Override
            public UserNameStep withAccountId(UUID accountId) {
                this.accountId = accountId;
                return this;
            }

            @Override
            public EmailStep withUserName(String userName) {
                this.userName = userName;
                return this;
            }

            @Override
            public FinalStep withEmail(String email) {
                this.email = email;
                return this;
            }

            @Override
            public UserContext build() {
                return new UserContext(accountId, userName, email);
            }
        }
    }
}
