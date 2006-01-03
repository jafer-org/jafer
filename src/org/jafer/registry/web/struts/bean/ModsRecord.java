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

package org.jafer.registry.web.struts.bean;

import java.util.Map;
import java.util.HashMap;
import org.jafer.record.Field;

/**
 * This bean represents a mods record
 */
public class ModsRecord
{

    /**
     * Use this string to get the author field
     */
    public final transient static String AUTHOR = "author";

    /**
     * Use this string to get the type field
     */
    public final transient static String TYPE = "type";

    /**
     * Use this string to get the title field
     */
    public final transient static String TITLE = "title";

    /**
     * Use this string to get the owner field
     */
    public final transient static String OWNER = "owner";

    /**
     * Use this string to get the isbn field
     */
    public final transient static String ISBN = "isbn";

    /**
     * Stores a reference to the mods fields this bean displays
     */
    private Map fields = null;

    /**
     * Constructor
     *
     * @param map The map of fileds
     */
    public ModsRecord(Map map) {
        fields = map;
    }

    private static String getSubNodeText(Field f, String n1) {
        Field s = f.getFirst(n1);
        if (f != null) {
            return s.getValue();
        } else {
            return "";
        }
    }
    private static String getSubNodeText(Field f, String n1, String n2) {
        Field s = f.getFirst(n1);
        if (f != null) {
            return getSubNodeText(s, n2);
        } else {
            return "";
        }
    }


    public ModsRecord(Field modsroot)
    {
        HashMap map = new HashMap();
        String str;
        // title
        map.put("title", stripLastChar(getSubNodeText(modsroot, "titleInfo", "title")));
        // author - find the first name entry and if there's a displayForm use
        // it, else try for namePart
        str = getSubNodeText(modsroot, "name", "displayForm");
        if (str.length() == 0)
        {
            str = getSubNodeText(modsroot, "name", "namePart");
        }
        map.put("author", stripLastChar(str));
        // owner - find a name where there is a role/roleTerm of 'creator' - if
        // so use displayForm or namePart as above
        str = getOwner(modsroot);
        if (str.length() > 0)
        {
            map.put("owner", stripLastChar(str));
        }
        // ohters are straighforward
        map.put("type", getSubNodeText(modsroot, "typeOfResource"));
        map.put("publisher", stripLastChar(getSubNodeText(modsroot, "originInfo", "publisher")));
        map.put("dateCreated", stripLastChar(getSubNodeText(modsroot, "originInfo", "dateCreated")));
        map.put("dateIssued", stripLastChar(getSubNodeText(modsroot, "originInfo", "dateIssued")));
        map.put("isbn", modsroot.getFirst("identifier", "type", "isbn").getValue());
        map.put("issn", modsroot.getFirst("identifier", "type", "issn").getValue());
        map.put("annotation", modsroot.getFirst("note", "type", "annotation").getValue());
        map.put("abstract", getSubNodeText(modsroot, "abstract"));
        map.put("weburl", getSubNodeText(modsroot, "location", "url"));
        fields = map;
    }


    /**
     * Gets the specified field from the record
     *
     * @param key The key of the field required
     * @return The field text
     */
    public String getField(String key)
    {
        if (fields != null)
        {
            String data = (String) fields.get(key);
            if (data != null)
            {
                return data;
            }
        }
        return "";
    }

    /**
     * Gets the author
     *
     * @return Returns the author.
     */
    public String getAuthor()
    {
        return getField(AUTHOR);
    }

    /**
     * Gets the ISBN
     *
     * @return Returns the ISBN.
     */
    public String getIsbn()
    {
        return getField(ISBN);
    }

    /**
     * Gets the Owner
     *
     * @return Returns the Owner.
     */
    public String getOwner()
    {
        return getField(OWNER);
    }

    /**
     * Gets the title
     *
     * @return Returns the title.
     */
    public String getTitle()
    {
        return getField(TITLE);
    }

    /**
     * Gets the type
     *
     * @return Returns the type.
     */
    public String getType()
    {
        return getField(TYPE);
    }

    private static String stripLastChar(String str)
    {
        str = str.trim();
        if (str.length() == 0)
            return str;
        char ch = str.charAt(str.length() - 1);
        if (ch == '/' || ch == ',' || ch == ':')
        {
            return str.substring(0, str.length() - 1);
        }
        else
        {
            return str;
        }
    }

    /**
     * Retrieves the owner's displayForm or namePart if exists.
     */
    private static String getOwner(Field modsroot)
    {
        String name = "";
        Field[] list = modsroot.get("name");
        for (int i = 0; i < list.length; i++)
        {
            Field node = list[i];
            String role = getSubNodeText(node, "role", "roleTerm");
            if (role.equals("creator"))
            {
                name = getSubNodeText(node, "displayForm");
                if (name.length() == 0)
                {
                    name = getSubNodeText(node, "namePart");
                }
                return name;
            }
        }
        return name;
    }
}
