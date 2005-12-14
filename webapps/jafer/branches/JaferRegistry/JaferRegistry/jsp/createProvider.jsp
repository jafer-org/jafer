<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="createprovider.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/createProviderRedirect.do" focus="providerName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>

<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="create_provider"/> 
<html:hidden property="currentpage" value="create_provider"/> 
<html:hidden property="providerId"/> 

<%@ include file="editProviderPanel.jsp" %>

<br>
<table border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
<tr>
	<td align="right"> 
		<html:submit property="submit" onclick="setAndSubmit('create_provider');" value="create"/>
		<html:submit property="submit" onclick="setAndSubmit('cancel');" value="cancel"/>
	</td>
</tr>	
</table>
</html:form>
</body>
</html:html>