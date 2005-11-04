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

package org.jafer.query.converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jafer.conf.Config;
import org.jafer.exception.JaferException;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import z3950.v3.AttributeElement;
import z3950.v3.AttributeElement_attributeValue;
import z3950.v3.AttributeList;
import z3950.v3.AttributeSetId;
import z3950.v3.AttributesPlusTerm;
import z3950.v3.Operand;
import z3950.v3.Operator;
import z3950.v3.RPNQuery;
import z3950.v3.RPNStructure;
import z3950.v3.RPNStructure_rpnRpnOp;
import z3950.v3.Term;
import asn1.ASN1Integer;
import asn1.ASN1Null;
import asn1.ASN1ObjectIdentifier;
import asn1.ASN1OctetString;

/**
 * This helper class contains all the methods to convert Z3950 RPN queries to
 * and from Jafer Queries
 */
public class RPNQueryConverter extends Converter
{
    /**
     * This method converts a z3950.v3.RPNQuery to the JaferQuery format
     * 
     * @param rpnQuery The z3950.v3.RPNQuery
     * @return the constructed jafer query
     * @throws QueryException
     */
    public static JaferQuery convertRPNToJafer(RPNQuery rpnQuery) throws QueryException
    {
        return new JaferQuery(processRPNStructure(rpnQuery.s_rpn));
    }

    /**
     * This method converts a jafer query to the z3950.v3.RPNQuery format. It
     * expects that the jafer querie has been normalised first
     * 
     * @param jaferQuery TThe jafer query to convert
     * @return the constructed rpn query
     * @throws QueryException
     */
    public static RPNQuery convertJaferToRPN(JaferQuery jaferQuery) throws QueryException
    {
        // create a new RPN Query structure
        RPNQuery rpnQuery = new RPNQuery();
        // process the Jafer query into RPN
        rpnQuery.s_rpn = processNode(jaferQuery.getQuery());
        // Set the attribute set being used
        rpnQuery.s_attributeSet = new AttributeSetId();
        rpnQuery.s_attributeSet.value = new ASN1ObjectIdentifier(Config.convertSyntax(Config.getAttributeSetSyntax()));
        return rpnQuery;
    }

    /**
     * This method processes the supplied jafer querey node and converts it to
     * an RPN structure. It can be called recursively to process nodes lower in
     * the query tree.
     * 
     * @param node The jafer query node to process
     * @return The constructed RPNStructure for the node being processed
     * @throws QueryException
     */
    private static RPNStructure processNode(Node node) throws QueryException
    {

        RPNStructure rpn;
        String nodeName = node.getNodeName();
        // match the node name to the allowed jafer topp level nodes
        if (nodeName.equalsIgnoreCase("constraintModel"))
        {
            // the node is a contraint model so process it
            rpn = processConstraintModelNode(node);
        }

        else if (nodeName.equalsIgnoreCase("and"))
        {
            // the node is an AND so process it
            rpn = processAndNode(node);
        }

        else if (nodeName.equalsIgnoreCase("or"))
        {
            // the node is a OR so process it
            rpn = processOrNode(node);
        }
        else if (nodeName.equalsIgnoreCase("not"))
        {
            // top level node is <NOT> node can not be converted to RPN so
            // report an error
            throw new QueryException("RPNQuery can not support top level NOT nodes. Check that the "
                    + "jafer query has been normalised", 108, "");
        }
        else
        {
            // we have a node of an invalid type
            String message = "Invalid Jafer Node: " + nodeName;
            Logger.getLogger("org.jafer.query").log(Level.SEVERE, message);
            throw new QueryException(message, 108, "");
        }
        return rpn;
    }

