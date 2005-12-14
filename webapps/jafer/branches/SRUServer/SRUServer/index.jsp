<link rel="stylesheet" type="text/css" href="css/sru-styles.css" title="SRUServer" />

<html lang="en">
<head>
	<title>SRU Server</title>
</head>
<body> 

<form name="SRUForm" method="post" action="/SRUServer/execute">

<!-- SCRIPT TO SET OPERATION DEPENDING ON BUTTON PRESSED -->
<script type="text/javascript">
	 function setAndSubmit(operation)
	 {
	 	//alert(operation);
	  	document.forms[0].operation.value=operation;  
	 }
</script>

<input name='operation'  type='hidden' value='explain'  />
<input name='version'    type='hidden' value='1.1'      />

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
    <tr><td>This is a simple utility page for calling the Jafer SRU Server</td></tr>
    </tbody>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr><td>Click Explain to describe the connected SRU server</td></tr>
    </tbody>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2">
<tbody>
    <tr>
    	<td align="left">
	    	<input type="submit" name="search" tabindex="2" value="Explain" onclick="setAndSubmit('explain');" >
    	</td>
   </tr>	
</tbody>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr><td>To perform a search on the SRU server enter search criteria and press search ( * Mandatory field )</td></tr>
    </tbody>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2">
<tbody>
	<tr>
	    <th align="right">Start record:</th>
    	<td align="left"><input name='startRecord' tabindex="3" size="5" type='text' value='1'/></td>
    </tr>
    <tr>
	    <th align="right">Max records:</th>
    	<td align="left"><input name='maximumRecords' tabindex="4" size="5" type='text' value='5'/></td>
    </tr>
    <tr>
	    <th align="right">CQL Query *:</th>
    	<td align="left"><input name='query' tabindex="5" size="100" type='text' value=''/></td>
    </tr>
    <tr>
    	<th/>
    	<td align="left">
	    	<input type="submit" name="search" tabindex="6" value="Search" onclick="setAndSubmit('searchRetrieve');" >
	 	</td>
   </tr>	
</tbody>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr><td>To perform a scan on the SRU server enter search criteria and press scan ( * Mandatory field )</td></tr>
    </tbody>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2">
<tbody>
	<tr>
	    <th align="right">Response position:</th>
    	<td align="left"><input name='responsePosition' tabindex="7" size="5" type='text' value='1'/></td>
    </tr>
    <tr>
	    <th align="right">Max terms:</th>
    	<td align="left"><input name='maximumTerms' tabindex="8" size="5" type='text' value='5'/></td>
    </tr>
    <tr>
	    <th align="right">Scan clause *:</th>
    	<td align="left"><input name='scanClause' tabindex="9" size="100" type='text' value=''/></td>
    </tr>
    <tr>
    	<th/>
    	<td align="left">
	    	<input type="submit" name="scan" tabindex="10" value="Scan" onclick="setAndSubmit('scan');" >
	 	</td>
   </tr>	
</tbody>
</table>
</form>

</body>
</html>
