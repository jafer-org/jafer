<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<link rel="stylesheet" type="text/css" href="css/registry-styles.css" title="JAFER Registry" />



<table  border="0" cellpadding="2" cellspacing="2" bgcolor="99CCCC">
    <tr><td align="left"><bean:message key="viewprovider.heading"/></td></tr>
</table>
<br>
<table border="0" width="100%">
	<tr>
		<th align="right"><bean:message key="viewprovider.provider.name.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="6" property="providerName" size="50"/></td>
	</tr>
	<tr>
		<th align="right" style="vertical-align:top"><bean:message key="viewprovider.provider.description.heading"/></th>
		<td align="left"><html:textarea readonly="true" cols="70" rows="3" tabindex="7" property="providerDescription"/></td>
	</tr>
	<tr>
		<th align="right"><bean:message key="viewprovider.provider.homepage.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="8" property="providerHomepage" size="100"/></td>	
	</tr>
	<tr>
		<th align="right"><bean:message key="viewprovider.provider.numservices.heading"/></th>
		<td align="left"><html:text readonly="true" tabindex="9" property="numberofProviderServices" size="3"/></td>	
	</tr>
</table>

