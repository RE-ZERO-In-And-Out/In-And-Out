package com.rezero.inandout.configuration;

import com.rezero.inandout.member.repository.MemberRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        String oauthUsername = authentication.getName();
        String frontLoginUrl = "https://re-zero-in-and-out.github.io/In-And-Out/calendar";

        /*
        LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate endDate = LocalDate.of(now.getYear(), now.getMonth(), now.lengthOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);
        String mainUrl = "/api/calendar?startDt=" + start + "&endDt=" + end;
        // ex) /api/calendar?start_dt=2022-11-01&end_dt=2022-11-30
        */

        log.info("[Member Authentication] member: " + oauthUsername);
        setDefaultTargetUrl(frontLoginUrl);
        super.onAuthenticationSuccess(request, response, authentication);

    }

}