/**
 * JAFER Toolkit Project.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

/**
 *
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.interfaces;

import org.jafer.exception.JaferException;

/**
 * @todo should this interface import org.w3c.dom.Node and z3950.v3.RPNQuery, or
 *       better to more generalised? What about JaferException?
 */
// import org.w3c.dom.Node;
// import z3950.v3.RPNQuery;
/**
 * Methods for searching
 * 
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 */
public interface Search
{

    /**
     * @todo document this
     */
    // public void setSearchProfile(String searchProfile);
    /**
     * @todo document this
     */
    // public String getSearchProfile();
    /**
     * @todo document this
     */
    public void setResultSetName(String resultSetName);

    /**
     * @todo document this
     */
    public String getResultSetName();

    /**
     * Set database to search
     * 
     * @param database database
     */
    public void setDatabases(String database);

    /**
     * Set databases to search
     * 
     * @param databases databases
     */
    public void setDatabases(String[] databases);

    /**
     * Get databases currently searched
     * 
     * @return databases
     */
    public String[] getDatabases();

    /**
     * @todo document this
     */
    public void setParseQuery(boolean parseQuery);

    /**
     * @todo document this
     */
    public boolean isParseQuery();

    /**
     * Send query (can be in XML form)
     * 
     * @return number of records found
     * @todo document XML structure
     */
    public int submitQuery(Object query) throws JaferException;

    /**
     * @todo Do we need this?
     */
    public void saveQuery(String file) throws JaferException;

    /**
     * Get number of results for last query
     * 
     * @return number of results
     */
    public int getNumberOfResults();

    /**
     * Get number of results from the named database for last query
     * 
     * @return number of results
     */
    public int getNumberOfResults(String databaseName);

    /**
     * Get the last submitted query
     * 
     * @return query
     */
    public Object getQuery();

    /**
     * If a search fails this method will return the JaferException for the
     * specified database
     * 
     * @param database The name of the database to check
     * @return null if no errors were found
     * @throws JaferException
     */
    JaferException getSearchException(String database) throws JaferException;

    /**
     * If a search fails this method will return the JaferException for the
     * specified databases
     * 
     * @param databases The databases to search
     * @return An empty array if no errors were found
     * @throws JaferException
     */
    JaferException[] getSearchException(String[] databases) throws JaferException;
}
