package com.rezero.inandout.configuration;


import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.member.model.MemberRole;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOauth2UserService principalOauth2UserService;

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    UserAuthenticationFailureHandler getFailureHandler() {
        return new UserAuthenticationFailureHandler();
    }


    @Bean
    UserAuthenticationSuccessHandler getSuccessHandler() {
        return new UserAuthenticationSuccessHandler(memberRepository);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String frontLoginUrl = "http://3.34.206.181:3000";

        http.csrf().disable();

        // 일반 유저, oauth 유저에 모두 적용
        http.authorizeRequests()
            .antMatchers("/api/income/**", "/api/expense/**",
                "/api/report/**", "/api/excel/**").authenticated();

        // 일반 유저에만 적용 (oauth 유저는 아래 url 접속 불가능)
        http.authorizeRequests().antMatchers( "/api/member/password")
            .hasAnyAuthority(MemberRole.ROLE_MEMBER.toString())
            .anyRequest().permitAll();

        /*
         http.formLogin()
         .defaultSuccessUrl(mainUrl)
         .defaultSuccessUrl(tmpUrl)
         .failureUrl("/api/signin")
         .and()
         .logout()
         .logoutUrl("/api/signout")
         .logoutSuccessUrl(mainUrl);
         */

        http.oauth2Login()
            .loginPage(frontLoginUrl)
            .failureHandler(getFailureHandler())
            .successHandler(getSuccessHandler())
            .userInfoEndpoint()
            .userService(principalOauth2UserService);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService)
            .passwordEncoder(getPasswordEncoder());
        super.configure(auth);
    }
}
