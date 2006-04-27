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

public class Record implements org.osid.repository.Record {

	String displayName = "";
	java.util.Vector partVector;
	RecordStructure recordStructure;
	
	protected Record(String displayName,
		RecordStructure recordStructure,
		java.util.Vector partVector) 
		throws org.osid.repository.RepositoryException 
	{
		this.displayName = displayName;
		this.partVector = partVector;
		this.recordStructure = recordStructure;
	}
	
	public String getDisplayName() throws org.osid.repository.RepositoryException {
		return this.displayName;
	}

	public org.osid.repository.PartIterator getParts() throws org.osid.repository.RepositoryException {
		return new PartIterator(this.partVector);
	}

	public org.osid.repository.RecordStructure getRecordStructure() throws org.osid.repository.RepositoryException {
		return recordStructure;
	}

	public boolean isMultivalued() throws org.osid.repository.RepositoryException {
		return false;
	}


	public org.osid.shared.Id getId() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public org.osid.repository.Part createPart(org.osid.shared.Id partStructureId,java.io.Serializable value) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public void deletePart(org.osid.shared.Id partId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public void updateDisplayName(String displayName) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

}
