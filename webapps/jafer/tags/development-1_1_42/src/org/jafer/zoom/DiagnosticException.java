package org.jafer.zoom;

public class DiagnosticException extends org.z3950.zoom.DiagnosticException {
		public DiagnosticException() {  }
		public DiagnosticException(String message) { super(message); }
    public String getErrorCode() { return ""; }
    public String getErrorMessage() { return ""; }
    public String getAdditionalInformation() { return ""; }
}

