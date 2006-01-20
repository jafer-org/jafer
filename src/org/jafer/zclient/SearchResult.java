package org.jafer.zclient;

import org.jafer.exception.JaferException;

public class SearchResult {
    private String databaseName = null;
    private int noOfResults = 0;
    private JaferException diagnostic = null;

    public String getDatabaseName() {
        return this.databaseName;
    }
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    public int getNoOfResults() {
        return this.noOfResults;
    }
    public void setNoOfResults(int noOfResults) {
        this.noOfResults = noOfResults;
    }
    public JaferException getDiagnostic() {
        return this.diagnostic;
    }
    public void setDiagnostic(JaferException diagnostic) {
        this.diagnostic = diagnostic;
    }
}
