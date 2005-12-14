/**
 * JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
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

package org.jafer.registry.uddi.model;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.util.TModelBag;
import org.uddi4j.util.TModelKey;

/**
 * This class represents a specific TModel.This Class should never be directly
 * instantiated.
 */
public class TModel
{

    /**
     * Stores a reference to the set of actual TModels
     */
    private Vector models = null;

    /**
     * Stores a reference to the name of this TModel
     */
    private String name = null;

    /**
     * TModel Constructor
     */
    public TModel(Vector models, String name)
    {
        this.models = models;
        this.name = name;
    }

    /**
     * Get the name of this TModel
     * 
     * @return The Tmodel name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the actual UDDI TModels for this model. This is not exposed on the
     * interface to the caller and therfore should not be used outside of this
     * framework.
     * 
     * @return List of actual tmodels
     */
    public List getActualModels()
    {
        return models;
    }

    /**
     * Adds the TModel to the tmodels if it does not already exist
     * 
     * @param tmodels The TModelInstanceDetails list
     * @return Updated TModelInstanceDetails list
     */
    public TModelInstanceDetails addTModel(TModelInstanceDetails tmodels)
    {
        // if it deos not exist then create it
        if (tmodels == null)
        {
            tmodels = new TModelInstanceDetails();
        }
        // set when the tmodel key is already found in list
        boolean found = false;
        // get the list of TModels to add
        Iterator modelsToAdd = this.getActualModels().iterator();
        while (modelsToAdd.hasNext())
        {
            found = false;
            // get TModel to add
            org.uddi4j.datatype.tmodel.TModel modelToAdd = (org.uddi4j.datatype.tmodel.TModel) modelsToAdd.next();
            // check not already added
            Iterator iter = tmodels.getTModelInstanceInfoVector().iterator();
            while (iter.hasNext())
            {
                TModelInstanceInfo info = (TModelInstanceInfo) iter.next();
                if (info.getTModelKey().equals(modelToAdd.getTModelKey()))
                {
                    // no need to add this model so stop searching
                    found = true;
                    break;
                }
            }
            // if it was not found then add it
            if (!found)
            {
                tmodels.getTModelInstanceInfoVector().add(new TModelInstanceInfo(modelToAdd.getTModelKey()));
            }
        }
        return tmodels;

    }

    /**
     * Removes the TModel to the tmodels if exists
     * 
     * @param tmodels The TModelInstanceDetails list
     * @return Updated TModelInstanceDetails list
     * @throws TModelDoesNotExistException
     */
    public TModelInstanceDetails removeTModel(TModelInstanceDetails tmodels)// throws
                                                                            // TModelDoesNotExistException
    {
        // if it deos not exist then create it
        if (tmodels == null)
        {
            // throw new TModelDoesNotExistException(this.getName());
        }

        // before we can delete we need to make sure that all elements in the
        // TModel exist
        boolean found = false;
        Vector toDelete = new Vector();
        // get the models to remove
        Iterator modelsToRemove = this.getActualModels().iterator();
        // now loop round and make sure they all exist
        while (modelsToRemove.hasNext())
        {
            found = false;
            // get TModel to remove
            org.uddi4j.datatype.tmodel.TModel modelToRemove = (org.uddi4j.datatype.tmodel.TModel) modelsToRemove.next();
            // check not already added
            Iterator iter = tmodels.getTModelInstanceInfoVector().iterator();
            while (iter.hasNext())
            {
                TModelInstanceInfo info = (TModelInstanceInfo) iter.next();
                if (info.getTModelKey().equals(modelToRemove.getTModelKey()))
                {
                    // found so add to delete list
                    found = true;
                    toDelete.add(info);
                    break;
                }
            }
            // if it was not found then throw erro
            if (!found)
            {
                //throw new TModelDoesNotExistException(this.getName());
            }
        }
        // remove the infos to delete
        tmodels.getTModelInstanceInfoVector().removeAll(toDelete);
        return tmodels;
    }

    /**
     * Checks to see if the TModel is supported
     * 
     * @param tmodels The TModelInstanceDetails list to check against
     * @return True if the TModel is found
     */
    public boolean supportsTModel(TModelInstanceDetails tmodels)
    {
        // if it deos not exist then can not be supported
        if (tmodels == null)
        {
            return false;
        }

        // get the models to remove
        Iterator modelsToSearch = this.getActualModels().iterator();
        // now loop round and make sure at least one exists
        while (modelsToSearch.hasNext())
        {
            // get TModel to search for
            org.uddi4j.datatype.tmodel.TModel modelToSearch = (org.uddi4j.datatype.tmodel.TModel) modelsToSearch.next();
            // check to see if exists
            Iterator iter = tmodels.getTModelInstanceInfoVector().iterator();
            while (iter.hasNext())
            {
                TModelInstanceInfo info = (TModelInstanceInfo) iter.next();
                if (info.getTModelKey().equals(modelToSearch.getTModelKey()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method adds this Tmodel to the tmodel bag supplied. This is not
     * exposed on the interface to the caller and therefore should not be used
     * outside of this framework.
     * 
     * @param tModelBag The tmodel bag to add tmodel to
     * @return The updated tmodel bag
     */
    public TModelBag addToTModelBag(TModelBag tModelBag)
    {
        // if we do not have a tmodel bag create one
        if (tModelBag == null)
        {
            tModelBag = new TModelBag();
        }
        
        boolean found = false;
        // The tmodel contains multiple models to add so process each one at a time
        Iterator iter = models.iterator();
        while(iter.hasNext())
        {
            found = false;
            // get the uddi TModel
            org.uddi4j.datatype.tmodel.TModel model = (org.uddi4j.datatype.tmodel.TModel) iter.next();
            // check each to see if tmodel already exists
            Iterator bagIter = tModelBag.getTModelKeyVector().iterator();
            while (bagIter.hasNext())
            {
                TModelKey tmodelKey = (TModelKey) bagIter.next();
                // compare to see if it already exists
                if(tmodelKey.getText().equals(model.getTModelKey()))
                {
                    // mark it as found and break the inner loop
                    found = true;
                    break;
                }
            }
            // if it was not found add it to the tmodel bag
            if (!found)
            {
                // add the uddi TModel to the bag
                tModelBag.add(new TModelKey(model.getTModelKey()));
            }
        }
        return tModelBag;
    }
}
