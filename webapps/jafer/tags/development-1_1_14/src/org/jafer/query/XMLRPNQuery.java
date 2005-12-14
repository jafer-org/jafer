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

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.query;

import org.jafer.conf.Config;
import org.jafer.exception.JaferException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import z3950.v3.*;
import asn1.*;
import org.apache.xpath.CachedXPathAPI;

public class XMLRPNQuery {

  private static CachedXPathAPI xPathAPI;
  private static Node contextNode;

/**
 * <p>Methods to convert XML queries to RPN queries, and the reverse.</p>
 * <p>XML queries are re-structured according to de Morgan's rules in the process, as RPN queries
 * use an ANDNOT instead of a NOT.</p>
 * <p>(Some checking of XML query structure, attribute names, and content.)</p>
 */

/** @todo Modify incoming XML query to match RPN as it is built, and provide getModifiedQueryNode() */


  public static RPNQuery getRPNQuery(Node domQuery) throws QueryException {

    RPNQuery rpnQuery = new RPNQuery();
    rpnQuery.s_rpn = processNode(domQuery, null);
    rpnQuery.s_attributeSet = new AttributeSetId();
    rpnQuery.s_attributeSet.value = new ASN1ObjectIdentifier(
	Config.convertSyntax(Config.getAttributeSetSyntax()));
    return rpnQuery;
  }


  private static RPNStructure processNode(Node node, RPNStructure parent) throws QueryException {

    RPNStructure rpn;
    String nodeName = node.getNodeName();

    if (nodeName.equalsIgnoreCase("constraintModel"))
      rpn = processConstraintModel(node);

    else if (nodeName.equalsIgnoreCase("and"))
      rpn = processAndNode(node, parent);

    else if (nodeName.equalsIgnoreCase("or"))
      rpn = processOrNode(node, parent);

    else if (nodeName.equalsIgnoreCase("not"))// top level node is <NOT> node
      throw new QueryException("XML query cannot be represented as an RPNQuery", 108, "");

    else {
      String message = "Node not expected: " + nodeName;
      Logger.getLogger("org.jafer.query").log(Level.SEVERE, message);
      throw new QueryException(message, 108, "");
    }
    return rpn;
  }


