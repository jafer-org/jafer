<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="logon.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/logonRedirect.do" focus="username">
<%@ include file="scripts.jsp" %>
<%@ include file="logonbanner.jsp" %>
 
<html:hidden  property="submitaction" value="logon"/> 
<html:hidden  property="currentpage" value="logon"/> 

<!-- Although not used here these are added to get round the reset issue on the form during struts action chaining -->
<html:hidden property="providerId" /> 
<html:hidden property="serviceId" /> 

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="logon.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
<tr>
	<th align="right"><bean:message key="logon.username.heading"/></th>
	<td align="left"><html:text tabindex="6" property="username" size="50" /></td>
</tr>
<tr>
	<th align="right"><bean:message key="logon.credential.heading"/></th>
	<td align="left"><html:password tabindex="7" property="credential" size="44"/></td>
</tr>
<tr>
	<th/>
	<td align="left">
		<html:submit property="submit" tabindex="8" onclick="setAndSubmit('logon');" value="logon"/>
		<html:submit property="submit" tabindex="9" onclick="setAndSubmit('cancel');" value="cancel"/>
	</td>
</tr>
</table>

</html:form>
</body>
</html:html>