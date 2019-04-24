package com.arckz.location;

import com.arckz.location.utils.PageRedirect;

import java.io.IOException;
import java.io.PrintWriter;


/**
 * <pre>
 *
 *     author: Hy
 *     time  : 2019/04/24  20:36
 *     desc  : 下发定位消息的主入口
 *
 * </pre>
 */
public class main extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException
    {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
//        out.println("Serverlet 测试成功");

        String site = new String("http://api.map.baidu.com/marker?location=40.047669,116.313082&title=我的位置&content=百度奎科大厦&output=html&src=webapp.baidu.openAPIdemo ");

        PageRedirect.getInstance().setPageRedirect(response,site);
    }
}
