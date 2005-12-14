package org.jafer.zoom;

public class SystemException extends org.z3950.zoom.SystemException {
    public SystemException() { }
    public SystemException(String message) { super(message); }
    public String getErrorCode() { return ""; }
    public String getErrorMessage() { return getMessage(); }
    public String getAdditionalInformation() { return ""; }
}

