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
import java.util.Hashtable;
import java.util.Vector;
import java.io.StringWriter;

import org.osid.repository.RecordStructureIterator;

import org.jafer.osid.Util;
import org.jafer.osid.ModsUtils;

import org.jafer.util.xml.XMLSerializer;
import org.jafer.zclient.ZClient;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;
import org.jafer.record.Field;
import org.w3c.dom.Element;

/**
 * Main implementation class for the Jafer OSID provider. Please see the OKI osid
 * api 2.0 for all interface documentation.
 * <p>
 * The approach taken in this implementation is to create Maps and Vectors containing
 * the PartStructure elements for the different resultType types that are supported.
 * When a consumer performs a search, it can specify which resultType should be
 * created and the Part and PartStructure lists will provide the results according to that
 * Type. Presently implemented are; a "standard" Jafer type containing typical fields
 * from a z39.50 library repository, a MODS xml string, and MIT's Type from the Sakai
 * project.
 * <p>
 * A search is performed by calling the Jafer ZClient class to search against a target
 * z39.50 server. Results are returned by Jafer as MODS records and then mapped to the
 * Type specified as a request option (as described above).
 */

public class Repository implements org.osid.repository.Repository {

	//static final String DEFAULT_RECORD_SCHEMA = "http://www.openarchives.org/OAI/oai_marc";
	static final String DEFAULT_RECORD_SCHEMA = "http://www.loc.gov/mods/v3";

	// Types that can be used to request result set types
	private org.osid.shared.Type jaferType = new Type("jafer.org","recordStructure","library_content");
	private org.osid.shared.Type mitType = new Type("mit.edu","recordStructure","library_content");
	private org.osid.shared.Type modsType = new Type("loc.gov","recordStructure","mods_v3");

	private String displayName = null;

	// we could make the following static, but we'll as instance for now in case we use a separate
	// class to manage the types map and vectors for different result types...
	
	// jaferType
	Hashtable jaferTypesMap = new Hashtable();
	Vector jaferTypesVector = new Vector();

	// modsType
	Hashtable modsTypesMap = new Hashtable();
	Vector modsTypesVector = new Vector();

	// mitType
	Hashtable mitTypesMap = new Hashtable();
	Vector mitTypesVector = new Vector();


