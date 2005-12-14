<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="searchaccesspoint.title"/></title>
<html:base/>
</head>
<body>  

<html:form action="/searchAccessPointRedirect.do" focus="keyword">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>

<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="search"/> 
<html:hidden property="currentpage" value="search_access_point"/> 
<html:hidden property="serviceId"/> 

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchaccesspoint.accesspoint.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="addaccesspoint.protocol.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="6" property="protocolType" size="50"/>		
	</tr>
	<tr>
		<th align="right"><bean:message key="addaccesspoint.url.heading"/></th>
		<td align="left"><html:text  readonly="true" tabindex="7" property="accessPointUrl" size="100"/>
		</td>
	</tr>	
</table>
<br>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchaccesspoint.heading"/></td></tr>
</table>
<div style="color:#008000;">
	<bean:message key="searchaccesspoint.requiredformat"/>
</div>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="searchaccesspoint.author.heading"/></th>
		<td align="left"><html:text tabindex="8" property="author" size="50"/>
	</tr>
	<tr>
		<th align="right"><bean:message key="searchaccesspoint.title.heading"/></th>
		<td align="left"><html:text tabindex="9" property="title" size="50"/>
	</tr>	
	<tr>
		<th/>
		<td align="left">
			<html:submit property="submit" tabindex="10" onclick="setAndSubmit('search');" value="search"/>
			<html:submit property="submit" tabindex="11" onclick="setAndSubmit('cancel');" value="cancel"/>
		</td>
	</tr>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchservices.results.heading"/>
    <logic:notEmpty name="registryForm" property="modsRecords" >
	    <bean:size id="numResults" name="registryForm" property="modsRecords"/>
	    <bean:message key="searchservices.found.prefix"/> 
	    <%=numResults%> 
	        <% if (numResults.intValue() == 1) { %>
		    <bean:message key="searchservices.found.one.suffix"/> 
		    <% } else { %>
		    <bean:message key="searchservices.found.mul.suffix"/>  
		    <% } %>
    </logic:notEmpty>	
    </td>
    	<td align="right">
    		<bean:message key="search.maxresults.heading"/>
    		<bean:parameter id="maxrows" name="maxResults" value="10"/>
    		<html:text tabindex="11"  property="maxResults" size="3" maxlength="3" value="<%=maxrows%>" />
    	</td>
    </tr>
</table>

<logic:notEmpty name="registryForm" property="modsRecords" >
<table style="border: 2px solid black" width="100%">
		<tr>				
			<th align="left"><bean:message key="searchaccesspoint.results.col.type"/></th>
			<th align="left"><bean:message key="searchaccesspoint.results.col.title"/></th>
			<th align="left"><bean:message key="searchaccesspoint.results.col.author"/></th>
			<th align="left"><bean:message key="searchaccesspoint.results.col.owner"/></th>
			<th align="left"><bean:message key="searchaccesspoint.results.col.isbn"/></th>
		</tr>
	<logic:iterate id="modsRec" name="registryForm" property="modsRecords" 
					type="org.jafer.registry.web.struts.bean.ModsRecord" >
		<tr>				
			<td align="left"> <bean:write name="modsRec" property="type"/></td>
			<td align="left"> <bean:write name="modsRec" property="title"/></td>
			<td align="left"> <bean:write name="modsRec" property="author"/></td>
			<td align="left"> <bean:write name="modsRec" property="owner"/></td>
			<td align="left"> <bean:write name="modsRec" property="isbn"/></td>			
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="modsRecords" >
<br>
<p style="text-align:center"><bean:message key="searchaccesspoint.nonfound"/></p>
</logic:empty>
</html:form>
</body>
</html:html>