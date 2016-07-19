<%-- 
    Document   : home
    Created on : Jul 17, 2016, 11:43:37 AM
    Author     : vchaitankar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Simple Ad Server - Home</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h1>Welcome to Simple Ad Server </h1>

        <p>Please select one of the following operations</p>

        <ul>
            <li><a href="${pageContext.request.contextPath}/createAd">Create a new Ad</a></li>
            <li><a href="${pageContext.request.contextPath}/retrieveAd">Retrieve an existing Ad</a></li>
            <li><a href="${pageContext.request.contextPath}/getAllAds">List of all Ad Campaigns</a></li>
        </ul>
    </body>
</html>

