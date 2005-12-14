<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.model.CategoryType" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="addcategory.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/addCategoryRedirect.do" focus="categoryType">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>


<html:hidden property="submitaction" value="add_category"/> 
<html:hidden property="currentpage" value="add_category"/> 
<html:hidden property="serviceId" /> 
<html:hidden property="providerId" /> 
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="addcategory.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="addcategory.type.heading"/></th>
		<td align="left">
			<html:select tabindex="6" size="1" property="categoryType">
				<html:options property="categoryTypes"/>
			</html:select>
		</td>
	</tr>
	<tr>
		<th align="right"><bean:message key="addcategory.value.heading"/></th>
		<td align="left"><html:text  tabindex="7" property="categoryValue" size="50"/>
		</td>
	</tr>
	<tr>
		<th/>
		<td align="left"> 
			<html:submit property="submit" tabindex="8" onclick="setAndSubmit('add_category');" value="add"/>
			<html:submit property="submit" tabindex="9" onclick="setAndSubmit('cancel');" value="cancel"/>
		</td>
	</tr>	

</table>
<br>

</html:form>
</body>
</html:html>