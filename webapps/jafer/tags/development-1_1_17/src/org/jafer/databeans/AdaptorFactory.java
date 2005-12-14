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
 *
 *
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2002
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.databeans;

import org.jafer.interfaces.DatabeanFactory;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.Z3950Connection;
import org.jafer.exception.JaferException;
import org.jafer.util.xml.XMLTransformer;
import javax.xml.transform.Templates;
import java.net.URL;

abstract public class AdaptorFactory extends DatabeanFactory {

  private String styleSheetPath, sourceSchema, targetSchema;
  private DatabeanFactory databeanFactory;

  public AdaptorFactory() {}

  public Databean getDatabean(Adaptor adaptor) {

    adaptor.setDatabean(databeanFactory.getDatabean());

    if (this.getTransform() != null) {
      Templates template = null;
      try {
        URL resource = this.getClass().getClassLoader().getResource(getTransform());
        template = XMLTransformer.createTemplate(resource);
      } catch (JaferException ex) {
        System.out.println("AdaptorFactory; cannot create Templates object");
      }
      adaptor.setTransform(template);
      adaptor.setSourceSchema(this.getSourceSchema());
      adaptor.setTargetSchema(this.getTargetSchema());
    }

    return adaptor;
  }

  public void setTransform(String styleSheetPath) {
    this.styleSheetPath = styleSheetPath;
  }

  public String getTransform() {
    return styleSheetPath;
  }

  public void setSourceSchema(String sourceSchema) {
    this.sourceSchema = sourceSchema;
  }

  public String getSourceSchema() {
    return sourceSchema;
  }

  public void setTargetSchema(String targetSchema) {
    this.targetSchema = targetSchema;
  }

  public String getTargetSchema() {
    return targetSchema;
  }

  public void setDatabeanFactory(DatabeanFactory databeanFactory) {
    this.databeanFactory = databeanFactory;
  }

  public DatabeanFactory getDatabeanFactory() {
    return databeanFactory;
  }
}