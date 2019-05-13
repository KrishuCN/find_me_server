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
    private String locName = "I'm here"; //坐标点上显示的名称
    private String conName = ""; //内容显示的名称
    private static Boolean isBacked = false;//当客户端返回结果时用于退出循环的标志
    private static int PUSH_NUM = 0; //推送次数

    private static String site = "";

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
                token = jsonObj.getString("token");
            }

            if (jsonObj.has("back")) {
                back =jsonObj.getString("back");
            }

            if (jsonObj.has("lng")) {
                Lng = jsonObj.getString("lng");
            }

            if (jsonObj.has("lat")) {
                Lat = jsonObj.getString("lat");
            }

            if (jsonObj.has("loc")) {
                locName = jsonObj.getString("loc");
            }

            if (jsonObj.has("con")) {
                conName = jsonObj.getString("con");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (back.equals("")) {//网页请求没带参数的时候
            request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
            //下发定位命令
            //XingeApp带标签皆为Android设备推送
            PUSH_NUM+=1;
//            System.out.println(XingeApp.pushTokenAndroid(2100333325, "9f839394fe286525c26e3450b95cbdae", "定位", "第"+PUSH_NUM+"次", "411d9e11e7d4381e36739b65963e26c30578580e")); //推送给单个设备
            System.out.println(XingeApp.pushAccountAndroid(Constants.PUSH_ACCESSID, Constants.PUSH_SECETKEY, "定位", "第"+PUSH_NUM+"次", Constants.PUSH_ACCOUNT)); //推送给单个账号
//            System.out.println(XingeApp.pushAllAndroid(000, "myKey", "标题", "大家好!")); //推送给所有设备
//            System.out.println(XingeApp.pushTagAndroid(000, "myKey", "标题", "大家好!", "beijing")); //推送给标签选中设备

            //开启线程等待
            AsyncContext aCtx = request.startAsync(request, response);
            AsyncHandler asyncHandler = new AsyncHandler(aCtx);
            userExecutor.execute(asyncHandler);


        } else if (back.equals("1")) { //客户端请求带参数的时候
            if (UtilHelper.IsEmpty(token)) { //和客户端验证静态token
                out.println("Client empty token ..");
                out.flush();
                out.close();
            } else if (token.equals(Constants.STATIC_CLIENT_TOKEN)){
                if (!UtilHelper.IsEmpty(Lng) && !UtilHelper.IsEmpty(Lat)) {
                    back = "";
                    if (locName.equals("")){
                        locName = "你老公的位置";
                    }
                    if (conName.equals("")){
                        conName = "我不见咯";
                    }
                    site = Url.MAP_BASE_URL + "marker?location=" + Lat + "," + Lng + "&title=" + locName + "&content=" + conName + "&output=html&src=webapp.baidu.openAPIdemo";

//                    PageRedirect.getInstance().setPageRedirect(response, site);

                    isBacked = true; //放行线程

                } else {
                    out.println("Wrong client location parameter");
                    out.flush();
                    out.close();
                }
            }else {
                out.println("Client bad token ..");
                out.flush();
                out.close();
            }
        }
    }

    private static int seconds = 0;

    public class AsyncHandler implements Runnable {

        private AsyncContext ctx;

        AsyncHandler(AsyncContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            //耗时操作
            try {
                PrintWriter pw;
                pw = ctx.getResponse().getWriter();

                while (!isBacked && seconds < 20) {
                    Thread.sleep(1000);
                    seconds++;
                }
                if (seconds < 20) {
                    //网页重定向
                    isBacked = false;
                    PageRedirect.getInstance().setPageRedirect(response, site);
                } else {

                    String site = new String("http://api.map.baidu.com/marker?location=30.739507,103.980957&title=ThisisName&content=This is Content Test&output=html&src=webapp.baidu.openAPIdemo");

                    byte[] temp = site.getBytes("UTF-8");
                    String siteUTF8 = new String(temp,"UTF-8");

                    PageRedirect.getInstance().setPageRedirect(response, siteUTF8);

//                    pw.print("请求超时...请返回重试");
                }

                pw.flush();
                pw.close();
            } catch (Exception e) {
                seconds = 0;
                e.printStackTrace();
            }
            seconds = 0;
            ctx.complete();
        }

    }
}
