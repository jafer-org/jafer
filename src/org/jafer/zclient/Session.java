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

import java.util.Vector;

import org.jafer.exception.JaferException;
import org.jafer.transport.ConnectionException;
import org.jafer.zclient.operations.PresentException;
import org.w3c.dom.Node;

/**
 * This interface defines the methods that a session handler should support. It
 * is used by the abstract client to search and retrieve data from the various
 * session types currently SRW / ZClients
 */
public interface Session
{

    /**
     * This method closes the current connection and any underlying binding
     * classes
     */
    public void close();

    /**
     * Returns the group for this session. TODO NEED MORE DETAIL HERE
     * 
     * @return The string representation of the group for this session.
     */
    public String getGroup();

    /**
     * Return the session Identifier
     * 
     * @return The session identifier
     */
    public int getId();

    /**
     * Return the name allocatted to this session
     * 
     * @return The name of this session
     */
    public String getName();

    /**
     * Return the password for the user connecting via this session
     * 
     * @return The users password for the connection
     */
    public String getPassword();

    /**
     * Return the username for the user connecting via this session
     * 
     * @return The users username for the connection
     */
    public String getUsername();

    /**
     * Initalise the session
     * 
     * @param group The group name for this session
     * @param username The username of the user connecting via this session
     * @param password The password of the user connecting via this session
     * @throws ConnectionException
     */
    public void init(String group, String username, String password) throws ConnectionException;

    /**
     * Obtain the requested records via the sessions connection to the server
     * and return them to the caller
     * 
     * @param nRecord The index of the record that the retrieve should start at
     * @param nRecords The maximum number of records that should be returned
     * @param recordOID The record object identifier
     * @param eSpec The element spec attribute of the ZClient
     * @param resultSetName The result set name
     * @return A Vector of DataObject returned from the server
     * @throws PresentException
     * @throws ConnectionException
     */
    public Vector present(int nRecord, int nRecords, int[] recordOID, String eSpec, String resultSetName)
            throws PresentException, ConnectionException;

    /**
     * Performs a scan on the sessions connection to the sever to retrieve a
     * list of related terms
     * 
     * @param databases An array of database names that are being scanned
     * @param nTerms The maximum number of terms to return
     * @param step ?????????????????????????????????????????????????????????
     * @param position ?????????????????????????????????????????????????????
     * @param term ?????????????????????????????????????????????????????????
     * @return A vector of DataObjects representing the terms found
     * @throws JaferException
     * @throws ConnectionException
     */
    public Vector scan(String[] databases, int nTerms, int step, int position, Node term) throws JaferException,
            ConnectionException;

    /**
     * Performs a scan on the sessions connection to the sever to retrieve a
     * list of related terms
     * 
     * @param databases An array of database names that are being scanned
     * @param nTerms The maximum number of terms to return
     * @param step ?????????????????????????????????????????????????????????
     * @param position ?????????????????????????????????????????????????????
     * @param termObject ???????????????????????????????????????????????????
     * @return A vector of DataObjects representing the terms found
     * @throws JaferException
     * @throws ConnectionException
     */
    public Vector scan(String[] databases, int nTerms, int step, int position, Object termObject) throws JaferException,
            ConnectionException;

    /**
     * Performs a search on the sessions connection to the sever to work out how
     * many results would be returned for the search query
     * 
     * @param queryObject The query object to search against. Normally RPNNode
     *        or a simple Node that represents a JaferQuery
     * @param databases The list of databases to be searched
     * @param resultSetName The name to be given to the result set
     * @return An array of search result objects that identify the number of
     *         results for each database searched
     * @throws JaferException
     * @throws ConnectionException
     */
    public SearchResult[] search(Object queryObject, String[] databases, String resultSetName) throws JaferException,
            ConnectionException;

}
