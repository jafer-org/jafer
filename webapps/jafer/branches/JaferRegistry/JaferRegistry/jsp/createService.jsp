<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="createservice.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/createServiceRedirect.do" focus="serviceName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>

<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="create_service"/> 
<html:hidden property="currentpage" value="create_service"/>
<html:hidden property="providerId"/>
<html:hidden property="serviceId"/>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="createservice.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="viewservice.service.name.heading"/></th>
		<td align="left"><html:text  tabindex="6" property="serviceName" size="50"/></td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="viewservice.service.description.heading"/></th>
		<td align="left"><html:textarea  cols="70" rows="3" tabindex="7" property="serviceDescription"/></td>
	</tr>
	<tr>
		<th/>
		<td align="left">
			<html:submit property="submit" tabindex="8" onclick="setAndSubmit('create_service');" value="create"/>
			<html:submit property="submit" tabindex="9" onclick="setAndSubmit('cancel');" value="cancel"/>
		</td>
	</tr>
</table>

</html:form>
</body>
</html:html>