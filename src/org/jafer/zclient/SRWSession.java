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

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.interfaces.SRWPort;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.jafer.exception.JaferException;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryException;
import org.jafer.record.XMLRecord;
import org.jafer.transport.ConnectionException;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLSerializer;
import org.jafer.zclient.operations.PresentException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import z3950.v3.RPNQuery;

/**
 * This class represents a session that manages a connection against an srw
 * server
 */
public class SRWSession implements Session
{

    /**
     * Stores a reference to record packing ID for string records
     */
    private static final String RECORD_PACKING_STRING = "string";

    /**
     * Stores a reference to record packing ID for xml records
     */
    private static final String RECORD_PACKING_XML = "xml";

    /**
     * Stores a reference to the logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.zclient");

    /**
     * Stores a reference to the SRWPort that binds this session against a
     * connection to the server - Currently these are SRU Binding or SRWBinding
     * depending on the connection type supported by the server. This is
     * detected during creation of the session in the SRWClient
     */
    private SRWPort binding;

    /**
     * Stores a reference to the last query that was executed so that present
     * gets the records for that query
     */
    private String query;

    /**
     * Create the SRW session supplying the SRW or SRU binding to use
     * 
     * @param binding The binding that connectes this session to the server
     */
    public SRWSession(SRWPort binding)
    {
        this.binding = binding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#close()
     */
    public void close()
    {
        // no connection to close here
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#getGroup()
     */
    public String getGroup()
    {
        // return empty string as group is not used in SRW
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#getId()
     */
    public int getId()
    {
        // return 0 as ID is not used in SRW
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#getName()
     */
    public String getName()
    {
        // return empty string as username is not used in SRW
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#getPassword()
     */
    public String getPassword()
    {
        // return empty string as password is not used in SRW
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#getUsername()
     */
    public String getUsername()
    {
        // return empty string as username is not used in SRW
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#init(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void init(String group, String username, String password) throws ConnectionException
    {
        // all these values are ignored for an SRW connection and default values
        // are returned from the getters
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#search(java.lang.Object,
     *      java.lang.String[], java.lang.String)
     */
    public SearchResult[] search(Object queryObject, String[] databases, String resultSetName)
            throws JaferException, ConnectionException
    {
        try
        {
            logger.fine("SRWSession: Performing search");

            if (queryObject instanceof CQLQuery)
            {
                logger.fine("Convert CQLQuery Node to CQL query string");
                query = ((CQLQuery) queryObject).getCQLQuery();
            }
            else if (queryObject instanceof JaferQuery)
            {
                logger.fine("Convert JaferQuery Node to CQL query string");
                query = ((JaferQuery) queryObject).toCQLQuery().getCQLQuery();
            }
            else if (queryObject instanceof RPNQuery)
            {
                logger.fine("Convert RPN query object to CQL query string");
                org.jafer.query.RPNQuery rpnQuery = new org.jafer.query.RPNQuery(
                        (RPNQuery) queryObject);
                query = new CQLQuery(rpnQuery.toJaferQuery()).getCQLQuery();
            }
           
            else if (queryObject instanceof Node)
            {
                logger.fine("Convert JaferQuery Node to CQL query string");
                query = new CQLQuery(new JaferQuery((Node) queryObject)).getCQLQuery();
            }
            else
            {
                // bad query
                throw new QueryException("Query type: " + queryObject.getClass().getName()
                        + " not supported", 107, "");
            }

            logger.fine("Creating Search Request = version 1.1, start record = 1, max records = 0");
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(query);
            request.setStartRecord(new PositiveInteger("1"));
            request.setMaximumRecords(new NonNegativeInteger("0"));

            logger.fine("Excuting search via binding");
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            logger.fine("Processing search results");

            SearchResult result = new SearchResult();
            // database names are ignored in SRW
            result.setDatabaseName("");
            // did we get any diagnostic information returned
            if (response.getDiagnostics() != null && response.getDiagnostics().length > 0)
            {
                // store the first diagnotic object
                JaferException exc = new JaferException("Failure executing query: " + query
                        + " due to " + response.getDiagnostics()[0].getMessage());
                result.setDiagnostic(exc);
            }
            // did we get a number of results set on response
            if (response.getNumberOfRecords() != null)
            {
                result.setNoOfResults(response.getNumberOfRecords().intValue());
            }

            return new SearchResult[] { result };
        }
        catch (JaferException exc)
        {
            logger.severe(exc.getMessage());
            throw exc;
        }
        catch (RemoteException exc)
        {
            logger.severe(exc.getMessage());
            throw new ConnectionException(exc);
        }
        finally
        {
            logger.fine("SRWSession: Completed search");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#present(int, int, int[], java.lang.String,
     *      java.lang.String)
     */
    public Vector present(int nRecord, int nRecords, int[] recordOID, String eSpec,
            String resultSetName) throws PresentException, ConnectionException
    {
        logger.fine("SRWSession: Performing retrieve (present)");

        try
        {
            int recordsReturned = 0;
            Vector dataObjects = new Vector();

            // make sure query is set
            if (query != null && query.length() == 0)
            {
                logger.severe("SRWSession: No Query set");
                throw new PresentException(PresentException.STATUS_TERMINAL_FAILURE, 0,
                        "No Query set in SRWSession, need to perform a search first");
            }

            logger.fine("Creating Search Request = version 1.1, Record Packing = string, start record = "
                    + nRecord + ", max records = " + nRecords);

            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(query);
            request.setStartRecord(new PositiveInteger(Integer.toString(nRecord)));
            request.setMaximumRecords(new NonNegativeInteger(Integer.toString(nRecords)));
            request.setRecordPacking("string");

            logger.fine("Excuting retrieve via binding");
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            logger.fine("Processing search results");

            // store the number of records that were returned by the search
            if (response.getNumberOfRecords() != null)
            {
                recordsReturned = response.getNumberOfRecords().intValue();
            }

            // make sure we have record objects that can be processed
            if (response.getRecords() != null)
            {
                // stores the parsed root of the record data
                Node root = null;
                RecordType[] records = response.getRecords();
                // loop round processing returned records
                for (int index = 0; index < records.length; index++)
                {
                    // make sure all the data objects exist that we require
                    // inorder to process the record
                    if (records[index].getRecordData() != null
                            && records[index].getRecordData().get_any() != null
                            && records[index].getRecordData().get_any() != null
                            && records[index].getRecordData().get_any().length > 0
                            && records[index].getRecordData().get_any()[0].getNodeValue() != null)
                    {

                        String recordPacking = records[index].getRecordPacking();
                        if (recordPacking == null)
                        {
                            // strictly recordPacking should never be null but
                            // if it is we will assume its a string and try and
                            // continue to process the records
                            recordPacking = RECORD_PACKING_STRING;
                        }

                        // Is the record packed as a String or XML
                        if (recordPacking.equalsIgnoreCase(RECORD_PACKING_STRING))
                        {
                            logger.fine("Processing record [" + index + "] as a string");

                            // get the first node value for the data
                            String data = records[index].getRecordData().get_any()[0].getNodeValue();
                            try
                            {
                                Document doc = DOMFactory.parse(data);
                                root = doc.getDocumentElement();

                                // if fine level logging on output the xml
                                if (logger.isLoggable(Level.FINE))
                                {
                                    StringWriter writer = new StringWriter();
                                    XMLSerializer.out(root, "xml", writer);
                                    writer.flush();
                                    logger.fine("Record [" + index + "] :" + writer.toString());
                                }
                            }
                            catch (JaferException exc)
                            {
                                String msg = "Exeception Parsing Record [" + index + "]: ";
                                logger.severe(msg + exc);
                                throw new PresentException(
                                        PresentException.STATUS_TERMINAL_FAILURE, recordsReturned,
                                        msg, exc);
                            }
                        }
                        else if (recordPacking.equalsIgnoreCase(RECORD_PACKING_XML))
                        {
                            logger.fine("Processing record " + index + " as xml");
                            root = records[index].getRecordData().get_any()[0].getFirstChild();
                            // if fine level logging on output the xml
                            if (logger.isLoggable(Level.FINE))
                            {
                                try
                                {
                                    StringWriter writer = new StringWriter();
                                    XMLSerializer.out(root, "xml", writer);
                                    writer.flush();
                                    logger.fine("Record [" + index + "] :" + writer.toString());
                                }
                                catch (JaferException exc)
                                {
                                    // log but continue
                                    logger.severe(exc.getMessage());
                                }
                            }
                        }
                        else
                        {
                            String msg = "Invalid record packing value: [" + recordPacking
                                    + "] for Record: " + index;
                            logger.severe(msg);
                            throw new PresentException(PresentException.STATUS_TERMINAL_FAILURE,
                                    recordsReturned, msg);
                        }

                        logger.fine("Processing schema information for record " + index);
                        String schema = records[index].getRecordSchema();
                        if (schema != null)
                        {
                            if (!schema.equalsIgnoreCase("default"))
                            {
                                schema = org.jafer.util.Config.translateSRWSchemaName(schema);
                            }
                            else
                            {
                                schema = org.jafer.util.Config.translateSRWSchemaName(root.getNamespaceURI());
                            }
                        }
                        else
                        {
                            // ************************************************
                            // MATTHEW: WHY ARE WE NOT TRANSLATING
                            // SCHEMA HERE AS WELL
                            // ************************************************
                            schema = root.getNamespaceURI();
                        }
                        logger.fine("Creating and adding XMLRecord to return array for record "
                                + index);
                        XMLRecord record = new XMLRecord(root, schema);
                        dataObjects.add(record);
                    }
                    else
                    {
                        String msg = "Record Data not available in response for Record: " + index;
                        logger.severe(msg);
                        throw new PresentException(PresentException.STATUS_TERMINAL_FAILURE,
                                recordsReturned, msg);
                    }
                }
            }
            else
            {
                logger.warning("No records returned for query: " + query);
            }

            return dataObjects;
        }
        catch (RemoteException exc)
        {
            logger.severe(exc.getMessage());
            throw new ConnectionException(exc);
        }
        finally
        {
            logger.fine("SRWSession: Completed retrieve (present)");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#scan(java.lang.String[], int, int, int,
     *      org.w3c.dom.Node)
     */
    public Vector scan(String[] databases, int nTerms, int step, int position, Node term)
            throws JaferException, ConnectionException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.zclient.Session#scan(java.lang.String[], int, int, int,
     *      java.lang.Object)
     */
    public Vector scan(String[] databases, int nTerms, int step, int position, Object termObject)
            throws JaferException, ConnectionException
    {
        return null;
    }
}
