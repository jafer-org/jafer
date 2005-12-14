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

import java.io.Serializable;

/**
 * When a search can return multiple services this interface is returned in the
 * list. It provides a cut down view of the found services to reduce excess data
 * being returned. It allows the caller to decide if they want the full serive
 * provider information which can be obtained by supplying this object to the
 * service manager get calls.
 */
public interface ServiceInfo extends Serializable
{
    /**
     * Returns the uniquie id of the service found.
     * 
     * @return The service id. 
     */
    public String getId();
    
    /**
     * Returns the name of the service found.
     * 
     * @return The service name
     */
    public String getName();

    
}
