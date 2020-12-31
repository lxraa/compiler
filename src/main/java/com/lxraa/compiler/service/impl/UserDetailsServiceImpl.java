package com.lxraa.compiler.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if("lxr".equals(s)){
            List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
            return new User("lxr",new BCryptPasswordEncoder().encode("lxr"),auths);
        }

        if("hlt".equals(s)){
            List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("user");
            return new User("hlt",new BCryptPasswordEncoder().encode("hlt"),auths);
        }

        throw new UsernameNotFoundException("not found");
    }
}