    /**
     * Process an AND jaffer query node. Expects the Node to have already been
     * normalised
     * 
     * @param node The AND node to process
     * @return A constructed RPNStructure for the AND node
     * @throws QueryException
     */
    private static RPNStructure processAndNode(Node node) throws QueryException
    {

        // create the RPN structure to support the AND node
        RPNStructure rpn = new RPNStructure();
        // create a new operator structure
        rpn.c_rpnRpnOp = new RPNStructure_rpnRpnOp();
        rpn.c_rpnRpnOp.s_op = new Operator();

        // Get the position of any NOT nodes on the left and right operands of
        // the query
        int position = findNotChild(node);

        if (position == 0)
        {
            // neither operand is a <NOT> node so set operater to be AND in the
            // RPNStructure and process the left and right operand of the jafer
            // query
            rpn.c_rpnRpnOp.s_op.c_and = new ASN1Null();
            rpn.c_rpnRpnOp.s_rpn1 = processNode(getFirstChild(node));
            rpn.c_rpnRpnOp.s_rpn2 = processNode(getSecondChild(node));
        }
        else if (position == 2)
        {
            // Right operand is a NOT node so change AND to be an ANDNOT
            rpn.c_rpnRpnOp.s_op.c_and_not = new ASN1Null();
            rpn.c_rpnRpnOp.s_rpn1 = processNode(getFirstChild(node));
            // converted AND to ANDNOT so process the child of the not rather
            // than the not itself
            rpn.c_rpnRpnOp.s_rpn2 = processNode(selectNode(node, "./not/*"));
        }
        else if (position == 1)
        {
            // ERROR: Left operand is <NOT> node meaning the query has not been
            // normalised so report an error
            throw new QueryException("JQF has not been normailised as NOT A AND B would "
                    + "have been convertered to B AND NOT A", 108, "");
        }
        else if (position == 3)
        {
            // ERROR: Both operand are NOT nodes meaning the query has not been
            // normalised so report an error
            throw new QueryException("JQF has not been normailised to apply demorgans law (NOT A and NOT B) -> Not(A or B)", 108,
                    "");
        }
        return rpn;
    }

    /**
     * Process an OR jaffer query node. Expects the Node to have already been
     * normalised
     * 
     * @param node The OR node to process
     * @return A constructed RPNStructure for the OR node
     * @throws QueryException
     */
    private static RPNStructure processOrNode(Node node) throws QueryException
    {
        // create the RPN structure to support the AND node
        RPNStructure rpn = new RPNStructure();
        // create a new operator structure
        rpn.c_rpnRpnOp = new RPNStructure_rpnRpnOp();
        rpn.c_rpnRpnOp.s_op = new Operator();

        // Get the position of any NOT nodes on the left and right operands of
        // the query
        int position = findNotChild(node);

        if (position == 0)
        {
            // neither operand is a <NOT> node so set operater to be OR in the
            // RPNStructure and process the left and right operand of the jafer
            // query
            rpn.c_rpnRpnOp.s_op.c_or = new ASN1Null();
            rpn.c_rpnRpnOp.s_rpn1 = processNode(getFirstChild(node));
            rpn.c_rpnRpnOp.s_rpn2 = processNode(getSecondChild(node));

            return rpn;
        }
        if (position == 1 || position == 2)
        {
            // ERROR: Left or Right operand is <NOT> node meaning the query has
            // not been normalised so report an error
            throw new QueryException("JQF has not been normailised to remove (A or NOT B) ,  (NOT B or A)", 108, "");
        }
        else if (position == 3)
        {
            // ERROR: Both operand are NOT nodes meaning the query has not been
            // normalised so report an error
            throw new QueryException("JQF has not been normailised to apply demorgans law (NOT A and NOT B) -> Not(A or B)", 108,
                    "");
        }
        return rpn;
    }

