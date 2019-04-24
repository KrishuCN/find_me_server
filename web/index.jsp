<%--
  Created by IntelliJ IDEA.
  User: Hy
  Date: 2019/4/24
  Time: 20:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<html>
  <head>
    <title>偷偷定你位哦</title>
  </head>
  <body>

  <form action="${pageContext.request.contextPath}/PushService" method=post>
    <input type="button" name="获取位置" value="获取位置" onclick="window.location='${pageContext.request.contextPath}/PushService';" />
  </form>

  </body>
</html>
