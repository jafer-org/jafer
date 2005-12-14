<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.jafer.registry.web.struts.SessionKeys" %>

<%@ include file="bannerlogos.jsp" %>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr>
    <td align="left">
		<html:link tabindex="3" action="go_welcome.do">Home</html:link>
		<html:link tabindex="4" forward="register">Register</html:link>
		<logic:present name="<%=SessionKeys.USERNAME%>" scope="session">
			<html:submit tabindex="5" property="submit" onclick="setAndSubmit('go_logoff');">Logoff</html:submit>
		</logic:present>
		<logic:notPresent name="<%=SessionKeys.USERNAME%>" scope="session">
			<html:submit tabindex="5" property="submit" onclick="setAndSubmit('go_logon');">Logon</html:submit>
		</logic:notPresent>
		 
	</td>
	<td align="right">
		<logic:present name="<%=SessionKeys.USERNAME%>" scope="session">
			Logged in as: <bean:write name="<%=SessionKeys.USERNAME%>"/>
		</logic:present>
		<logic:notPresent name="<%=SessionKeys.USERNAME%>" scope="session">
			Logged in as: Guest
		</logic:notPresent>
	</td>
	</tr>
    </tbody>
</table>
<br>
<div style="color:#FF0000;">
<html:errors />
</div>
<logic:messagesPresent message="true"> 
<div style="color:#008000;">
<bean:message key="message.header"/>
	 <html:messages id="message" message="true"> 
	 <bean:message key="message.prefix"/>
	 	<bean:write name="message"/>
	 <bean:message key="message.suffix"/>
	 </html:messages> 
<bean:message key="message.footer"/>
</div>
</logic:messagesPresent>
