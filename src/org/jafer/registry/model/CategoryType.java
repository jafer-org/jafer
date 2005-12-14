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

package org.jafer.registry.model;

import java.util.List;
import java.util.Vector;

/**
 * This class represents the category types supported by the registry that can
 * be attached to service providers and services
 */
public final class CategoryType
{

    /**
     * Stores a reference to the DDC (dewey decimal characterisation) category
     */
    public final static CategoryType CATEGORY_DDC = new CategoryType("DDC");

    /**
     * Stores a reference to the LCSH (library of congress subject headings)
     * category.
     */
    public final static CategoryType CATEGORY_LCSH = new CategoryType("LCSH");

    /**
     * Stores a reference to the general keyword category.
     */
    public final static CategoryType CATEGORY_GENERAL_KEYWORDS = new CategoryType("KEYWORD");

    /**
     * Stores a reference to the name of the category.
     */
    private String name = "";

    /**
     * Stores a reference to all the categoryTypes
     */
    public static List categories = null;
   
    /**
     * Sets up the list of global categories
     */
    static 
    {
        Vector categoriesList =  new Vector();
        categoriesList.add(CATEGORY_GENERAL_KEYWORDS);
        categoriesList.add(CATEGORY_LCSH);
        categoriesList.add(CATEGORY_DDC);
        categories = categoriesList;
    }
    
    /**
     * Returns a list of all the supported category types
     * @return list of Protocol objects
     */
    public static List getAllCategoryTypes()
    {
        return categories;
    }   
    
    /**
     * Private constructor to create the supported category
     * 
     * @param name The name of the category being created
     */
    private CategoryType(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the category
     * 
     * @return The category name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Compares two category instances to see if they are equal
     * 
     * @param category The category to compare against this instance
     * @return true if the two instances are the same
     */
    public boolean equals(CategoryType category)
    {
        // as we only every create static objects we can compare by hash code
        return this == category;
    }

    /**
     * Returns the to String representation of the category
     * 
     * @return The category name
     */
    public String toString()
    {
        return getName();
    }

}
