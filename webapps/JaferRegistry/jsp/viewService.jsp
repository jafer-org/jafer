<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.model.ServiceProviderInfo" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="viewservice.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/viewServiceRedirect.do" focus="serviceName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="view_provider"/> 
<html:hidden property="currentpage" value="view_service"/> 
<html:hidden property="providerId" /> 
<html:hidden property="serviceId" /> 
<html:hidden property="categoryType" /> 
<html:hidden property="categoryValue" />
<html:hidden property="protocolType" /> 
<html:hidden property="pointType" />

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewservice.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="viewservice.service.name.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="6" property="serviceName" size="50"/>
		</td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="viewservice.service.description.heading"/></th>
		<td align="left"><html:textarea readonly="true" cols="70" rows="3" tabindex="7" property="serviceDescription"/></td>
	</tr>

</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewservice.categories.heading"/></td></tr>
</table>
<logic:notEmpty name="registryForm" property="serviceCategories" >
<table style="border: 2px solid black" width="100%">
		<tr>	
			<th align="left"><bean:message key="viewservice.categories.type.heading"/></th>
			<th align="left"><bean:message key="viewservice.categories.value.heading"/></th>	
		</tr>
	<logic:iterate id="category" name="registryForm" property="serviceCategories" 
					type="org.jafer.registry.model.Category" >
		<tr>	
			<td width="45%" align="left"> <bean:write name="category" property="name"/></td>
			<td width="45%" align="left"> <bean:write name="category" property="value"/></td>	
			<td width="10%" align="center"> <html:submit property="submit" tabindex="8" 
				onclick="<%= "setCategoryAndAction(\'" + category.getName() + 
												   "\',\'" + category.getValue() + 
												   "\',\'remove_category\');" %>"
				value="remove"/></td>
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="serviceCategories" >
<br>
<p style="text-align:center"><bean:message key="viewservice.categories.nonfound"/></p>
</logic:empty>
<br>
<table>
<tr>
	<td align="right"> 
		<html:submit property="submit"  tabindex="9" onclick="setAndSubmit('go_add_category');" value="add category"/>
	</td>
<tr>	
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewservice.accesspoints.heading"/></td></tr>
</table>
<logic:notEmpty name="registryForm" property="serviceAccessPoints" >
<table style="border: 2px solid black" width="100%">
		<tr>	
			<th align="left"><bean:message key="viewservice.accesspoints.protocol.heading"/></th>
			<th align="left"><bean:message key="viewservice.accesspoints.type.heading"/></th>	
			<th align="left"><bean:message key="viewservice.accesspoints.url.heading"/></th>	
		</tr>
	<logic:iterate id="accessUrl" name="registryForm" property="serviceAccessPoints" 
					type="org.jafer.registry.web.struts.bean.ServiceAccessUrl" >
		<tr>	
			<td width="15%" align="left"> <bean:write name="accessUrl" property="protocol"/></td>
			<td width="15%" align="left"> <bean:write name="accessUrl" property="type"/></td>	
			<td width="50%" align="left"> <bean:write name="accessUrl" property="url"/></td>	
			<td width="10%" align="center"> <html:submit property="submit"  tabindex="10"
				onclick="<%= "setAccessPointAndAction(\'" + accessUrl.getProtocol() + 
												   "\',\'" + accessUrl.getType() + 
												   "\',\'remove_access_point\');" %>"
				value="remove"/></td>
			<td width="10%" align="center"> <html:submit property="submit"  tabindex="11"
				onclick="<%= "setAccessPointAndAction(\'" + accessUrl.getProtocol() + 
												   "\',\'" + accessUrl.getType() + 
												   "\',\'go_edit_access_point\');" %>"
				value="edit"/></td>
			<td width="10%" align="center"> <html:submit property="submit"  tabindex="12"
				disabled="<%= accessUrl.isWSDL() %>" 
				onclick="<%= "setAccessPointAndAction(\'" + accessUrl.getProtocol() + 
												   "\',\'" + accessUrl.getType() + 
												   "\',\'go_search_access_point\');" %>"
				value="search"/></td>
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="serviceAccessPoints" >
<br>
<p style="text-align:center"><bean:message key="viewservice.accesspoints.nonfound"/></p>
</logic:empty>
<br>
<table>
<tr>
	<td align="right"> 
		<html:submit property="submit"  tabindex="13" onclick="setAndSubmit('go_add_access_point');"
		 	value="add access point"/>
	</td>
<tr>	
</table>
<br>
<table border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
<tr>
	<td align="right"> 
		<html:submit property="submit" tabindex="14" onclick="setAndSubmit('view_provider');" value="view provider"/>		
		<html:submit property="submit"  tabindex="15" onclick="setAndSubmit('delete_service');" value="delete service"/>
		<html:submit property="submit"  tabindex="16" onclick="setAndSubmit('go_edit_service');" value="edit service"/>
	</td>
</tr>	
</table>
</html:form>
</body>
</html:html>