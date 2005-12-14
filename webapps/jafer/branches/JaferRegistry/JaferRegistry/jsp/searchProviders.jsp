<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.model.ServiceProviderInfo" %>


<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="searchproviders.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/searchProviderRedirect.do" focus="providerName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
			
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="search"/> 
<html:hidden property="currentpage" value="search_providers"/> 
<html:hidden property="providerId" value=""/> 


<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchproviders.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="searchproviders.provider.name.heading"/></th>
		<td align="left"><html:text tabindex="6" property="providerName" size="50"/>
			<bean:message key="searchproviders.wildcard.heading"/>
		</td>
	</tr>
	<tr>
		<th align="right"><bean:message key="search.protocol.heading"/></th>
		<td align="left">
			<html:select tabindex="7" size="1" property="protocolType">
				<html:options property="protocolTypes"/>
			</html:select>
		</td>
	</tr>
	<tr>
		<th align="right"><bean:message key="search.type.heading"/></th>
		<td align="left">
			<html:select tabindex="8" size="1" property="categoryType">
				<html:options property="categoryTypes"/>
			</html:select>
		</td>
	</tr>
	<tr>
		<th align="right"><bean:message key="search.value.heading"/></th>
		<td align="left"><html:text  tabindex="9" property="categoryValue" size="50"/>
		</td>
	</tr>
	<tr>
		<th/>
		<td align="left">
			<html:submit property="submit" tabindex="10" onclick="setAndSubmit('search');" value="search"/>
			<html:submit property="submit" tabindex="11" onclick="setAndSubmit('go_create_provider');" value="create"/>
		</td>
	</tr>
</table>
<br>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchproviders.results.heading"/> 
    <logic:notEmpty name="registryForm" property="foundProviders" >
	    <bean:size id="numResults" name="registryForm" property="foundProviders"/>
	    <bean:message key="searchproviders.found.prefix"/> 
		    <%=numResults%> 
		    <% if (numResults.intValue() == 1) { %>
		    <bean:message key="searchproviders.found.one.suffix"/> 
		    <% } else { %>
		    <bean:message key="searchproviders.found.mul.suffix"/>  
		    <% } %>
    </logic:notEmpty>	
    </td>
    	<td align="right">
    		<bean:message key="search.maxresults.heading"/>
    		<bean:parameter id="maxrows" name="maxResults" value="10"/>
    		<html:text tabindex="12"  property="maxResults" size="3" maxlength="3" value="<%=maxrows%>" />
    	</td>
    </tr>
</table>
<logic:notEmpty name="registryForm" property="foundProviders" >
<table style="border: 2px solid black" width="100%">
		<tr>	
			<th align="left"><bean:message key="searchproviders.results.col.name"/></th>
			<th align="left"><bean:message key="searchproviders.results.col.description"/></th>	
		</tr>
	<logic:iterate id="providerInfo" name="registryForm" property="foundProviders" 
					type="org.jafer.registry.model.ServiceProviderInfo" >
		<tr>	
			<td width="45%" align="left"> <bean:write name="providerInfo" property="name"/></td>
			<td width="45%" align="left"> <bean:write name="providerInfo" property="description"/></td>	
			<td width="10%" align="center"> <html:submit property="submit" 
				onclick="<%= "setSelectedProviderIdAndAction(\'" + providerInfo.getId() + "\',\'view_provider\');"%>"
				value="view"/></td>
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="foundProviders" >
<br>
<p style="text-align:center"><bean:message key="searchproviders.nonfound"/></p>
</logic:empty>

</html:form>
</body>
</html:html>