/** JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
 * University. This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jafer.zclient;

import org.jafer.exception.JaferException;

/**
 * This class represents a search result returned by a search performed on a
 * session connection to a SRW/ZClient server
 */
public class SearchResult
{

    /**
     * Stores a reference to the database name this result set is against
     */
    private String databaseName = null;

    /**
     * Stores a reference to the number of results returned from this database.
     * Default this to 0 records
     */
    private int noOfResults = 0;

    /**
     * Stores a reference to the diagnostic information returned if the search
     * failed
     */
    private JaferException diagnostic = null;

    /**
     * Return the name of the database that this result set represents
     * 
     * @return The database name
     */
    public String getDatabaseName()
    {
        return this.databaseName;
    }

    /**
     * Set the name of the database that this result set represents
     * 
     * @param databaseName The name of the database
     */
    public void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }

    /**
     * Return the number of results found for this database. <br>
     * <br>
     * <b>Note 0 results could also mean that an error occurred and hence the
     * diagnostic value should also be checked to see if it is not null in this
     * case</b>
     * 
     * @return The number of results found
     */
    public int getNoOfResults()
    {
        return this.noOfResults;
    }

    /**
     * Set the number of results found for this database
     * 
     * @param noOfResults The number of results found
     */
    public void setNoOfResults(int noOfResults)
    {
        this.noOfResults = noOfResults;
    }

    /**
     * Return the diagnostic if the search failed for this database
     * 
     * @return The diagnostic record - This can be null
     */
    public JaferException getDiagnostic()
    {
        return this.diagnostic;
    }

    /**
     * Set the diagnostic if the search failed for this database
     * 
     * @param diagnostic The diagnostic record
     */
    public void setDiagnostic(JaferException diagnostic)
    {
        this.diagnostic = diagnostic;
    }
}
