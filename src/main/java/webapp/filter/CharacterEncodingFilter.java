package webapp.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * Created by Administrator on 2017/2/22.
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns={"/*"},initParams={@WebInitParam(name="encoding",value="UTF-8")})
public class CharacterEncodingFilter implements Filter {
    //存储系统使用的字符编码
    private String encoding = null;

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //解决表单提交时的中文乱码问题
        req.setCharacterEncoding(encoding);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        //encoding在web.xml中指定
        this.encoding = config.getInitParameter("encoding");
    }

}