	protected Repository(String displayName) {
		this.displayName = displayName;
		// jaferType
		jaferTypesMap.put("title", new Type("jafer.org","partStructure","Title","Resource Title"));
		jaferTypesMap.put("author", new Type("jafer.org","partStructure","Author","First Author"));
		jaferTypesMap.put("owner", new Type("jafer.org","partStructure","Owner","Resource Owner"));
		jaferTypesMap.put("type", new Type("jafer.org","partStructure","Type","Resource Type"));
		jaferTypesMap.put("publisher", new Type("jafer.org","partStructure","Publisher","Publisher"));
		jaferTypesMap.put("dateCreated", new Type("jafer.org","partStructure","DateCreated","Date Created"));
		jaferTypesMap.put("dateIssued", new Type("jafer.org","partStructure","DateIssued","Date Issued"));
		jaferTypesMap.put("isbn", new Type("jafer.org","partStructure","isbn","ISBN"));
		jaferTypesMap.put("issn", new Type("jafer.org","partStructure","issn","ISSN"));
		jaferTypesMap.put("annotation", new Type("jafer.org","partStructure","Annotation","Annotation"));
		jaferTypesMap.put("abstract", new Type("jafer.org","partStructure","Abstract","Abstract"));
		jaferTypesMap.put("weburl", new Type("jafer.org","partStructure","URL","Web URL"));
		jaferTypesVector.addElement("title");
		jaferTypesVector.addElement("author");
		jaferTypesVector.addElement("owner");
		jaferTypesVector.addElement("type");
		jaferTypesVector.addElement("publisher");
		jaferTypesVector.addElement("dateCreated");
		jaferTypesVector.addElement("dateIssued");
		jaferTypesVector.addElement("isbn");
		jaferTypesVector.addElement("issn");
		jaferTypesVector.addElement("annotation");
		jaferTypesVector.addElement("abstract");
		jaferTypesVector.addElement("weburl");
		// modsType
		modsTypesMap.put("xml", new Type("loc.gov","partStructure","Mods3","Mods3 Xml"));
		modsTypesVector.addElement("xml");
		// mitType
		mitTypesMap.put("contributor", new Type("mit.edu","partStructure","contributor","Examples of Contributor include a person, an organization, or a service. Typically, the name of a Contributor should be used to indicate the entity."));
		mitTypesMap.put("coverage", new Type("mit.edu","partStructure","coverage", "Typically, Coverage will include spatial location (a place name or geographic coordinates), temporal period (a period label, date, or date range) or jurisdiction (such as a named administrative entity)."));
		mitTypesMap.put("creator", new Type("mit.edu","partStructure","creator", "Examples of Creator include a person, an organization, or a service. Typically, the name of a Creator should be used to indicate the entity."));
		mitTypesMap.put("date", new Type("mit.edu","partStructure","date", "Typically, Date will be associated with the creation or availability of the resource."));
		mitTypesMap.put("format", new Type("mit.edu","partStructure","format", "Typically, Format may include the media-type or dimensions of the resource. Format may be used to identify the software, hardware, or other equipment needed to display or operate the resource. Examples of dimensions include size and duration."));
		mitTypesMap.put("language", new Type("mit.edu","partStructure","language", "A language of the intellectual content of the resource."));
		mitTypesMap.put("publisher", new Type("mit.edu","partStructure","publisher", "Examples of Publisher include a person, an organization, or a service. Typically, the name of a Publisher should be used to indicate the entity."));
		mitTypesMap.put("relation", new Type("mit.edu","partStructure","relation", "A reference to a related resource."));
		mitTypesMap.put("rights", new Type("mit.edu","partStructure","rights", "Typically, Rights will contain a rights management statement for the resource, or reference a service providing such information. Rights information often encompasses Intellectual Property Rights (IPR), Copyright, and various Property Rights. If the Rights element is absent, no assumptions may be made about any rights held in or over the resource."));
		mitTypesMap.put("source", new Type("mit.edu","partStructure","source", "The present resource may be derived from the Source resource in whole or in part. Recommended best practice is to identify the referenced resource by means of a string or number conforming to a formal identification system."));
		mitTypesMap.put("subject", new Type("mit.edu","partStructure","subject", "Typically, Subject will be expressed as keywords, key phrases or classification codes that describe a topic of the resource."));
		mitTypesMap.put("type", new Type("mit.edu","partStructure","type", "Type includes terms describing general categories, functions, genres, or aggregation levels for content."));
		mitTypesMap.put("URL", new Type("mit.edu","partStructure","URL", "Link to content."));
		mitTypesVector.addElement("contributor");
		mitTypesVector.addElement("coverage");
		mitTypesVector.addElement("creator");
		mitTypesVector.addElement("date");
		mitTypesVector.addElement("format");
		mitTypesVector.addElement("language");
		mitTypesVector.addElement("publisher");
		mitTypesVector.addElement("relation");
		mitTypesVector.addElement("rights");
		mitTypesVector.addElement("source");
		mitTypesVector.addElement("subject");
		mitTypesVector.addElement("type");
		mitTypesVector.addElement("URL");
	}

