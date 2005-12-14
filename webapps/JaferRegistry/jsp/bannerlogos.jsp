<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<table  border="0" cellpadding="2" cellspacing="2">
  <tbody>
    <tr>
      <td align="center">
      		<html:link tabindex="1" forward="oxford">
	      		<img style="width: 90px; height: 107px;" alt="Oxford University"
	      		 border="0" src=<bean:message key="banner.oxfordlogo"/>>
      		</html:link>
      </td>
      <td > 
      		<html:link tabindex="2" action="go_welcome.do">
	      		<img style="width: 379px; height: 79px;" alt="Jafer Registry "  
	      		 border="0" src=<bean:message key="banner.registrytitle"/>>
	      	</html:link>
      </td>
    </tr>
    </tbody>
</table>
