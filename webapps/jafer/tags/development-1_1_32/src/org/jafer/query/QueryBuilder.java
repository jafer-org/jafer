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

/**
 * Title: JAFER Toolkit Description: Copyright: Copyright (c) 2001 Company:
 * Oxford University
 * 
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */

package org.jafer.query;

import org.jafer.conf.Config;
import org.jafer.util.xml.DOMFactory;
import org.jafer.exception.JaferException;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for building XML representations of Z39.50 Bib1 queries.
 * <p>
 * A default search profile is used if one has been set using an appropriate
 * constructor.
 * </p>
 * <p>
 * Some getNode() methods allow the default search profile to be altered, or
 * ignored for that query.
 * </p>
 * 
 * @see: <code>org.jafer.conf.Bib1Attributes.xml</code> for valid attribute
 *       values.
 * @see Schema of XML structure available at <a
 *      href="http://www.jafer.org">www.jafer.org </a>.
 */
public class QueryBuilder implements org.jafer.interfaces.QueryBuilder
{

    private Document document;

    int[] defaultAttributes = { 0, 0, 0, 0, 0, 0 };

    /**
     * Constructs a new QueryBuilder without any default search profile.
     */
    public QueryBuilder()
    {

        this.document = DOMFactory.newDocument();
    }

    /**
     * Constructs a new QueryBuilder with a default Bib1 search profile which
     * will be used in creating queries.
     * <p>
     * The profile parameter is interpreted in the following order:
     * </p>
     * <p>
     * [1] Use, [2] Relation, [3] Position, [4] Structure, [5] Truncation, [6]
     * Completeness
     * </p>
     * <p>
     * A value of zero in the array will be ignored, and can be used as a
     * spacer.
     * </p>
     * <p>
     * (Default search profile can be overidden when using certain getNode()
     * methods.)
     * </p>
     * 
     * @param defaultSearchProfile int array (0 < length < 7)
     */
    public QueryBuilder(int[] defaultSearchProfile)
    {

        if (defaultSearchProfile.length < 1 || defaultSearchProfile.length > 6)
            throw new RuntimeException("Invalid attribute array length, should be between 1 and 6");

        for (int i = 0; i < defaultSearchProfile.length; i++)
        {
            if (defaultSearchProfile[i] < 0)
            {
                throw new RuntimeException("Attribute can not be a negative value");
            }
            defaultAttributes[i] = defaultSearchProfile[i];
        }
        this.document = DOMFactory.newDocument();
    }

    /**
     * Constructs a new QueryBuilder with a default Bib1 search profile which
     * will be used in creating queries.
     * <p>
     * The profile parameter consists of a dot delimited String (eg:
     * 4.3.3.101.100.1), with values interpreted in the following order:
     * </p>
     * <p>
     * Use, Relation, Position, Structure, Truncation, Completeness.
     * </p>
     * <p>
     * A value of zero will be ignored, and can be used as a spacer.
     * </p>
     * <p>
     * (Default search profile can be overidden when using certain getNode()
     * methods.)
     * </p>
     * 
     * @param defaultSearchProfile dot delimited String with at least 1, and a
     *        maximum of 6 items.
     */
    public QueryBuilder(String defaultSearchProfile)
    {

        try
        {
            int[] newDefaultArray = Config.convertSyntax(defaultSearchProfile);

            if (newDefaultArray.length < 1 || newDefaultArray.length > 6)
                throw new RuntimeException("Invalid attribute array length, should be between 1 and 6");

            for (int i = 0; i < newDefaultArray.length; i++)
            {
                if (newDefaultArray[i] < 0)
                {
                    throw new RuntimeException("Attribute can not be a negative value");
                }
                defaultAttributes[i] = newDefaultArray[i];
            }

            this.document = DOMFactory.newDocument();
        }
        catch (RuntimeException e)
        {
            throw new RuntimeException("Unable to convert search profile");
        }
    }

