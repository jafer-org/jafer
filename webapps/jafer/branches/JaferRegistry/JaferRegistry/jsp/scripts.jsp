<script type="text/javascript">

 function setAndSubmit(action)
 {
 	//alert(action);
  	document.forms[0].submitaction.value=action;  
 }
 
 function setSelectedProviderIdAndAction(id,action)
 {
	document.forms[0].providerId.value=id;  	
    setAndSubmit(action);
 }
 
 function setSelectedServiceIdAndAction(id,action)
 {
	document.forms[0].serviceId.value=id;  	
    setAndSubmit(action);
 }
 
 function setCategoryAndAction(type,value,action)
 {
	document.forms[0].categoryType.value=type; 
	document.forms[0].categoryValue.value=value;   	
    setAndSubmit(action);
 }
 
 function setAccessPointAndAction(type,value,action)
 {
	document.forms[0].protocolType.value=type; 
	document.forms[0].pointType.value=value;   	
    setAndSubmit(action);
 }

</script> 