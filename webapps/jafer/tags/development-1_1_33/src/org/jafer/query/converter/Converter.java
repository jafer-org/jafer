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
 */

package org.jafer.query.converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xpath.CachedXPathAPI;
import org.jafer.query.QueryException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Abstract base class that provides basic XML routines and Jafer Node processing
 */
public abstract class Converter
{
    /**
     * Stores a reference to the xpathAPI last used
     */
    private static CachedXPathAPI xPathAPI;

    /**
     * Stores a reference to the node last queried by XPath
     */
    private static Node contextNode;
    
    /**
     * Utility method to get the first c for the supplied node
     * 
     * @param sourceNode The node to process
     * @return The first node found
     * @throws QueryException
     */
    protected static Node getFirstChild(Node sourceNode) throws QueryException
    {
        // this method ignores text nodes, Node.getFirstChild doesn't.
        return selectNode(sourceNode, "./*[position()='1']");
    }

    /**
     * Utility method to get the second child for the supplied node
     * 
     * @param sourceNode The node to process
     * @return The second node found
     * @throws QueryException
     */
    protected static Node getSecondChild(Node sourceNode) throws QueryException
    {
        // this method ignores text nodes, Node.getLastChild doesn't.
        return selectNode(sourceNode, "./*[position()='2']");
    }
    
    /**
     * This method selects the node specified by the xpath from the source node
     * 
     * @param sourceNode The node to select form
     * @param XPath The XPath consition used to select the node
     * @return The node selected
     * @throws QueryException
     */
    protected static Node selectNode(Node sourceNode, String XPath) throws QueryException
    {
        try
        {
            // This code is present to reduce the number of times the XPathAPI
            // is required to be constructed when it is dealing with the same
            // data
            if (sourceNode != contextNode)
            {
                xPathAPI = new CachedXPathAPI();
                contextNode = sourceNode;
            }

            return xPathAPI.selectSingleNode(sourceNode, XPath);

        }
        catch (Exception ex)
        {
            String message = "Error in XML query format (parent: " + sourceNode.getNodeName() + " XPath: " + XPath + ")";
            Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, ex);
            throw new QueryException(message, ex);
            /** @todo throw new QueryException(message, 108, ""); */
        }
    }
    
    /**
     * This method selects the nodes specified by the xpath from the source node
     * 
     * @param sourceNode The node to select form
     * @param XPath The XPath consition used to select the nodes
     * @return The nodes selected
     * @throws QueryException
     */
    protected static NodeList selectNodeList(Node sourceNode, String XPath) throws QueryException
    {
        try
        {
            // This code is present to reduce the number of times the XPathAPI
            // is required to be constructed when it is dealing with the same
            // data if (sourceNode != contextNode)
            {
                xPathAPI = new CachedXPathAPI();
                contextNode = sourceNode;
            }

            return xPathAPI.selectNodeList(sourceNode, XPath);

        }
        catch (Exception ex)
        {
            String message = "Error in XML query format (parent: " + sourceNode.getNodeName() + " XPath: " + XPath + ")";
            Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, ex);
            throw new QueryException(message, ex);
            /** @todo throw new QueryException(message, 108, ""); */
        }
    }
    
    /**
     * Utility method to get the value of the node
     * 
     * @param node The node to extracy the value from
     * @return The string representation of the value
     * @throws QueryException
     */
    protected static String getNodeValue(Node node) throws QueryException
    {

        try
        {
            if (selectNode(node, "./text()") != null)
                return selectNode(node, "./text()").getNodeValue();
        }
        catch (QueryException ex)
        {
            String message = "Error in getting Node value: " + node.getNodeName();
            Logger.getLogger("org.jafer.query").log(Level.WARNING, message, ex);
            throw new QueryException(message);
        }

        return "";
    }
    
    /**
     * Finds the position of the NOT node from the specified node
     * 
     * @param node The node to process
     * @return The position of the NOT node - 0 if not found
     * @throws QueryException
     */
    protected static int findNotChild(Node node) throws QueryException
    {

        int position = 0;
        // first child <NOT> node, position = 1,
        // second child <NOT> node, position = 2,
        // both children <NOT> nodes, position = 3,
        // no <NOT> node, position = 0,
        try
        {
            if (getFirstChild(node).getNodeName().equalsIgnoreCase("not"))
                position = 1;
            else if (getSecondChild(node).getNodeName().equalsIgnoreCase("not"))
                return 2;
            else
                return 0;

            if (getSecondChild(node).getNodeName().equalsIgnoreCase("not"))
                position = 3;
        }
        catch (Exception ex)
        {
            String message = "Inconsistency in XML query format. (parent node: " + node.getNodeName() + ")";
            Logger.getLogger("org.jafer.query").log(Level.WARNING, message, ex);
            throw new QueryException(message, 108, "");
        }

        return position;
    }

}
