/**
 * JAFER Toolkit Poject. Copyright (C) 2002, JAFER Toolkit Project, Oxford
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

package org.jafer.utils.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Parse factory for parsing documents. Cut down version of the Jafer DOM Factory
 */
public class ParseFactory
{

    /**
     * Stores a reference to the document builder factory
     */
    private static DocumentBuilderFactory factory;

    /**
     * Stores a reference to the logger
     */
    private static Logger logger;

    static
    {
        logger = Logger.getLogger("org.jafer.utils.xml");
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
    }

    /**
     * Gets the document builder
     * @return The document builder
     */
    public static synchronized DocumentBuilder getDocumentBuilder()
    {

        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            String message = "DOMFactory, static initialization: Parser with specified options cannot be built!";
            logger.log(Level.SEVERE, message, e);
            System.err.print("FATAL: " + message + " Could not initialize DOMFactory.");
            System.exit(-1);
        }

        return builder;
    }

    /**
     * Creates a new document
     * @return The created blank document
     */
    public static Document newDocument()
    {

        return getDocumentBuilder().newDocument();
    }

    /**
     * Parses the resource to extract the document
     * @param resource The resource to parse
     * @return The Document found
     * @throws ParsingException
     */
    public static Document parse(URL resource) throws ParsingException
    {
        try
        {
            // convert to URL to URI first to take care of %20 encoding
            return parse(new File(new URI(resource.toString())));
        }
        catch (URISyntaxException e)
        {
            String message = "DOMFactory, public static Document parse(URL resource): Cannot create URI from URL; "
                    + e.toString();
            logger.severe(message);
            throw new ParsingException(message, e);
        }
        catch (NullPointerException e)
        {
            String message = "DOMFactory, public static Document parse(URL resource): Cannot parse resource; " + e.toString();
            logger.severe(message);
            throw new ParsingException(message, e);
        }
    }
    /**
     * Parses the file to extract the document
     * @param file The resource to parse
     * @return The Document found
     * @throws ParsingException
     */
    public static Document parse(File file) throws ParsingException
    {

        Document document = null;
        try
        {
            DocumentBuilder builder = getDocumentBuilder();
            document = builder.parse(file);
        }
        catch (IOException e)
        {
            String message = "DOMFactory, public static Document parse(File file): Cannot parse file " + file + "; "
                    + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }
        catch (SAXException e)
        {
            String message = "DOMFactory:public static Document parse(File file) - Cannot parse file " + file + "; "
                    + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }
        catch (IllegalArgumentException e)
        {
            String message = "DOMFactory:public static Document parse(File file) - Cannot parse file " + file + "; "
                    + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }

        return document;
    }
    /**
     * Parses the input stream to extract the document
     * @param inStream The input stream to parse
     * @return The Document found
     * @throws ParsingException
     */
    public static Document parse(InputStream inStream) throws ParsingException
    {

        Document document = null;
        try
        {
            DocumentBuilder builder = getDocumentBuilder();
            document = builder.parse(inStream);
        }
        catch (IOException e)
        {
            String message = "DOMFactory, public static Document parse(InputStream inStream): Cannot parse stream; "
                    + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }
        catch (SAXException e)
        {
            String message = "DOMFactory, public static Document parse(InputStream inStream): Cannot parse stream; "
                    + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }
        catch (IllegalArgumentException e)
        {
            String message = "DOMFactory, public static Document parse(InputStream inStream): Cannot parse stream; "
                    + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }

        return document;
    }
    /**
     * Parses the string to extract the document
     * @param data The string to parse
     * @return The Document found
     * @throws ParsingException
     */
    public static Document parse(String data) throws ParsingException
    {

        Document document = null;
        try
        {
            DocumentBuilder builder = getDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(data)));
        }
        catch (IOException e)
        {
            String message = "DOMFactory, public static Document parse(String data): Cannot parse data; " + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }
        catch (SAXException e)
        {
            String message = "DOMFactory, public static Document parse(String data): Cannot parse data; " + e.toString();
            logger.log(Level.SEVERE, message, e);
            throw new ParsingException(message, e);
        }

        return document;
    }
    
}