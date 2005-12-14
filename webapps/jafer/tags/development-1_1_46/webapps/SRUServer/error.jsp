<link rel="stylesheet" type="text/css" href="css/sru-styles.css" title="SRUServer" />

<html lang="en">
<head>
	<title>SRU Server</title>
</head>
<body> 

<table  border="0" cellpadding="2" cellspacing="2">
  <tbody>
    <tr>
      <td align="left">
      		<a href="http://www.ox.ac.uk" tabindex="1"><img style="width: 90px; height: 107px;" alt="Oxford University"
	      		 border="0" src="./images/oxfordcrest.gif"></a>
      </td>     
      <!--
      <td > 
      		<a tabindex="2"><img style="width: 379px; height: 79px;" alt="Jafer SRU "  
	      		 border="0" src="./images/SRUTitle.png"></a>
      </td> 
      -->
    </tr>
    </tbody>
</table>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr><td>A fatal error has occured on the server</td></tr>
    </tbody>
</table>
<div style="color:#FF0000;">
<p>
<%= request.getAttribute("errormsg") %>
<p>
</div>
</body>
</html>
