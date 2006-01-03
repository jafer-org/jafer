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

package org.jafer.record;
import org.jafer.exception.JaferException;

import org.jafer.zclient.ZClient;
import org.jafer.util.Config;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLTransformer;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;
import java.net.URL;
import java.lang.reflect.Constructor;
//imported exceptions for instantiating a Record class
import java.lang.ClassNotFoundException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.transform.Templates;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * <p>getBER returns BER object from XML - if necessary, transforms to schema required by record serializer via lookup in Config class.
 * getXML returns XML from BER after transformation (via lookup in Config class) to requested schema.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class RecordFactory {

  private static Hashtable templatesMap, fromSerializer, toSerializer, cachedTemplates;
  private static Logger logger;

  public RecordFactory() {

    logger = Logger.getLogger("org.jafer.record");
    templatesMap = new Hashtable();
    fromSerializer = new Hashtable();
    toSerializer = new Hashtable();
    templatesMap.put(Boolean.TRUE, fromSerializer);
    templatesMap.put(Boolean.FALSE, toSerializer);
    /** @todo SRW/XMLRecord hack: */
    cachedTemplates = new Hashtable();
  }

  public Object getBER(DataObject dataObject, Document document, int recNo) throws JaferException {

    int[] recordSyntax;
    String serializerSchema, sourceSchema, dbName = "";
    Node recordRoot, recordNode;
    Class recordClass;
    DataObject recordObject;

    try {
      recordSyntax = dataObject.getRecordSyntax();
      serializerSchema = Config.getRecordSerializerTargetSchema(Config.convertSyntax(recordSyntax));
      dbName = dataObject.getDatabaseName();
      recordClass = getRecordClass(recordSyntax);
      sourceSchema = dataObject.getRecordSchema();

      recordRoot = dataObject.getXML(document);
      recordNode = recordRoot.getFirstChild();
      recordRoot.removeChild(recordNode);
      recordNode = transformRecord(recordNode, recordSyntax, sourceSchema, false);
      recordNode = getRecordRoot(recordNode, recordSyntax, serializerSchema, dbName, recNo);

      recordObject = getRecordObject(recordClass, new Object[] {dbName, recordNode});
      return recordObject.getBER();
    } catch (Exception e) {
      String message = "Error generating BER from XML record";
      logger.severe(message + "; " + e.getMessage());
      throw new JaferException(message, e);
    }
  }

  public Object getXML(DataObject dataObject, Document document, String targetSchema, int recNo) throws JaferException {

    int[] recordSyntax;
    Node recordNode;
    String dbName = dataObject.getDatabaseName();

    if (dataObject.getXML() != null)
      recordNode = getXML(dataObject, targetSchema);
    else
      try {
        recordNode = getXML(dataObject, document, targetSchema);
      }
      catch (JaferException e) {
        recordNode = (Element) DOMFactory.getExceptionNode(document, e, e.getStackTrace(), "Error generating or getting XML from record");
        logger.warning(e.getMessage());
        /** @todo we need recordSchema for XML JaferException */
        return getRecordRoot(recordNode, Config.convertSyntax(Config.getRecordSyntaxFromName("JAFER")), "JaferException", dbName, recNo);
      }
    logger.exiting("RecordFactory", "public Element getXML(DataObject dataObject, Integer recNo)");

    return getRecordRoot(recordNode, dataObject.getRecordSyntax(), targetSchema, dbName, recNo);
  }

  private Node getXML(DataObject dataObject, String requestedRecordSchema) {

    Node recordNode;
    String recordSchema;

    recordNode = dataObject.getXML();

    try {
      recordSchema = dataObject.getRecordSchema();
      if (requestedRecordSchema.equalsIgnoreCase(recordSchema))
        return recordNode;

      recordNode = transformRecord(recordNode, recordSchema, requestedRecordSchema);

    } catch (JaferException e) {
      String message = "RecordFactory: cannot transform XMLRecord - use setCheckRecordFormat() to identify XMLRecords that do not conform to requested format";
      logger.warning(message);
      logger.fine(e.getMessage());
    }

    return recordNode;
  }

  private Node getXML(DataObject dataObject, Document document, String targetSchema) throws JaferException{

/** @todo method assumes Record class has a BEREncoding object... */
    int[] recordSyntax;
    Node recordNode;
    Class recordClass;
    DataObject recordObject;
    Object record;

    recordSyntax = dataObject.getRecordSyntax();
    recordClass = getRecordClass(recordSyntax);

    record = dataObject.getBER();
    recordObject = getRecordObject(recordClass, new Object[] {dataObject.getDatabaseName(), record});
    recordNode = recordObject.getXML(document);

    try {
      recordNode = transformRecord(recordNode, recordSyntax, targetSchema, true);
    } catch (JaferException e) {
      logger.fine("RecordFactory: cannot transform XMLRecord - use setCheckRecordFormat() to identify XMLRecords that do not conform to requested format");
      logger.fine(e.getMessage());
    }

    return recordNode;
  }

  private Class getRecordClass(int[] recordSyntax) throws JaferException {

    Class recordClass = null;
    try{
        String className = Config.getRecordSerializer(Config.convertSyntax(recordSyntax));
        recordClass = Class.forName(className);
    } catch (ClassNotFoundException e) {// also exception if serialiser not specified in recordDescriptor.xml
      String message = "RecordFactory (cannot locate Record class) " + e.toString();
      throw new JaferException(message, e);
    }
    return recordClass;
  }

  private DataObject getRecordObject(Class recordClass, Object[] initArgs) throws JaferException {
    Constructor[] constructors = recordClass.getConstructors();
    Class[] paramTypes;
    int index;
    for (index = 0; index < constructors.length; index++) {
        paramTypes = constructors[index].getParameterTypes();
        int j = 0;
        while (paramTypes[j].isInstance(initArgs[j])) {
            if (++j == paramTypes.length) {
                break;
            }
        }
        if (j == paramTypes.length) {
            break;
        }
    }
    DataObject recordObject = null;
    try{
      recordObject = (DataObject)constructors[index].newInstance(initArgs);
    } catch (SecurityException e) {
      String message = "RecordFactory (cannot instantiate Record Object) " + e.toString();
      throw new JaferException(message, e);
    } catch (InstantiationException e) {
      String message = "RecordFactory (cannot instantiate Record Object) " + e.toString();
      throw new JaferException(message, e);
    } catch (IllegalAccessException e) {
      String message = "RecordFactory (cannot instantiate Record Object) " + e.toString();
      throw new JaferException(message, e);
    } catch (IllegalArgumentException e) {
      String message = "RecordFactory (cannot instantiate Record Object) " + e.toString();
      throw new JaferException(message, e);
    } catch (InvocationTargetException e) {
      String message = "RecordFactory (cannot instantiate Record Object) " + e.toString();
      throw new JaferException(message, e);
    } catch (ArrayIndexOutOfBoundsException e) {
      String message = "RecordFactory (cannot instantiate Record Object) " + e.toString();
      e.printStackTrace();//////////////////////
      throw new JaferException(message, e);
    }

    return recordObject;
  }

  private Node transformRecord(Node recordNode, int[] recordSyntax, String requestedRecordSchema, boolean fromSerializer) throws JaferException {

    String styleSheet;
    URL resource;
    Templates template;
    Vector transforms;
    Hashtable  map = (Hashtable)templatesMap.get(Boolean.valueOf(fromSerializer));

    transforms = Config.getTransforms(fromSerializer, Config.convertSyntax(recordSyntax), requestedRecordSchema);
    for (int i = 0; i < transforms.size(); i++) {
        styleSheet = (String)transforms.get(i);
        if (!map.containsKey(styleSheet)) {// lookup in hashtable
          try {
            resource =  this.getClass().getClassLoader().getResource(styleSheet);
            template = XMLTransformer.createTemplate(resource);
            map.put(styleSheet, template);
          } catch (Exception e) {
            throw new JaferException("Error creating template from styleSheet: " + styleSheet, e);
          }
        }

        template = (Templates)map.get(styleSheet);
 // ZClient version
      recordNode = XMLTransformer.transform(recordNode, template);
    }
    return recordNode;
  }

  private Node transformRecord(Node recordNode, String recordSchema, String requestedRecordSchema) throws JaferException {

    String styleSheet;
    URL resource;
    Templates template;
    Vector transforms;

    transforms = Config.getTransforms(recordSchema, requestedRecordSchema);
    for (int i = 0; i < transforms.size(); i++) {
        styleSheet = (String)transforms.get(i);
        if (!cachedTemplates.containsKey(styleSheet)) {
          try {
            resource =  this.getClass().getClassLoader().getResource(styleSheet);
            template = XMLTransformer.createTemplate(resource);
            cachedTemplates.put(styleSheet, template);
          } catch (Exception e) {
            throw new JaferException("Error creating template from stylesheet: " + styleSheet, e);
          }
        }
        template = (Templates)cachedTemplates.get(styleSheet);
//SRW version
		recordNode.normalize();
	    recordNode = XMLTransformer.transform(recordNode, template);
    }
    return recordNode;
  }

//  private Element getRecordRoot(Document document, Node recordNode, int[] recordSyntax, String recordSchema, String dbName, int recNo) {
  private Element getRecordRoot(Node recordNode, int[] recordSyntax, String recordSchema, String dbName, int recNo) {

    /** @todo test for null params? */
    Document document = recordNode.getOwnerDocument();
    Element recordRoot = document.createElement("jaferRecord");
    recordRoot.appendChild(recordNode);
    recordRoot.setAttribute("syntax", Config.convertSyntax(recordSyntax));
    recordRoot.setAttribute("schema", recordSchema);
    recordRoot.setAttribute("dbName", dbName);
    recordRoot.setAttribute("number", String.valueOf(recNo));
    return recordRoot;
  }
}
