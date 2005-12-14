package org.jasig.portal.portlet.xslt.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;

public class HtmlUtils {

    /**
      * returns the decoded HTML text.
      * @param text String to decode
      * @return a decoded HTML string
      */
     public static String decodeHTML(String text) {
         if (text != null) {
             text = text.replaceAll("&quot;", "\"");
             text = text.replaceAll("&nbsp;", " ");
             text = text.replaceAll("&#39;", "\'");
             text = text.replaceAll("&lt;", "<");
             text = text.replaceAll("&gt;", ">");
             text = text.replaceAll("%26", "&");
             text = text.replaceAll("%3B", ";");
         }
         return text;
     }

     public static String encodeHTML(String text) {
         if (text != null) {
             text = text.replaceAll("\"", "&quot;");
             text = text.replaceAll(" ", "&nbsp;");
             text = text.replaceAll("\'", "&#39;");
             text = text.replaceAll("<", "&lt;");
             text = text.replaceAll(">", "&gt;");
             text = text.replaceAll("&", "%26");
             text = text.replaceAll(";", "%3B");
         }
         return text;
     }
 }
