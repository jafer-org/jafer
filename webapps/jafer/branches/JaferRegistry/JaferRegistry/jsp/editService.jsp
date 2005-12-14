<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.model.ServiceProviderInfo" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="editservice.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/editServiceRedirect.do" focus="serviceName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="update_service"/> 
<html:hidden property="currentpage" value="edit_service"/> 
<html:hidden property="serviceId" /> 
<html:hidden property="providerId" /> 
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="createservice.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="viewservice.service.name.heading"/></th>
		<td align="left"><html:text  tabindex="5" property="serviceName" size="50"/>
		</td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="viewservice.service.description.heading"/></th>
		<td align="left"><html:textarea  cols="70" rows="3" tabindex="6" property="serviceDescription"/></td>
	</tr>

</table>
<br>
<br>
<table border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
<tr>
	<td align="right"> 
		<html:submit property="submit" tabindex="7" onclick="setAndSubmit('update_service');" value="update"/>
		<html:submit property="submit" tabindex="8" onclick="setAndSubmit('cancel');" value="cancel"/>
	</td>
</tr>	
</table>
</html:form>
</body>
</html:html>