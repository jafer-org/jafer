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

package org.jafer.util.xml;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jafer.utils.xml.*;

/**
 * This class contains a number of parsing utility functions
 */
public class ParsingUtils
{

    /**
     * Stores a reference to the xpath api
     */
    private static CachedXPathAPI xPathAPI = new CachedXPathAPI();

    /**
     * Select a list of nodes
     *
     * @param node The node to process from
     * @param xPath the path to the list
     * @return the list of nodes
     * @throws JaferException
     */
    public static NodeList selectNodeList(Node node, String xPath) throws ParsingException
    {
        return selectNodeList(node, xPath, false);
    }

    /**
     * Select a list of nodes
     *
     * @param node The node to process from
     * @param xPath the path to the list
     * @param retry Used to fix cache error
     * @return the list of nodes
     * @throws JaferException
     */
    private static NodeList selectNodeList(Node node, String xPath, boolean retry) throws ParsingException
    {
        NodeList nodeList = null;
        xPathAPI = new CachedXPathAPI();
        try
        {
            nodeList = xPathAPI.selectNodeList(node, xPath);
        }
        catch (javax.xml.transform.TransformerException e)
        {
            String message = "Error selecting Nodes: " + e.getMessage();
            throw new ParsingException(message, e);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            /**
             * There appears to be a problem with some versions of
             * CachedXPathAPI which produces array out of bounds when used for a
             * long time This is an attempt to workaround - i.e. refresh the
             * Cache if this occurs
             */
            if (retry)
            {
                xPathAPI = new CachedXPathAPI();
                return selectNodeList(node, xPath, true);
            }
            else
            {
                String message = "Error selecting Nodes: " + e.getMessage();
                throw new ParsingException(message, e);
            }
        }
        return nodeList;
    }

    /**
     * Select a single node
     *
     * @param node The node to process from
     * @param xPath the XPath to select
     * @return The selected node
     * @throws JaferException
     */
    public static Node selectSingleNode(Node node, String xPath) throws ParsingException
    {
        return selectSingleNode(node, xPath, false);
    }

    /**
     * Select a single node
     *
     * @param node the node to select from
     * @param xPath the XPath to select
     * @param retry Used to fix cache error
     * @return The selected node
     * @throws JaferException
     */
    private static Node selectSingleNode(Node node, String xPath, boolean retry) throws ParsingException
    {
        Node selection = null;
        try
        {
            selection = xPathAPI.selectSingleNode(node, xPath);
        }
        catch (javax.xml.transform.TransformerException e)
        {
            String message = "Error selecting Node: " + e.getMessage();
            throw new ParsingException(message, e);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            /**
             * There appears to be a problem with some versions of
             * CachedXPathAPI which produces array out of bounds when used for a
             * long time This is an attempt to workaround - i.e. refresh the
             * Cache if this occurs
             */
            if (retry)
            {
                xPathAPI = new CachedXPathAPI();
                return selectSingleNode(node, xPath, true);
            }
            else
            {
                String message = "Error selecting Node: " + e.getMessage();
                throw new ParsingException(message, e);
            }
        }

        return selection;
    }

    /**
     * Gets the value of the node
     *
     * @param node The node to process from
     * @return The returned value
     */
    public static String getValue(Node node)
    {
        /**
         * @todo Should method throw exception instead of returning empty
         *       string??
         */
        if (node == null)
            return null;

        if (node.getNodeType() == Node.ATTRIBUTE_NODE)
            return node.getNodeValue();

        if (node.hasChildNodes() && node.getFirstChild().getNodeType() == Node.TEXT_NODE)
        {
            if (node.getFirstChild().getNodeValue() != null)
                return node.getFirstChild().getNodeValue();
            else
                return ""; //example: text node created with null content.
        }

        if (node.getNodeValue() != null)
            return node.getNodeValue();
        else
            return ""; //example: empty node.
    }

}
