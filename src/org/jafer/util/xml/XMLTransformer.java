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

package org.jafer.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jafer.exception.JaferException;
import org.w3c.dom.Node;


/**
 * <p>
 * Provides methods for transforming xml using a templates object or by
 * specifying a file, or URL for the styleSheet. NB use OutputStreams instead of
 * Writers to preserve the required output character encoding
 * </p>
 * 
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class XMLTransformer
{

	private static Logger logger;
	private static TransformerFactory tFactory;

    private static URIResolver uriResolver = null;
	static
	{
		logger = Logger.getLogger("org.jafer.util");
        tFactory = TransformerFactory.newInstance();

		if( !(tFactory.getFeature(DOMSource.FEATURE) && tFactory.getFeature(DOMResult.FEATURE)) )
		{
			String message = "DOM node processing not supported - cannot continue!";
			logger.severe(message);
			System.exit(-1);
		}
	}

    /**
     * To avoid altering all the interfaces when a transformer is created
     * internally the transformer will have this URIResolver set on it if it is
     * not currently null
     * 
     * @param resolver The URIResolver to use for all transformations
     */
    public static void setURIResoverForNewTransformers(URIResolver resolver)
    {
        uriResolver = resolver;
    }
	public static Node transform(Node sourceNode, Transformer transformer) throws JaferException
	{

        logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, Transformer transformer)");

		try
		{
			// Define a DOMSource object.
			DOMSource domSource = new DOMSource(sourceNode);
			// Create an empty DOMResult for the Result.
			DOMResult domResult = new DOMResult(sourceNode.cloneNode(false));
			// Perform the transformation, placing the output in the DOMResult.
			transformer.transform(domSource, domResult);
			return domResult.getNode().getFirstChild();
		}
		catch( TransformerException e )
		{
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( IllegalArgumentException e )
		{// eg. node is null
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( NullPointerException e )
		{// eg. node is null
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, Transformer transformer)");
		}
	}

    public static void transform(Node sourceNode, Transformer transformer, OutputStream stream) throws JaferException
	{

        logger.entering("XMLTransformer",
				"public static void transform(Node sourceNode, Transformer transformer, OutputStream stream");

		try
		{
			DOMSource domSource = new DOMSource(sourceNode);
			StreamResult streamResult = new StreamResult(stream);
			transformer.transform(domSource, streamResult);
		}
		catch( TransformerException e )
		{
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( IllegalArgumentException e )
		{// eg. node is null
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( NullPointerException e )
		{// eg. node is null
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer",
					"public static void transform(Node sourceNode, Transformer transformer, OutputStream stream");
		}
	}

    public static void transform(Node sourceNode, Transformer transformer, Writer writer) throws JaferException
	{

		logger
			.entering("XMLTransformer",
				"public static void transform(Node sourceNode, Transformer transformer, Writer writer)");

		try
		{
			DOMSource domSource = new DOMSource(sourceNode);
			StreamResult streamResult = new StreamResult(writer);
			transformer.transform(domSource, streamResult);
		}
		catch( TransformerException e )
		{
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( IllegalArgumentException e )
		{// eg. node is null
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( NullPointerException e )
		{// eg. node is null
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer",
					"public static void transform(Node sourceNode, Transformer transformer, Writer writer)");
		}
	}

	public static Node transform(Node sourceNode, Templates template) throws JaferException
	{

        logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, Templates template)");

		try
		{// Create Transformer object from thread safe templates object
            Transformer transformer = template.newTransformer();
            // if we have a URIResolver set apply it to transformer
            if (uriResolver != null)
            {
                transformer.setURIResolver(uriResolver);
            }
            // Create Transformer object from thread safe templates object
            return transform(sourceNode, transformer);
		}
		catch( TransformerConfigurationException e )
		{
            String message = "XMLTransformer; cannot create transformer object from template. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, Templates template)");
        }
    }

    public static Node transform(Node sourceNode, InputStream stream) throws JaferException
    {

        logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, InputStream stream)");

        Transformer transformer = null;
        try
        {
            transformer = tFactory.newTransformer(new StreamSource(stream));
            // if we have a URIResolver set apply it to transformer
            if (uriResolver != null)
            {
                transformer.setURIResolver(uriResolver);
            }
            return transform(sourceNode, transformer);
        }
        catch (TransformerConfigurationException e)
        {
            String message = "XMLTransformer; cannot create transformer object from styleSheet input stream. " + e.toString();
            logger.severe(message);
            throw new JaferException(message, e);
        }
        catch (NullPointerException e)
        {
            String message = "XMLTransformer; cannot transform node, NULL template. " + e.toString();
            logger.severe(message);
            throw new JaferException(message, e);
        }
        finally
        {
            logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, InputStream stream)");
        }
    }

    public static Node transform(Map paramMap, Node sourceNode, InputStream stream) throws JaferException
    {

        logger.entering("XMLTransformer", "public static Node transform(Map paramMap, Node sourceNode, InputStream stream)");

        Transformer transformer = null;
        try
        {
            transformer = tFactory.newTransformer(new StreamSource(stream));
            Iterator keys = paramMap.keySet().iterator();
            while (keys.hasNext())
            {
                String param = (String) keys.next();
                String value = (String) paramMap.get(param);
                transformer.setParameter(param, value);
            }
            // if we have a URIResolver set apply it to transformer
            if (uriResolver != null)
            {
                transformer.setURIResolver(uriResolver);
            }
            return transform(sourceNode, transformer);
        }
        catch (TransformerConfigurationException e)
        {
            String message = "XMLTransformer; cannot create transformer object from stylesheet input stream. " + e.toString();
            logger.severe(message);
            throw new JaferException(message, e);
        }
        catch (NullPointerException e)
        {
            String message = "XMLTransformer; cannot transform node. " + e.toString();
            logger.severe(message);
            throw new JaferException(message, e);
        }
        finally
        {
            logger.exiting("XMLTransformer", "public static Node transform(Map paramMap, Node sourceNode, InputStream stream)");
		}
	}

	public static Node transform(Node sourceNode, StreamSource path) throws JaferException
	{

        logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, String path)");

		Transformer transformer = null;
		try
		{
			transformer = tFactory.newTransformer(path);
            if (uriResolver != null)
            {
                transformer.setURIResolver(uriResolver);
            }
			return transform(sourceNode, transformer);
		}
		catch( TransformerConfigurationException e )
		{
            String message = "XMLTransformer; cannot create transformer object from styleSheet " + path + ". " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( NullPointerException e )
		{
            String message = "XMLTransformer; cannot transform node, NULL template. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, String path)");
		}
	}

	public static Node transform(Map paramMap, Node sourceNode, String path) throws JaferException
	{

        logger.entering("XMLTransformer", "public static Node transform(Map paramMap, Node sourceNode, String path)");

		Transformer transformer = null;
		try
		{
			transformer = tFactory.newTransformer(new StreamSource(path));
			Iterator keys = paramMap.keySet().iterator();
			while( keys.hasNext() )
			{
				String param = (String) keys.next();
				String value = (String) paramMap.get(param);
				transformer.setParameter(param, value);
			}
            if (uriResolver != null)
            {
                transformer.setURIResolver(uriResolver);
            }
			return transform(sourceNode, transformer);
		}
		catch( TransformerConfigurationException e )
		{
            String message = "XMLTransformer; cannot create transformer object from stylesheet " + path + ". " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		catch( NullPointerException e )
		{
			String message = "XMLTransformer; cannot transform node. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer", "public static Node transform(Map paramMap, Node sourceNode, String path)");
		}
	}

	public static Node transform(Map paramMap, Node sourceNode, URL resource) throws JaferException
	{

        logger.entering("XMLTransformer", "public static Node transform(Map paramMap, Node sourceNode, URL resource)");

		try
		{
			return transform(paramMap, sourceNode, resource.getFile());
		}
		catch( NullPointerException e )
		{
            String message = "XMLTransformer; cannot transform node, NULL resource. " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
            logger.exiting("XMLTransformer", "public static Node transform(Map paramMap, Node sourceNode, URL resource)");
		}
	}

	public static Templates createTemplate(StreamSource source) throws JaferException
	{

		logger.entering("XMLTransformer", "public static Templates createTemplate(String path)");

		try
		{// Create a templates object, which is the processed, thread-safe representation of the stylesheet - NB. namespace?
			return tFactory.newTemplates(source);
		}
		catch( TransformerConfigurationException e )
		{
			String message = "XMLTransformer; cannot create template using stylesheet: " + source
				+ ". " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
		finally
		{
			logger.exiting("XMLTransformer", "public static Templates createTemplate(String path)");
		}
	}

	public static Templates createTemplate(URL resource) throws JaferException
	{

        if (resource != null)
		try
		{
			return createTemplate(new StreamSource(resource.openStream()));
		}
		catch( IOException e )
		{
			throw new JaferException(e);
		}
        else
            throw new JaferException("Resource necessary for creating XML transformer template not found");
	}

	public static Node transform(Node xmlIn, URL resource) throws JaferException
	{

		try
		{
			return transform(xmlIn, new StreamSource(resource.openStream()));
		}
		catch( IOException e )
		{
			throw new JaferException(e);
		}
	}
}