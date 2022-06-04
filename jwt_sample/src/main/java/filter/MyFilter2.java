package filter;


import javax.servlet.*;
import java.io.IOException;

public class MyFilter2 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("필터 2");

        // 현재 필터에서 멈추지 말고 다른 필터에도 걸리게 만든다.
        chain.doFilter(request, response);
    }
}
