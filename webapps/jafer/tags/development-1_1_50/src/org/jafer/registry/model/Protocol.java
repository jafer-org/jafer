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

package org.jafer.registry.model;

import java.util.List;
import java.util.Vector;

/**
 * This class represents the protocols that services support. The implementation
 * of the registry must map these to its objects that represent the specified
 * protocol.
 */
/**
 * 
 */
public final class Protocol
{

    /**
     * Stores a reference to the Z3950 protocol.
     */
    public final static Protocol PROTOCOL_Z3950 = new Protocol("Z3950");

    /**
     * Stores a reference to the SRW protocol.
     */
    public final static Protocol PROTOCOL_SRW = new Protocol("SRW");

    /**
     * Stores a reference to all the protocols
     */
    public static List protocols = null;
   
    /**
     * Sets up the list of global protocols
     */
    static 
    {
        Vector protocolList =  new Vector();
        protocolList.add(PROTOCOL_Z3950);
        protocolList.add(PROTOCOL_SRW);
        protocols = protocolList;
    }
    
    /**
     * Returns a list of all the supported protocols
     * @return list of Protocol objects
     */
    public static List getAllProtocols()
    {
        return protocols;
    }   
    
    /**
     * Stores a reference to the name of the protocol. 
     */
    private String name = "";

    /**
     * Private constructor to create the supported protocols
     * 
     * @param name The name of the protocol being created
     */
    private Protocol(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the protocol
     * 
     * @return The protocols name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Compares two protocol instances to see if they are equal
     * 
     * @param protocol The protocol to compare against this instance
     * @return true if the two instances are the same
     */
    public boolean equals(Protocol protocol)
    {
        // as we only every create static objects we can compare by hash code
        return this == protocol;
    }

    /**
     * Returns the to String representation of the protocol
     * 
     * @return The protocols name
     */
    public String toString()
    {
        return getName();
    }

}
