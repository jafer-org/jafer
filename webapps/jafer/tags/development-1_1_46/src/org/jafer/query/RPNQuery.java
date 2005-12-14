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

import org.jafer.query.converter.RPNQueryConverter;

/**
 * This class wraps a RPN Query
 */
public class RPNQuery implements QueryConverter
{

    /**
     * Stores a reference to the Z3950 RPNQuery
     */
    protected z3950.v3.RPNQuery query = null;

    /**
     * Default constructor
     */
    public RPNQuery()
    {
        query = null;
    }

    /**
     * constructor
     * 
     * @param the Z3950 RPNQuery
     */
    public RPNQuery(z3950.v3.RPNQuery rpnQuery)
    {
        query = rpnQuery;
    }

    /**
     * constructor
     * 
     * @param the jafer query to construct from
     * @throws QueryException
     */
    public RPNQuery(JaferQuery jaferQuery) throws QueryException
    {
        query = RPNQueryConverter.convertJaferToRPN(jaferQuery);
    }

    /**
     * Get the constructed query object
     * 
     * @return the Z3950 RPNQuery
     */
    public z3950.v3.RPNQuery getQuery()
    {
        return query;
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
        // convert to jafer and then to CQL
        return toJaferQuery().toCQLQuery();
    }

    /**
     * This method converts the current Query representation into a JaferQuery
     * object
     * 
     * @return A new JaferQuery
     * @throws QueryException
     */
    public JaferQuery toJaferQuery() throws QueryException
    {
        return RPNQueryConverter.convertRPNToJafer(query);
    }

    /**
     * This method converts the current Query representation into a RPNQuery
     * object
     * 
     * @return A new RPNQuery
     */
    public RPNQuery toRPNQuery()
    {
        // this is already a RPNQuery so just return it
        return this;
    }

    /**
     * This method returns a string representation of the XML for the current
     * query. As RPN are not in XML this returns the jafer version of the query
     * 
     * @return The query in XML
     * @throws QueryException
     */
    public String getXML() throws QueryException
    {
        // this format does not have xml so convert to jafer first
        return toJaferQuery().getXML();
    }
}