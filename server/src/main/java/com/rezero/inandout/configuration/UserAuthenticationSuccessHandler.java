package com.rezero.inandout.configuration;

import com.rezero.inandout.member.repository.MemberRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    @Value(value = "${url.after.login}")
    private String urlAfterLogin;

    @Value(value = "${url.after.google.login}")
    private String urlAfterGoogleLogin;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        String oauthUsername = authentication.getName();

        if(oauthUsername.startsWith("google_")) {
            setDefaultTargetUrl(urlAfterGoogleLogin + "?id=" + oauthUsername);  //

        }else {
            setDefaultTargetUrl(urlAfterLogin + "?id=" + oauthUsername);
        }

        log.info("[Member Authentication] OAuth member: " + oauthUsername);
        super.onAuthenticationSuccess(request, response, authentication);

    }

}