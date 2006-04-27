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
package org.jafer.osid.provider;

import java.util.Vector;
import java.util.Map;


public class Part implements org.osid.repository.Part {

	String partKey;
	Map partValues;
	Map partTypes;
	
	protected Part(String partKey, Map partValues, Map partTypes) {
		this.partKey = partKey;
		this.partValues = partValues;
		this.partTypes = partTypes;
	}

	public java.io.Serializable getValue() throws org.osid.repository.RepositoryException {
		return (java.io.Serializable)partValues.get(partKey);
	}

	public String getDisplayName() throws org.osid.repository.RepositoryException {
		Type partType = (Type)partTypes.get(partKey);
		return partType.getKeyword();
	}
	
	public org.osid.repository.PartStructure getPartStructure() throws org.osid.repository.RepositoryException {
		return new PartStructure(partKey, partTypes);
	}


	public org.osid.repository.PartIterator getParts() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public org.osid.shared.Id getId() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	
	public org.osid.repository.Part createPart(org.osid.shared.Id partStructureId, java.io.Serializable value) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public void deletePart(org.osid.shared.Id partStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	
	public void updateDisplayName(String displayName) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	
	public void updateValue(java.io.Serializable value) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

}

