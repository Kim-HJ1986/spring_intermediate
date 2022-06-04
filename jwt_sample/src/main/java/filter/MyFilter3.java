package filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 해당 필터는 SecurityConfig에서 http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class);로 실행된다.
// 즉 시큐리티 필터 체인이 시작되기 전에 실행되는 필터이다.
public class MyFilter3 implements Filter { //

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

//        토큰: token인 경우 판별 -> id, pw가 정상적으로 들어와서 로그인이 완료된다면 토큰을 만들어주고 응답에 보내준다.
//         요청할 때 마다 header에 Authorization에 value값으로 토큰을 가지고 온다.
//         그 때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증하면 된다. (RSA, HS256)
        if (req.getMethod().equals("POST")){
            String headerAuth = req.getHeader("Authorization");
            System.out.println(headerAuth);
            System.out.println("필터 3");

            if (headerAuth.equals("token")){
                // 현재 필터에서 멈추지 말고 다른 필터에도 걸리게 만든다.
                chain.doFilter(request, response);
            }else{
                PrintWriter out = res.getWriter();
                out.println("인증 안됨");
            }
        }
    }
}
