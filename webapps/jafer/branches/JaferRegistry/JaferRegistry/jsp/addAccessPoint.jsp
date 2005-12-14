<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.web.struts.action.AddAccessPointAction" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="addaccesspoint.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/addAccessPointRedirect.do" focus="protocolType">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="add_access_point"/> 
<html:hidden property="currentpage" value="add_access_point"/> 
<html:hidden property="serviceId" /> 

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="addaccesspoint.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="addaccesspoint.protocol.heading"/></th>
		<td align="left">
			<html:select tabindex="6" size="1" property="protocolType">
				<html:options property="protocolTypes"/>
			</html:select>
		</td>
	</tr>
	<tr>
		<th align="right"><bean:message key="addaccesspoint.type.heading"/></th>
		<td>
			<html:radio  tabindex="7" property="pointType" value="<%=AddAccessPointAction.ACCESS%>">
				<bean:message key="accessurl.accesspoint.type"/>
			</html:radio> 
			<html:radio tabindex="8"  property="pointType" value="<%=AddAccessPointAction.WSDL%>">
				<bean:message key="accessurl.wsdl.type"/>
			</html:radio> 
		</td>
	</tr>	
	<tr>
		<th align="right"><bean:message key="addaccesspoint.url.heading"/></th>
		<td align="left"><html:text  tabindex="9" property="accessPointUrl" size="100"/>
		</td>
	</tr>
	<tr>
		<th/>
		<td align="left"> 
			<html:submit property="submit" tabindex="10" onclick="setAndSubmit('add_access_point');" value="add"/>
			<html:submit property="submit" tabindex="11" onclick="setAndSubmit('cancel');" value="cancel"/>
		</td>
	</tr>	

</table>
<br>

</html:form>
</body>
</html:html>