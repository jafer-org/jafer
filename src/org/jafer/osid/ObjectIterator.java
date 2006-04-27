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

public class ObjectIterator implements org.osid.shared.ObjectIterator {

	private java.util.Vector vector = new java.util.Vector();
	private int i = 0;
	
	public ObjectIterator(java.util.Vector vector) throws org.osid.shared.SharedException {
		this.vector = vector;
	}
	
	public boolean hasNextObject() throws org.osid.shared.SharedException {
		return i < vector.size();
	}
	
	public java.io.Serializable nextObject() throws org.osid.shared.SharedException {
		if (i < vector.size()) {
			return (java.io.Serializable)vector.elementAt(i++);
		} else {
			throw new org.osid.shared.SharedException(org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS);
		}
	}

}
