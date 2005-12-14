/**
 * JAFER Toolkit Project.
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
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.servlet;

import org.jafer.zclient.*;
import org.jafer.util.xml.*;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.exception.JaferException;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.*;
import java.net.URL;
import java.io.*;

import org.w3c.dom.*;

public class ZServlet extends HttpServlet {

  private ServletConfig servletConfig;
  private static final String[] initParams = {"target", "port", "databaseName", "maxHits"};
  private int maxHits;
  private URL recordXSLT, listXSLT, errorXSLT;
  private static Map attributeMap;

  public void doGet(HttpServletRequest request, HttpServletResponse response)
                                        throws ServletException, IOException {


    response.setContentType("text/html; charset=UTF-8");
    PrintWriter writer = response.getWriter();


    ZClient client = new ZClient();

  /* load required and optional initParameters, including stylesheets: */
    loadInitParams(client);

    client.setRemoteAddress(request.getRemoteAddr());
    client.setCheckRecordFormat(true);
    client.setParseQuery(false);

    Node root = client.getDocument().createElement("root");

    int totalResults;

    try {
      totalResults = submitQuery(request, client);
    } catch (JaferException j) {
      root.appendChild(createExceptionNode(client, j, "Connection or query error."));
      createErrorOutput(root, writer);
      close(client);
      return;
    }

    if (totalResults == 0)
      createOutput(root, listXSLT, writer, totalResults);

    else if (totalResults == 1) {
      try {
        client.setRecordCursor(1);
	root.appendChild(client.getCurrentRecord().getXML());
        createOutput(root, recordXSLT, writer, totalResults);
      } catch (JaferException j) {
        root.appendChild(createExceptionNode(client, j, "Error in record request/transformation."));
        createErrorOutput(root, writer);
      }
    }

    else {
      for (int i = 1; i <= ((totalResults > maxHits) ? maxHits : totalResults); i++) {
        try {
          client.setRecordCursor(i);
          root.appendChild(client.getCurrentRecord().getXML());
        } catch (JaferException j) {
        root.appendChild(createExceptionNode(client, j, "Error in record request/transformation."));
        }
      }
      createOutput(root, listXSLT, writer, totalResults);
    }
   close(client);
  }


  public void doPost(HttpServletRequest request, HttpServletResponse response)
                                        throws ServletException, IOException {
    doGet(request, response);
  }


  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    this.servletConfig = super.getServletConfig();
  }


  private void loadInitParams(ZClient client) throws ServletException {

    /* check for required initParameters: */
    for (int i=0; i<initParams.length; i++) {
      if (servletConfig.getInitParameter(initParams[i]) == null ||
            servletConfig.getInitParameter(initParams[i]).equals(""))
        throw new ServletException("Initialisation parameter incorrectly set/missing: "+initParams[i]);
    }

    try {
     /* these parameter names match the names in initParams array: */
      client.setHost(servletConfig.getInitParameter("target"));
      client.setPort(Integer.parseInt(servletConfig.getInitParameter("port")));
      client.setDatabases(servletConfig.getInitParameter("databaseName"));

      maxHits = Integer.parseInt(servletConfig.getInitParameter("maxHits"));
      client.setDataCacheSize(maxHits);

    } catch (NumberFormatException e) {
      throw new ServletException("Invalid numerical value for port or maxHits initialisation parameter/s.");
    }

  /* optional initParameters: (if parameter is missing, ZClient uses its defaults.) */

    client.setRecordSchema(servletConfig.getInitParameter("recordSchema"));
    client.setElementSpec(servletConfig.getInitParameter("elementSpec"));
    client.setUsername(servletConfig.getInitParameter("username"));
    client.setPassword(servletConfig.getInitParameter("password"));


  /* other initParameters: */
    try {
      if (servletConfig.getInitParameter("autoReconnect") != null)
        client.setAutoReconnect(Integer.parseInt(servletConfig.getInitParameter("autoReconnect")));
      if (servletConfig.getInitParameter("timeout") != null)
        client.setTimeout(Integer.parseInt(servletConfig.getInitParameter("timeout")));
      if (servletConfig.getInitParameter("fetchSize") != null)
        client.setFetchSize(Integer.parseInt(servletConfig.getInitParameter("fetchSize")));
      if (servletConfig.getInitParameter("fetchView") != null)
        client.setFetchView(Double.parseDouble(servletConfig.getInitParameter("fetchView")));

      attributeMap = DOMFactory.getMap(loadResource("defaultAttributes"));
    }
    catch (NumberFormatException e) {
      throw new ServletException("Invalid numerical value for autoReconnect, timeout, fetchSize or fetchView parameter: "+e.getMessage());
    }
    catch (JaferException j) {
      throw new ServletException (j.getMessage(), j);
    }

  /* stylesheet initParameters: */
    recordXSLT = loadResource("recordXSLT");
    listXSLT = loadResource("listXSLT");
    errorXSLT = loadResource("errorXSLT");
  }


  private URL loadResource(String paramName) throws ServletException {

    URL resource;

    try {
      resource = this.getClass().getClassLoader().getResource(servletConfig.getInitParameter(paramName));
    } catch (NullPointerException e) {
      throw new ServletException("Parameter specifying stylesheet or resource location not found: "+paramName, e);
    }

    if (resource != null)
      return resource;
    else
      throw new ServletException("Stylesheet or resource with parameter name: "+paramName+" not loaded.");
  }


  public int submitQuery(HttpServletRequest request, ZClient client) throws JaferException {

    Enumeration paramNames = request.getParameterNames();

    String paramName, paramValue, useAttribute;
    String[] values;
    Vector terms = new Vector();
    Vector useAttributes = new Vector();

    if (paramNames.hasMoreElements()) {
      while (paramNames.hasMoreElements()) {
        paramName = ((String)paramNames.nextElement());
        values = request.getParameterValues(paramName);

// paramName temporarily changed to lowercase in lookup:

        if (attributeMap.containsKey(paramName.toLowerCase())) {
          useAttribute = (String)attributeMap.get(paramName.toLowerCase());
        }
        else  throw new QueryException("Input parameter ("+paramName+") not valid.");

        if (!useAttribute.equalsIgnoreCase("ignore")) {
          for (int i=0; i<values.length; i++) {
            paramValue = values[i];
            useAttributes.add(paramName.toLowerCase());
            terms.add(paramValue);
          }
        }
      }
    }
    else  throw new QueryException("At least one input parameter needed.");

    Node queryNode = processQueryTerms(useAttributes, terms);
    return client.submitQuery(queryNode);
  }


  private Node processQueryTerms(Vector attributes, Vector terms) throws QueryException, JaferException {

    QueryBuilder qBuilder;

    if (servletConfig.getInitParameter("searchProfile") != null)
      qBuilder = new QueryBuilder(servletConfig.getInitParameter("searchProfile"));
    else
      qBuilder = new QueryBuilder();

    Vector orAttributes = new Vector();
    Vector orTerms = new Vector();
    Vector andAttributes = new Vector();
    Vector andTerms = new Vector();


    for (int i=0; i<attributes.size(); i++) {
      String att = attributes.get(i).toString();
      String term = terms.get(i).toString();

      if (orAttributes.contains(att)) {
        int j = orAttributes.lastIndexOf(att)+1;
        orAttributes.add(j, att);
        orTerms.add(j, term);
      }
      else if (andAttributes.contains(att)) {
        int j = andAttributes.indexOf(att);
        orAttributes.add(andAttributes.remove(j));
        orTerms.add(andTerms.remove(j));
        orAttributes.add(att);
        orTerms.add(term);
      }
      else {
        andAttributes.add(att);
        andTerms.add(term);
      }
    }

    Node currentNode;

    try {
      if (andAttributes.size() > 0) {

     // replace each item in the Vector with nodes made from each att/term:
        for (int i=0; i<andAttributes.size(); i++) {
          int att = Integer.parseInt((String)attributeMap.get((String)andAttributes.get(i)));
          currentNode = qBuilder.getNode(att, andTerms.get(i).toString());
          andAttributes.remove(i);
          andAttributes.add(i, currentNode);
        }
      }

    Node left, right, orNode = null, orTree = null;

    if (orAttributes.size() > 0) {
      int previousAtt = 0;
      currentNode = null;

      for (int i=0; i<orAttributes.size(); i++) {
        int att = Integer.parseInt((String)attributeMap.get((String)orAttributes.get(i)));
        if (currentNode != null) {
          if (att == previousAtt) {
            right = qBuilder.getNode(att, orTerms.get(i).toString());
            currentNode = qBuilder.or(currentNode, right);
          }
          else {
            andAttributes.add(0, currentNode);
            left = qBuilder.getNode(att, orTerms.get(i).toString());
            right = qBuilder.getNode(orAttributes.get(i+1).toString(), orTerms.get(i+1).toString());
            currentNode = qBuilder.or(left, right);
            i++;
          }
        }
        else
          currentNode = qBuilder.getNode(att, orTerms.get(i).toString());

        previousAtt = att;
      }
      andAttributes.add(0, currentNode);
    }

  //AND all nodes in andAttributes Vector:
    currentNode = null;
    for (int i=0; i<andAttributes.size(); i++) {
      if (currentNode != null)
        currentNode = qBuilder.and(currentNode, (Node)andAttributes.get(i));
      else
        currentNode = (Node)andAttributes.get(i);
    }
    return currentNode;

    } catch (QueryException e) {
        throw new QueryException(e.getMessage(), e);
    }
  }


  private void createOutput(Node node, URL stylesheet, PrintWriter writer, int totalResults) {

    Node outputNode;

    Map paramMap = new Hashtable();
    paramMap.put("maxHits", String.valueOf(maxHits));
    paramMap.put("totalResults", String.valueOf(totalResults));

    try {
      XMLSerializer.transformOutput(node, stylesheet, paramMap, writer);
    }
    catch (JaferException j) {
      Node root = node.getOwnerDocument().createElement("root");
      root.appendChild(DOMFactory.getExceptionNode(node.getOwnerDocument(), j, j.getStackTrace(), "Problem with transformation"));
      createErrorOutput(root, writer);
    }
  }


  private void createErrorOutput(Node node, PrintWriter writer) {

    createOutput(node, errorXSLT, writer, 0);
  }


  private Node createExceptionNode(ZClient client, Exception e, String errorMessage) {

    Node exceptionNode = DOMFactory.getExceptionNode(client.getDocument(), e, e.getStackTrace(), errorMessage);
    return exceptionNode;
  }


  private void close(ZClient client) {

   try {
      client.close();
    } catch (JaferException j) {
      System.err.print("WARNING: Could not close ZClient connection " + j.toString());
      }
    client = null;
    System.gc();
  }

}