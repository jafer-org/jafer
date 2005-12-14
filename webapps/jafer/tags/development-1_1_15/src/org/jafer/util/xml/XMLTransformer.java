/**
 * JAFER Toolkit Poject.
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

package org.jafer.util.xml;
import org.jafer.exception.JaferException;

import java.io.File;
import java.io.Writer;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Iterator;
//Imported TraX classes
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Templates;
// test FEATURE of parser
import org.xml.sax.SAXNotSupportedException;
// Imported DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;
// Imported Logger classes
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>Provides methods for transforming xml using a templates object or by specifying a file, or URL for the styleSheet.
 * NB use OutputStreams instead of Writers to preserve the required output character encoding</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class XMLTransformer {

  private static Logger logger;
  private static TransformerFactory tFactory;

    static {
      logger = Logger.getLogger("org.jafer.util");
      tFactory = TransformerFactory.newInstance();

      if(!(tFactory.getFeature(DOMSource.FEATURE) &&
           tFactory.getFeature(DOMResult.FEATURE))) {
          String message = "DOM node processing not supported - cannot continue!";
          logger.severe(message);
          System.exit(-1);
      }
    }

  public static Node transform(Node sourceNode, Transformer transformer) throws JaferException {

    logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, Transformer transformer)");

    try {
      // Define a DOMSource object.
      DOMSource domSource = new DOMSource(sourceNode);
      // Create an empty DOMResult for the Result.
      DOMResult domResult = new DOMResult(sourceNode.cloneNode(false));
      // Perform the transformation, placing the output in the DOMResult.
      transformer.transform(domSource, domResult);
      return domResult.getNode().getFirstChild();
    } catch (TransformerException e) {
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (IllegalArgumentException e) {// eg. node is null
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (NullPointerException e) {// eg. node is null
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, Transformer transformer)");
    }
  }

  public static void transform(Node sourceNode, Transformer transformer, OutputStream stream) throws JaferException {

    logger.entering("XMLTransformer", "public static void transform(Node sourceNode, Transformer transformer, OutputStream stream");

    try {
      DOMSource domSource = new DOMSource(sourceNode);
      StreamResult streamResult = new StreamResult(stream);
      transformer.transform(domSource, streamResult);
    } catch (TransformerException e) {
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (IllegalArgumentException e) {// eg. node is null
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (NullPointerException e) {// eg. node is null
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer", "public static void transform(Node sourceNode, Transformer transformer, OutputStream stream");
    }
  }

  public static void transform(Node sourceNode, Transformer transformer, Writer writer) throws JaferException {

    logger.entering("XMLTransformer", "public static void transform(Node sourceNode, Transformer transformer, Writer writer)");

    try {
      DOMSource domSource = new DOMSource(sourceNode);
      StreamResult streamResult = new StreamResult(writer);
      transformer.transform(domSource, streamResult);
    } catch (TransformerException e) {
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (IllegalArgumentException e) {// eg. node is null
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (NullPointerException e) {// eg. node is null
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer", "public static void transform(Node sourceNode, Transformer transformer, Writer writer)");
    }
  }

  public static Node transform(Node sourceNode, Templates template) throws JaferException {

    logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, Templates template)");

    try {// Create Transformer object from thread safe templates object
      return transform(sourceNode, template.newTransformer());
    } catch (TransformerConfigurationException e) {
      String message = "XMLTransformer; cannot create transformer object from template. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, Templates template)");
    }
  }

  public static Node transform(Node sourceNode, String path) throws JaferException {

    logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, String path)");

    Transformer transformer = null;
    try {
      transformer = tFactory.newTransformer(new StreamSource(path));
      return transform(sourceNode, transformer);
    } catch (TransformerConfigurationException e) {
      String message = "XMLTransformer; cannot create transformer object from styleSheet " + path + ". " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (NullPointerException e) {
      String message = "XMLTransformer; cannot transform node, NULL template. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer", "public static Node transform(Node sourceNode, String path)");
    }
  }

  public static Node transform(Map paramMap, Node sourceNode, String path) throws JaferException {

    logger.entering("XMLTransformer",  "public static Node transform(Map paramMap, Node sourceNode, String path)");

    Transformer transformer = null;
    try {
      transformer = tFactory.newTransformer(new StreamSource(path));
      Iterator keys = paramMap.keySet().iterator();
      while (keys.hasNext()) {
        String param = (String)keys.next();
        String value = (String)paramMap.get(param);
        transformer.setParameter(param, value);
      }
      return transform(sourceNode, transformer);
    } catch (TransformerConfigurationException e) {
      String message = "XMLTransformer; cannot create transformer object from stylesheet " + path + ". " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } catch (NullPointerException e) {
      String message = "XMLTransformer; cannot transform node. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer",  "public static Node transform(Map paramMap, Node sourceNode, String path)");
    }
  }

  public static Node transform(Node sourceNode, URL resource) throws JaferException {

    logger.entering("XMLTransformer", "public static Node transform(Node sourceNode, URL resource)");

    try {
      return transform(sourceNode, resource.getFile());
    } catch (NullPointerException e) {
      String message = "XMLTransformer; cannot transform node, NULL resource. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer",  "public static Node transform(Node sourceNode, URL resource)");
    }
  }

  public static Node transform(Map paramMap, Node sourceNode, URL resource) throws JaferException {

    logger.entering("XMLTransformer",  "public static Node transform(Map paramMap, Node sourceNode, URL resource)");

    try {
      return transform(paramMap, sourceNode, resource.getFile());
    } catch (NullPointerException e) {
      String message = "XMLTransformer; cannot transform node, NULL resource. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer",  "public static Node transform(Map paramMap, Node sourceNode, URL resource)");
    }
  }

  public static Node transform(Node sourceNode, File file) throws JaferException {

    logger.entering("XMLTransformer",  "public static Node transform(Node sourceNode, File file)");

    try {
      return transform(sourceNode, file.getPath());
    } catch (NullPointerException e) {
      String message = "XMLTransformer; cannot transform node, NULL file. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer",  "public static Node transform(Node sourceNode, File file)");
    }
  }

  public static Node transform(Map paramMap, Node sourceNode, File file) throws JaferException {

    logger.entering("XMLTransformer",  "public static Node transform(Map paramMap, Node sourceNode, File file)");

    try {
      return transform(paramMap, sourceNode, file.getPath());
    } catch (NullPointerException e) {
      String message = "XMLTransformer; cannot transform node, NULL file. " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer",  "public static Node transform(Map paramMap, Node sourceNode, File file)");
    }
  }

  public static Templates createTemplate(String path) throws JaferException {

    logger.entering("XMLTransformer", "public static Templates createTemplate(String path)");

    try {// Create a templates object, which is the processed, thread-safe representation of the stylesheet - NB. namespace?
      return tFactory.newTemplates(new StreamSource(path));
    } catch (TransformerConfigurationException e) {
      String message = "XMLTransformer; cannot create template using stylesheet: " + path + ". " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    } finally {
      logger.exiting("XMLTransformer", "public static Templates createTemplate(String path)");
    }
  }

  public static Templates createTemplate(URL resource) throws JaferException {

    if (resource != null)
      return createTemplate(resource.getFile());
    else
      throw new JaferException("Resource necessary for creating XML transformer template not found");
  }

  public static Templates createTemplate(File file) throws JaferException {

    if (file != null)
      return createTemplate(file.getPath());
    else
      throw new JaferException("Resource necessary for creating XML transformer template not found");
  }
}