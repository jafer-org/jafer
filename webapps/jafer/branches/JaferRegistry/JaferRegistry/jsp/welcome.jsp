<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<html:html locale="true">
<head>
<title><bean:message key="welcome.title"/></title>
<html:base/>
</head>
<body> 

<html:form action="/welcomeRedirect.do">
<%@ include file="scripts.jsp" %>
<%@ include file="banner.jsp" %>

<html:hidden property="submitaction" value="error"/> 
<html:hidden property="currentpage" value="welcome"/> 

<h2 style="text-align:center">
	<bean:message key="welcome.heading"/><br>
	<bean:message key="welcome.jafer.heading"/>
		<html:link tabindex="6" forward="jafer">here.</html:link>
</h2>

<table  border="0" cellpadding="2" cellspacing="2">
  <tbody>
    <tr>
      <td align="center">
	      <html:link tabindex="7" forward="register">
	      		<img style="width: 175px; height: 78px;" border="0" alt="Microsoft UDDI (Register)"  
	      		src=<bean:message key="welcome.uddi.logo"/>>	      		
	      </html:link>
      </td>
      <td align="left" > 
      	  <bean:message key="welcome.microsoft.register"/>
    </tr>
    </tbody>
</table>

<table  border="0" cellpadding="2" cellspacing="2">
  <tbody>
  	<tr>
      <td width="50%" align="center">
      		<html:link tabindex="8" action="/go_search_providers.do" >
	      		<img style="width: 358px; height: 300px;" border="0" alt="Manage Providers"  
	      		src=<bean:message key="welcome.providers.logo"/>>	      		
	      </html:link>	
      </td>
      <td width="50%" align="center">
      		<html:link tabindex="9" action="/go_search_services.do">
	      		<img style="width: 358px; height: 300px;" border="0" alt="Manage Services"  
	      		src=<bean:message key="welcome.services.logo"/>>	      		
	      </html:link>		     
      </td>
    </tr>
    <tr>
      <td width="50%" align="center">
      	<html:link tabindex="10" action="/go_search_providers.do">
      		<bean:message key="welcome.providers.title"/>
      	</html:link>	
      </td>
      <td width="50%"  align="center">
      	<html:link tabindex="11" action="/go_search_services.do" >
      		<bean:message key="welcome.services.title"/>	
      	</html:link>	     
      </td>
    </tr>    
    </tbody>
</table>

 

</html:form>

</body>
</html:html>