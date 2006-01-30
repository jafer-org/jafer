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

package org.jafer.query.converter;

import java.io.IOException;
import java.io.InputStream;

import org.jafer.util.XMLFileUriResolver;
import org.jafer.exception.JaferException;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryException;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLTransformer;
import org.w3c.dom.Node;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;

/**
 * This helper class contains all the methods to convert CQL queries to and from
 * Jafer Queries
 */
/**
 *
 */
public class CQLQueryConverter extends Converter
{

    /**
     * Stores a reference to the CQLParser object
     */
    private static CQLParser cqlParser = new CQLParser();

    /**
     * This method converts a cql query to the JaferQuery format
     * 
     * @param cqlQuery The cql query object
     * @return the constructed jafer query
     * @throws QueryException
     */
    public static JaferQuery convertXCQLToJafer(CQLQuery cqlQuery) throws QueryException
    {
        Node outNode = null;
        try
        {
            InputStream XcqlToJqfStylesheet = CQLQueryConverter.class.getClassLoader().getResourceAsStream(
                    "org/jafer/xsl/cql/XCQLtoJQF.xsl");
            // set the URI resolver to use when doing this transform
            XMLTransformer.setURIResoverForNewTransformers(new XMLFileUriResolver("org/jafer/xsl/cql/"));
            outNode = XMLTransformer.transform(cqlQuery.getXCQLQuery(), XcqlToJqfStylesheet);
            // set the URI resolver back to null
            XMLTransformer.setURIResoverForNewTransformers(null);
            // make sure we normalise the result as XSLT will add extra
            // textNodes when processing that may throw other XSLTs off etc
            outNode.normalize();
        }
        catch (JaferException exc)
        {
            throw new QueryException("Unable to convert to JQF: " + exc.toString(), exc);
        }
        return new JaferQuery(outNode);
    }

    /**
     * This method converts a jafer query to the CQL format
     * 
     * @param jaferQuery TThe jafer query to convert
     * @return the constructed cql query
     * @throws QueryException
     */
    public static Node convertJaferToXCQL(JaferQuery jaferQuery) throws QueryException
    {
        Node outNode = null;
        try
        {
            InputStream jqfToXcqlStylesheet = CQLQueryConverter.class.getClassLoader().getResourceAsStream(
                    "org/jafer/xsl/cql/JQFtoXCQL.xsl");
            // set the URI resolver to use when doing this transform
            XMLTransformer.setURIResoverForNewTransformers(new XMLFileUriResolver("org/jafer/xsl/cql/"));
            outNode = XMLTransformer.transform(jaferQuery.getQuery(), jqfToXcqlStylesheet);
            // set the URI resolver back to null
            XMLTransformer.setURIResoverForNewTransformers(null);
            // make sure we normalise the result as XSLT will add extra
            // textNodes when processing that may throw other XSLTs off etc
            outNode.normalize();
        }
        catch (JaferException exc)
        {
            throw new QueryException("Unable to convert to XCQL: " + exc.toString(), exc);
        }
        return outNode;
    }

    /**
     * This method converts an XCQL query to the straight CQL
     * 
     * @param xcql The root <XCQL> node
     * @return The CQL text
     * @throws QueryException
     */
    public static String convertXCQLtoCQL(CQLQuery xcql) throws QueryException
    {

        Node outNode = null;
        try
        {
            InputStream xcqlToCqlStylesheet = CQLQueryConverter.class.getClassLoader().getResourceAsStream(
                    "org/jafer/xsl/cql/XCQLtoCQL.xsl");
            outNode = XMLTransformer.transform(xcql.getXCQLQuery(), xcqlToCqlStylesheet);
            // make sure we normalise the result as XSLT will add extra
            // textNodes when processing that may throw other XSLTs off etc
            outNode.normalize();
        }
        catch (JaferException exc)
        {
            throw new QueryException("Unable to convert to XCQL: " + exc.toString(), exc);
        }
        // the returned CQL is contained in a <CQL> block so extract its value
        // to return
        return getNodeValue(outNode);
    }

    /**
     * This method converts a CQL query to XCQL
     * 
     * @param cql The cql querey to convert
     * @return The <XCQL> root node
     * @throws QueryException
     */
    public static Node convertCQLtoXCQL(String cql) throws QueryException
    {

        Node outNode = null;
        try
        {
            // create a string buffer with the <XCQL> root node
            StringBuffer buf = new StringBuffer("<XCQL>");
            // add the XCQL representing the CQL supplied switch formatting off
            // with -1
            buf.append(cqlParser.parse(cql).toXCQL(-1));
            // add the closing </XCQL> node
            buf.append("</XCQL>");
            // parse the XML into a NODE and normailise it
            outNode = DOMFactory.parse(buf.toString());
            outNode.normalize();
        }
        catch (JaferException exc)
        {
            throw new QueryException("Unable to convert to XCQL: " + exc.toString(), exc);
        }
        catch (CQLParseException exc)
        {
            throw new QueryException("Unable to parse the supplied cql: " + exc.toString(), exc);
        }
        catch (IOException exc)
        {
            throw new QueryException("Unable to parse the supplied cql:" + exc.toString(), exc);
        }
        // the returned XCQL
        return outNode;
    }

}
