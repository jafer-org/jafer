<!DOCTYPE html public "-//W3C//DTD HTML 4.0 Final//EN">
<html>
  <head>
    <title>JAFER Exception</title>
  </head>
  <body bgcolor="#dddddd">
  <%@ page isErrorPage="true" %>

   <h1>JAFER Exception:</h1>
    <p><b>The following error has ocurred:</b><br><br>
    <p><%= exception.getMessage() %></p>
    <p><%= exception.toString() %></p>

    <!-- <% exception.printStackTrace(new java.io.PrintWriter(out)); %> -->

  </body>
</html>