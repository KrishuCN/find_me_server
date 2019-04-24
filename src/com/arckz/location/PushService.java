package com.arckz.location;

import com.arckz.location.Constant.Constants;
import com.arckz.location.Constant.Url;
import com.arckz.location.utils.PageRedirect;
import com.arckz.location.utils.UtilHelper;
import com.tencent.xinge.XingeApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 *
 *     author: Hy
 *     time  : 2019/04/24  22:26
 *     desc  : 向客户端发起定位命令
 *
 * </pre>
 */
@WebServlet(name = "PushService", urlPatterns = {"/PushService"}, asyncSupported = true)
public class PushService extends HttpServlet {

    private ScheduledThreadPoolExecutor userExecutor = new ScheduledThreadPoolExecutor(5);
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String token = "";
    private String back= ""; //用于判断客户端回传标志
    private String Lng= "";//经度
    private String Lat= "";//纬度
    private String locName = "老公在这"; //坐标点上显示的名称
    private String conName = ""; //内容显示的名称
    private Boolean isBacked = false;//当客户端返回结果时用于退出循环的标志

    private String site = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.request = request;
        this.response = response;

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonObj = UtilHelper.GetPostJSONObject(request);

        try {

            if (jsonObj.has("token")) {
                token = UtilHelper.ReplaceString(jsonObj.getString("token"), "[^0-9]");
            }

            if (jsonObj.has("back")) {
                back = UtilHelper.ReplaceString(jsonObj.getString("back"), "[^0-9]");
            }

            if (jsonObj.has("lng")) {
                Lng = UtilHelper.ReplaceString(jsonObj.getString("lng"), "[^0-9]");
            }

            if (jsonObj.has("lat")) {
                Lat = UtilHelper.ReplaceString(jsonObj.getString("lat"), "[^0-9]");
            }

            if (jsonObj.has("loc")) {
                locName = UtilHelper.ReplaceString(jsonObj.getString("loc"), "[^0-9]");
            }

            if (jsonObj.has("con")) {
                conName = UtilHelper.ReplaceString(jsonObj.getString("con"), "[^0-9]");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (back.equals("")) {//网页请求没带参数的时候

            request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
            //下发定位命令
            System.out.println(XingeApp.pushTokenAndroid(000, "secretKey", "test", "测试", "token"));
            //开启线程等待
            AsyncContext aCtx = request.startAsync(request, response);
            AsyncHandler asyncHandler = new AsyncHandler(aCtx);
            userExecutor.execute(asyncHandler);

        } else if (back.equals(Constants.STATIC_CLIENT_TOKEN)) {
            if (UtilHelper.IsEmpty(token)) {
                out.println("参数错误");
                out.flush();
                out.close();
            } else {
                if (!UtilHelper.IsEmpty(Lng) && !UtilHelper.IsEmpty(Lat)) {
                    isBacked = true; //放行线程

                    site = Url.MAP_BASE_URL + "marker?location=" + Lng + "," + Lat + "&title=" + locName + "&content=" + conName + "&output=html&src=webapp.baidu.openAPIdemo";

                } else {
                    out.println("位置参数不正确");
                    out.flush();
                    out.close();
                }
            }
        }

//        System.out.println(XingeApp.pushTokenAndroid(0, "secretKey", "test", "测试", "token"));

    }

    public class AsyncHandler implements Runnable {

        private AsyncContext ctx;
        private int seconds = 0;

        AsyncHandler(AsyncContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            //耗时操作
            try {

                while (!isBacked && seconds < 20) {
                    Thread.sleep(1000);
                    seconds++;
                }

                if (seconds < 20) {
                    //网页重定向
                    PageRedirect.getInstance().setPageRedirect(response, site);

                } else {
                    PrintWriter pw;
                    pw = ctx.getResponse().getWriter();
                    pw.print("请求超时...请返回重试");
                    pw.flush();
                    pw.close();
                }

            } catch (Exception e) {
                seconds = 0;
                e.printStackTrace();
            }
            seconds = 0;
            ctx.complete();
        }

    }
}
