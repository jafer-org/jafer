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

package org.jafer.record;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.xpath.NodeSet;
import java.util.Vector;
import org.jafer.exception.*;

public class Field  {

  private Node recordRoot;
  private Node recordFragment;

  public Field(Node recordRoot, Node recordFragment) {
    this.recordRoot = recordRoot;
    this.recordFragment = recordFragment;
  }

  public Node getRoot() {

    return recordRoot;
  }

  public Node getXML() {

    return recordFragment;
  }

  public String getRecordSyntax() {

    if (recordRoot == null) return "unknown";
    return ((Element)recordRoot).getAttribute("syntax");
  }

  public String getDataBaseName() {

    if (recordRoot == null) return "unknown";
    return ((Element)recordRoot).getAttribute("dbName");
  }

  public String getRecordSchema() {

    if (recordRoot == null) return "unknown";
    return ((Element)recordRoot).getAttribute("schema");
  }

/**
 * Returns the textual content of the field. (Doesn't include children/subfield content.)
 *
 * @return the textual content of the field, or an empty string.
 */
  public String getValue() {

    String data = "";

    if (recordFragment == null)
      return data;
    else if (recordFragment.hasChildNodes() &&
        recordFragment.getFirstChild().getNodeType() == Node.TEXT_NODE)
        data = recordFragment.getFirstChild().getNodeValue();

    return data;
  }

  public String getName() {
/** @todo rename? getFieldName()? */
    return ((Element)recordFragment).getTagName();
  }

  public Field[] get(String fieldName) {

     return get(fieldName, new String[0]);
  }

  public Field[] get(String fieldName, String attributeName, String attributeValue) {

    String[] attributeProfile = {attributeName, attributeValue};

    return get(fieldName, attributeProfile);
  }

  public Field[] get(String fieldName, String[] attributeProfile) {

    NodeList list = getNamedChildren(fieldName, attributeProfile, recordFragment, true);

    return toFieldArray(list);
  }

/**
 * Returns the first occurrence of the named field.
 * <p>Searches by level-order traversal, i.e. all fields at one level before their children.</p>
 *
 * @param  fieldName the name of the field to find.
 * @return the first occurrence of the named field, or a new null Field.
 */
  public Field getFirst(String fieldName) {

    return getFirst(fieldName, new String[0]);
  }

/**
 * Returns the first occurrence of the named field with the given attribute name and value.
 * <p>Searches by level-order traversal, i.e. all fields at one level before their children.</p>
 *
 * @param  fieldName the name of the field to find.
 * @param  attributeName the name of the required attribute.
 * @param  attributeValue the required value of the attribute.
 * @return the first matching occurrence of the field, or a new null Field.
 */

