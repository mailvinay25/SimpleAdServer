<%-- 
    Document   : home
    Created on : Jul 17, 2016, 11:43:37 AM
    Author     : vchaitankar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Simple Ad Server - Retrieve Ad</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
        <script type="text/javascript" src="http://yandex.st/json2/2011-10-19/json2.min.js"></script>
        <script type="text/javascript">
            function submitRetrieveAdform() {
                var partnerId = $('#partnerId').val();
                $.ajax({
                    type: "GET",
                    url: "./ad/" + partnerId,
                    success: function (response) {
                        var result = response.toString();
                        var link = '<a href="${pageContext.request.contextPath}/retrieveAd">Retrieve another Ad</a>';
                        var bodyMessage = '<p>' + result + '</p><br/>' + link;
                        jQuery("body").html(bodyMessage);
                    },
                    error: function () {
                        window.location.href = "error";
                    }
                });
            }
            ;
        </script>
    </head>
    <body>
        <h1>Please provide partner Id to retrieve the advertisement </h1>
        <form name="retrieveAdForm">
            <table>
                <tr>
                    <td>Partner Id : </td>
                    <td><input type="text" name="partner_id" id="partnerId"/></td>
                </tr>                
                <tr>
                    <td colspan="2" style="text-align: center">
                        <input value="Submit" name= "retrieve_add" type="button" onclick="submitRetrieveAdform()"/>               
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>