    /**
     * Creates a basic query node incorporating the Use attribute and term
     * supplied.
     * <p>
     * Attribute supplied must be the name of a Use attribute (eg: "Title"), or
     * a String representation of its corresponding numeric value (eg: "4").
     * </p>
     * <p>
     * Note: If an attribute name is supplied, it is checked against lookup
     * table via <code>org.jafer.conf.Config.getAttributeValue()</code>, but
     * not if a corresponding numeric value is used, in which case any positive
     * value within the int range is accepted.
     * </p>
     * 
     * @param useAttribute represents the Use attribute to be used in the query,
     *        e.g. "title".
     * @param term the term to be used in the query.
     * @return a query node.
     * @throws org.jafer.zclient.QueryException if <code>useAttribute</code>
     *         parameter is not valid, and <code>term</code> is empty..
     */
    public Node getNode(String useAttribute, String term) throws QueryException
    {
        /* invalid value of <= zero is caught in buildConstraintModelNode(). */
        int[] attributes = { lookUpUseAttribute(useAttribute) };
        return getNode(attributes, term);
    }

    /**
     * Creates a basic query node incorporating the Use attribute and term
     * supplied.
     * <p>
     * Attribute value is not checked, any positive int value is accepted.
     * </p>
     * 
     * @param useAttribute represents the Use attribute to be used in the query.
     * @param term the term to be used in the query.
     * @return a query node.
     * @throws org.jafer.query.QueryException if parameters are not valid or
     *         empty.
     */
    public Node getNode(int useAttribute, String term) throws QueryException
    {
        /* invalid value of <= zero is caught in buildConstraintModelNode(). */
        int[] attributes = { useAttribute };
        return getNode(attributes, term);
    }

    /**
     * Creates a basic query node incorporating the attributes and term
     * supplied.
     * <p>
     * The int[] parameter values are used in order for:
     * </p>
     * <p>
     * {Use, Relation, Position, Structure, Truncation, Completeness}
     * </p>
     * <p>
     * e.g. { 4, 3, 3, 2, 100, 1 }
     * </p>
     * <p>
     * A smaller sized array can be used, in which case the values given will be
     * interpreted in order, following the pattern above.
     * </p>
     * <p>
     * A parameter value of 0 will cause the corresponding attribute type to be
     * excluded from the query.
     * </p>
     * <p>
     * (If a default search profile has been set, it will be overridden for this
     * query: any values supplied in the parameter will replace corresponding
     * default values.)
     * </p>
     * 
     * @param attributes represents the attribute values to be used in the
     *        query.
     * @param term the term to be used in the query.
     * @return a query node.
     * @throws org.jafer.zclient.QueryException if <code>attributes</code>
     *         array size is smaller than 1, or larger than 6.
     */
    public Node getNode(int[] attributes, String term) throws QueryException
    {
        /*
         * copies defaultAttributes as initial array, with all values shifted up
         * by one position (as array position [0] is ignored in
         * buildConstraintModelNode()). Then copies any values that have been
         * set in attributes parameter.
         */

        if (attributes.length < 1 || attributes.length > 6)
            throw new QueryException("Invalid attribute array length, should be between 1 and 6");
        int[] modifiedArray = new int[defaultAttributes.length + 1];
        System.arraycopy(defaultAttributes, 0, modifiedArray, 1, defaultAttributes.length);

        for (int i = 0; i < attributes.length; i++)
            modifiedArray[i + 1] = attributes[i];

        return buildConstraintModelNode(modifiedArray, term);
    }