  public Field getFirst(String fieldName, String attributeName, String attributeValue) {

    String[] attributeProfile = {attributeName, attributeValue};
    return getFirst(fieldName, attributeProfile);
  }

/**
 * Returns the first occurrence of the named field with the given attributes and values.
 * <p>(The attributeProfile param is a String[] containing 1 or more attribute names and the required attribute values, in the format:
 * "nameA", "valueA", "nameB", "valueB"...)</p>
 * <p>Searches by level-order traversal, i.e. all fields at one level before their children.</p>
 *
 * @param  fieldName the name of the field to find.
 * @param  attributeProfile  an array of attribute name/ value pairs.
 * @return the first matching occurrence of the field, or a new null Field.
 */
  public Field getFirst(String fieldName, String[] attributeProfile) {

    NodeList list = getNamedChildren(fieldName, attributeProfile, recordFragment, false);
    if (list.getLength() > 0)
      return toFieldArray(list)[0];
    else
      return new Field(null, null);
  }

/**
 * Returns the textual content of a field and all its subfields, each separated by the delimiter/s supplied (which can be an empty string.)
 * <p>Suggested use for convenience: getFirst("name").getAllFieldData(", ") which would return a concatenation of the subfield data
 *  of the first <name> element found, separated by a comma and a space.</p>
 *  <p>If only one subfield is present, no delimiter is applied.</p>
 * @param  delimiter the string to use as a delimiter between field and subfield contents.
 * @return a concatenated string of the textual content of a field and all its subfields content, or an empty string.
 */
  public String getAllFieldData(String delimiter) {

/* Changed how this works, needs re-naming??:
   Instead of supplying a field name, and returning a concatenation of all the fields with that name,
   it now can be called after isolating the field, and will return a concatenation of all the subfield data,
   which may already have the intended punctuation within the subfields. (see javadoc above.)*/
    String data = "";

    if (recordFragment == null)
      return data;

    NodeList list = recordFragment.getChildNodes();
    for (int i=0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.TEXT_NODE)
        data += list.item(i).getNodeValue();
      else {
      Field field = new Field(this.getRoot(), list.item(i));
      data += delimiter + field.getAllFieldData(delimiter);
      }
    }
    return data;
  }

  /**
   *  Returns a NodeList of all ancestor nodes with the name/s and attribute value/s supplied in attributeProfile[].
   *  Searches by level-order traversal, i.e. all sibling children of the start node before their children.
   *@param  name  the name of the node/s to search for.
   *@param  attributeProfile  the names and values of the required attributes.
   *@param  startNode  the node whose children should be searched.
   *@param  recursive  when set to false, processing will halt when the first matching node is found.
   *@return  NodeList of all ancestor nodes with this name and attribute values, or an empty NodeList.
   */
    private NodeList getNamedChildren(String name, String[] attributeProfile, Node startNode, boolean recursive) {

      NodeSet matchingNodes = new NodeSet();

      if (startNode != null && startNode.getNodeType() != Node.TEXT_NODE) {

	NodeList list = startNode.getChildNodes();

	for (int i=0; i < list.getLength(); i++) {
	  if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
	    Element current = (Element)list.item(i);
	    if (current.getLocalName().equalsIgnoreCase(name) && checkAttributeValues(current, attributeProfile)){
	      matchingNodes.addElement(current);
	      if (!recursive){
		return matchingNodes;
	      }
	    }
	  }
	}
	for (int i=0; i < list.getLength(); i++) {// repeated for level-order traversal of child nodes.
	  NodeList matchingChildren = getNamedChildren(name, attributeProfile, list.item(i), recursive);
	  matchingNodes.addNodes(matchingChildren);
	  if (matchingChildren.getLength() > 0 && !recursive)
	    return matchingNodes;
	}
      }
      return matchingNodes;
    }

    private boolean checkAttributeValues(Element element, String[] attributeProfile) {

      for (int i=0; i < attributeProfile.length; i=i+2){
	if (!element.getAttribute(attributeProfile[i]).equalsIgnoreCase(attributeProfile[i+1]))
	  return false;
      }
      return true;
  }

  private Field[] toFieldArray(NodeList nodeList) {

    Field[] fieldArray = new Field[nodeList.getLength()];

    for (int i = 0; i < nodeList.getLength(); i++)
      fieldArray[i] = new Field(this.getRoot(), nodeList.item(i));

    return fieldArray;
  }

  /**
   * Concatenates the textual content of all fields with the given name, and all their subfields, separated by a space.
   * <p>If no field is found, an empty String is returned.</p>
   * @deprecated Use <code>get(fieldName)</code> or <code>getFirst(fieldName)</code> with <code>getAllFieldData(delimiter)</code> to produce a more controllable result.
   * @param  field the field name to search for.
   * @return a concatenated string of the textual content of all fields with the given name and their subfields content, or an empty string.
 */
  public String getFieldData(String field) {

    return getData(getList(field));
  }

  public String getFieldData(String field, String attributeName, String attributeValue) {

    NodeList nodeList = getList(field);
    String data = "";

    for (int i = 0; i < nodeList.getLength(); i++) {
      if (((Element)nodeList.item(i)).hasAttribute(attributeName) &&
            ((Element)nodeList.item(i)).getAttribute(attributeName).equalsIgnoreCase(attributeValue))
      data += nodeList.item(i).getFirstChild().getNodeValue();
    }

    return data;
  }

  private String getData(NodeList nodeList) {

    String fieldData = "";
    for (int i = 0; i < nodeList.getLength(); i++) {
      if (nodeList.item(i).hasChildNodes())
        fieldData += getData(nodeList.item(i).getChildNodes());
      else if (nodeList.item(i).getNodeType() == Node.TEXT_NODE)
        fieldData += nodeList.item(i).getNodeValue() + " ";// "\n"; separator?
    }
    return fieldData;
  }

  public NodeList getList(String field) {

    return ((Element)recordFragment).getElementsByTagName(field);
  }
}