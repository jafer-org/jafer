package org.jasig.portal.portlet.xslt.util;

import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.HashMap;

public class ResourceLoader {

  private String resourceName;

  public ResourceLoader( String resourceName ) {
    this.resourceName = resourceName;
  }

  public Map getLocaleResources(String locale) {
    return getLocaleResources(new Locale(locale));
  }

  public Map getLocaleResources(Locale locale) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceName,locale);
    Map resources = new HashMap();
    for (Enumeration keys = resourceBundle.getKeys(); keys.hasMoreElements(); ) {
      String key = (String) keys.nextElement();
      resources.put(key, resourceBundle.getObject(key));
    }
    return resources;
  }

}
