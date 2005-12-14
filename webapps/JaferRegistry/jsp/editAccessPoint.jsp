<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.web.struts.action.AddAccessPointAction" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="editaccesspoint.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/editAccessPointRedirect.do" focus="protocolType">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="update_access_point"/> 
<html:hidden property="currentpage" value="edit_access_point"/> 
<html:hidden property="serviceId" /> 

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="addaccesspoint.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="addaccesspoint.protocol.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="6" property="protocolType" size="50"/>		
	</tr>
	<tr>
		<th align="right"><bean:message key="addaccesspoint.type.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="7" property="pointType" size="50"/>
	</tr>	
	<tr>
		<th align="right"><bean:message key="addaccesspoint.url.heading"/></th>
		<td align="left"><html:text  tabindex="8" property="accessPointUrl" size="100"/>
		</td>
	</tr>
	<tr>
		<th/>
		<td align="left"> 
			<html:submit property="submit" tabindex="9" onclick="setAndSubmit('update_access_point');" value="update"/>
			<html:submit property="submit" tabindex="10" onclick="setAndSubmit('cancel');" value="cancel"/>
		</td>
	</tr>	
</table>
<br>

</html:form>
</body>
</html:html>