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
 *
 *
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jafer.conf.Config;
import org.jafer.exception.JaferException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
// thrown by parsing errors
import org.xml.sax.SAXException;

import org.xml.sax.InputSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.IOException;

import java.util.Map;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.*;

public class DOMFactory {

	private static DocumentBuilderFactory factory;
	private static Logger logger;

  static {
		logger = Logger.getLogger("org.jafer.util");
      factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
	}

  public static synchronized DocumentBuilder getDocumentBuilder() {

		DocumentBuilder builder = null;
    try {
			builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
			String message = "DOMFactory, static initialization: Parser with specified options cannot be built!";
			logger.log(Level.SEVERE, message, e);
			System.err.print("FATAL: " + message + " Could not initialize DOMFactory.");
			System.exit(-1);
		}

		return builder;
	}

  public static Document newDocument() {

		return getDocumentBuilder().newDocument();
	}

	public static Document parse(URL resource) throws JaferException
	{
		try
		{
			return parse(resource.openStream());
		}
		catch (IOException ioe)
		{
			String message = "DOMFactory, public static Document parse(URL resource): Cannot parse resource; "
				+ ioe.toString();
			logger.severe(message);
			throw new JaferException(message, ioe);
		}
		catch( NullPointerException e )
		{
          String message = "DOMFactory, public static Document parse(URL resource): Cannot parse resource; " + e.toString();
			logger.severe(message);
			throw new JaferException(message, e);
		}
	}

	public static Document parse(InputStream inStream) throws JaferException
	{

		Document document = null;
		try
		{
			DocumentBuilder builder = getDocumentBuilder();
			document = builder.parse(inStream);
		}
		catch( IOException e )
		{
			String message = "DOMFactory, public static Document parse(InputStream inStream): Cannot parse stream; "
				+ e.toString();
			logger.log(Level.SEVERE, message, e);
			throw new JaferException(message, e);
		}
		catch( SAXException e )
		{
			String message = "DOMFactory, public static Document parse(InputStream inStream): Cannot parse stream; "
				+ e.toString();
			logger.log(Level.SEVERE, message, e);
			throw new JaferException(message, e);
		}
		catch( IllegalArgumentException e )
		{
			String message = "DOMFactory, public static Document parse(InputStream inStream): Cannot parse stream; "
				+ e.toString();
			logger.log(Level.SEVERE, message, e);
			throw new JaferException(message, e);
		}

		return document;
	}

  	public static Document parse(String data) throws JaferException {


		Document document = null;
    try {
			DocumentBuilder builder = getDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(data)));
    } catch (IOException e) {
      String message = "DOMFactory, public static Document parse(String data): Cannot parse data; " + e.toString();
      logger.log(Level.SEVERE, message, e);
      throw new JaferException(message, e);
    } catch (SAXException e) {
      String message = "DOMFactory, public static Document parse(String data): Cannot parse data; " + e.toString();
			logger.log(Level.SEVERE, message, e);
			throw new JaferException(message, e);
		}

    return document;
  }

  public static Document parse(DocumentBuilder builder, String data) throws JaferException {

    Document document = null;
    try {
//      DocumentBuilder builder = getDocumentBuilder();
      document = builder.parse(new InputSource(new StringReader(data)));
    } catch (IOException e) {
      String message = "DOMFactory, public static Document parse(String data): Cannot parse data; " + e.toString();
      logger.log(Level.SEVERE, message, e);
      throw new JaferException(message, e);
    } catch (SAXException e) {
      String message = "DOMFactory, public static Document parse(String data): Cannot parse data; " + e.toString();
			logger.log(Level.SEVERE, message, e);
			throw new JaferException(message, e);
		}

		return document;
	}


	//
	//  private static String getValue(Node node) {
	//
	//    if (node.hasChildNodes() &&
	//        node.getFirstChild().getNodeType() == Node.TEXT_NODE)
	//          return node.getFirstChild().getNodeValue();
	//
	//    return null;
	//  }
  public static Node getExceptionNode(Document document, Throwable e) {

		Element exception = document.createElement("exception");

    try {
			exception.setAttribute("oid", Config.getRecordSyntaxFromName("JAFER"));
    } catch (JaferException ex) {
			exception.setAttribute("oid", "unknown");
		}

		exception.setAttribute("type", e.getClass().getName());
		Node txt = document.createTextNode(e.toString());
		exception.appendChild(txt);

		if( e.getCause() != null )
			exception.appendChild(getExceptionNode(document, e.getCause()));

    return (Node)exception;
	}

  public static Node getExceptionNode(Document document, Throwable e, String msg) {

		Node exception = getExceptionNode(document, e);
		Node message = document.createElement("message");
		Node txt = document.createTextNode(msg);
		message.appendChild(txt);
		exception.appendChild(message);
		return exception;
	}

  public static Node getExceptionNode(Document document, Throwable e, StackTraceElement[] stackTraceElement) {

		Node exception = getExceptionNode(document, e);
		appendtStackTrace(document, exception, stackTraceElement);
		return exception;
	}

  public static Node getExceptionNode(Document document, Throwable e, StackTraceElement[] stackTraceElement, String msg) {

		Node exception = getExceptionNode(document, e, msg);
		appendtStackTrace(document, exception, stackTraceElement);
		return exception;
	}

  private static Node appendtStackTrace(Document document, Node exception, StackTraceElement[] stackTraceElement) {

    for (int i = 0; i < stackTraceElement.length; i++) {
			Node stackTrace = document.createElement("stackTrace");
      Node txt = document.createTextNode(stackTraceElement[i].toString());
			stackTrace.appendChild(txt);
			exception.appendChild(stackTrace);
		}
		return exception;
	}
}