    /**
     * Creates a basic query node incorporating the term and attributes
     * supplied.
     * <p>
     * The int[][] parameter holds the attribute types and corresponding values.
     * </p>
     * <p>
     * e.g. {{1,1003}, {2,3}, {4,2}} would set:
     * </p>
     * <p>
     * Use = 1003, Relation = 3 and Structure = 2.
     * </p>
     * <p>
     * A parameter value of 0 will result in the corresponding attribute type to
     * be excluded from the query.
     * </p>
     * <p>
     * (If a default search profile has been set, it will be altered for this
     * query: any values given in the parameter will replace corresponding
     * default values.)
     * </p>
     * 
     * @param attTypesValues represents the attribute types and their
     *        corresponding values to be used in the query.
     * @param term the term to be used in the query.
     * @return a query node.
     * @throws org.jafer.zclient.QueryException if <code>attTypesValues</code>
     *         array size is smaller than 1, or larger than 6. Also if the first
     *         attribute type is missing, or not a positive value.
     */
    public Node getNode(int[][] attTypesValues, String term) throws QueryException
    {

        /*
         * copies defaultAttributes as initial array, with all values shifted up
         * by one position (as array position [0] is ignored in
         * buildConstraintModelNode()). Then copies any values that have been
         * set in attTypesValues parameter.
         */

        if (attTypesValues.length < 1 || attTypesValues.length > 6)
            throw new QueryException("Invalid attribute array length, should be between 1 and 6");

        for (int i = 0; i < attTypesValues.length; i++)
        {
            // make sure we only have two items in array
            // the first value is in the range 1 to 6
            // the second value is not negative
            if (attTypesValues[i].length != 2 || attTypesValues[i][1] < 0 || attTypesValues[i][0] < 0 || attTypesValues[i][0] > 6)
                throw new QueryException("Attributes in attribute array incorrectly set.");
        }

        int[] modifiedArray = new int[defaultAttributes.length + 1];
        System.arraycopy(defaultAttributes, 0, modifiedArray, 1, defaultAttributes.length);

        for (int i = 0; i < attTypesValues.length; i++)
            modifiedArray[attTypesValues[i][0]] = attTypesValues[i][1];

        return buildConstraintModelNode(modifiedArray, term);
    }

    /**
     * Creates a basic query node incorporating the term and attributes
     * supplied.
     * <p>
     * The String[] parameter holds the term to be used in position [0], and the
     * remaining attributes thereafter, eg:
     * </p>
     * <p>
     * {"Shakespeare", "1003", "3", "3", "2", "100", "1"}
     * </p>
     * <p>
     * Shorter arrays can be used, and values of "0" will cause that attribute
     * to be omitted from the query.
     * </p>
     * <p>
     * (If a default search profile has been set, it will be ignored for this
     * query)
     * </p>
     * 
     * @param termAndAttributes the term (position [0]) and attributes to be
     *        used in the query.
     * @return a query node.
     * @throws org.jafer.query.QueryException
     */
    public Node getNode(String[] termAndAttributes) throws QueryException
    {
        if (termAndAttributes.length < 1 || termAndAttributes.length > 7)
            throw new QueryException("Invalid attribute array length, should be between 1 and 7");

        int[] attributes = new int[7];

        for (int i = 1; i < termAndAttributes.length && termAndAttributes[i] != null; i++)
            attributes[i] = Integer.parseInt(termAndAttributes[i]);

        return buildConstraintModelNode(attributes, termAndAttributes[0]);
    }

    /**
     * Builds an XML representation of a query, using Bib1 attributes.
     * 
     * @see Schema of XML structure available at <a
     *      href="http://www.jafer.org">www.jafer.org </a>.
     * @param attributes represents the attribute values to be used in the
     *        query.
     * @param term the term to be used in the query.
     * @return a query node.
     * @throws org.jafer.zclient.QueryException if there is an error in the
     *         attribute lookup, or the term is empty.
     */

    private Node buildConstraintModelNode(int[] attributes, String term) throws QueryException
    {

        if (term == null || term.equals(""))
            throw new QueryException("Query term supplied is either empty or null");

        Node constraintModel, constraint, n;

        constraintModel = document.createElement("constraintModel");
        constraint = constraintModel.appendChild(document.createElement("constraint"));
        n = constraintModel.appendChild(document.createElement("model"));
        n.appendChild(document.createTextNode(term));
        /*
         * loop starts at 1, as attributes[0] is undefined. only attributes that
         * are not set to 0 are used in the query being built. a check is made
         * to ensure that a Use attribute has been set.
         */

        if (attributes[1] < 1)
            throw new QueryException("Use attribute has been incorrectly set.");

        for (int i = 1; i < attributes.length; i++)
        {
            if (attributes[i] < 0)
                throw new QueryException("Attribute can not be a negative value");

            if (attributes[i] > 0)
            {
                try
                {
                    n = constraint.appendChild(document.createElement(Config.getAttributeType(i)));
                }
                catch (JaferException e)
                {
                    throw new QueryException("Attribute type not found in lookup: " + i, e);
                }

                n.appendChild(document.createTextNode(Integer.toString(attributes[i])));
            }
        }

        return constraintModel;
    }