    /**
     * This method processes a constraint model Node.
     * 
     * @param constraintModel The constraint model node to process
     * @return An RPNStructure representation of the constraint
     * @throws QueryException
     */
    public static RPNStructure processConstraintModelNode(Node constraintModel) throws QueryException
    {

        NodeList list = selectNodeList(constraintModel, "constraint/*");
        Node constraint;
        AttributeElement[] ae = new AttributeElement[list.getLength()];
        int type, value;

        // loop round the constraints
        for (int i = 0; i < list.getLength(); i++)
        {
            // get the constraint node
            constraint = list.item(i);
            try
            {
                // work out the constraint type and its value
                // semantic / relation / position / structure / truncation /
                // completeness
                type = Config.getAttributeType(constraint.getNodeName());
                value = Integer.parseInt(getNodeValue(constraint));
                // add the new attribute element
                ae[i] = new AttributeElement();
                ae[i].s_attributeType = new ASN1Integer(type);
                ae[i].s_attributeValue = new AttributeElement_attributeValue();
                ae[i].s_attributeValue.c_numeric = new ASN1Integer(value);
            }
            catch (NumberFormatException n)
            {
                String message = "Invalid value (" + getNodeValue(constraint) + ") found for " + constraint.getNodeName()
                        + " attribute";
                Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, n);
                throw new QueryException(message, n);
            }
            catch (JaferException e)
            {
                String message = "Invalid query attribute submitted: " + constraint.getNodeName();
                Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, e);
                throw new QueryException(message, e);
            }
        }

        // process the model to construct the operand including the term

        String term = getNodeValue(selectNode(constraintModel, "model"));

        Operand operand = new Operand();
        operand.c_attrTerm = new AttributesPlusTerm();
        operand.c_attrTerm.s_attributes = new AttributeList();
        operand.c_attrTerm.s_attributes.value = ae;
        operand.c_attrTerm.s_term = new Term();
        operand.c_attrTerm.s_term.c_general = new ASN1OctetString(term);

        // create a new rpnstructure setting the operand
        RPNStructure rpn = new RPNStructure();
        rpn.c_op = operand;

        return rpn;
    }

    /**
     * This method creates a jafer query node for the supplied RPNStructure. It
     * is a recursive method that calls itself in order to process the sub
     * RPNStructures
     * 
     * @param structure The rpn structure to process
     * @return A jafer query node representation of the structure
     * @throws QueryException
     */
    private static Node processRPNStructure(RPNStructure structure) throws QueryException
    {

        Node queryNode = null;
        QueryBuilder builder = new QueryBuilder();
        RPNStructure_rpnRpnOp operator = structure.c_rpnRpnOp;

        // do we have an operator
        if (operator == null)
        {
            // just build the straight constraint model
            return buildConstraintModel(structure, builder);
        }

        try
        {
            if (operator.s_op.c_and != null)
            {
                // operator is AND so process each structure
                queryNode = builder.and(processRPNStructure(operator.s_rpn1), processRPNStructure(operator.s_rpn2));
            }
            else if (operator.s_op.c_or != null)
            {
                // operator is OR so process each structure
                queryNode = builder.or(processRPNStructure(operator.s_rpn1), processRPNStructure(operator.s_rpn2));
            }
            else if (operator.s_op.c_and_not != null)
            {
                // operator is ANDNOT so process first structure and build a NOT
                // node for the second structure
                queryNode = builder.and(processRPNStructure(operator.s_rpn1), builder.not(processRPNStructure(operator.s_rpn2)));
            }
        }
        catch (QueryException q)
        {
            String message = "Error in building XML query version of RPNQuery supplied.";
            Logger.getLogger("org.jafer.query").log(Level.SEVERE, message, q);
            throw new QueryException(message, 108, "");
        }

        return queryNode;
    }

    /**
     * Builds a jafer constraint model node from the supplied rpnStructure
     * 
     * @param structure The structure to process
     * @param builder Instance of a query builder to use to create the node
     * @return The constructed constarint model node
     * @throws QueryException
     */
    private static Node buildConstraintModel(RPNStructure structure, QueryBuilder builder) throws QueryException
    {

        AttributeElement[] ae = structure.c_op.c_attrTerm.s_attributes.value;
        // holds the attributes used to construct a constraint model in the
        // QueryBuilder
        int[] attributes = new int[6];
        int att;

        // loop round the atributes building the attributes array to pass to the
        // query builder
        for (int i = 0; i < ae.length; i++)
        {
            att = ae[i].s_attributeType.get();
            if (att > 0 && att < 7)
                attributes[att - 1] = ae[i].s_attributeValue.c_numeric.get();
            else
            {
                String message = "Invalid RPNStructure, or unsupported attribute in query";
                Logger.getLogger("org.jafer.query").log(Level.SEVERE, message);
                throw new QueryException(message, 113, Integer.toString(att));
            }
        }

        // extract the term and build constraint
        String term = structure.c_op.c_attrTerm.s_term.c_general.get();
        Node constraintModel = builder.getNode(attributes, term);

        return constraintModel;
    }
    
    
}
