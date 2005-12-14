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

import org.jafer.registry.model.CategoryDoesNotExistException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.KeyedReference;

/**
 * This class represents an instance of a category. A category can be attached
 * to any service provider or service to allow easy identification when using
 * the service locator. A category for the UDDI is related to a TModelkey that
 * is hidden inside this implementation
 */
public class Category implements org.jafer.registry.model.Category
{

    /**
     * Stores a reference to the TModel key for the category
     */
    private String key = null;

    /**
     * Stores a reference to the name of the category
     */
    private String name = null;

    /**
     * Stores a reference to the value of this category
     */
    private String value = null;

    /**
     * Constructor
     * 
     * @param categoryTModel The TModel representing the category
     * @param value The value to apply to the category
     */
    public Category(TModel categoryTModel, String value)
    {
        // Category TModels only ever have one defined TModel so get first key.
        // This must exist as empty TModels can not be constructed. Cast the
        // TModel up to its actual internal implementation
        org.uddi4j.datatype.tmodel.TModel uddi4jModel = (org.uddi4j.datatype.tmodel.TModel) ((org.jafer.registry.uddi.model.TModel) categoryTModel)
                .getActualModels().get(0);

        this.key = uddi4jModel.getTModelKey();
        // name is always the same as the the TModel Key
        this.name = categoryTModel.getName();
        this.value = value;
    }

    /**
     * Create a category from a Keyed Reference
     * 
     * @param reference The keyed reference to create from
     */
    public Category(KeyedReference reference)
    {
        key = reference.getTModelKey();
        name = reference.getKeyName();
        value = reference.getKeyValue();
    }
    
    /**
     * Converts the category to a keyed reference
     * 
     * @return The cretaed keyed reference
     */
    public KeyedReference toKeyedReference()
    {
        return new KeyedReference(name, value, key);
    }

    /**
     * Checks if the keyed reference values match the values in the category
     * 
     * @param reference The reference to check
     * @return true if all values match
     */
    public boolean equalsKeyedReference(KeyedReference reference)
    {
        return (key.equalsIgnoreCase(reference.getTModelKey()) && name.equalsIgnoreCase(reference.getKeyName()) && value
                .equalsIgnoreCase(reference.getKeyValue()));
    }

    /**
     * Gets the TModel key for the category.This is not exposed on the interface
     * to the caller and therfore should not be used outside of this framework.
     * 
     * @return Returns the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Gets the name of this category
     * 
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the value of this category
     * 
     * @return Returns the value.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value on this category
     * 
     * @param value The value to set.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Helper method to extract all categories out of a category bag. This is
     * not exposed on the interface to the caller and therefore should not be
     * used outside of this framework.
     * 
     * @param categoryBag The category bag to extract from
     * @return The list of created categories
     */
    public static List getCategories(CategoryBag categoryBag)
    {
        Vector categories = new Vector();
        if (categoryBag != null)
        {
            // get all the categories
            Iterator iter = categoryBag.getKeyedReferenceVector().iterator();
            while (iter.hasNext())
            {
                // for each category create an internal category
                Category cat = new org.jafer.registry.uddi.model.Category((KeyedReference) iter.next());
                categories.add(cat);
            }
        }
        // return empty collection
        return categories;
    }

    /**
     * This method adds this category to the category bag supplied. This is not
     * exposed on the interface to the caller and therefore should not be used
     * outside of this framework.
     * 
     * @param categoryBag The category bag to add category to
     * @return The updated category bag
     */
    public CategoryBag addToCategoryBag(CategoryBag categoryBag)
    {
        // if we do not have a category bag create one
        if (categoryBag == null)
        {
            categoryBag = new CategoryBag();
        }
        // check if category already exists
        Iterator iter = categoryBag.getKeyedReferenceVector().iterator();
        while (iter.hasNext())
        {
            // check if the category equals the keyed reference returned
            if (this.equalsKeyedReference((KeyedReference) iter.next()))
            {
                // category already added so ignore
                return categoryBag;
            }
        }
        // add the category
        categoryBag.add(this.toKeyedReference());
        return categoryBag;
    }

    /**
     * This method removes this category to the category bag supplied. This is
     * not exposed on the interface to the caller and therefore should not be
     * used outside of this framework.
     * 
     * @param categoryBag The category bag to delete category from
     * @return The updated category bag
     * @throws CategoryDoesNotExistException
     */
    public CategoryBag removeFromCategoryBag(CategoryBag categoryBag) throws CategoryDoesNotExistException
    {
        // signals if the category was removed
        boolean removed = false;
        // if we do not have a category bag nothing to delete so just return
        if (categoryBag == null)
        {
            throw new CategoryDoesNotExistException(this.getName(), this.getValue());
        }
        // check if category already exists
        Iterator iter = categoryBag.getKeyedReferenceVector().iterator();
        while (iter.hasNext())
        {
            KeyedReference keyedReference = (KeyedReference) iter.next();
            // check if the category equals the keyed reference returned
            if (this.equalsKeyedReference(keyedReference))
            {
                // category exists so delete and return
                categoryBag.remove(keyedReference);
                removed = true;
                break;
            }
        }
        // check if we managed to remove
        if (!removed)
        {
            // did not remove so throw exception
            throw new CategoryDoesNotExistException(this.getName(), this.getValue());
        }
        return categoryBag;
    }
    
 }
