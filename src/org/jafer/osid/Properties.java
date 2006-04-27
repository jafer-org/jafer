/** JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
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
package org.jafer.osid;

import java.util.Map;
import java.util.Iterator;
import java.util.Vector;

public class Properties implements org.osid.shared.Properties {

	org.osid.shared.Type type;
	Map properties;

	public Properties(org.osid.shared.Type type, Map properties) {
		this.type = type;
		this.properties = properties;
	}

	public org.osid.shared.Type getType() throws org.osid.shared.SharedException {
		return type;
	}

	public java.io.Serializable getProperty(java.io.Serializable key) throws org.osid.shared.SharedException {
		return (java.io.Serializable)properties.get(key);
	}

	public org.osid.shared.ObjectIterator getKeys() throws org.osid.shared.SharedException {
		Iterator itr = properties.keySet().iterator();
		Vector vec = new Vector();
		while (itr.hasNext()) {
			vec.addElement(itr.next());
		}
		return new ObjectIterator(vec);
	}

}
