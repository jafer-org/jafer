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
package org.jafer.util;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;

import org.jafer.exception.JaferException;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This class resolves all XSLT transformer references from with in an XSLT
 * stylesheet
 */
public class XMLFileUriResolver implements URIResolver
{

    /**
     * Stores a reference to package that should be placed before the href to
     * retrieve the requested file
     */
    private String packageRef = "";

    /**
     * Constructor
     *
     * @param packageRef Ref of the package that should be placed before the
     *        href to retrieve the requested file format org/jafer ....
     */
    public XMLFileUriResolver(String packageRef)
    {
        this.packageRef = packageRef;
    }

    /**
     * Resolves any references inside XSLT that require
     *
     * @param href An href attribute, which may be relative or absolute.
     * @param base The base URI against which the first argument will be made
     *        absolute if the absolute URI is required.
     */
    public Source resolve(String href, String base) throws TransformerException
    {

        try
        {
            // get the input stream requested
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(packageRef + href);
            // parse the stream into a node
            Node node = (Node) DOMFactory.parse(input);
            // create the DOMSource to be returned
            DOMSource domSource = new DOMSource(node);
            return domSource;
        }
        catch (JaferException e)
        {
            // something went wrong parsng the file throw the exception to stop
            // the processing of the calling file
            throw new TransformerException(e);
        }
    }

}
