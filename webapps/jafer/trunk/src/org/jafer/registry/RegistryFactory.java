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

package org.jafer.registry;

/** * This interface defines the methods that the Registry Factory must provide.
 * 
 * @uml.dependency supplier="org.jafer.registry.RegistryManager" stereotypes="Basic::Create"
 */

public class RegistryFactory
{

    /**
     * This method creates a registry manager that manages an instance to the
     * specified registry using the supplied URLs.
     * 
     * @param inquiryURL The URL to the registry inquiry service
     * @param publishURL The URL to the registry publish service
     * @return An instance of the registry manager 
     * @throws RegistryInitialisationException
     */
    public static RegistryManager createRegistryManager(String inquiryURL, String publishURL)
            throws RegistryException
    {
        return new org.jafer.registry.uddi.RegistryManager(inquiryURL, publishURL);
    }
}
