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
package org.jafer.osid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.jafer.util.xml.DomUtils;

/**
 * Extracts a set of fields from a DOM document in MODS 3.0 format and returns
 * them in a Map for display purposes. We should load this class in a factory so
 * it can be customised for different implementations (later if required)
 */
public class ModsUtils {

    /**
     * Extract Mods 3.0 Reource fields from DOM object and return as a Map
     */
    public static Map getFields(Element modsroot) {
        HashMap map = new HashMap();
        NodeList list;
        String str;
        // title
        map.put("title", stripLastChar(DomUtils.getSubNodeText(modsroot, "titleInfo", "title")));
        // author - find the first name entry and if there's a displayForm use
        // it, else try for namePart
        str = DomUtils.getSubNodeText(modsroot, "name", "displayForm");
        if (str.length() == 0)
        {
            str = DomUtils.getSubNodeText(modsroot, "name", "namePart");
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
        map.put("type", DomUtils.getChildTextValue(modsroot, "typeOfResource"));
        map.put("publisher", stripLastChar(DomUtils.getSubNodeText(modsroot, "originInfo", "publisher")));
        map.put("dateCreated", stripLastChar(DomUtils.getSubNodeText(modsroot, "originInfo", "dateCreated")));
        map.put("dateIssued", stripLastChar(DomUtils.getSubNodeText(modsroot, "originInfo", "dateIssued")));
        map.put("isbn", DomUtils.getElementTextByAttr(modsroot, "identifier", "type", "isbn"));
        map.put("issn", DomUtils.getElementTextByAttr(modsroot, "identifier", "type", "issn"));
        map.put("annotation", DomUtils.getElementTextByAttr(modsroot, "note", "type", "annotation"));
        map.put("abstract", DomUtils.getChildTextValue(modsroot, "abstract"));
        map.put("weburl", DomUtils.getSubNodeText(modsroot, "location", "url"));
        return map;
    }

    public static String stripLastChar(String str)
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
     * Creates a mods collection element in the document
     * 
     * @param doc
     * @return the created mods collection
     */
    public static Node addModCollection(Document doc)
    {
        return doc.appendChild(doc.createElement("modscollection"));
    }

    /**
     * Updates the mods records first annotation with supplied text or creates
     * one if none found
     * 
     * @param modsElement the mods element to update
     * @param annotation The new annotation text to set
     */
    public static void updateAnnotation(Element modsElement, String annotation)
    {
        // see if we can find annotation text already in Mods element
        if (DomUtils.hasElementWithAttr(modsElement, "note", "type", "annotation"))
        {
            // just update this element
            DomUtils.setElementTextByAttr(modsElement, "note", "type", "annotation", annotation);
        }
        else
        {
            // we need to add a new annotation element
            addAnnotation(modsElement, annotation);
        }
    }

    public static Node addAnnotation(Node docroot, String annotation)
    {
        Document doc = docroot.getOwnerDocument();
        Element recordRoot = doc.getDocumentElement();
        Element annonode = doc.createElement("note");
        annonode.setAttribute("type", "annotation");
        Text txt;
        txt = doc.createTextNode(annotation);
        annonode.appendChild(txt);
        recordRoot.appendChild(annonode);
        return recordRoot;
    }


    /**
     * Generates a mods record.
     * 
     * @param fields - fields to generate
     * @param docId - the docId representing this mods document's db identifier
     * @param origin - value identifying the mods record source (i.e. which
     *        collection it comes from)
     * @param mdctype - enumerated mdc type signifying mdc record type (header
     *        or list item)
     * @param refDocIds - a list of consituent docid's if this mods record is a
     *        RL header (may be null if not)
     */
    public static String toModsRecordXml(Map fields, String docId, String origin, String mdctype, List refDocIds)
    {
        String str, str1;
        StringBuffer sb = new StringBuffer("");
				sb.append("<mods version=\"3.0\" xmlns=\"http://www.loc.gov/mods/v3\"");
				if (docId != null && docId.length() > 0) {
					sb.append(" ID=\"").append(docId).append("\"");
				}
				sb.append(">\n");
        sb.append("<titleInfo>").append("\n");
        sb.append("<title>").append(ModsUtils.getField(fields, "title")).append("</title>").append("\n");
        sb.append("</titleInfo>").append("\n");
        // author
        sb.append("<name type=\"personal\">").append("\n");
        sb.append("<namePart>").append(ModsUtils.getField(fields, "author")).append("</namePart>").append("\n");
        sb.append("<displayForm>").append(ModsUtils.getField(fields, "author")).append("</displayForm>").append("\n");
        sb.append("<role><roleTerm type=\"text\">author</roleTerm></role>").append("\n");
        sb.append("</name>").append("\n");
        // owner
        str = ModsUtils.getField(fields, "owner");
        sb.append("<name type=\"personal\">").append("\n");
        sb.append("<displayForm>").append(str).append("</displayForm>").append("\n");
        sb.append("<role><roleTerm type=\"text\">creator</roleTerm></role>").append("\n");
        sb.append("</name>").append("\n");
        // others - straightforward
        str = ModsUtils.getField(fields, "type");
        if (str.length() > 0)
            sb.append("<typeOfResource>").append(str).append("</typeOfResource>").append("\n");
        str = ModsUtils.getField(fields, "abstract");
        if (str.length() > 0)
            sb.append("<abstract>").append(str).append("</abstract>").append("\n");
        sb.append("<originInfo>").append("\n");
        str = ModsUtils.getField(fields, "publisher");
        if (str.length() > 0)
            sb.append("<publisher>").append(str).append("</publisher>").append("\n");
        str = ModsUtils.getField(fields, "dateIssued");
        if (str.length() > 0)
            sb.append("<dateIssued>").append(str).append("</dateIssued>").append("\n");
        sb.append("</originInfo>").append("\n");
        str = ModsUtils.getField(fields, "isbn");
        if (str.length() > 0)
            sb.append("<identifier type=\"isbn\">").append(str).append("</identifier>").append("\n");
        str = ModsUtils.getField(fields, "issn");
        if (str.length() > 0)
            sb.append("<identifier type=\"issn\">").append(str).append("</identifier>").append("\n");
        str = ModsUtils.getField(fields, "annotation");
        if (str.length() > 0)
            sb.append("<note type=\"annotation\">").append(str).append("</note>").append("\n");
        str = ModsUtils.getField(fields, "weburl");
        if (str.length() > 0)
            sb.append("<location><url>").append(str).append("</url></location>").append("\n");
        // recordInfo used in repository
        sb.append("<recordInfo>\n");
        sb.append("<recordContentSource>").append(mdctype).append("</recordContentSource>").append("\n");
        sb.append("<recordOrigin>").append(origin).append("</recordOrigin>").append("\n");
        //sb.append("<recordIdentifier>").append(docId).append("</recordIdentifier>").append("\n");
        sb.append("</recordInfo>\n");
        // refDocIds - these occur in a RL header - the related constituent
        // resources
        if (refDocIds != null)
        {
            for (int i = 0; i < refDocIds.size(); i++)
            {
                sb.append("<relatedItem type=\"constituent\"><identifier type=\"local\">");
                sb.append(refDocIds.get(i));
                sb.append("</identifier></relatedItem>").append("\n");
            }
        }
        sb.append("</mods>").append("\n");
        return sb.toString();
    }

    private void dumpNodeList(NodeList list)
    {
        System.out.println("nodelistdump:");
        for (int i = 0; i < list.getLength(); i++)
        {
            Element node = (Element) list.item(i);
            System.out.println("tag:" + node.getNodeName());
        }
        System.out.println("endnodelistdump.");
    }

    /**
     * Retrieves the owner's displayForm or namePart if exists.
     */
    private static String getOwner(Element modsroot)
    {
        String name = "";
        NodeList list = modsroot.getElementsByTagName("name");
        for (int i = 0; i < list.getLength(); i++)
        {
            Element node = (Element) list.item(i);
            String role = DomUtils.getSubNodeText(node, "role", "roleTerm");
            if (role.equals("creator"))
            {
                name = DomUtils.getChildTextValue(node, "displayForm");
                if (name.length() == 0)
                {
                    name = DomUtils.getChildTextValue(node, "namePart");
                }
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves RL's constituent docId's as relatedItems.
     */
    public static List getRelatedDocIds(Element mdcroot)
    {
        ArrayList al = new ArrayList();
        NodeList l = mdcroot.getElementsByTagName("relatedItem");
        for (int i = 0; i < l.getLength(); i++)
        {
            Node nd = l.item(i);
            NodeList l1 = ((Element) nd).getElementsByTagName("identifier");
            if (l1.getLength() > 0)
            {
                Node nd1 = l1.item(0);
                String resourceid = DomUtils.getTextValue(nd1);
                al.add(resourceid);
            }
        }
        return al;
    }

		public static String getField(Map fields, String fieldName) {
			String field = "";
			if (fieldName != null && fields != null) field = (String)fields.get(fieldName);
			if (field == null) field = "";
			return field;
		}

}