	private Asset createAsset(Element modsRoot, org.osid.shared.Type resultType) throws org.osid.repository.RepositoryException {
		// jaferType
		if (resultType.isEqual(jaferType)) {
			Map modsMap = ModsUtils.getFields(modsRoot);
			RecordStructure recStructure = new RecordStructure(
				"JaferRecord", // displayName
				"Jafer Search Result Record",
				jaferType,
				jaferTypesVector,
				jaferTypesMap
			);
			Vector partsVector = new Vector();
			for (int i=0; i < jaferTypesVector.size(); i++) {
				String partKey = (String)jaferTypesVector.get(i);
				Part part = new Part(partKey, modsMap, jaferTypesMap);
				partsVector.addElement(part);
			}
			Record record = new Record( "JaferRecord", recStructure, partsVector );
			Vector recordsVector = new Vector();
			recordsVector.addElement(record);
			Asset asset = new Asset("JaferAsset", recordsVector);
			return asset;
		}
		else 
		// modsType
		if (resultType.isEqual(modsType)) {
			Hashtable map = new Hashtable();
			//get modsRoot as String
			String xml = toXMLString(modsRoot);
			map.put("xml",xml);
			RecordStructure recStructure = new RecordStructure(
				"ModsRecord", // displayName
				"Jafer Search Result Mods Record",
				modsType,
				modsTypesVector,
				modsTypesMap
			);
			Vector partsVector = new Vector();
			for (int i=0; i < modsTypesVector.size(); i++) {
				String partKey = (String)modsTypesVector.get(i);
				Part part = new Part(partKey, map, modsTypesMap);
				partsVector.addElement(part);
			}
			Record record = new Record( "JaferRecord", recStructure, partsVector );
			Vector recordsVector = new Vector();
			recordsVector.addElement(record);
			Asset asset = new Asset("JaferModsAsset", recordsVector);
			return asset;
		}
		else
		// mitType
		if (resultType.isEqual(mitType)) {
			Map modsMap = ModsUtils.getFields(modsRoot);
			Hashtable mitMap = new Hashtable();
			mitMap.put("contributor", modsMap.get("author"));
			mitMap.put("coverage",  "" );
			mitMap.put("creator",  modsMap.get("owner"));
			mitMap.put("date",  modsMap.get("dateIssued"));
			mitMap.put("format",  "" );
			mitMap.put("language",  "" );
			mitMap.put("publisher",  modsMap.get("publisher"));
			mitMap.put("relation",  "" );
			mitMap.put("rights", "" );
			mitMap.put("source",  modsMap.get("isbn"));
			mitMap.put("subject",  modsMap.get("title"));
			mitMap.put("type",  modsMap.get("type"));
			mitMap.put("URL",  modsMap.get("weburl"));
			RecordStructure recStructure = new RecordStructure(
				"MITRecord", // displayName
				"Jafer Search Result MIT Record",
				mitType,
				mitTypesVector,
				mitTypesMap
			);
			Vector partsVector = new Vector();
			for (int i=0; i < mitTypesVector.size(); i++) {
				String partKey = (String)mitTypesVector.get(i);
				Part part = new Part(partKey, mitMap, mitTypesMap);
				partsVector.addElement(part);
			}
			Record record = new Record( "MITRecord", recStructure, partsVector );
			Vector recordsVector = new Vector();
			recordsVector.addElement(record);
			Asset asset = new Asset("JaferMITAsset", recordsVector);
			return asset;
		}
		else throw new org.osid.repository.RepositoryException("resultType not found for request property");
	}

	private Vector jaferSearch(String host, int port, String databaseName, int maxRecords, String cqlString, org.osid.shared.Type resultType) throws org.osid.repository.RepositoryException {
		ZClient zclient = null;
		String recordSchema;
		Vector vec = new Vector();
		try {
			System.out.println("jaferSearch, connecting, host:"+host+" port:"+port+" databaseName:"+databaseName);
			zclient = new ZClient();
			//if (presentChunk != 0) zclient.setFetchSize(presentChunk);
			//zclient.setDataCacheSize(jaferTarget.getDataCacheSize());
			zclient.setHost(host);
			zclient.setPort(port);
			zclient.setAutoReconnect(0);
			
			System.out.println("jaferSearch, search query:"+cqlString);
			if (databaseName != null) { 
				zclient.setDatabases(databaseName);
			}
			//else if (databaseNames != null) {
			//	zclient.setDatabases(databaseNames);
			//}
			recordSchema = DEFAULT_RECORD_SCHEMA;
			//if (preferredRecordSyntax != null) recordSchema = preferredRecordSyntax;
			zclient.setRecordSchema(recordSchema);
			CQLQuery cqlQuery = new CQLQuery(cqlString);
			System.out.println("cqlQuery xml:"+cqlQuery.getXML());
			JaferQuery jaferQuery = cqlQuery.toJaferQuery();
			System.out.println("GOT jaferQuery:"+jaferQuery);
			System.out.println("jaferQuery.getQuery():"+jaferQuery.getQuery());
			// call submit
			int nCount = zclient.submitQuery(jaferQuery.getQuery());
			System.out.println("jaferSearch, search, nCount:"+nCount);

			// Get result records
			
			int nfetch;
			if (maxRecords > -1) nfetch = nCount <= maxRecords ? nCount : maxRecords;
			else nfetch = nCount;
			
			for (int i = 1; i <= nfetch; i++) { 
				zclient.setRecordCursor(i);
				// get result as OAI_MARC document
				Field field = zclient.getCurrentRecord();
				String fieldSchema = field.getRecordSchema();
				String fieldSyntax = field.getRecordSyntax();
				System.out.println("getRecord(), i="+i+" fieldSchema:"+fieldSchema+" fieldSyntax:"+fieldSyntax);
				//RecordImpl record = new RecordImpl(field.getRoot(), fieldSchema);
				Element modsRoot = (Element)field.getXML();
				Asset asset = createAsset(modsRoot, resultType);
				vec.addElement(asset);
			}

			return vec;

		} catch (Exception e) {
			throw new org.osid.repository.RepositoryException(e.toString());
		} finally {
			if (zclient != null) {
				try { zclient.close(); } catch (Exception e) {}
			}
		}
	}


	
	public String getDisplayName() throws org.osid.repository.RepositoryException {
		return this.displayName;
	}

