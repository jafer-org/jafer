<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.model.ServiceProviderInfo" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="viewprovider.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/viewProviderRedirect.do" focus="providerName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="view_provider_services"/> 
<html:hidden property="currentpage" value="view_provider"/> 
<html:hidden property="providerId" /> 
<html:hidden property="categoryType" /> 
<html:hidden property="categoryValue" />
 
<%@ include file="providerPanel.jsp" %>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewprovider.contact.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="viewprovider.contact.name.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="10" property="contactName" size="50"/></td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="viewprovider.contact.description.heading"/></th>
		<td align="left"><html:textarea readonly="true" cols="70" rows="2" tabindex="11" property="contactDescription" /></td>	
	</tr>
	<tr>
		<th align="right"><bean:message key="viewprovider.contact.phone.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="12" property="contactPhone" size="50"/></td>	
	</tr>
	<tr>
		<th align="right"><bean:message key="viewprovider.contact.email.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="13" property="contactEmail" size="50"/></td>	
	</tr>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewprovider.categories.heading"/></td></tr>
</table>
<logic:notEmpty name="registryForm" property="providerCategories" >
<table style="border: 2px solid black" width="100%">
		<tr>	
			<th align="left"><bean:message key="viewprovider.categories.type.heading"/></th>
			<th align="left"><bean:message key="viewprovider.categories.value.heading"/></th>	
		</tr>
	<logic:iterate id="category" name="registryForm" property="providerCategories" 
					type="org.jafer.registry.model.Category" >
		<tr>	
			<td width="45%" align="left"> <bean:write name="category" property="name"/></td>
			<td width="45%" align="left"> <bean:write name="category" property="value"/></td>	
			<td width="10%" align="center"> <html:submit tabindex="14" property="submit" 
				onclick="<%= "setCategoryAndAction(\'" + category.getName() + 
												   "\',\'" + category.getValue() + 
												   "\',\'remove_category\');" %>"
				value="remove"/></td>
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="providerCategories" >
	<br>
	<p style="text-align:center"><bean:message key="viewprovider.categories.nonfound"/></p>
</logic:empty>
<br>
<table>
<tr>
	<td align="right"> 
		<html:submit property="submit" tabindex="15" onclick="setAndSubmit('go_add_category');" value="add category"/>
	</td>
<tr>	
</table>
<br>
<table border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
<tr>
	<td align="right"> 
		<html:submit property="submit" tabindex="16" onclick="setAndSubmit('view_provider_services');" value="view services"/>
		<html:submit property="submit" tabindex="17" onclick="setAndSubmit('delete_provider');" value="delete provider"/>
		<html:submit property="submit" tabindex="18" onclick="setAndSubmit('go_edit_provider');" value="edit provider"/>
	</td>
</tr>	
</table>
</html:form>
</body>
</html:html>