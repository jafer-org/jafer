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

import org.jafer.osid.provider.*;

public class RecordStructure implements org.osid.repository.RecordStructure {

	String displayName = "";
	String description = "";
	String schema = "";
	org.osid.shared.Type type;
	Vector partTypesVector;
	Map partTypesMap;

	protected RecordStructure(
		String displayName, 
		String description, 
		org.osid.shared.Type type,
		Vector partTypesVector,
		Map partTypesMap
		) 
	{
		this.displayName = displayName;
		this.description = description;
		this.type = type;
		this.partTypesVector = partTypesVector;
		this.partTypesMap = partTypesMap;
	}
	
	public String getDisplayName() throws org.osid.repository.RepositoryException {
		return this.displayName;
	}
	
	public org.osid.repository.PartStructureIterator getPartStructures() throws org.osid.repository.RepositoryException {
		Vector vec = new Vector();
		for (int i=0; i < partTypesVector.size(); i++) {
			String partKey = (String)partTypesVector.get(i);
			PartStructure partStructure = new PartStructure(partKey, partTypesMap);
			vec.addElement(partStructure);
		}
		return new PartStructureIterator(vec);
	}
	
	public String getDescription() throws org.osid.repository.RepositoryException {
		return this.description;
	}
	
	public org.osid.shared.Type getType() throws org.osid.repository.RepositoryException {
		return this.type;
	}

	public String getSchema() throws org.osid.repository.RepositoryException {
		return this.schema;
	}


	public String getFormat() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public boolean isRepeatable() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public org.osid.shared.Id getId() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public void updateDisplayName(String displayName) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

	public boolean validateRecord(org.osid.repository.Record record) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}

}