	public org.osid.repository.AssetIterator getAssetsBySearch(java.io.Serializable searchCriteria,
		org.osid.shared.Type searchType,
		org.osid.shared.Properties searchProperties
		) throws org.osid.repository.RepositoryException 
	{
		String host;
		int port;
		String databaseName; 
		int maxRecords = 10;
		org.osid.shared.Type resultType;
		String cqlString; 
		if (searchCriteria == null) {
				throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		if (searchType == null) {
				throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		if (!(searchCriteria instanceof String)) {
				throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
		}
		cqlString = (String)searchCriteria;
		System.out.println("searchProperties:"+searchProperties);
		System.out.println("searchCriteria:"+searchCriteria);

		// get searchProperties
		try {
			host = (String)searchProperties.getProperty("host");
			if (host == null || host.length() == 0) throw new org.osid.repository.RepositoryException("Search property not found:host");
			String portstr = (String)searchProperties.getProperty("port");
			if (portstr == null || host.length() == 0) throw new org.osid.repository.RepositoryException("Search property not found:port");
			try {
				port = Integer.parseInt(portstr);
			} catch (Exception x) {
				throw new org.osid.repository.RepositoryException("Search property invalid:port:"+portstr);
			}
			databaseName = (String)searchProperties.getProperty("databaseName");
			if (databaseName == null || host.length() == 0) throw new org.osid.repository.RepositoryException("Search property not found:databaseName");
			String maxRecordsStr = (String)searchProperties.getProperty("maxRecords");
			try {
				maxRecords = Integer.parseInt(maxRecordsStr);
			} catch (Exception x) { }
			resultType = (org.osid.shared.Type)searchProperties.getProperty("resultType");
			if (resultType == null) throw new org.osid.repository.RepositoryException("Search property not found:resultType");
		} catch (org.osid.shared.SharedException ex) {
			throw new org.osid.repository.RepositoryException("Could not get required search property.");
		}
		
		java.util.Vector vec = jaferSearch(host, port, databaseName, maxRecords, cqlString, resultType);

		return new AssetIterator(vec);
	}

	/**
	* Helper class to print out the xml returned.
	*/
	private String toXMLString(Element node) {
		try	{
			StringWriter writer = new StringWriter();
			XMLSerializer.out(node, "xml", writer);
			writer.flush();
			return writer.toString();
		}	catch (org.jafer.exception.JaferException e) {
			return "Unable to convert XML to string:"+e.toString();
		}
	}



	public void updateDisplayName(String displayName) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.Id getId() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.Type getType() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public String getDescription() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void updateDescription(String description) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.Asset createAsset(String displayName, String description, org.osid.shared.Type assetType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void deleteAsset(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.AssetIterator getAssets() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.AssetIterator getAssetsByType(org.osid.shared.Type assetType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.TypeIterator getAssetTypes() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.Properties getPropertiesByType(org.osid.shared.Type propertiesType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.TypeIterator getPropertyTypes() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.PropertiesIterator getProperties() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public RecordStructureIterator getRecordStructures() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public RecordStructureIterator getRecordStructuresByType(org.osid.shared.Type recordStructureType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public RecordStructureIterator getMandatoryRecordStructures(org.osid.shared.Type assetType) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.TypeIterator getSearchTypes() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.TypeIterator getStatusTypes() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.Type getStatus(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public boolean validateAsset(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public void invalidateAsset(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.Asset getAsset(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.repository.Asset getAssetByDate(org.osid.shared.Id assetId, long date) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.LongValueIterator getAssetDates(org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public org.osid.shared.Id copyAsset(org.osid.repository.Asset asset) throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public boolean supportsVersioning() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	public boolean supportsUpdate() throws org.osid.repository.RepositoryException {
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
	}


}