    /**
     * Produces an AND node from 2 nodes, which can be query or Boolean nodes.
     * 
     * @param leftNode a query, AND, OR or NOT node.
     * @param rightNode a query, AND, OR or NOT node.
     * @return an AND node.
     * @throws org.jafer.query.QueryException if either Node parameter is null.
     */
    public Node and(Node leftNode, Node rightNode) throws QueryException
    {

        Node n = document.createElement("and");
        if (leftNode != null && rightNode != null)
        {
            n.appendChild(document.importNode(leftNode, true));
            n.appendChild(document.importNode(rightNode, true));
            return n;
        }
        else
            throw new QueryException("Node being used in building the query is null.");
    }

    /**
     * Produces an AND node from a pair of attributes, or a tree of AND nodes if
     * more than 2 attributes are given.
     * <p>
     * Vector parameters: Minimum size is 2, and must contain only String
     * objects.
     * </p>
     * <p>
     * Attributes supplied must be the name of a Use attribute (eg: "Title"), or
     * a String representation of its corresponding numeric value (eg: "4").
     * </p>
     * <p>
     * Note: If an attribute name is supplied, it is checked against lookup
     * table via <code>org.jafer.conf.Config.getAttributeValue()</code>, but
     * not if a corresponding numeric value is used, in which case any positive
     * value within the int range is accepted.
     * </p>
     * 
     * @param useAttributes the attribute values to be used in building the
     *        query.
     * @param terms the corresponding terms to be used in building the query.
     * @return an AND node or tree.
     * @throws org.jafer.query.QueryException if a problem is encountered with
     *         the input Vectors.
     * @see: <code>org.jafer.util.Bib1Attributes.java</code> for possible
     *       <code>useAttribute</code> values.
     */
    public Node and(Vector useAttributes, Vector terms) throws QueryException
    {

        Node left, right, currentNode = null;

        if (useAttributes.size() == terms.size() && useAttributes.size() > 1)
        {
            // to build a balanced tree from useAttributes with even number of
            // elements:
            for (int i = 1; i < useAttributes.size(); i += 2)
            {
                left = getNode((String) useAttributes.get(i - 1), (String) terms.get(i - 1));
                right = getNode((String) useAttributes.get(i), (String) terms.get(i));
                if (currentNode != null)
                {
                    currentNode = and(currentNode, and(left, right));
                }
                else
                    currentNode = and(left, right);
            }
            // if useAttributes has odd number of elements, handle last element:
            if (useAttributes.size() % 2 == 1)
            {
                int j = useAttributes.size() - 1;
                right = getNode((String) useAttributes.get(j), (String) terms.get(j));
                currentNode = and(currentNode, right);
            }
            return currentNode;
        }
        else
            throw new QueryException("input vector problem...");
    }

    /**
     * Produces an OR node from 2 nodes, which can be query or Boolean nodes.
     * 
     * @param leftNode a query, AND, OR or NOT node.
     * @param rightNode a query, AND, OR or NOT node.
     * @return an OR node.
     * @throws org.jafer.query.QueryException if either Node parameter is null.
     */
    public Node or(Node leftNode, Node rightNode) throws QueryException
    {

        Node n = document.createElement("or");
        if (leftNode != null && rightNode != null)
        {
            n.appendChild(document.importNode(leftNode, true));
            n.appendChild(document.importNode(rightNode, true));
            return document.importNode(n, true);
        }
        else
            throw new QueryException("Node being used in building the query is null.");
    }

