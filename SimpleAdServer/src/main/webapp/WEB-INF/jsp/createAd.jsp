<%-- 
    Document   : Create Ad
    Created on : Jul 17, 2016, 11:43:37 AM
    Author     : vchaitankar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Simple Ad Server - Create Ad</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
        <script type="text/javascript" src="http://yandex.st/json2/2011-10-19/json2.min.js"></script>
        <script type="text/javascript">
            function submitCreateAdform(e) {
                var temp = {
                    partner_id: $('#partnerId').val(),
                    duration: $('#duration').val(),
                    ad_content: $('#adContent').val()
                };
                var data = JSON.stringify(temp);
                $.ajax({
                    type: "POST",
                    url: "./ad",
                    async: false,
                    data: data,
                    success: function (response) {
                        var result = response.toString();
                        if (result.indexOf('Successfully created') >= 0) {
                            window.location.href = "adCreateSuccess";
                        } else {
                            window.location.href = "error/" + result;
                        }
                    },
                    error: function () {
                        window.location.href = "error";
                    },
                    contentType: "application/json"
                });
            }
            ;
        </script>
    </head>
    <body>
        <h1>Please fill the following advertisement form </h1>
        <form name="createAdForm">
            <table>
                <tr>
                    <td>Partner Id : </td>
                    <td><input type="text" value="${partnerId}" name="partnerId" id="partnerId"/></td>
                </tr>
                <tr>
                    <td>Duration:</td>
                    <td><input type="number" name="duration" id="duration"/></td>
                </tr>
                <tr>
                    <td>Ad Content:</td>
                    <td><textarea  name="adContent" id="adContent" rows="25" cols="50"></textarea></td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center">
                        <input value="Submit" name= "create_add" type="button" onclick="submitCreateAdform()"/>               
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>

