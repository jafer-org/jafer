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

import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Element;
// imports for non-implemented methods

//import org.osid.repository.AssetIterator;
//import org.osid.repository.Record;
//import org.osid.repository.RecordIterator;
//import org.osid.repository.RecordStructure;
//import org.osid.repository.RecordStructureIterator;
//import org.osid.repository.Part;
//import org.osid.repository.PartIterator;

import org.jafer.osid.ModsUtils;


/**
 * Asset manages the Asset itself.  Assets have content as well as Records
 * appropriate to the AssetType and RecordStructures for the Asset.  Assets
 * may also contain other Assets.
 */
public class Asset implements org.osid.repository.Asset {

	private org.osid.shared.Type assetType = new Type("jafer.org","asset","library_content");
	
	String displayName = "";
	Vector recordsVector;

	protected Asset(String displayName, Vector recordsVector) throws org.osid.repository.RepositoryException {
		this.displayName = displayName;
		this.recordsVector = recordsVector;
	}

	public org.osid.repository.RecordIterator getRecords() throws org.osid.repository.RepositoryException {
		return new org.jafer.osid.provider.RecordIterator(recordsVector);
	} 

	public String getDisplayName() throws org.osid.repository.RepositoryException {
		return displayName;
	}
	public org.osid.shared.Type getAssetType() throws org.osid.repository.RepositoryException {
		return assetType;
	}



	public org.osid.shared.Id getId() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public String getDescription() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public java.io.Serializable getContent() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.RecordIterator getRecordsByRecordStructureType(org.osid.shared.Type recordStructureType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void updateDisplayName(String displayName) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void updateEffectiveDate(long effectiveDate) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void updateExpirationDate(long expirationDate) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public long getEffectiveDate() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public long getExpirationDate() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void updateDescription(String description) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.Id getRepository() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void updateContent(java.io.Serializable content) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void addAsset(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void removeAsset(org.osid.shared.Id assetId, boolean includeChildren) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.AssetIterator getAssets() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.AssetIterator getAssetsByType(org.osid.shared.Type assetType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.Record createRecord(org.osid.shared.Id recordStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void inheritRecordStructure(org.osid.shared.Id assetId, org.osid.shared.Id recordStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void copyRecordStructure(org.osid.shared.Id assetId, org.osid.shared.Id recordStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void deleteRecord(org.osid.shared.Id recordId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.RecordIterator getRecordsByRecordStructure(org.osid.shared.Id recordStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.RecordStructureIterator getRecordStructures() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.RecordStructure getContentRecordStructure() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.Record getRecord(org.osid.shared.Id recordId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
	}
	public org.osid.repository.Part getPart(org.osid.shared.Id partId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
	}
	public java.io.Serializable getPartValue(org.osid.shared.Id partId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.PartIterator getPartsByPartStructure(org.osid.shared.Id partStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.ObjectIterator getPartValuesByPartStructure(org.osid.shared.Id partStructureId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}


}
