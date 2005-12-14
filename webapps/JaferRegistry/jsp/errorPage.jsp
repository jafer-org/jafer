<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.apache.struts.Globals" %>

<html:html locale="true">
<head>
<title><bean:message key="errorPage.title"/></title>
<html:base/>
</head>
<body> 

<%@ include file="bannerlogos.jsp" %>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr>
    <td align="left">
		<html:link action="go_welcome.do">Home</html:link>
	</tr>
    </tbody>
</table>
<h3><bean:message key="errorPage.overview"/></h3>

<logic:present name="<%=Globals.EXCEPTION_KEY%>">
<p><bean:message key="errorPage.registryException"/><p>
<br><bean:message key="errorPage.errorNumber"/><bean:write name="<%=Globals.EXCEPTION_KEY%>" property="errorNumber"/>
<br><bean:message key="errorPage.errorCode"/><bean:write name="<%=Globals.EXCEPTION_KEY%>" property="errorCode"/>
<br>
<br>
<bean:write name="<%=Globals.EXCEPTION_KEY%>" property="message"/>
 (<bean:write name="<%=Globals.EXCEPTION_KEY%>" property="errorText"/>)
</logic:present>

<logic:notPresent name="<%=Globals.EXCEPTION_KEY%>">
<html:errors />
</logic:notPresent >

</body>
</html:html>