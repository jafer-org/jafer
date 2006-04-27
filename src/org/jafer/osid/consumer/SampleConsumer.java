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
package org.jafer.osid.consumer;

import java.util.Hashtable;

import org.jafer.osid.Properties;


public class SampleConsumer {

	public static void main(String[] args) throws Exception {
		SampleConsumer con = new SampleConsumer();
		con.sampleCallJaferType();
		con.sampleCallModsType();
		con.sampleCallMitType();
	}

	public void sampleCallJaferType() {
		try {
			org.osid.OsidContext context = new org.osid.OsidContext();
			java.util.Properties config = new java.util.Properties();
			org.osid.repository.RepositoryManager repositoryManager = (org.osid.repository.RepositoryManager)org.osid.OsidLoader.getManager(
				"org.osid.repository.RepositoryManager",
				"org.jafer.osid.provider",
				context,
				config
			);
			// query properties
			org.osid.shared.Type queryType = new Type("loc.gov","srw","cql");
			java.io.Serializable searchCriteria = "dc.title=frog";
			// result properties
			org.osid.shared.Type resultType = new Type("jafer.org","recordStructure","library_content");
			Hashtable props = new Hashtable();
			props.put("host","library.ox.ac.uk");
			props.put("port","210");
			props.put("databaseName","advance");
			props.put("maxRecords","5");
			props.put("resultType", resultType);
			org.osid.shared.Properties searchProperties = new Properties(resultType, props);
			// now send query to available repositories
			org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
			while (repositoryIterator.hasNextRepository()) {
				org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
				System.out.println("Found a Repository called " + nextRepository.getDisplayName());
				
				org.osid.repository.AssetIterator assetIterator = nextRepository.getAssetsBySearch(
					searchCriteria,
					queryType, //new org.osid.types.mit.KeywordSearchType(),
					searchProperties
				);
				while (assetIterator.hasNextAsset()) {
					org.osid.repository.Asset asset = assetIterator.nextAsset();
					System.out.println("Found an Asset called " + asset.getDisplayName());
					// Firstly record for Type("jafer.org","recordStructure","library_content")
					System.out.println("Records for asset:"+asset.getDisplayName());
					org.osid.repository.RecordIterator recordIterator = asset.getRecords();
					while (recordIterator.hasNextRecord()) {
						org.osid.repository.Record record = recordIterator.nextRecord();
						System.out.println("Record:"+record.getDisplayName());
						org.osid.repository.PartIterator partIterator = record.getParts();
						while (partIterator.hasNextPart()) {
							org.osid.repository.Part part = partIterator.nextPart();
							String field = part.getPartStructure().getDisplayName();
							String value = (String)part.getValue();
							System.out.println("Part:"+field+" value:"+value);
						}
					}				
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void sampleCallModsType() {
		try {
			org.osid.OsidContext context = new org.osid.OsidContext();
			java.util.Properties config = new java.util.Properties();
			org.osid.repository.RepositoryManager repositoryManager = (org.osid.repository.RepositoryManager)org.osid.OsidLoader.getManager(
				"org.osid.repository.RepositoryManager",
				"org.jafer.osid.provider",
				context,
				config
			);
			// query properties
			org.osid.shared.Type queryType = new Type("loc.gov","srw","cql");
			java.io.Serializable searchCriteria = "dc.title=frog";
			// result properties
			org.osid.shared.Type resultType = new Type("loc.gov","recordStructure","mods_v3");
			Hashtable props = new Hashtable();
			props.put("host","library.ox.ac.uk");
			props.put("port","210");
			props.put("databaseName","advance");
			props.put("maxRecords","5");
			props.put("resultType", resultType);
			org.osid.shared.Properties searchProperties = new Properties(resultType, props);
			// now send query to available repositories
			org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
			while (repositoryIterator.hasNextRepository()) {
				org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
				System.out.println("Found a Repository called " + nextRepository.getDisplayName());
				
				org.osid.repository.AssetIterator assetIterator = nextRepository.getAssetsBySearch(
					searchCriteria,
					queryType, //new org.osid.types.mit.KeywordSearchType(),
					searchProperties
				);
				while (assetIterator.hasNextAsset()) {
					org.osid.repository.Asset asset = assetIterator.nextAsset();
					System.out.println("Found an Asset called " + asset.getDisplayName());
					// Firstly record for Type("jafer.org","recordStructure","library_content")
					System.out.println("Records for asset:"+asset.getDisplayName());
					org.osid.repository.RecordIterator recordIterator = asset.getRecords();
					while (recordIterator.hasNextRecord()) {
						org.osid.repository.Record record = recordIterator.nextRecord();
						System.out.println("Record:"+record.getDisplayName());
						org.osid.repository.PartIterator partIterator = record.getParts();
						while (partIterator.hasNextPart()) {
							org.osid.repository.Part part = partIterator.nextPart();
							String field = part.getPartStructure().getDisplayName();
							String value = (String)part.getValue();
							System.out.println("Part:"+field+" value:"+value);
						}
					}				
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void sampleCallMitType() {
		try {
			org.osid.OsidContext context = new org.osid.OsidContext();
			java.util.Properties config = new java.util.Properties();
			org.osid.repository.RepositoryManager repositoryManager = (org.osid.repository.RepositoryManager)org.osid.OsidLoader.getManager(
				"org.osid.repository.RepositoryManager",
				"org.jafer.osid.provider",
				context,
				config
			);
			// query properties
			org.osid.shared.Type queryType = new Type("loc.gov","srw","cql");
			java.io.Serializable searchCriteria = "dc.title=frog";
			// result properties
			org.osid.shared.Type resultType = new Type("mit.edu","recordStructure","library_content");
			Hashtable props = new Hashtable();
			props.put("host","library.ox.ac.uk");
			props.put("port","210");
			props.put("databaseName","advance");
			props.put("maxRecords","5");
			props.put("resultType", resultType);
			org.osid.shared.Properties searchProperties = new Properties(resultType, props);
			// now send query to available repositories
			org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
			while (repositoryIterator.hasNextRepository()) {
				org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
				System.out.println("Found a Repository called " + nextRepository.getDisplayName());
				
				org.osid.repository.AssetIterator assetIterator = nextRepository.getAssetsBySearch(
					searchCriteria,
					queryType, //new org.osid.types.mit.KeywordSearchType(),
					searchProperties
				);
				while (assetIterator.hasNextAsset()) {
					org.osid.repository.Asset asset = assetIterator.nextAsset();
					System.out.println("Found an Asset called " + asset.getDisplayName());
					// Firstly record for Type("jafer.org","recordStructure","library_content")
					System.out.println("Records for asset:"+asset.getDisplayName());
					org.osid.repository.RecordIterator recordIterator = asset.getRecords();
					while (recordIterator.hasNextRecord()) {
						org.osid.repository.Record record = recordIterator.nextRecord();
						System.out.println("Record:"+record.getDisplayName());
						org.osid.repository.PartIterator partIterator = record.getParts();
						while (partIterator.hasNextPart()) {
							org.osid.repository.Part part = partIterator.nextPart();
							String field = part.getPartStructure().getDisplayName();
							String value = (String)part.getValue();
							System.out.println("Part:"+field+" value:"+value);
						}
					}				
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

		
}
