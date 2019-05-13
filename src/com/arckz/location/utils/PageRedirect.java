package com.arckz.location.utils;

import com.sun.istack.internal.NotNull;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 *
 *     author: Hy
 *     time  : 2019/04/24  22:06
 *     desc  : 网页重定向
 *
 * </pre>
 */
public class PageRedirect {

    private static PageRedirect pageRedirect = null;

    private PageRedirect(){}

    public static PageRedirect getInstance() {
        if (pageRedirect == null){
            pageRedirect = new PageRedirect();
        }
        return pageRedirect;
    }

    /**
     * 设置重定向
     * @param response  HttpServletResponse
     * @param url 重定向的地址
     */
    public void setPageRedirect(@NotNull HttpServletResponse response,String url) throws IOException {
        if (response == null){
            throw new NullPointerException("HttpServletResponse can't be null..");
        }else {
            if (url == null || url.isEmpty()){
                throw new NullPointerException("The site url can't be null or empty!");
            }else {
                // 要重定向的新位置
                if (response!=null){
                    response.setStatus(response.SC_MOVED_TEMPORARILY);
                    response.setHeader("Content-Type","text/html;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");
//                response.setHeader("Location", url);
                    String encodeUrl = response.encodeURL(url);
                    response.sendRedirect(url);
                }
            }
        }
    }
}
