<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.model.ServiceProviderInfo" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="viewproviderservices.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/viewProviderServicesRedirect.do" focus="providerName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="view_provider"/> 
<html:hidden property="currentpage" value="view_provider_services"/> 
<html:hidden property="providerId" /> 
<html:hidden property="serviceId" /> 

<%@ include file="providerPanel.jsp" %>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewproviderservices.service.heading"/></td></tr>
</table>
<logic:notEmpty name="registryForm" property="providerServices" >
<table style="border: 2px solid black" width="100%">
		<tr>	
			<th align="left"><bean:message key="viewproviderservices.service.name.heading"/></th>
		</tr>
	<logic:iterate id="serviceInfo" name="registryForm" property="providerServices" 
					type="org.jafer.registry.model.ServiceInfo" >
		<tr>	
			<% if (serviceInfo.getName().length() == 0) { %>
				<td width="70%" align="left" style="color:#FF0000;"> <bean:message key="viewproviderservices.badservicelink"/></td>
			<% } else { %>
				<td width="70%" align="left"> <bean:write name="serviceInfo" property="name"/></td>
			<% } %>
			<td width="10%" align="center"> <html:submit property="submit"  tabindex="10"
				onclick="<%= "setSelectedServiceIdAndAction(\'" + serviceInfo.getId() + "\',\'remove_service\');"%>"
				value="remove"/></td>
			<td width="10%" align="center"> <html:submit property="submit" tabindex="11" disabled="<%=serviceInfo.getName().length() == 0%>"
				onclick="<%= "setSelectedServiceIdAndAction(\'" + serviceInfo.getId() + "\',\'go_edit_service\');"%>"
				value="edit"/></td>
			<td width="10%" align="center"> <html:submit property="submit" tabindex="12" disabled="<%=serviceInfo.getName().length() == 0%>"
				onclick="<%= "setSelectedServiceIdAndAction(\'" + serviceInfo.getId() + "\',\'view_service\');"%>"
				value="view"/></td>
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="providerServices" >
	<br>
	<p style="text-align:center"><bean:message key="viewproviderservices.services.nonfound"/></p>
</logic:empty>
<br>
<table>
<tr>
	<td align="right"> 
		<html:submit property="submit" tabindex="13" onclick="setAndSubmit('go_add_service');" value="add service"/>
		<html:submit property="submit" tabindex="14" onclick="setAndSubmit('go_create_service');" value="create service"/>
	</td>
</tr>	
</table>
<br>
<table border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
<tr>
	<td align="right"> 
		<html:submit property="submit" tabindex="15" onclick="setAndSubmit('view_provider');" value="back to provider"/>
	</td>
</tr>	
</table>
</html:form>
</body>
</html:html>