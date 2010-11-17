package org.jafer.interfaces;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Represents a search run at a particular time.
 * 
 * @author <a href="mailto:jasper.tredgold@bristol.ac.uk">Jasper Tredgold</a>
 * @version $Id$
 *
 */
public interface RecordedSearch {

    public String[] getDatabases();

    public RPNItem[] getRPN();

    public Node getQuery();
    
    public Date getDate();
    
    public Node getNode(Document xml);

    public Node getNode(Document xml, Integer index);

    public Node getNode(Document xml, int index);

}
