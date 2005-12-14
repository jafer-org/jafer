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

package org.jafer.registry.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.jafer.registry.model.CategoryType;

/**
 * This class links a display value against the category types supported
 */
public final class CategoryTypesDisplayLink
{

    /**
     * Stores a reference to the map of display links to categories
     */
    private static HashMap displayLink = new HashMap();

    /**
     * Stores a reference to the map of display links to categories
     */
    private static List displayKeys = new Vector();

    /**
     * Stores a reference to the display string for general keywords
     */
    public final static String GENERAL_KEYWORDS = "General keyword";

    /**
     * Stores a reference to the display string for lcsh
     */
    public final static String LCSH = "LCSH (Library of Congress Subject Headings)";

    /**
     * Stores a reference to the display string for ddc
     */
    public final static String DDC = "DDC (Dewey Decimal Characterisation)";
    
    /**
     * Stores a reference to the SELECT display key string
     */
    public final static String SELECT = "Select from the following";
    /**
     * Sets up the list of global categories
     */
    static
    {
        displayLink.put(GENERAL_KEYWORDS, CategoryType.CATEGORY_GENERAL_KEYWORDS);
        displayLink.put(LCSH, CategoryType.CATEGORY_LCSH);
        displayLink.put(DDC, CategoryType.CATEGORY_DDC);
        // could get key set but want to enforce display order
        displayKeys.add(SELECT);
        displayKeys.add(GENERAL_KEYWORDS);
        displayKeys.add(LCSH);
        displayKeys.add(DDC);
    }

    /**
     * Returns a list of all the display keys
     * 
     * @return A list of display keys
     */
    public static List getDisplayKeys()
    {
        return displayKeys;
    }

    /**
     * Return the category type for the display key
     * 
     * @param key the display key
     * @return the related category type
     */
    public static CategoryType getCategoryType(String key)
    {
        return (CategoryType) displayLink.get(key);
    }
}