    /**
     * Produces an OR node from a pair of attributes, or a tree of OR nodes if
     * more than 2 attributes are given.
     * <p>
     * Vector parameters: minimum size is 2, and must contain only String
     * objects.
     * </p>
     * <p>
     * Attributes supplied must be the name of a Use attribute (eg: "Title"), or
     * a String representation of its corresponding numeric value (eg: "4").
     * </p>
     * <p>
     * Note: If an attribute name is supplied, it is checked against lookup
     * table via <code>org.jafer.conf.Config.getAttributeValue()</code>, but
     * not if a corresponding numeric value is used, in which case any positive
     * value within the int range is accepted.
     * </p>
     * 
     * @param useAttributes the attribute values to be used in building the
     *        query.
     * @param terms the corresponding terms to be used in building the query.
     * @return an OR node or tree.
     * @throws org.jafer.query.QueryException if a problem is encountered with
     *         the input Vectors.
     */
    public Node or(Vector useAttributes, Vector terms) throws QueryException
    {

        Node left, right, currentNode = null;

        if (useAttributes.size() == terms.size() && useAttributes.size() > 1)
        {
            // to build a balanced tree from useAttributes with even number of
            // elements:
            for (int i = 1; i < useAttributes.size(); i += 2)
            {
                left = getNode((String) useAttributes.get(i - 1), (String) terms.get(i - 1));
                right = getNode((String) useAttributes.get(i), (String) terms.get(i));
                if (currentNode != null)
                {
                    currentNode = or(currentNode, or(left, right));
                }
                else
                    currentNode = or(left, right);
            }
            // if useAttributes has odd number of elements, handle last element:
            if (useAttributes.size() % 2 == 1)
            {
                int j = useAttributes.size() - 1;
                right = getNode((String) useAttributes.get(j), (String) terms.get(j));
                currentNode = or(currentNode, right);
            }
            return currentNode;
        }
        else
            throw new QueryException("input vector problem...");
    }

    /**
     * Produces a NOT node from the supplied node, which can be a query or
     * Boolean node. i.e. produces a negated version of the supplied node.
     * 
     * @param inputNode a query, AND or OR node.
     * @return a NOT node.
     * @throws org.jafer.query.QueryException if inputNode parameter is null..
     */
    public Node not(Node inputNode) throws QueryException
    {

        Node n = document.createElement("not");
        if (inputNode != null)
        {
            n.appendChild(document.importNode(inputNode, true));
            return n;
        }
        else
            throw new QueryException("Node being used in building the query is null.");
    }

    /**
     * Create a Node from a query string
     */
    public Node getNode(String queryExp) throws QueryException
    {

        if (queryExp != null && queryExp.length() > 0)
        {

            StringTokenizer st = new StringTokenizer(queryExp, "= ", true);
            String attribute = null, op = null;
            boolean isAttribute = true, isTerm = false;
            Node node = null;

            while (st.hasMoreTokens())
            {
                try
                {
                    String s = st.nextToken().trim();
                    if (s.length() > 0)
                    {

                        if (s.equals("="))
                        {
                            isTerm = true;

                        }
                        else if (isTerm)
                        {
                            isTerm = false;

                            if (node == null)
                            {
                                if (op == null)
                                    node = getNode(attribute, s);
                                else if (op.equalsIgnoreCase("not"))
                                    node = this.not(getNode(attribute, s));
                            }
                            else if (op.equalsIgnoreCase("and"))
                            {
                                node = this.and(node, getNode(attribute, s));
                            }
                            else if (op.equalsIgnoreCase("or"))
                            {
                                node = this.or(node, getNode(attribute, s));
                            }
                            else if (op.equalsIgnoreCase("not"))
                            {
                                node = this.and(node, this.not(getNode(attribute, s)));
                            }
                            else if (op.equalsIgnoreCase("andNot"))
                            {
                                node = this.and(node, this.not(getNode(attribute, s)));
                            }
                            else if (op.equalsIgnoreCase("orNot"))
                            {
                                node = this.or(node, this.not(getNode(attribute, s)));
                            }
                        }
                        else if (s.equalsIgnoreCase("and") || s.equalsIgnoreCase("or") || s.equalsIgnoreCase("not")
                                || s.equalsIgnoreCase("andNot") || s.equalsIgnoreCase("orNot"))
                        {
                            isAttribute = true;
                            op = s;

                        }
                        else if (isAttribute)
                        {
                            isAttribute = false;
                            attribute = s;
                        }
                        else
                            throw new QueryException();
                    }

                }
                catch (Exception e)
                {
                    throw new QueryException("Cannot parse query expression; " + e.toString(), e);
                }
            }
            if (node == null)
                throw new QueryException("Cannot parse query expression");

            return node;

        }
        else
            throw new QueryException("Null or empty query expression");
    }

