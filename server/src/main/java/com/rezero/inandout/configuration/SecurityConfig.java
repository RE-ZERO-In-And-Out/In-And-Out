package com.rezero.inandout.configuration;


import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.member.service.MemberService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate endDate = LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);
        String mainUrl = "/api/calendar?start_dt=" + start + "&end_dt=" + end;
        String tmpUrl = "/api/calendar";

        http.csrf().disable();
        http.authorizeRequests()
            .antMatchers("/api/member/**", "/api/income/**", "/api/expense/**",
            "/api/report/**", "/api/excel/**").authenticated()  // 로그인해야만 접근 가능
            .anyRequest().permitAll();

        http.formLogin()
//            .defaultSuccessUrl(mainUrl)
            .defaultSuccessUrl(tmpUrl)
            .failureUrl("/api/signin")

            .and()
            .logout()
            .logoutUrl("/api/signout")
            .logoutSuccessUrl(mainUrl);

        http.oauth2Login()
            .loginPage("/api/signin")
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
