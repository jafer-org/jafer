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

import org.jafer.zclient.ZClient;
import org.jafer.query.QueryBuilder;
import org.jafer.record.Diagnostic;
import org.jafer.zclient.operations.PresentException;
import org.jafer.record.RecordException;
import org.jafer.util.Config;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLTransformer;
import org.jafer.util.xml.XMLSerializer;
import org.jafer.exception.JaferException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.URL;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.*;
import javax.servlet.http.*;

public class ZiNGServlet extends HttpServlet {

  public static final String BIB1_ATTRIBUTES = "org/jafer/conf/bib1Attributes.xml";
  public static final String FAULT_SCHEMA = "schemas.xmlsoap.org/soap/envelope/";
  private static Logger logger;
  private static Hashtable servletConfigMap;

  public void init(ServletConfig servletConfig) throws ServletException {

    super.init(servletConfig);

    logger = Logger.getLogger("org.jafer.zclient");

    servletConfigMap = new Hashtable();
    Enumeration initParams = servletConfig.getInitParameterNames();
    while (initParams.hasMoreElements()) {
      String initParam = (String)initParams.nextElement();
      servletConfigMap.put(initParam, servletConfig.getInitParameter(initParam));
    } // no need for mutiple params
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
                                        throws ServletException, IOException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
                                        throws ServletException, IOException {

    response.setContentType("text/xml");
    PrintWriter out = response.getWriter();

    Hashtable requestMap = new Hashtable();
    Enumeration requestParams = request.getParameterNames();
    while (requestParams.hasMoreElements()) {
      String requestParam = (String)requestParams.nextElement();
      requestMap.put(requestParam, request.getParameter(requestParam));
    }

    ZClient bean = null;
    Document document = null;
    Node root = null;

    String userIP = request.getRemoteAddr();
    Hashtable paramMap = new Hashtable();
    try {

      String query = getStringValue(requestMap, "query");

      if (query.equals("explain")) {
          document = DOMFactory.parse(getResource(
                      getStringValue(servletConfigMap, "bib1Attributes")));
          root = document.createElement("root");
          Node explain = getExplainNode(document);
          root.appendChild(explain);
      } else {
          bean = new ZClient();
          document = bean.getDocument();
          root = document.createElement("root");

          initializeBean(bean, requestMap, userIP);
          int totalHits = submitQuery(bean, query);
          int presentStatus = getResults(bean, requestMap, root, totalHits);

          root.appendChild(getStatusNode(document, presentStatus));
          paramMap.put("totalHits", String.valueOf(totalHits));
          paramMap.put("resultSetName", query);
      }

      output(requestMap, paramMap, root, out, userIP);

    } catch (Exception e) {
      try {
// check exception nodes generated via XMLFactory fit to schema

        logger.log(Level.SEVERE, "<" + userIP + ">ZiNGException: " + e.toString());
        outputFault(requestMap, paramMap, getSOAPException(DOMFactory.newDocument(), e), out, userIP);
      } catch (JaferException je) {// try to return exception
        logger.log(Level.SEVERE, "<" + userIP + ">ZiNGException: " + e.toString(), e);
        out.print("soapException: " + e);
      }
    } finally {
      close(bean, userIP);
    }
  }

  private void outputFault(Hashtable requestMap, Hashtable paramMap, Node node, PrintWriter out, String userIP) throws JaferException {
/** @todo need to identify cause and set faultcode and faultstring accordingly */
    requestMap.clear();
    requestMap.put("responseSchema", FAULT_SCHEMA);
    paramMap.clear();
    paramMap.put("faultcode", "server");
    paramMap.put("faultstring", "internal server error");
    output(requestMap, paramMap, node, out, userIP);
  }

  private void output(Hashtable requestMap, Hashtable paramMap, Node node, PrintWriter out, String userIP) throws JaferException {

    String responseSchema = null;
    try {
      responseSchema = getStringValue(requestMap, "responseSchema");
    } catch (JaferException e1) {
      try {
        responseSchema = getStringValue(servletConfigMap, "defaultResponseSchema");
        logger.log(Level.FINE, "<" + userIP + ">Using default response schema: " + responseSchema);
      } catch (JaferException e2) {
        throw new JaferException("defaultResponseSchema not specified in deployment descriptor file; responseSchema not supplied with query", e2);
      }
    }

    String styleSheetPath = null;
    try {
      styleSheetPath = getStringValue(servletConfigMap, responseSchema);
    } catch (JaferException e) {
        throw new JaferException("StyleSheet for responseSchema (" + responseSchema + ") not specified in deployment descriptor file; cannot transform XML to requested schema", e);
    }

    URL responseStyleSheet = getResource(styleSheetPath);
    if (responseStyleSheet == null)
      throw new JaferException("StyleSheet (" + styleSheetPath + ") for responseSchema (" + responseSchema + ") not found on local System; cannot transform XML to requested schema");

    Node soapNode = XMLTransformer.transform(paramMap, node, responseStyleSheet);
    XMLSerializer.out((Element)soapNode, true, out);
    XMLSerializer.out((Element)soapNode, false, "zng.xml");
  }

  private void initializeBean(ZClient bean, Hashtable requestMap, String userIP) throws JaferException {

    String host, dataBase;
    int port;
    try { // get servletConfig parameters
      host = getStringValue(servletConfigMap, "host");
      port = getIntValue(servletConfigMap, "port");
      dataBase = getStringValue(servletConfigMap, "dataBase");
    } catch (JaferException e) { // userName, PassWord
      throw new JaferException("Bad parameter in deployment descriptor file: " + e.getMessage(), e);
    }

    String recordSchema = null;
    try {
      recordSchema = getStringValue(requestMap, "recordSchema");
    } catch (JaferException e1) {
      try {
        recordSchema = getStringValue(servletConfigMap, "defaultRecordSchema");
        logger.log(Level.FINE, "<" + userIP + ">Using default record schema: " + recordSchema);
      } catch (JaferException e2) {
        throw new JaferException("defaultRecordSchema not specified in deployment descriptor file; recordSchema not supplied with query", e2);
      }
    }

    bean.setRemoteAddress(userIP);
    bean.setHost(host);
    bean.setPort(port);
    bean.setDatabases(dataBase);
    bean.setCheckRecordFormat(true);
    bean.setRecordSchema(recordSchema);
    bean.setFetchSize(5);
  }

  private int submitQuery(ZClient bean, String query) throws JaferException {

    int totalHits = 0;
    try {
      totalHits = bean.submitQuery(new QueryBuilder().getNode(query));
//      totalHits = bean.submitQuery(((QueryBuilder)bean.newQueryBuilder()).getNode(query));
    } catch (JaferException e) {
      throw new JaferException("Error submitting query: " + e.getMessage());
    }

    return totalHits;
  }

  private int getResults(ZClient bean, Hashtable requestMap, Node root, int totalHits) throws JaferException {

    int startRecord, endRecord, maxRecords;
    try {
      startRecord = getIntValue(requestMap, "startRecord");
      if (startRecord < 1 || startRecord > totalHits) {
        logger.log(Level.FINE, "<" + bean.getRemoteAddress() + ">startRecord parameter = " + startRecord +
          ", totalHits = " + totalHits + "; value reset to 1");
        startRecord = 1;
      }
    } catch (JaferException e) {
        logger.log(Level.FINE, "<" + bean.getRemoteAddress() + ">startRecord not specified in query; using default startRecord = 1");
      startRecord = 1;
    }

    try {
      maxRecords = getIntValue(requestMap, "maxRecords");
      if (maxRecords < 1 || maxRecords > totalHits) {
        logger.log(Level.FINE, "<" + bean.getRemoteAddress() + ">maxRecords parameter = " + maxRecords +
          "; value reset to totalHits = " + totalHits);
        maxRecords = totalHits;
      }
    } catch (JaferException e) {
      logger.log(Level.FINE, "<" + bean.getRemoteAddress() + ">maxRecords not specified in query; using default totalHits = " + totalHits);
      maxRecords = totalHits;
    }
    endRecord = startRecord + maxRecords > totalHits ? totalHits : startRecord + maxRecords - 1;
    int presentStatus = 0;

    for (int n = startRecord; n <= endRecord; n++) {

      try {
        bean.setRecordCursor(n);
        root.appendChild(getRecordNode(bean.getDocument(), bean.getCurrentRecord().getXML(), bean.getRecordSchema()));
      } catch (RecordException e) {
        processException(bean.getDocument(), root, e);
      } catch (PresentException e) {
        presentStatus = processException(bean.getDocument(), root, e);
      }

    }
    return presentStatus;
  }

  private Node getExplainNode(Document document) throws JaferException {

    Node recordData = document.createElement("recordData");
    recordData.appendChild(document.getFirstChild());
    return wrapRecordNode(document, recordData, "explain Schema");
  }

  private int processException(Document document, Node root, RecordException e) throws JaferException {

    Node node = e.getRecord().getXML();
    String format = e.getRecord().getRecordSyntax();

    if (Config.isSyntaxEqual(format, Config.getRecordSyntaxFromName("DIAG_BIB1"))) {                  // diagnostic
        root.appendChild(getDiagnosticNode(document, node, "diagnostic schema"));

    } else if (Config.isSyntaxEqual(format, Config.getRecordSyntaxFromName("JAFER"))) {               // exception
        root.appendChild(getRecordNode(document, node, "JAFER exception schema"));

    } else {/** @todo report this */                          // Format is different to that requested
        root.appendChild(getRecordNode(document, node, "unknown schema"));
    }
    /** @todo use to set presentStatus in getResults ? */
    return 0;
  }

  private int processException(Document document, Node root, PresentException e) {

    int presentStatus = e.getStatus();
    Node statusNode = getStatusNode(document, presentStatus);

    if (e.hasDiagnostics()) {
      Diagnostic[] diagnostic = e.getDiagnostics();

      for (int i = 0; i < diagnostic.length; i++) {
        try {
          statusNode.appendChild(diagnostic[i].getXML(document));
        } catch (RecordException re) {}
      }
    }

    root.appendChild(statusNode);

    return presentStatus;
  }

  private Node getSOAPException(Document document, Exception e) {

    Node detail = document.createElement("detail");

    Node exceptionNode = DOMFactory.getExceptionNode(document, e, e.getStackTrace(), e.getMessage());
    detail.appendChild(exceptionNode);
    return detail;
  }

  private Node getStatusNode(Document document, int presentStatus) {

    Node statusNode = document.createElement("status");
    Node statusCode = document.createElement("statusCode");
    Node statusCodeText = document.createTextNode(String.valueOf(presentStatus));
    statusNode.appendChild(statusCode);
    statusCode.appendChild(statusCodeText);

    return statusNode;
  }

  private Node getRecordNode(Document document, Node record, String schema) {

    /** @todo  is there a better way to append all the child nodes ? */
    Node recordData = document.createElement("recordData");
    for (int i = 0; i < record.getChildNodes().getLength(); i++)
        recordData.appendChild(record.getChildNodes().item(i));

    return wrapRecordNode(document, recordData, schema);
  }

  private Node getDiagnosticNode(Document document, Node diagnosticNode, String schema) {

    Node recordData = document.createElement("recordData");
    recordData.appendChild(diagnosticNode);

    return wrapRecordNode(document, recordData, schema);
  }

  private Node wrapRecordNode(Document document, Node recordData, String schema) {

    Node recordNode = document.createElement("record");
    Node recordSchema = document.createElement("recordSchema");
    Node schemaText = document.createTextNode(schema);

    recordNode.appendChild(recordSchema);
    recordSchema.appendChild(schemaText);
    recordNode.appendChild(recordData);

    return recordNode;
  }

  private void close(ZClient bean, String userIP) {

    if (bean != null) {
      try {
        bean.close();
      } catch (JaferException e) {
        logger.log(Level.WARNING, "<" + userIP + ">Could not close ZClient connection " + e.toString());
      }
      bean = null;
    }
    System.gc();
  }

  private URL getResource(String path) {

    return this.getClass().getClassLoader().getResource(path);
  }

  private String getStringValue(Hashtable map, String paramName) throws JaferException {

    if (map.containsKey(paramName))
      return(String)map.get(paramName);
    else throw new JaferException("Parameter not found (" + paramName + ")");
  }

  private int getIntValue(Hashtable map, String paramName) throws JaferException {

    String paramValue = getStringValue(map, paramName);
    int i;
    try {
      i = Integer.parseInt(paramValue);
    } catch (IllegalArgumentException e) {
      throw new JaferException("Parameter " + paramName + " is not valid (" + paramValue + ")");
    }
    return i;
  }
}