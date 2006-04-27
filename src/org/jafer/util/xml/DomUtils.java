/**
 * Jafer Poject.
 * Copyright (C) 2005, Jafer Project, Oxford University.
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
 *
 */

package org.jafer.util.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.templates.OutputProperties;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DomUtils
{

    private static DocumentBuilderFactory factory;
    static
    {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
    }

    /**
     * Retrieves text of element by attribute value (e.g. <identifier
     * type="issn">xxx </identifier>).
     */
    public static String getElementTextByAttr(Element modsroot, String nodename, String attrname, String attrvalue)
    {
        String str = "";
        NodeList list = modsroot.getElementsByTagName(nodename);
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = (Node) list.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Attr attr = (Attr) attrs.getNamedItem(attrname);
            if (attr != null)
            {
                if (attr.getValue().equals(attrvalue))
                {
                    str = getTextValue(node);
                    break;
                }
            }
        }
        return str;
    }

    public static boolean hasElementWithAttr(Element modsroot, String nodename, String attrname, String attrvalue)
    {
        boolean found = false;
        NodeList list = modsroot.getElementsByTagName(nodename);
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = (Node) list.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Attr attr = (Attr) attrs.getNamedItem(attrname);
            if (attr != null)
            {
                if (attr.getValue().equals(attrvalue))
                {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Sets text of element by attribute value (e.g. <identifier type="issn">xxx
     * </identifier>).
     */
    public static void setElementTextByAttr(Element modsroot, String nodename, String attrname, String attrvalue, String newValue)
    {
        NodeList list = modsroot.getElementsByTagName(nodename);
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = (Node) list.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Attr attr = (Attr) attrs.getNamedItem(attrname);
            if (attr != null)
            {
                if (attr.getValue().equals(attrvalue))
                {
                    setTextValue(node, newValue);
                    break;
                }
            }
        }
    }

    /**
     * Retrieves name/subname text value.
     */
    public static String getSubNodeText(Element modsroot, String nodename, String subnodename)
    {
        StringBuffer sb = new StringBuffer("");
        NodeList list = modsroot.getElementsByTagName(nodename);
        if (list.getLength() > 0)
        {
            Element node = (Element) list.item(0);
            NodeList l = node.getElementsByTagName(subnodename);
            if (l.getLength() > 0)
                sb.append(getTextValue(l.item(0)));
        }
        return sb.toString();
    }

    /**
     * Retrieves name/subname/subsub text value.
     */
    public static String getSubSubNodeText(Element modsroot, String nodename, String subnodename, String subsubnodename)
    {
        StringBuffer sb = new StringBuffer("");
        NodeList list = modsroot.getElementsByTagName(nodename);
        if (list.getLength() > 0)
        {
            Element node = (Element) list.item(0);
            NodeList l = node.getElementsByTagName(subnodename);
            if (l.getLength() > 0)
            {
                Element node1 = (Element) list.item(0);
                NodeList l1 = node1.getElementsByTagName(subsubnodename);
                if (l1.getLength() > 0)
                {
                    sb.append(getTextValue(l.item(0)));
                }
            }
        }
        return sb.toString();
    }

    /**
     * Extracts all text values from a list of Element nodes. Appends them with
     * '\n' preceding
     */
    public static String getTextFields(NodeList list)
    {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = (Node) list.item(i);
            if (i > 0)
                sb.append("\n");
            sb.append(getTextValue(node));
        }
        return sb.toString();
    }

    /**
     * Get the text node from an element node.
     */
    public static String getTextValue(Node node)
    {
        StringBuffer sb = new StringBuffer("");
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            if (list.item(i).getNodeType() == Node.TEXT_NODE)
            {
                sb.append(list.item(i).getNodeValue());
            }
        }
        //System.out.println("returning:"+sb.toString());
        return sb.toString();
    }
    
    /**
     * set the text node from an element node.
     */
    public static void setTextValue(Node node,String newValue)
    {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            if (list.item(i).getNodeType() == Node.TEXT_NODE)
            {
                list.item(i).setNodeValue(newValue);
            }
        }
    }

    /**
     * Get the text node from an element node.
     */

    public static String getChildTextValue(Element node, String child)
    {
        NodeList list = node.getElementsByTagName(child);
        if (list.getLength() > 0)
        {
            Node nd = list.item(0);
            return getTextValue(nd);
        }
        return "";
    }

    public static String getNodeAsString(Node node) throws DomUtilsException
    {
        StringWriter writer;
        writer = new StringWriter();
        node.normalize();
        transform(node, writer);
        writer.flush();
        return writer.toString();
    }

    // classes for parsing xml

    /**
     * Gets the DocumentBuilder for parsing xml documents.
     */
    public static synchronized DocumentBuilder getDocumentBuilder() throws DomUtilsException
    {
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (Exception e)
        {
            throw new DomUtilsException("DOMUtils, getDocumentBuilder error:" + e.toString());
        }
        return builder;
    }

    /**
     * Gets an empty Document for manipulating with DOM..
     */
    public static Document newDocument() throws DomUtilsException
    {
        return getDocumentBuilder().newDocument();
    }

    /**
     * Parses an xml document.
     */
    public static Document parse(String data) throws DomUtilsException
    {
        Document document = null;
        try
        {
            DocumentBuilder builder = getDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(data)));
        }
        catch (Exception e)
        {
            throw new DomUtilsException("DOMUtils, parse error:" + e.toString());
        }
        return document;
    }

    /**
     * Transforms a DOM node to a string and writes it to a writer.
     */
    public static void transform(Node sourceNode, Writer writer) throws DomUtilsException
    {
        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            Properties properties = OutputProperties.getDefaultMethodProperties("xml");
            transformer.setOutputProperties(properties);
            DOMSource domSource = new DOMSource(sourceNode);
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(domSource, streamResult);
        }
        catch (Exception e)
        {
            throw new DomUtilsException("DOMUtils, transform error:" + e.toString());
        }
    }

}
