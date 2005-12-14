<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ include file="bannerlogos.jsp" %>

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
  <tbody>
    <tr>
    <td align="left">
		<html:link tabindex="3" action="go_welcome.do">Home</html:link>
		<html:link tabindex="4" forward="register">Register</html:link>
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

