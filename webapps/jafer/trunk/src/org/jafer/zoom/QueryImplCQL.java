package org.jafer.zoom;

import org.z3950.zoombase.Query;
import org.z3950.zoom.ResultSet;
import org.z3950.zoom.ScanSet;
import org.z3950.zoom.SystemException;
import org.z3950.zoom.DiagnosticException;
import org.z3950.zoom.Exception;

public class QueryImplCQL extends Query {

/***
	* Implementation of Zoom Query class for CQL queries. Note that this is loaded
	* by the Zoom QueryFactory class, which looks for the class name in a file
	* called META-INF/services/org.z3950.zoom.query.CQL in the classpath.
	* 
	*/
	public Object getValue() {
		return super.getValue();
	}
	
	public void setValue(Object value) throws Exception {
		System.out.println("QueryImplCQL setValue() value is:"+value.toString());
		super.setValue(value);
	}
	
	public boolean isProcessedByClient() {
		return false;
	}
	
	public void setProcessedByClient(boolean processedByClient) {
		;
	}


}
