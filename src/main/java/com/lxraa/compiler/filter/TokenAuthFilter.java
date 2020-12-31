package com.lxraa.compiler.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class TokenAuthFilter extends BasicAuthenticationFilter {
    public TokenAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
}
