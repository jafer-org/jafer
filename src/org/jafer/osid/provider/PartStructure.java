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


public class PartStructure implements org.osid.repository.PartStructure {

	String partKey;
	Map partTypes;

	protected PartStructure(String partKey, Map partTypes)	{
		this.partKey = partKey;
		this.partTypes = partTypes;
	}		

	public String getDisplayName() throws org.osid.repository.RepositoryException {
		Type partType = (Type)partTypes.get(partKey);
		return partType.getKeyword();
	}

	public String getDescription() throws org.osid.repository.RepositoryException {
		Type partType = (Type)partTypes.get(partKey);
		return partType.getDescription();
	}

	public org.osid.shared.Type getType() throws org.osid.repository.RepositoryException {
		Type partType = (Type)partTypes.get(partKey);
		return partType;
	}

	public boolean isPopulatedByRepository() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public boolean isMandatory() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public boolean isRepeatable() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public void updateDisplayName(String displayName) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public org.osid.shared.Id getId() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public org.osid.repository.RecordStructure getRecordStructure() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public org.osid.repository.PartStructureIterator getPartStructures() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public boolean validatePart(org.osid.repository.Part part) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

}