  private static RPNStructure processConstraintModel(Node cM) throws QueryException {

    NodeList list = selectNodeList(cM, "constraint/*");
    Node child;
    AttributeElement[] ae = new AttributeElement[list.getLength()];
    int type, value;

    for (int i=0; i<list.getLength(); i++) {
      child = list.item(i);
      try {
	type = Config.getAttributeType(child.getNodeName());
	value = Integer.parseInt(getNodeValue(child));
	ae[i] = new AttributeElement();
	ae[i].s_attributeType = new ASN1Integer(type);
	ae[i].s_attributeValue = new AttributeElement_attributeValue();
	ae[i].s_attributeValue.c_numeric = new ASN1Integer(value);

      } catch (NumberFormatException n) {
	  String message = "Invalid value ("+getNodeValue(child)+") found for "+child.getNodeName()+" attribute";
	  Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, n);
	  throw new QueryException(message, n);
	  /** @todo throw new QueryException(message, 108, ""); */
	} catch (JaferException e) {
	    String message = "Invalid query attribute submitted: "+ child.getNodeName();
	    Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, e);
	    throw new QueryException(message, e);
	  /** @todo throw new QueryException(message, 108, ""); */
	}
    }

    String term = getNodeValue(selectNode(cM, "model"));

    Operand operand = new Operand();
    operand.c_attrTerm = new AttributesPlusTerm();
    operand.c_attrTerm.s_attributes = new AttributeList();
    operand.c_attrTerm.s_attributes.value = ae;
    operand.c_attrTerm.s_term = new Term();
    operand.c_attrTerm.s_term.c_general = new ASN1OctetString(term);

    RPNStructure rpn = new RPNStructure();
    rpn.c_op = operand;

    return rpn;
  }


  private static RPNStructure processAndNode(Node node, RPNStructure parent) throws QueryException {

    RPNStructure rpn = new RPNStructure();
    rpn.c_rpnRpnOp = new RPNStructure_rpnRpnOp();
    rpn.c_rpnRpnOp.s_op = new Operator();

    int position = findNotChild(node);

    if (position == 0) {// neither child is <NOT> node
      rpn.c_rpnRpnOp.s_op.c_and = new ASN1Null();
      rpn.c_rpnRpnOp.s_rpn1 = processNode(getFirstChild(node), rpn);
      rpn.c_rpnRpnOp.s_rpn2 = processNode(getSecondChild(node), rpn);
    }

    else if (position == 1) {// first child is <NOT> node
      Node firstChild = selectNode(node, "./not/*");
      Node secondChild = getSecondChild(node);

      rpn.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();
      rpn.c_rpnRpnOp.s_rpn1 = processNode(secondChild, rpn);
      rpn.c_rpnRpnOp.s_rpn2 = processNode(firstChild, rpn);
    }

    else if (position == 2) {// second child is <NOT> node
      Node firstChild = getFirstChild(node);
      Node secondChild = selectNode(node, "./not/*");

      rpn.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();
      rpn.c_rpnRpnOp.s_rpn1 = processNode(firstChild, rpn);
      rpn.c_rpnRpnOp.s_rpn2 = processNode(secondChild, rpn);
    }

    else if (position == 3) {// both are <NOT> nodes

      Node firstChild = selectNode(node, "./not[position()=1]/*");
      Node secondChild = selectNode(node, "./not[position()=2]/*");
      if (parent != null && parent.c_rpnRpnOp.s_op.c_and != null) {
      // change parent RPN operator to 'andnot' instead of 'and':
	parent.c_rpnRpnOp.s_op.c_and = null;
	parent.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();
      // make this RPN an 'or':
	rpn.c_rpnRpnOp.s_op.c_or = new ASN1Null();
	rpn.c_rpnRpnOp.s_rpn1 = processNode(firstChild, rpn);
	rpn.c_rpnRpnOp.s_rpn2 = processNode(secondChild, rpn);
      }
      else throw new QueryException("XML query cannot be represented as an RPNQuery", 108, "");
    }
    else throw new QueryException("XML query cannot be represented as an RPNQuery", 108, "");

    return rpn;
  }


  private static RPNStructure processOrNode(Node node, RPNStructure parent) throws QueryException {

    RPNStructure rpn = new RPNStructure();
    rpn.c_rpnRpnOp = new RPNStructure_rpnRpnOp();
    rpn.c_rpnRpnOp.s_op = new Operator();

    int position = findNotChild(node);

    if (position == 0) {// neither child is <NOT> node
      rpn.c_rpnRpnOp.s_op.c_or = new ASN1Null();
      rpn.c_rpnRpnOp.s_rpn1 = processNode(getFirstChild(node), rpn);
      rpn.c_rpnRpnOp.s_rpn2 = processNode(getSecondChild(node), rpn);

      return rpn;
    }

    if (parent != null && parent.c_rpnRpnOp.s_op.c_and != null) {
      // change parent RPN operator to 'andnot' instead of 'and':
	parent.c_rpnRpnOp.s_op.c_and = null;
	parent.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();

	if (position == 1) {// first child is <NOT> node.
	  Node firstChild = selectNode(node, "./not/*");
	  Node secondChild = getSecondChild(node);
	  // make this RPN an 'andnot':
	  rpn.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();
	  rpn.c_rpnRpnOp.s_rpn1 = processNode(firstChild, rpn);
	  rpn.c_rpnRpnOp.s_rpn2 = processNode(secondChild, rpn);
	}
	else if (position == 2) {// second child is <NOT> node.
	  Node firstChild = node.getFirstChild();
	  Node secondChild = selectNode(node, "./not/*");
	  // make this RPN an 'andnot', reverse children:
	  rpn.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();
	  rpn.c_rpnRpnOp.s_rpn1 = processNode(secondChild, rpn);//2nd
	  rpn.c_rpnRpnOp.s_rpn2 = processNode(firstChild, rpn);//1st
	}

	else if (position == 3) {// both children are <NOT> nodes.
	  Node firstChild = selectNode(node, "./not[position()=1]/*");
	  Node secondChild = selectNode(node, "./not[position()=2]/*");
	  // make this RPN an 'and':
	  rpn.c_rpnRpnOp.s_op.c_and = new ASN1Null();
	  rpn.c_rpnRpnOp.s_rpn1 = processNode(firstChild, rpn);
	  rpn.c_rpnRpnOp.s_rpn2 = processNode(secondChild, rpn);
	}
    }
    else throw new QueryException("XML query cannot be represented as an RPNQuery", 108, "");

    return rpn;
  }


  private static int findNotChild(Node node) throws QueryException {

    int position = 0;
    // first child <NOT> node, position = 1,
    // second child <NOT> node, position = 2,
    // both children <NOT> nodes, position = 3,
    // no <NOT> node, position = 0,
    try {
      if (getFirstChild(node).getNodeName().equalsIgnoreCase("not"))
        position = 1;
      else if (getSecondChild(node).getNodeName().equalsIgnoreCase("not"))
        return 2;
      else
        return 0;

      if (getSecondChild(node).getNodeName().equalsIgnoreCase("not"))
        position = 3;
    }
    catch (Exception ex) {
      String message = "Inconsistency in XML query format. (parent node: "+node.getNodeName()+")";
      Logger.getLogger("org.jafer.query").log(Level.WARNING, message, ex);
      throw new QueryException(message, 108, "");
    }

    return position;
  }


  private static Node getFirstChild(Node sourceNode) throws QueryException {
  // this method ignores text nodes, Node.getFirstChild doesn't.
    return selectNode(sourceNode, "./*[position()='1']");
  }


  private static Node getSecondChild(Node sourceNode) throws QueryException {
  // this method ignores text nodes, Node.getLastChild doesn't.
    return selectNode(sourceNode, "./*[position()='2']");
  }


  private static String getNodeValue(Node node) {

    try {
      if (selectNode(node, "./text()") != null)
	return selectNode(node, "./text()").getNodeValue();
    }
    catch (QueryException ex) {
      String message = "Error in getting Node value: " + node.getNodeName();
      Logger.getLogger("org.jafer.query").log(Level.WARNING, message, ex);
    }

    return "";
  }


  private static Node selectNode(Node sourceNode, String XPath) throws QueryException {
  /** @todo improve CachedXPathAPI handling... */
    try {
      if (sourceNode != contextNode) {
	xPathAPI = new CachedXPathAPI();
	contextNode = sourceNode;
      }

      return xPathAPI.selectSingleNode(sourceNode, XPath);

      } catch (Exception ex) {
	String message = "Error in XML query format (parent: "+sourceNode.getNodeName()+" XPath: "+XPath+")";
	Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, ex);
	throw new QueryException(message, ex);
	/** @todo throw new QueryException(message, 108, ""); */
      }
  }


  private static NodeList selectNodeList(Node sourceNode, String XPath) throws QueryException {
  /** @todo improve CachedXPathAPI handling... */
    try {
      if (sourceNode != contextNode) {
	xPathAPI = new CachedXPathAPI();
	contextNode = sourceNode;
      }

      return xPathAPI.selectNodeList(sourceNode, XPath);

      } catch (Exception ex) {
	String message = "Error in XML query format (parent: "+sourceNode.getNodeName()+" XPath: "+XPath+")";
	Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, ex);
	throw new QueryException(message, ex);
	/** @todo throw new QueryException(message, 108, ""); */
      }
  }


  public RPNStructure processConstraintModelNode(Node node) throws QueryException {
    // used by zclient.operations.Scan.java
    return processConstraintModel(node);
  }


  public static Node getXMLQuery(RPNQuery query) throws QueryException {

    QueryBuilder builder = new QueryBuilder();
    Node queryNode = processRPNStructure(query.s_rpn);
    return queryNode;
  }


  private static Node processRPNStructure(RPNStructure structure) throws QueryException {

    Node queryNode = null;
    QueryBuilder builder = new QueryBuilder();
    RPNStructure_rpnRpnOp operator = structure.c_rpnRpnOp;

    if (operator == null)
      return buildConstraintModel(structure, builder);

    try {
      if (operator.s_op.c_and != null) {
	queryNode = builder.and(
	    processRPNStructure(operator.s_rpn1),
	    processRPNStructure(operator.s_rpn2));
      }
      else if (operator.s_op.c_or != null) {
	queryNode = builder.or(
	    processRPNStructure(operator.s_rpn1),
	    processRPNStructure(operator.s_rpn2));
      }
      else if (operator.s_op.c_and_not != null) {
	queryNode = builder.and(
	    processRPNStructure(operator.s_rpn1),
	    builder.not(processRPNStructure(operator.s_rpn2)));
      }
    } catch (QueryException q) {
      String message = "Error in building XML query version of RPNQuery supplied.";
      Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, q);
      throw new QueryException(message, 108, "");
    }

    return queryNode;
  }


  private static Node buildConstraintModel(RPNStructure rpn, QueryBuilder builder) throws QueryException {

    AttributeElement[] ae = rpn.c_op.c_attrTerm.s_attributes.value;
    int[] attributes = new int[6]; // used by org.jafer.query.QueryBuilder
    int att;

    for (int i=0; i < ae.length; i++) {
      att = ae[i].s_attributeType.get();
      if (att > 0 && att < 7)
	attributes[att-1] = ae[i].s_attributeValue.c_numeric.get();
      else {
	String message = "Invalid RPNStructure, or unsupported attribute in query";
	Logger.getLogger("org.jafer.query").log(Level.SEVERE, message);
	throw new QueryException(message, 113, Integer.toString(att));
      }
    }

    String term = rpn.c_op.c_attrTerm.s_term.c_general.get();
    Node cM = builder.getNode(attributes, term);

    return cM;
  }
}