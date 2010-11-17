package org.jafer.query;

import org.jafer.interfaces.RPNItem;

/**
 * @author <a href="mailto:jasper.tredgold@bristol.ac.uk">Jasper Tredgold</a>
 * @version $Id$
 *
 */
public class RPNOperator implements RPNItem {

    public enum Op { AND, OR }
    
    private Op value;
    
    public RPNOperator(Op value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.jafer.interfaces.RPNItem#getName()
     */
    public String getName() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jafer.interfaces.RPNItem#getValue()
     */
    public String getValue() {
        return value.toString();
    }

}
