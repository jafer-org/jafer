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
import org.jafer.query.converter.JaferQueryConverter;
import org.jafer.util.xml.XMLSerializer;
import org.w3c.dom.Node;

/**
 * This class wraps a Jafer XML Query that can be constructed by the Query
 * Builder. It is wrapped in this class so that code can be clear when it is
 * dealing with a jafer query rather than a standard xml NODE.
 */
/**
 * 
 */
public class JaferQuery implements QueryConverter
{

    /**
     * Stores a reference to the root of the jafer query
     */
    protected Node queryRoot = null;

    /**
     * Protected empty constructor to allow test utilities to create query
     * without performing normalisation
     */
    protected JaferQuery()
    {

    }

    /**
     * Constructor that forbids a NOT clause at the start of the query to avoid
     * conversion errors when translating to a query language that does not
     * support UNARY not clauses.
     * 
     * @param query A root node element in the format of a JaferQuery
     * @throws QueryException
     */
    public JaferQuery(Node query) throws QueryException
    {
        initialise(query, false);
    }

    /**
     * Constructor that optionally allows a NOT clause at the start of the query
     * 
     * @param query A root node element in the format of a JaferQuery
     * @param allowTopLevelNot If true the JaferQuery is allowed to be created
     *        when the normalisation process completes with a NOT at the top
     *        level.
     * @throws QueryException
     */
    public JaferQuery(Node query, boolean allowTopLevelNot) throws QueryException
    {
        initialise(query, allowTopLevelNot);
    }

    /**
     * Initialises the JaferQuery by normalising the query and optionally
     * allowing a NOT clause at the start of the query. If NOT is allowed then
     * this means that if its translated to a query that does not support unary
     * NOTs then it will either fail conversion or an extra entry of all records
     * will be added
     * 
     * @param query A root node element in the format of a JaferQuery
     * @param allowTopLevelNot If true the JaferQuery is allowed to be created
     *        when the normalisation process completes with a NOT at the top
     *        level.
     * @throws QueryException
     */
    private void initialise(Node query, boolean allowTopLevelNot) throws QueryException
    {
        // normalise the query
        queryRoot = JaferQueryConverter.normaliseJaferQuery(query);
        // check for top level not
        if (allowTopLevelNot == false && queryRoot.getNodeName().equalsIgnoreCase("not"))
        {
            // Throw an error as the query has a top level not that was not
            // allowed in this construction call
            throw new QueryException("The query can not be constructed due to a top level not."
                    + " Call JaferQuery(query,true) to overide");
        }
    }

    /**
     * Get the constructed query object
     * 
     * @return the root node of the jafer query
     */
    public Node getQuery()
    {
        return queryRoot;
    }

    /**
     * This method converts the current Query representation into a CQLQuery
     * object
     * 
     * @return A new CQLQuery
     * @throws QueryException
     */
    public CQLQuery toCQLQuery() throws QueryException
    {
        return new CQLQuery(this);
    }

    /**
     * This method converts the current Query representation into a JaferQuery
     * object
     * 
     * @return A new JaferQuery
     */
    public JaferQuery toJaferQuery()
    {
        // this is already a JaferQuery so just return it
        return this;
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
        return new RPNQuery(this);
    }

    /**
     * This method returns a string representation of the XML for the current
     * query
     * 
     * @return The query in XML
     * @throws JaferException
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
