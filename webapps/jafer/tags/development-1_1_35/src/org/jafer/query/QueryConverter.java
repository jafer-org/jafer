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


/**
 * This interface defines the conversion methods that any Query type must
 * implement
 */
public interface QueryConverter
{

    /**
     * This method converts the current Query representation into a CQLQuery
     * object
     * 
     * @return A new CQLQuery
     */
    public CQLQuery toCQLQuery() throws QueryException;

    /**
     * This method converts the current Query representation into a JaferQuery
     * object
     * 
     * @return A new JaferQuery
     */
    public JaferQuery toJaferQuery() throws QueryException;

    /**
     * This method converts the current Query representation into a RPNQuery
     * object
     * 
     * @return A new RPNQuery
     */
    public RPNQuery toRPNQuery() throws QueryException;

    /**
     * This method returns a string representation of the XML for the current query
     * @return The query in XML
     * @throws QueryException
     */
    public String getXML() throws QueryException;

}
