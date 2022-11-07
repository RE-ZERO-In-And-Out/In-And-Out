package com.rezero.inandout.configuration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        String message = "로그인에 실패했습니다.";

        if (exception instanceof InternalAuthenticationServiceException) {
            message = exception.getMessage();
        }

        setUseForward(true);
        setDefaultFailureUrl("/api/signin");

        request.setAttribute("errorMessage", message);
        super.onAuthenticationFailure(request, response, exception);

    }

}
