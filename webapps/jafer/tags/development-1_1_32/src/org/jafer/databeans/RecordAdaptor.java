/**
 * JAFER Toolkit Poject.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jafer.databeans;

//import java.util.logging.Logger;
//import java.util.logging.Level;
import org.jafer.exception.*;
import org.jafer.interfaces.*;
import org.jafer.record.*;
import org.jafer.util.xml.*;
import org.w3c.dom.*;

/**
 * <p>transforms a record via getCurrentRecord method using templates object (eg. recordAdaptor.xsl - specified via server.xml).
 * If sourceSchema is specified check is done on schema of record prior to transformation</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class RecordAdaptor extends Adaptor {

  public RecordAdaptor() {}

  public Field getCurrentRecord() throws JaferException {

    String recordSchema, sourceSchema, targetSchema;
    Element recordRoot = (Element)((Present)getDatabean()).getCurrentRecord().getRoot();
    sourceSchema = getSourceSchema();
    targetSchema = getTargetSchema();

    if (recordRoot.hasAttribute("schema")) {
      recordSchema = recordRoot.getAttribute("schema");
    } else throw new JaferException("schema attribute not found in XMLRecord root");

    if (sourceSchema == null)
      recordRoot = transformRecord(recordRoot, targetSchema);
    else if (recordSchema.equalsIgnoreCase(sourceSchema))
      recordRoot = transformRecord(recordRoot, targetSchema);

    return new Field(recordRoot, recordRoot.getFirstChild());
  }

  private Element transformRecord(Element recordRoot, String targetSchema) throws JaferException {
    /** @todo we just transform the record - should we transform root wrapper as well?
     *  This would allow access to root attributes
     */
    Node record = recordRoot.getFirstChild();
    recordRoot.removeChild(record);
    record = XMLTransformer.transform(record, getTransform());
    recordRoot.appendChild(record);
    if (targetSchema != null)
      recordRoot.setAttribute("schema", targetSchema);
    return recordRoot;
  }
}