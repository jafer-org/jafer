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
package org.jafer.sru;

import java.io.InputStream;

import org.apache.xpath.CachedXPathAPI;
import org.jafer.exception.JaferException;
import org.jafer.query.converter.CQLQueryConverter;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This class manages the parsing and searching of the SRUtoSRWConfiguration
 * file
 */
public class SRUtoSRWConfig
{

    /**
     * Stores a reference to the xpathAPI last used
     */
    private CachedXPathAPI xPathAPI;

    /**
     * Stores a reference to the node last queried by XPath
     */
    private Node contextNode;

    /**
     * Stores a reference to root node of the config file
     */
    Node configRoot = null;

    /**
     * constructor
     * 
     * @throws SRUException
     */
    public SRUtoSRWConfig() throws SRUException
    {
        try
        {
            InputStream configXML = CQLQueryConverter.class.getClassLoader().getResourceAsStream(
                    "org/jafer/sru/SRUtoSRWBridgeConfig.xml");
            configRoot = (Node) DOMFactory.parse(configXML);
            configRoot.normalize();
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            throw new SRUException("unable to load bridge configuration", exc);
        }
    }

    /**
     * Gets the name of the bridge class for the specified operation and version
     * @param operation The operation name
     * @param version The version of the operation
     * @return The bridge class or NULL if not found
     * @throws SRUException
     */
    public String getOperationBridgeClass(String operation, String version) throws SRUException
    {
        Node bridge = selectNode(configRoot, "/operations/operation[@name='" + operation + "' and @version='" + version
                + "']/@bridge");
        if (bridge == null)
        {
            return null;
        }
        return (getNodeValue(bridge));
    }

    /**
     * This method selects the node specified by the xpath from the source node
     * 
     * @param sourceNode The node to select form
     * @param XPath The XPath consition used to select the node
     * @return The node selected
     * @throws SRUException
     */
    public Node selectNode(Node sourceNode, String XPath) throws SRUException
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
        catch (Exception exc)
        {
            String message = "Error in XML query format (parent: " + sourceNode.getNodeName() + " XPath: " + XPath + ")";
            throw new SRUException(message, exc);

        }
    }

    /**
     * Utility method to get the value of the node
     * 
     * @param node The node to extracy the value from
     * @return The string representation of the value
     * @throws SRUException
     */
    public String getNodeValue(Node node) throws SRUException
    {

        try
        {
            if (node != null)
            {
                if (node.getNodeType() == Node.ATTRIBUTE_NODE)
                {
                    return node.getNodeValue();
                }
                if (selectNode(node, "./text()") != null)
                {
                    return selectNode(node, "./text()").getNodeValue();
                }
            }
        }
        catch (Exception exc)
        {
            String message = "Error in getting Node value: " + node.getNodeName();
            throw new SRUException(message);
        }

        return "";
    }

}
