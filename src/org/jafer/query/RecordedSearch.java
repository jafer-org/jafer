package org.jafer.query;

import java.util.Date;

import org.jafer.interfaces.RPNItem;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A RecordedSearch captures a search executed at a particular time. It is immutable.
 * 
 * @author <a href="mailto:jasper.tredgold@bristol.ac.uk">Jasper Tredgold</a>
 * @version $Id$
 *
 */
public class RecordedSearch implements org.jafer.interfaces.RecordedSearch {

    private Node query;
    private String[] databases;
    private Date date;
    private Document document;
    private RPNItem[] rpn;
    
    public RecordedSearch(Node query, RPNItem[] rpn, String[] databases, Date date) {
        this.document = DOMFactory.newDocument();
        this.query = document.importNode(query, true);
        this.date = new Date(date.getTime());
        this.databases = new String[databases.length];
        System.arraycopy(databases, 0, this.databases, 0, databases.length);
        this.rpn = new RPNItem[rpn.length];
        System.arraycopy(rpn, 0, this.rpn, 0, rpn.length);
    }
    
    /* (non-Javadoc)
     * @see org.jafer.interfaces.RecordedSearch#getDatabases()
     */
    public String[] getDatabases() {
        String[] ret = new String[databases.length];
        System.arraycopy(databases, 0, ret, 0, databases.length);
        return ret;
    }

    public RPNItem[] getRPN() {
        RPNItem[] ret = new RPNItem[rpn.length];
        System.arraycopy(rpn, 0, ret, 0, rpn.length);
        return ret;
    }

    /* (non-Javadoc)
     * @see org.jafer.interfaces.RecordedSearch#getDate()
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /* (non-Javadoc)
     * @see org.jafer.interfaces.RecordedSearch#getQuery()
     */
    public Node getQuery() {
        return query.cloneNode(true);
    }
    
    public Node getNode(Document xml) {
        return getNode(xml, null);
    }

    public Node getNode(Document xml, int index) {
        return getNode(xml, new Integer(index));
    }

    public Node getNode(Document xml, Integer index) {
        
        Element srrNode = xml.createElement("search");
        if(index != null) { 
            srrNode.setAttribute("id", Integer.toString(index));
        }
        srrNode.setAttribute("date", getDate().toString());
        for(String database: getDatabases()) {
            Element dbNode = xml.createElement("database");
            dbNode.appendChild(xml.createTextNode(database));
            srrNode.appendChild(dbNode);
        }
        Node query = xml.importNode(getQuery(), true);
        srrNode.appendChild(query);
        Element rpnNode = xml.createElement("rpn");
        RPNItem[] rpn = getRPN();
        for(RPNItem item: rpn) {
            if(item == null)
                continue;
            Element itemNode = xml.createElement("item");
            if(item instanceof RPNOperand) {
                itemNode.setAttribute("name", item.getName());
            }
            itemNode.appendChild(xml.createTextNode(item.getValue()));
            rpnNode.appendChild(itemNode);
        }
        srrNode.appendChild(rpnNode);
        return srrNode;
    }

}
