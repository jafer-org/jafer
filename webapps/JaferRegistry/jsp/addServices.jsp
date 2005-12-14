<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="addservices.title"/></title>
<html:base/>
</head>
<body>  

<html:form action="/addServicesRedirect.do" focus="serviceName">
<!-- Sneaky way to make the search button always run the predefined submit default action on enter -->
<div class="hidden">
<html:submit property="submit"/>
</div>
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="add_search"/> 
<html:hidden property="currentpage" value="add_services"/> 
<html:hidden property="serviceId" /> 
<html:hidden property="providerId" />
 
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchservices.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="searchservices.service.name.heading"/></th>
		<td align="left"><html:text tabindex="6" property="serviceName" size="50"/>
			<bean:message key="searchservices.wildcard.heading"/>
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
		<html:submit property="submit" tabindex="10" onclick="setAndSubmit('add_search');" value="search"/>
		<html:submit property="submit" tabindex="11" onclick="setAndSubmit('cancel');" value="cancel"/>
	</td>
</tr>
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="searchservices.results.heading"/>
    <logic:notEmpty name="registryForm" property="foundServices" >
	    <bean:size id="numResults" name="registryForm" property="foundServices"/>
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
    		<html:text tabindex="12"  property="maxResults" size="3" maxlength="3" value="<%=maxrows%>" />
    	</td>
    </tr>
</table>

<logic:notEmpty name="registryForm" property="foundServices" >
<table style="border: 2px solid black" width="100%">
		<tr>				
			<th align="left"><bean:message key="searchservices.results.col.name"/></th>
		</tr>
	<logic:iterate id="serviceInfo" name="registryForm" property="foundServices" 
					type="org.jafer.registry.model.ServiceInfo" >
		<tr>				
			<td width="90%" align="left"> <bean:write name="serviceInfo" property="name"/></td>
			<td width="10%" align="center"> <html:submit property="submit" tabindex="13"
				onclick="<%= "setSelectedServiceIdAndAction(\'" + serviceInfo.getId() + "\',\'add_service\');"%>"
				value="add to service provider"/></td>
		</tr>
	</logic:iterate>
</table>
</logic:notEmpty>
<logic:empty name="registryForm" property="foundServices" >
<br>
<p style="text-align:center"><bean:message key="searchservices.nonfound"/></p>
</logic:empty>
</html:form>
</body>
</html:html>