    /**
     * Method returns a representation of an XML query as a 2 dimensional
     * String[].
     * <p>
     * Operands follow operators.
     * </p>
     * <p>
     * An operator is a String[] containing 1 String, eg: {"and"}
     * </p>
     * <p>
     * An operand is a String[] containing a term, followed by up to 6
     * attributes. eg:
     * </p>
     * <p>
     * {"Shakespeare", "1003", "3", "3", "2", "100", "1"}
     * </p>
     * 
     * @param queryNode the XML query node.
     * @return A String[][] representation of a query.
     * @throws org.jafer.query.QueryException if a problem is found in the query
     *         structure.
     */
    public String[][] getContent(Node queryNode) throws QueryException
    {

        Vector nodeData = new Vector();
        getNodeData(queryNode, nodeData);
        String[][] terms = new String[nodeData.size()][];
        for (int i = 0; i < nodeData.size(); i++)
            terms[i] = (String[]) nodeData.get(i);

        return terms;
    }

    private void getNodeData(Node node, Vector nodeData) throws QueryException
    {

        try
        {
            String name = node.getNodeName();

            if (name.equalsIgnoreCase("and") || name.equalsIgnoreCase("or"))
            {
                nodeData.add(new String[] { name });
                getNodeData(Config.selectSingleNode(node, "./*[position()=1]"), nodeData);
                getNodeData(Config.selectSingleNode(node, "./*[position()=2]"), nodeData);
            }
            else if (name.equalsIgnoreCase("not"))
            {
                nodeData.add(new String[] { name });
                getNodeData(Config.selectSingleNode(node, "./*[position()=1]"), nodeData);
            }
            else if (name.equalsIgnoreCase("constraintModel"))
                nodeData.add(getConstraintModelData(node));
        }
        catch (JaferException ex)
        {
            throw new QueryException("Error in query node: " + node.getNodeName(), ex);
        }
    }

    private String[] getConstraintModelData(Node cM) throws QueryException
    {

        String[] data;

        try
        {
            //      NodeList attributes = Config.selectNodeList(cM,
            // "./constraint/*");
            NodeList attributes = Config.selectNodeList(cM, "./*[name()='constraint']/*");
            data = new String[attributes.getLength() + 1];
            //      data[0] = getNodeValue(Config.selectSingleNode(cM, "./model"));
            data[0] = getNodeValue(Config.selectSingleNode(cM, "./*[name()='model']"));
            for (int i = 0; i < attributes.getLength(); i++)
            {
                data[i + 1] = getNodeValue(attributes.item(i));
            }

        }
        catch (JaferException ex)
        {
            throw new QueryException("Error in query node: " + cM.getNodeName(), ex);
        }

        return data;
    }

    private String getNodeValue(Node node)
    {

        String text = "";
        try
        {
            text = Config.selectSingleNode(node, "./text()").getNodeValue();
        }
        catch (Exception ex)
        {
            //String message = "Error in getting Node value: " + node.getNodeName();
            //      logger.log(Level.WARNING, message, ex);
        }

        return text;
    }

    private int lookUpUseAttribute(String attributeString) throws QueryException
    {

        int attributeInt = 0;
        try
        {
            attributeInt = Integer.parseInt(attributeString);
        }
        catch (IllegalArgumentException e)
        {

            String attributeSet, attributeName;
            int i = attributeString.indexOf('.');
            if (i > 0)
            {
                attributeSet = attributeString.substring(0, i);
                attributeName = attributeString.substring(i + 1);
            }
            else
            {
                attributeSet = Config.getAttributeSetName();
                attributeName = attributeString;
            }
            try
            {
                attributeInt = Config.getAttributeValue(attributeSet, "semantic", attributeName);
            }
            catch (JaferException ex)
            {
                throw new QueryException(ex.getMessage(), ex);
            }
        }
        return attributeInt;
    }
}