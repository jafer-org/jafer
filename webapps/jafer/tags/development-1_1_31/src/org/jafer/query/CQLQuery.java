/**
 * JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
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

package org.jafer.query;

import java.io.StringWriter;

import org.jafer.exception.JaferException;
import org.jafer.query.converter.CQLQueryConverter;
import org.jafer.util.xml.XMLSerializer;
import org.w3c.dom.Node;

/**
 * This class wraps a CQL Query
 */
public class CQLQuery implements QueryConverter
{

    /**
     * Stores a reference to the root of the cql query
     */
    protected Node queryRoot = null;

    /**
     * Stores a reference to the base cql for the XCQL query root
     */
    protected String cql = "";

    /**
     * constructor
     * 
     * @param jaferQuery the jafer query to construct from
     * @throws QueryException
     */
    public CQLQuery(JaferQuery jaferQuery) throws QueryException
    {
        // we do both transformation whether required or not so that if
        // construction succeeds the user knows they have valid XCQL and CQL.
        // Alternative is on demand conversion but then the user would need to
        // handle invalid CQL where ever they reference object
        queryRoot = CQLQueryConverter.convertJaferToXCQL(jaferQuery);
        cql = CQLQueryConverter.convertXCQLtoCQL(this);
    }
    
    /**
     * constructor
     * 
     * @param xcql the XCQL root node
     * @throws QueryException
     */
    public CQLQuery(Node xcql) throws QueryException
    {
        queryRoot = xcql;
        cql = CQLQueryConverter.convertXCQLtoCQL(this);
    }
    
    /**
     * constructor
     * 
     * @param cql the cql as a string
     * @throws QueryException
     */
    public CQLQuery(String cql) throws QueryException
    {
        queryRoot = CQLQueryConverter.convertCQLtoXCQL(cql);
        this.cql = cql;
    }

    /**
     * Get the constructed query object as XCQL
     * 
     * @return the root node of the cql query
     */
    public Node getXCQLQuery()
    {
        return queryRoot;
    }

    /**
     * Get the constructed query object as CQL
     * 
     * @return the cql query as straight CQL
     * @throws QueryException
     */
    public String getCQLQuery() throws QueryException
    {
        return cql;
    }

    /**
     * This method converts the current Query representation into a CQLQuery
     * object
     * 
     * @return A new CQLQuery
     */
    public CQLQuery toCQLQuery()
    {
        // this is already a CQLQuery so just return it
        return this;
    }

    /**
     * This method converts the current Query representation into a JaferQuery
     * object
     * 
     * @throws QueryException
     * @return A new JaferQuery
     */
    public JaferQuery toJaferQuery() throws QueryException
    {
        return CQLQueryConverter.convertXCQLToJafer(this);
    }

    /**
     * This method converts the current Query representation into a RPNQuery
     * object
     * 
     * @return A new RPNQuery
     * @throws QueryException
     */
    public RPNQuery toRPNQuery() throws QueryException
    {
        return toJaferQuery().toRPNQuery();
    }

    /**
     * This method returns a string representation of the XML for the current
     * query
     * 
     * @return The query in XML
     * @throws QueryException
     */
    public String getXML() throws QueryException
    {
        try
        {
            StringWriter writer = new StringWriter();
            XMLSerializer.out(queryRoot, "xml", writer);
            writer.flush();
            return writer.toString();
        }
        catch (JaferException e)
        {
            throw new QueryException("Unable to convert XML to string: ", e);
        }
    }
}