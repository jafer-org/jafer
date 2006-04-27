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

import java.io.*;

public class RepositoryManager implements org.osid.repository.RepositoryManager {

	org.osid.OsidContext context;
	java.util.Properties configuration;

	public void assignOsidContext(org.osid.OsidContext context) throws org.osid.OsidException {
		this.context = context;
	}
	public void assignConfiguration(java.util.Properties configuration) throws org.osid.OsidException {
		this.configuration = configuration;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		configuration.list(new PrintStream(bout));
		System.out.println("Repository Manager, config properties:\n"+bout.toString());
	}
	public org.osid.OsidContext getOsidContext() throws org.osid.OsidException {
		return context;
	}


	public org.osid.repository.RepositoryIterator getRepositories() throws org.osid.repository.RepositoryException {
		java.util.Vector v = new java.util.Vector();
		v.addElement(new Repository("Jafer Osid Repository"));
		return new RepositoryIterator(v);
	}

    public org.osid.repository.Repository getRepository(org.osid.shared.Id repositoryId) throws org.osid.repository.RepositoryException { 
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
		}
    public org.osid.repository.Repository createRepository(String displayName, String description, org.osid.shared.Type repositoryType) throws org.osid.repository.RepositoryException {
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
		}
    public void deleteRepository(org.osid.shared.Id repositoryId) throws org.osid.repository.RepositoryException {
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
		}
    public org.osid.repository.RepositoryIterator getRepositoriesByType(org.osid.shared.Type repositoryType) throws org.osid.repository.RepositoryException {
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
    public org.osid.repository.AssetIterator getAssetsBySearch(org.osid.repository.Repository[] repositories,java.io.Serializable searchCriteria, org.osid.shared.Type searchType,org.osid.shared.Properties searchProperties) throws org.osid.repository.RepositoryException {
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
		}
    public org.osid.shared.Id copyAsset(org.osid.repository.Repository repository,org.osid.shared.Id assetId) throws org.osid.repository.RepositoryException {
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
		}
    public org.osid.shared.TypeIterator getRepositoryTypes() throws org.osid.repository.RepositoryException {
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED); 
		}

		/**
     * Verify to OsidLoader that it is loading
     * 
     * <p>
     * OSID Version: 2.0
     * </p>
     * .
     */
		 public void osidVersion_2_0() throws org.osid.OsidException {
		 }


}


