package org.jafer.query.converter;

import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.w3c.dom.Node;

public class JaferQueryConverter extends Converter
{

    /**
     * Stores a reference to the query builder
     */
    private static QueryBuilder builder = new QueryBuilder();

    /**
     * normalises the jafer query node to apply demorgans laws and clear up any
     * double ngatives in the query
     * 
     * @param jaferQueryNode The query to process
     * @return The normailised query
     * @throws QueryException
     */
    public static Node normaliseJaferQuery(Node jaferQueryNode) throws QueryException
    {
        return processNode(jaferQueryNode);
    }

    /**
     * This method applies the demorgan laws to convert the query to a
     * normalised fashion. It parses the whole tree converting: <ui>
     * <li> <i>not(g) or not(p)</i> into <i> not(g and p)</i> </li>
     * <li> <i>not(g) and not(p)</i> into <i> not(g or p)</i></ui></li>
     * <li>and removes any double negatives <i>not(not(g))</i></li>
     * <br>
     * 
     * @param node The node to convert
     * @return The resulting new node
     * @throws QueryException
     */
    protected static Node processNode(Node node) throws QueryException
    {
        // make sure we have a node to process
        if (node != null)
        {
            // get the name of the node
            String nodeName = node.getNodeName();
            if (nodeName.equalsIgnoreCase("constraintModel"))
            {
                // if it is a constraint model we have nothing more to do so
                // just return it
                // A -> A
                return node;
            }
            else if (nodeName.equalsIgnoreCase("and"))
            {
                return processAndNode(node);
            }
            else if (nodeName.equalsIgnoreCase("or"))
            {
                return processOrNode(node);
            }
            else if (nodeName.equalsIgnoreCase("not"))
            {
                return processNotNode(node);
            }
        }
        return null;
    }

    /**
     * Apply demorgans laws to the OR node
     * 
     * @param node The node to process
     * @return The updated resulting node
     * @throws QueryException
     */
    protected static Node processOrNode(Node node) throws QueryException
    {
        Node newNode = null;
        // get the two nodes that form the OR clause
        Node left = getFirstChild(node);
        Node right = getSecondChild(node);

        // before we can apply de morgans laws we need to make sure the
        // left and right conditions of the OR node have already been processed
        // for demorgans laws as they could have now been converted to a NOT
        left = processNode(left);
        right = processNode(right);

        // we only apply demorgans law if both first and second are now NOTs
        if (left.getNodeName().equalsIgnoreCase("not") && right.getNodeName().equalsIgnoreCase("not"))
        {
            // we need to apply demorgans laws
            // NOT A OR NOT B -> NOT (A AND B)
            newNode = builder.not(builder.and(left.getFirstChild(), right.getFirstChild()));
        }
        // process a not as the left operand
        else if (left.getNodeName().equalsIgnoreCase("not"))
        {
            // NOT A OR B -> NOT (A AND NOT B)
            newNode = builder.not(builder.and(left.getFirstChild(), builder.not(right)));
        }
        // process a not as the right operand
        else if (right.getNodeName().equalsIgnoreCase("not"))
        {
            // A OR NOT B -> NOT (B AND NOT A)
            newNode = builder.not(builder.and(right.getFirstChild(), builder.not(left)));
        }
        else
        {
            // we do not need to apply demorgans law so build an OR
            // A OR B -> A or B
            newNode = builder.or(left, right);
        }
        return newNode;
    }

    /**
     * Apply demorgans laws to the AND node
     * 
     * @param node The node to process
     * @return The updated resulting node
     * @throws QueryException
     */
    protected static Node processAndNode(Node node) throws QueryException
    {
        Node newNode = null;
        // get the two nodes that form the AND clause
        Node left = getFirstChild(node);
        Node right = getSecondChild(node);

        // before we can apply de morgans laws we need to make sure the
        // left and right conditions of the AND node have already been processed
        // for demorgans laws as they could have now been converted to a NOT
        left = processNode(left);
        right = processNode(right);

        // we only apply demorgans law if both first and second are now NOTs
        if (left.getNodeName().equalsIgnoreCase("not") && right.getNodeName().equalsIgnoreCase("not"))
        {
            // we need to apply demorgans laws
            // NOT A AND NOT B -> NOT (A OR B)
            newNode = builder.not(builder.or(left.getFirstChild(), right.getFirstChild()));
        }
        // make all ands with a not look the same
        else if (left.getNodeName().equalsIgnoreCase("not"))
        {
            // normalise the query so all ands with a single not are viewied the
            // same way
            // NOT A AND B -> B AND NOT A
            newNode = builder.and(right, left);
        }
        else
        {
            // we do not need to apply demorgans law so we rebuild an and
            // A AND B -> A AND B
            // A AND NOT B -> A AND NOT B
            newNode = builder.and(left, right);
        }
        return newNode;
    }

    /**
     * Apply demorgans laws to the NOT node and remove any double negatives
     * 
     * @param node The node to process
     * @return The updated resulting node
     * @throws QueryException
     */
    protected static Node processNotNode(Node node) throws QueryException
    {
        Node newNode = null;
        // get the node that forms the NOT clause
        Node condition = getFirstChild(node);

        // before we can apply de morgans laws we need to make sure the
        // condition of the NOT node has already been processed
        // for demorgans laws as it could have now been converted to a NOT
        condition = processNode(condition);

        // we now need to check for to see if the condition of this NOT clause
        // has now become a NOT forming a double negative
        if (condition.getNodeName().equalsIgnoreCase("not"))
        {
            // we have a double negative to remove
            // NOT NOT A -> A
            newNode = condition.getFirstChild();
        }
        else
        {
            // we do not need to fix double negatives build a NOT
            // NOT A -> NOT A
            newNode = builder.not(condition);
        }

        return newNode;
    }
}
