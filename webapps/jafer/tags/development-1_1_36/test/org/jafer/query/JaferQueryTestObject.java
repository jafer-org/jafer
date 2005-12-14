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
package org.jafer.query;

import org.w3c.dom.Node;

/**
 * This test utility class allows a JAferQuery to be created without running
 * normalisation so that error scenarios on the converters can be tested.
 */
public class JaferQueryTestObject extends JaferQuery
{
    /**
     * Construct the JaferQuery without normalising the query
     * @param query The jafer query
     */
    public JaferQueryTestObject(Node query)
    {
        this.queryRoot = query;
    }
}
