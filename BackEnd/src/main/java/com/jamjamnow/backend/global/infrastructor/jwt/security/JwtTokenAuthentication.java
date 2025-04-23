package com.jamjamnow.backend.global.infrastructor.jwt.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtTokenAuthentication extends AbstractAuthenticationToken {

    private final MemberLoginActive principal;
    private final Object credentials;


    public JwtTokenAuthentication(MemberLoginActive principal, Object credentials,
        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
