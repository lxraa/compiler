package com.lxraa.compiler.security;

import com.lxraa.compiler.service.impl.UserDetailsServiceImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.userDetailsService).passwordEncoder(password());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin().loginProcessingUrl("/usr/login")
//                .defaultSuccessUrl("/api/getFirst")
//                .and().authorizeRequests()
//                .antMatchers("/test/index").permitAll()
//                .anyRequest().authenticated()
//                .and().csrf().disable();
        http.authorizeRequests()//配置安全策略
                //.antMatchers("/","/hello").permitAll()//定义/请求不需要验证
                .anyRequest().authenticated()//其余的所有请求都需要验证
                .and()
                .logout()
                .permitAll()//定义logout不需要验证
                .and()
                .formLogin();//使用form表单登录



        http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint())
                .and().addFilter(casFilter())
                .addFilterBefore(casLogoutFilter(),LogoutFilter.class)
                .addFilterBefore(singleSignOutFilter(),CasAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.authenticationProvider(casAuthenticationProvider());
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(){
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl("http://localhost:1234/login");
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }
    @Bean
    public ServiceProperties serviceProperties(){
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setAuthenticateAllArtifacts(true);
        serviceProperties.setService("http://localhost:1234/service");
        return serviceProperties;
    }
    @Bean
    public CasAuthenticationFilter casFilter() throws Exception{
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setFilterProcessesUrl("http://localhost:1234/casFilter");
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator(){
        return new Cas20ServiceTicketValidator("http://localhost:1234/ticket");
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(){
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(cas20ServiceTicketValidator());
        provider.setKey("casAuthenticationProviderKey");
        return provider;

    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter(){
        SingleSignOutFilter filter = new SingleSignOutFilter();
        return filter;
    }
    /**单点登出过滤器*/

//    public UserDetailsService userDetailsService(){
//        return new UserDetailsServiceImpl();
//    }

    public LogoutFilter casLogoutFilter(){
        LogoutFilter filter = new LogoutFilter("http://localhost:1234/logout",new SecurityContextLogoutHandler());

        filter.setFilterProcessesUrl("http://localhost:1234/index");
        return filter;
    }


}
