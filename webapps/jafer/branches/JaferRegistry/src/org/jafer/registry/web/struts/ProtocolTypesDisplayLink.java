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

package org.jafer.registry.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.jafer.registry.model.Protocol;

/**
 * This class links a display value against the protcol types supported
 */
public final class ProtocolTypesDisplayLink
{

    /**
     * Stores a reference to the map of display links to protocols
     */
    private static HashMap displayLink = new HashMap();

    /**
     * Stores a reference to the map of display links to protocols
     */
    private static List displayKeys = new Vector();

    /**
     * Stores a reference to the display string for Z3950
     */
    public final static String Z3950 = "Z3950 Protocol";

    /**
     * Stores a reference to the display string for SRW
     */
    public final static String SRW = "SRW Protocol";

    /**
     * Stores a reference to the SELECT display key string
     */
    public final static String SELECT = "Select from the following";
    /**
     * Sets up the list of global protocols
     */
    static
    {
        displayLink.put(Z3950, Protocol.PROTOCOL_Z3950);
        displayLink.put(SRW,Protocol.PROTOCOL_SRW);
        // could get key set but want to enforce display order
        displayKeys.add(SELECT);
        displayKeys.add(Z3950);
        displayKeys.add(SRW);
       
    }

    /**
     * Returns a list of all the display keys
     * 
     * @return A list of display keys
     */
    public static List getDisplayKeys()
    {
        return displayKeys;
    }

    /**
     * Return the protocol type for the display key
     * 
     * @param key the display key
     * @return the related protocol type
     */
    public static Protocol getProtocolType(String key)
    {
        return (Protocol) displayLink.get(key);
    }
}
