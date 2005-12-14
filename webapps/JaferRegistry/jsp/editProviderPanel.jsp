<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />

<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="createprovider.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="createprovider.provider.name.heading"/></th>
		<td align="left"><html:text  tabindex="6" property="providerName" size="50"/></td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="createprovider.provider.description.heading"/></th>
		<td align="left"><html:textarea cols="70" rows="3" tabindex="7" property="providerDescription"/></td>
	</tr>
	<tr>
		<th align="right"><bean:message key="createprovider.provider.homepage.heading"/></th>
		<td align="left"><html:text tabindex="8" property="providerHomepage" size="100"/></td>	
	</tr>	
</table>
<br>
<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="createprovider.contact.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="createprovider.contact.name.heading"/></th>
		<td align="left"><html:text tabindex="9" property="contactName" size="50"/></td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="createprovider.contact.description.heading"/></th>
		<td align="left"><html:textarea  cols="70" rows="2" tabindex="10" property="contactDescription" /></td>	
	</tr>
	<tr>
		<th align="right"><bean:message key="createprovider.contact.phone.heading"/></th>
		<td align="left"><html:text  tabindex="11" property="contactPhone" size="50"/></td>	
	</tr>
	<tr>
		<th align="right"><bean:message key="createprovider.contact.email.heading"/></th>
		<td align="left"><html:text tabindex="12" property="contactEmail" size="50"/></td>	
	</tr>
</table>
