package org.jafer.query;

import org.jafer.interfaces.RPNItem;

/**
 * @author <a href="mailto:jasper.tredgold@bristol.ac.uk">Jasper Tredgold</a>
 * @version $Id$
 *
 */
public class RPNOperand implements RPNItem {

    private String name;
    private String value;
    
    public RPNOperand(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    /* (non-Javadoc)
     * @see org.jafer.interfaces.RPNItem#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.jafer.interfaces.RPNItem#getValue()
     */
    public String getValue() {
        return value;
    }

}
