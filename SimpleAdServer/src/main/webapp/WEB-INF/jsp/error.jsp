<%-- 
    Document   : error
    Created on : Jul 17, 2016, 2:29:00 PM
    Author     : vchaitankar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Simple Ad Server - Error Page</title>
    </head>
    <body>
        <h1>Error while servicing the request</h1>
        <p>Message : ${errorMsg}</p>
        <p><a href="${pageContext.request.contextPath}">Home</a></p>
    </body>
</html>
