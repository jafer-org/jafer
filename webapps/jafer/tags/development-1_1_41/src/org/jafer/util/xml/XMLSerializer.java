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

import java.net.URL;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.logging.Logger;
//import java.util.logging.Level;
import java.util.Properties;
import java.util.Map;
import java.util.Iterator;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

//import org.apache.xalan.templates.OutputProperties;
import org.apache.xml.serializer.OutputPropertiesFactory;

import org.w3c.dom.Node;

/**
 * <p>Provides methods for serializing xml to a file, stream or writer.
 * Can also transform the xml prior to serialization using transformOutput methods.
 * NB use OutputStreams instead of Writers to preserve the required output character encoding</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class XMLSerializer {

  private static Logger logger;
  private static OutputPropertiesFactory propsFactory = new OutputPropertiesFactory();

  static {
    logger = Logger.getLogger("org.jafer.util");
  }

  public static void out(Node node, boolean omitXMLDeclaration, OutputStream stream) throws JaferException {

    Transformer transformer = getTransformer(getDefaultProperties("xml", omitXMLDeclaration));
    XMLTransformer.transform(node, transformer, stream);
  }

  public static void out(Node node, boolean omitXMLDeclaration, Writer writer) throws JaferException {

    Transformer transformer = getTransformer(getDefaultProperties("xml", omitXMLDeclaration));
    XMLTransformer.transform(node, transformer, writer);
  }

  public static void out(Node node, boolean omitXMLDeclaration, String filePath) throws JaferException {

    out(node, omitXMLDeclaration, getFileOutputStream(filePath));
  }

  public static void out(Node node, String method, OutputStream stream) throws JaferException {

    Transformer transformer = getTransformer(getDefaultProperties(method));
    XMLTransformer.transform(node, transformer, stream);
  }

  public static void out(Node node, String method, Writer writer) throws JaferException {

    Transformer transformer = getTransformer(getDefaultProperties(method));
    XMLTransformer.transform(node, transformer, writer);
  }

  public static void out(Node node, String method, String filePath) throws JaferException {

    out(node, method, getFileOutputStream(filePath));
  }

  public static void transformOutput(Node sourceNode, URL stylesheet, OutputStream stream) throws JaferException {

    Transformer transformer = getTransformer(stylesheet);
    XMLTransformer.transform(sourceNode, transformer, stream);
  }

  public static void transformOutput(Node sourceNode, URL stylesheet, Writer writer) throws JaferException {

    Transformer transformer = getTransformer(stylesheet);
    XMLTransformer.transform(sourceNode, transformer, writer);
  }

  public static void transformOutput(Node sourceNode, URL stylesheet, String filePath) throws JaferException {

    transformOutput(sourceNode, stylesheet, getFileOutputStream(filePath));
  }

  public static void transformOutput(Node sourceNode, URL stylesheet, Map parameters, OutputStream stream) throws JaferException {

    Transformer transformer = getTransformer(stylesheet, parameters);
    XMLTransformer.transform(sourceNode, transformer, stream);
  }

  public static void transformOutput(Node sourceNode, URL stylesheet, Map parameters, Writer writer) throws JaferException {

    Transformer transformer = getTransformer(stylesheet, parameters);
    XMLTransformer.transform(sourceNode, transformer, writer);
  }

  public static void transformOutput(Node sourceNode, URL stylesheet, Map parameters, String filePath) throws JaferException {

    transformOutput(sourceNode, stylesheet, parameters, getFileOutputStream(filePath));
  }

  public static Properties getDefaultProperties(String method) throws JaferException {

    method = method.toLowerCase();

    if (method.equals("xml") || method.equals("html") || method.equals("xhtml") || method.equals("text"))
//      return OutputProperties.getDefaultMethodProperties(method);
      return propsFactory.getDefaultMethodProperties(method);
    else
      throw new JaferException("Method supplied must be \"xml\", \"html\", \"xhtml\", or \"text\"");
  }

  private static Properties getDefaultProperties(String method, boolean omitXMLDeclaration) throws JaferException {

    Properties properties = getDefaultProperties(method);
    if (omitXMLDeclaration)
      properties.setProperty("omit-xml-declaration", "yes");
    return properties;
  }

  private static Transformer getTransformer() throws JaferException {
/** @todo should these 2 getTransformer() methods share a factory instance? */
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      transformer.setErrorListener(new org.jafer.util.xml.ErrorListener());
      return transformer;
    }
    catch (TransformerConfigurationException e) {
      String message = "XMLSerializer (Error in transformation/serialization)";
      logger.severe(message);
      throw new JaferException(message, e);
    }
  }

  private static Transformer getTransformer(URL stylesheet) throws JaferException {

    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet.getFile()));
      transformer.setErrorListener(new org.jafer.util.xml.ErrorListener());
      return transformer;
    }
    catch (TransformerConfigurationException e) {
      String message = "XMLSerializer (Error in transformation: " + stylesheet + ") " + e.toString();
      logger.severe(message);
      throw new JaferException(message, e);
    }
  }

  private static Transformer getTransformer(URL stylesheet, Map parameters) throws JaferException {

    Transformer transformer = getTransformer(stylesheet);
    Iterator keys = parameters.keySet().iterator();
    while (keys.hasNext()) {
      String param = (String)keys.next();
      transformer.setParameter(param, parameters.get(param));
    }
    return transformer;
  }

  private static Transformer getTransformer(Properties properties) throws JaferException {

      Transformer transformer = getTransformer();
      transformer.setOutputProperties(properties);
      return transformer;
  }

  private static FileOutputStream getFileOutputStream(String filePath) throws JaferException {

    try {
      return new FileOutputStream(filePath);
    }
    catch (IOException e) {
      String message = "Error in serializing output: (I/O error with path: " + filePath + ")";
      logger.severe(message);
      throw new JaferException(message, e);
    }
  }
}