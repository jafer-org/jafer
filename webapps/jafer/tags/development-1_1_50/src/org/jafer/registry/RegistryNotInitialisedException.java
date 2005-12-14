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

/**
 * This exception is thrown when an expected TModel was not found. This is not a
 * fatal exception it just informs caller they need to intialise the registry first.
 */
public class RegistryNotInitialisedException extends org.jafer.registry.uddi.RegistryExceptionImpl
{

    public RegistryNotInitialisedException(String name)
    {
        super("The model " + name + " was not found");
    }
}
