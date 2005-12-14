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

package org.jafer.record;

import org.jafer.conf.Config;
import org.jafer.util.xml.DOMFactory;
import org.jafer.exception.JaferException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p>Method toUnicode converts MARC8 character encoding to Unicode (UTF8) - identifies character set and performs lookup using xml file derived from LOC character sets including multibyte characters for EACC.
 * Loads character sets as required. Method toMARC8 converts from Unicode to MARC8 - performs sequential lookup using character sets.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class MARC8Unicode {

  public static final char UNICODE_UNKNOWN = (char)Integer.parseInt("FFFD", 16);
  public static final char MARC8_UNKNOWN   = (char)Integer.parseInt("7C", 16);// this is value for fill character

  public static final int ESC              = (int)Integer.parseInt("1B", 16);
  public static final int ASCII            = (int)Integer.parseInt("73", 16);
  public static final int MULTI_BYTE       = (int)Integer.parseInt("24", 16);
  public static final int G0_PAGE_CHAR1    = (int)Integer.parseInt("28", 16);
  public static final int G0_PAGE_CHAR2    = (int)Integer.parseInt("2C", 16);
  public static final int G1_PAGE_CHAR1    = (int)Integer.parseInt("29", 16);
  public static final int G1_PAGE_CHAR2    = (int)Integer.parseInt("2D", 16);

  public static final int C0_LIMITS_LOWER  = (int)Integer.parseInt("00", 16);
  public static final int C0_LIMITS_UPPER  = (int)Integer.parseInt("20", 16);
  // includes 20(HEX) space character which is both control character and graphic character
  public static final int C1_LIMITS_LOWER  = (int)Integer.parseInt("80", 16);
  public static final int C1_LIMITS_UPPER  = (int)Integer.parseInt("9F", 16);
  public static final int G0_LIMITS_LOWER  = (int)Integer.parseInt("20", 16);
  // includes 20(HEX) space character which is both control character and graphic character
  public static final int G0_LIMITS_UPPER  = (int)Integer.parseInt("7E", 16);
  public static final int G1_LIMITS_LOWER  = (int)Integer.parseInt("A1", 16);
  public static final int G1_LIMITS_UPPER  = (int)Integer.parseInt("FE", 16);

  public static final int DIACRITIC_BASIC_HEBREW_LOWER    = (int)Integer.parseInt("40", 16);
  public static final int DIACRITIC_BASIC_HEBREW_UPPER    = (int)Integer.parseInt("4F", 16);
  public static final int DIACRITIC_BASIC_ARABIC_LOWER    = (int)Integer.parseInt("40", 16);
  public static final int DIACRITIC_BASIC_ARABIC_UPPER    = (int)Integer.parseInt("7E", 16);
  public static final int DIACRITIC_EXTENDED_ARABIC_LOWER = (int)Integer.parseInt("F0", 16);
  public static final int DIACRITIC_EXTENDED_ARABIC_UPPER = (int)Integer.parseInt("FE", 16);
  public static final int DIACRITIC_BASIC_GREEK_LOWER     = (int)Integer.parseInt("21", 16);
  public static final int DIACRITIC_BASIC_GREEK_UPPER     = (int)Integer.parseInt("2F", 16);
  public static final int DIACRITIC_ANSEL_LOWER           = (int)Integer.parseInt("E0", 16);
  public static final int DIACRITIC_ANSEL_UPPER           = (int)Integer.parseInt("FE", 16);

  public static final String SUBSCRIPT         = "62";
  public static final String GREEK_SYMBOL      = "67";
  public static final String SUPERSCRIPT       = "70";
  public static final String ASCII_TYPE_1      = "73";

  public static final String EACC              = "31"; // EACC ie. Chinese, Japanese, Korean
  public static final String BASIC_HEBREW      = "32";
  public static final String BASIC_ARABIC      = "33";
  public static final String EXTENDED_ARABIC   = "34";
  public static final String ASCII_TYPE_2      = "42";
  public static final String BASIC_CYRILLIC    = "4E";
  public static final String EXTENDED_CYRILLIC = "51";
  public static final String BASIC_GREEK       = "53";
  public static final String ANSEL             = "ANSEL";

  private static final String CONTROL = "CONTROLFUNCTION";
  private boolean esc = false;
  private boolean multiByte = false;
  private boolean pageG0 = false;
  private boolean pageG1 = false;

  private static Hashtable characterSetsMap, marcKeyCharacterSets, unicodeKeyCharacterSets;
  private static Logger logger;
  private char marc8Unknown, unicodeUnknown;
  private String G0PageId, G1PageId, currentG0PageId, currentG1PageId, controlPageId;
  private Hashtable G0Page, G1Page, controlPage;
  private Vector lookUpList;

  static {
    try {
      logger = Logger.getLogger("org.jafer.record");
      loadCharacterSetsMap();
      marcKeyCharacterSets = new Hashtable();
      unicodeKeyCharacterSets = new Hashtable();
    } catch (JaferException ex) {
      String message = "Cannot load characterSetsMap: " + Config.CHARACTER_SETS_FILE;
      logger.log(Level.SEVERE, message, ex);
      System.err.print("FATAL: " + message + " - Could not initialize class MARC8Unicode");
      ex.printStackTrace(System.err);
      System.exit(-1);
    }
  }
/** @todo subfield $6 (linkage) gives info on field orientation - orientation not yet implemented*/

  public MARC8Unicode() {

    marc8Unknown = MARC8_UNKNOWN;
    unicodeUnknown = UNICODE_UNKNOWN;
    buildLookUpList();
    try {
      setControlPage(CONTROL);
    } catch (JaferException ex) {
      String message = "Cannot load controlFunction characterSet: " + Config.CHARACTER_SETS_FILE;
      logger.log(Level.SEVERE, message, ex);
      System.err.print("FATAL: " + message + " - Could not initialize class MARC8Unicode");
      ex.printStackTrace(System.err);
      System.exit(-1);
    }
  }

  public MARC8Unicode(char marc8Unknown, char unicodeUnknown) {

    this.marc8Unknown = marc8Unknown;
    this.unicodeUnknown = unicodeUnknown;
    buildLookUpList();
    try {
      setControlPage(CONTROL);
    } catch (JaferException ex) {
      String message = "Cannot load controlFunction characterSet: " + Config.CHARACTER_SETS_FILE;
      logger.log(Level.SEVERE, message, ex);
      System.err.print("FATAL: " + message + " - Could not initialize class MARC8Unicode");
      ex.printStackTrace(System.err);
      System.exit(-1);
    }
  }

  public String toMARC8(String unicode) throws JaferException {

    StringBuffer out = new StringBuffer();
    StringBuffer diac = new StringBuffer();
    StringBuffer multi = new StringBuffer();

    setCurrentG0PageId(ASCII_TYPE_1);
    setCurrentG1PageId(ANSEL);

    char[] c; // allows for 24 bit multibyte characters
    for (int n = 0; n < unicode.length(); n++) {
      c = getMARC8Chars(unicode.charAt(n));

      if (isDiacritic(c[0]) && c.length == 1) { // ie. !isMultiByte()
        diac.append(c[0]);
      } else {
        if (!isBufferEmpty(diac))
          diac = emptyBuffer(diac, out, -1);
        if (isNewPage())
          out.append(getPageESCSequence());
        out.append(c);
      }
    }
    // check if any diacritics
    if (!isBufferEmpty(diac))
      emptyBuffer(diac, out, -1);
    // re-set to ascii and new page if required
    setG0PageId(ASCII_TYPE_1);
    setPageG0(true);
    if (isNewPage())
      out.append(getPageESCSequence());

   return out.toString();
  }

  public String toUnicode(String marc8) throws JaferException {

    StringBuffer out = new StringBuffer();
    StringBuffer diac = new StringBuffer();
    StringBuffer multi = new StringBuffer();

    // set default pages latin - basic (ASCII) AND extended (ANSEL)
    setG0Page(ASCII_TYPE_1);
    setG1Page(ANSEL);
    setMultiByte(false);

    int c = 0;
    for (int n = 0; n < marc8.length(); n++) {
      c = marc8.charAt(n);

      if (isESCChar(c)) {
        setESC(true);
        setPageG0(false);
        setPageG1(false);
        setMultiByte(false);
      } else if (isESC()) {

        if (isMultiByte()) {
          if (isPageG0()) {
            setG0Page(c);
          } else if (isPageG1()) {
            setG1Page(c);
          } else if (isG1PageChar(c)) {
            setPageG1(true);
          } else if (isG0PageChar(c)) {
            setPageG0(true);
          } else {
            setG0Page(c);
          }

        } else if (isPageG0()) {
          setG0Page(c);// method 2
        } else if (isPageG1()) {
          setG1Page(c);// method 2
        } else if (isMultiByteChar(c)) {
          setMultiByte(true);
        } else if (isG0PageChar(c)) {
          setPageG0(true);
        } else if (isG1PageChar(c)) {
          setPageG1(true);
        } else {
          setG0Page(c);// method 1
        }

      } else {
        if (isMultiByte())
          multi = appendMultiByte(c, multi, out);// assume diactritic not associated with multibyte character
        else if (isDiacritic(c))
          diac.append(getUnicodeCharacter(c));
        else {
//          if (isControlFunction(c))// do we include these?
//            out.append(getControlCharacter(c));
//          else
            out.append(getUnicodeCharacter(c));
          if (!isBufferEmpty(diac))
            diac = emptyBuffer(diac, out, 0);
        }
      }
    }
    return out.toString();
  }

  public String toUnicode(byte[] marc8) throws JaferException {
      return toUnicode(marc8, 0, marc8.length);
  }

  public String toUnicode(byte[] marc8, int offset, int len) throws JaferException {

    StringBuffer out = new StringBuffer();
    StringBuffer diac = new StringBuffer();
    StringBuffer multi = new StringBuffer();

    // set default pages latin - basic (ASCII) AND extended (ANSEL)
    setG0Page(ASCII_TYPE_1);
    setG1Page(ANSEL);
    setMultiByte(false);

    int c = 0;
    for (int n = offset; n < marc8.length && n < offset + len; n++) {
      c = (int)(marc8[n] & 0x00ff);

      if (isESCChar(c)) {
        setESC(true);
        setPageG0(false);
        setPageG1(false);
        setMultiByte(false);
      } else if (isESC()) {

        if (isMultiByte()) {
          if (isPageG0()) {
            setG0Page(c);
          } else if (isPageG1()) {
            setG1Page(c);
          } else if (isG1PageChar(c)) {
            setPageG1(true);
          } else if (isG0PageChar(c)) {
            setPageG0(true);
          } else {
            setG0Page(c);
          }

        } else if (isPageG0()) {
          setG0Page(c);// method 2
        } else if (isPageG1()) {
          setG1Page(c);// method 2
        } else if (isMultiByteChar(c)) {
          setMultiByte(true);
        } else if (isG0PageChar(c)) {
          setPageG0(true);
        } else if (isG1PageChar(c)) {
          setPageG1(true);
        } else {
          setG0Page(c);// method 1
        }

      } else {
        if (isMultiByte())
          multi = appendMultiByte(c, multi, out);// assume diactritic not associated with multibyte character
        else if (isDiacritic(c))
          diac.append(getUnicodeCharacter(c));
        else {
//          if (isControlFunction(c))// do we include these?
//            out.append(getControlCharacter(c));
//          else
            out.append(getUnicodeCharacter(c));
          if (!isBufferEmpty(diac))
            diac = emptyBuffer(diac, out, 0);
        }
      }
    }
    return out.toString();
  }

  private static void loadCharacterSetsMap() throws JaferException {

    String id, file;
    characterSetsMap = new Hashtable();
    Element e;
    Document document = Config.getCharacterSetsMap();
    NodeList list = Config.selectNodeList(document, "characterSets/graphicCharacterSet");
    if (list.getLength() == 0)
      throw new JaferException("graphicCharacterSet node not found");

    for (int i = 0; i < list.getLength(); i++) {
      e = (Element)list.item(i);
      id = e.getAttribute("id").toUpperCase();
      file = e.getAttribute("file");
      characterSetsMap.put(id, file);
    }

    e = (Element)Config.selectSingleNode(document, "characterSets/controlFunctionSet");
    if (e == null)
      throw new JaferException("controlFunctionSet node not found");

    file = e.getAttribute("file");
    characterSetsMap.put(CONTROL, file);
  }

  private Hashtable getCharacterSet(String key, boolean marcKey) throws JaferException {

    if (!characterSetsMap.containsKey(key)) {
      String message = "Error looking up characterSet - " + key + "(HEX) not recognised as characterSet code";
      logger.log(Level.WARNING, message);
      throw new JaferException(message);
    }

    Hashtable characterSet = new Hashtable();
    String path = (String)characterSetsMap.get(key);
    String message = "loading CharacterSet " + key + "; path: " + path;
    logger.log(Level.FINE, message);

    Document document = DOMFactory.parse(Config.getResource(path));
    NodeList list = Config.selectNodeList(document, "characterSet/character");
    Character unicode, marc = null;
    String eacc;
    for (int i = 0; i < list.getLength(); i++) {
      Element e = (Element)list.item(i);

      if (key.equals(EACC)) {/** @todo allow for variants */
        if (e.hasAttribute("eacc") && e.hasAttribute("unicode")) {
          eacc = e.getAttribute("eacc").toUpperCase();
          unicode = new Character((char)Integer.parseInt(e.getAttribute("unicode").toUpperCase(), 16));
          if (marcKey)
            characterSet.put(eacc, unicode);
          else
            characterSet.put(unicode, eacc);
        } else {
          message = "Invalid node (line " + (i + 2) + ") in EACC characterSet " + path;
          logger.log(Level.SEVERE, message);
          throw new JaferException(message);
        }

      } else {
        if (e.hasAttribute("marc") && e.hasAttribute("unicode")) {
          marc = new Character((char)Integer.parseInt(e.getAttribute("marc").toUpperCase(), 16));
          unicode = new Character((char)Integer.parseInt(e.getAttribute("unicode").toUpperCase(), 16));
          if (marcKey)
            characterSet.put(marc, unicode);
          else
            characterSet.put(unicode, marc);
        } else {
          message = "Invalid node (line " + (i + 2) + ") in characterSet " + path;
          logger.log(Level.SEVERE, message);
          throw new JaferException(message);
        }
      }
    }
    // check if more than one key maps to same characterSet file in characterSetsMap
    Enumeration en = characterSetsMap.keys();
    while (en.hasMoreElements()) {
      String currentKey = (String)en.nextElement();
      if (characterSetsMap.get(currentKey).equals(characterSetsMap.get(key))) {
        if (marcKey)
          marcKeyCharacterSets.put(currentKey, characterSet);
        else
          unicodeKeyCharacterSets.put(currentKey, characterSet);
      }
    }

    return characterSet;
  }

  private void setControlPage(String key) throws JaferException {

    controlPage = getPage(key, true);
    setControlPageId(key);
  }

  private void setG0Page(String key) throws JaferException {

    G0Page = getPage(key, true);
    setG0PageId(key);
    setESC(false);
  }

  private void setG1Page(String key) throws JaferException {

    G1Page = getPage(key, true);
    setG1PageId(key);
    setESC(false);
  }

  private void setG0Page(int c) throws JaferException {

    String key = new String(Integer.toHexString(c));
    G0Page = getPage(key, true);
    setG0PageId(key);
    setESC(false);
  }

  private void setG1Page(int c) throws JaferException {

    String key = new String(Integer.toHexString(c));
    G1Page = getPage(key, true);
    setG1PageId(key);
    setESC(false);
  }

  private Hashtable getPage(String key, boolean marcKey) throws JaferException {

    key = key.toUpperCase();
    if (marcKey) {
      if (marcKeyCharacterSets.containsKey(key))
        return (Hashtable)marcKeyCharacterSets.get(key);
    } else {
      if (unicodeKeyCharacterSets.containsKey(key))
        return (Hashtable)unicodeKeyCharacterSets.get(key);
    }

    return getCharacterSet(key, marcKey);
  }

  private Hashtable getControlPage() {

    return controlPage;
  }

  private Hashtable getG0Page() {

    return G0Page;
  }

  private Hashtable getG1Page() {

    return G1Page;
  }

  private void setControlPageId(String id) {

    controlPageId = id;
  }

  private void setG0PageId(String id) {

    G0PageId = id;
  }

  private void setCurrentG0PageId(String id) {

    currentG0PageId = id;
  }

  private void setG1PageId(String id) {

    G1PageId = id;
  }

  private void setCurrentG1PageId(String id) {

    currentG1PageId = id;
  }

  private String getPageESCSequence() {

    if (isPageG1() && getG1PageId().equals(ANSEL))
      return ""; // how do we re-designate ANSEL?

    String id = null;
    CharArrayWriter out = new CharArrayWriter();
    out.write(ESC);

    if (isMultiByte()) // multibyte: Chinese, Japanaese, Korean
      out.write(MULTI_BYTE);

    if (isPageG0()) {
      id = getG0PageId();
      if (!(id.equals(GREEK_SYMBOL) || id.equals(ASCII_TYPE_1) ||
          id.equals(SUBSCRIPT) || id.equals(SUPERSCRIPT)))
        out.write(G0_PAGE_CHAR2);

    } else if (isPageG1()) {
      id = getG1PageId();
      out.write(G1_PAGE_CHAR2);
    }

    out.write(Integer.parseInt(id, 16));
    return out.toString();
  }

  private String getControlPageId() {

    return controlPageId;
  }

  private String getG0PageId() {

    return G0PageId;
  }

  private String getCurrentG0PageId() {

    return currentG0PageId;
  }

  private String getG1PageId() {

    return G1PageId;
  }

  private String getCurrentG1PageId() {

    return currentG1PageId;
  }

  private String getPageId(int c) throws JaferException {

    String id = null;

    if (isG0Page(c))
      id = getG0PageId();
    else if (isG1Page(c))
      id = getG1PageId();
    else if (isControlFunction(c))
      id = getControlPageId();

    if (id == null) {
      String message = "CharacterSet PageId (" + c + ") is null";
      logger.log(Level.WARNING, message);
      throw new JaferException(message);
    }
    return id;
  }

  private void setESC(boolean state) {

    esc = state;
  }

  private boolean isESC() {

    return esc;
  }

  private boolean isESCChar(int c) {

    return (c == ESC);
  }

  private void setPageG0(boolean state) {

    pageG0 = state;
  }

  private boolean isPageG0() {

    return pageG0;
  }

  private boolean isG0PageChar(int c) {

    return (c == G0_PAGE_CHAR1 || c == G0_PAGE_CHAR2);
  }

  private void setPageG1(boolean state) {

    pageG1 = state;
  }

  private boolean isPageG1() {

    return pageG1;
  }

  private boolean isG1PageChar(int c) {

    return (c == G1_PAGE_CHAR1 || c == G1_PAGE_CHAR2);
  }

  private boolean isBufferEmpty(StringBuffer buffer) {

    return buffer.length() < 1;
  }

  private StringBuffer emptyBuffer(StringBuffer bufferIn, StringBuffer bufferOut, int offset) {

    bufferOut.insert(bufferOut.length() + offset, bufferIn.toString());
    return new StringBuffer();
  }

  private boolean isNewPage() {

    if (isPageG0()) {
      if (getG0PageId().equals(getCurrentG0PageId())) {
        return false;
      } else {
        setCurrentG0PageId(getG0PageId());
        return true;
      }
    }

    if (isPageG1()) {
      if (getG1PageId().equals(getCurrentG1PageId())) {
        return false;
      } else {
        setCurrentG1PageId(getG1PageId());
        return true;
      }
    }

    return false; // page could be controlFunction page
  }

  private boolean isMultiByteChar(int c) {

    return (c == MULTI_BYTE);
  }

  private void setMultiByte(boolean state) {

    multiByte = state;
  }

  private boolean isMultiByte() {

    return multiByte;
  }

  private boolean isControlFunction(int c) {

    return (isC0Page(c) || isC1Page(c));
  }

  private boolean isC0Page(int c) {
    // includes 20(HEX) space character which is both control character and graphic character
    return (c >= C0_LIMITS_LOWER && c <= C0_LIMITS_UPPER);
  }

  private boolean isC1Page(int c) {

    return (c >= C1_LIMITS_LOWER && c <= C1_LIMITS_UPPER);
  }

  private boolean isG0Page(int c) {
    // includes 20(HEX) space character which is both control character and graphic character
    return (c >= G0_LIMITS_LOWER && c <= G0_LIMITS_UPPER);
  }

  private boolean isG1Page(int c) {

    return (c >= G1_LIMITS_LOWER && c <= G1_LIMITS_UPPER);
  }

  private boolean isDiacritic(int c) throws JaferException {

    if (getPageId(c).equals(BASIC_HEBREW)) {
      return (c >= DIACRITIC_BASIC_HEBREW_LOWER && c <= DIACRITIC_BASIC_HEBREW_UPPER);
    } else if (getPageId(c).equals(BASIC_ARABIC)) {
      return (c >= DIACRITIC_BASIC_ARABIC_LOWER && c <= DIACRITIC_BASIC_ARABIC_UPPER);
    } else if (getPageId(c).equals(EXTENDED_ARABIC)) {
      return (c >= DIACRITIC_EXTENDED_ARABIC_LOWER && c <= DIACRITIC_EXTENDED_ARABIC_UPPER);
    } else if (getPageId(c).equals(BASIC_GREEK)) {
      return (c >= DIACRITIC_BASIC_GREEK_LOWER && c <= DIACRITIC_BASIC_GREEK_UPPER);
    } else if (getPageId(c).equals(ANSEL)) {
      return (c >= DIACRITIC_ANSEL_LOWER && c <= DIACRITIC_ANSEL_UPPER);
    }

    return false;
  }

  private Character getControlCharacter(int c) throws JaferException {

    Character key = new Character((char)c);

    if (isControlFunction(c) && getControlPage().containsKey(key))
      return (Character)getControlPage().get(key);

    String message = key  + " (" + Integer.toHexString(c).toUpperCase() + " HEX) is not a MARC8 control function character or was not found in controlFunction characterSet";
    logger.log(Level.WARNING, message);
    throw new JaferException(message);
  }

  private char[] toMultiByte(String multiByteHexValue) {

    char[] c = new char[3];
    for (int n = 0; n < multiByteHexValue.length(); n += 2)
      c[((n + 2) / 2) -1] = (char)Integer.parseInt(multiByteHexValue.substring(n, n + 2), 16);
    return c;
  }

  private StringBuffer appendMultiByte(int c, StringBuffer bufferIn, StringBuffer bufferOut) throws JaferException {

    bufferIn.append(Integer.toHexString(c));
    if (bufferIn.length() == 6) {
      bufferOut.append(getEACCCharacter(bufferIn.toString()));
      return new StringBuffer();
    }
    return bufferIn;
  }

  private Character getEACCCharacter(String hexValue) throws JaferException {

    hexValue = hexValue.toUpperCase();

    if (getG0PageId().equals(EACC) && getG0Page().containsKey(hexValue))
      return (Character)getG0Page().get(hexValue);
    if (getG1PageId().equals(EACC) && getG1Page().containsKey(hexValue))
      return (Character)getG1Page().get(hexValue);

    String message = "MARC multi-byte character (" + hexValue + " HEX) not found in EACC character set (characterSetId: " + EACC + ")";
    logger.log(Level.WARNING, message);
    return new Character(unicodeUnknown);
  }

  private Character getUnicodeCharacter(int c) throws JaferException {

    Character key = new Character((char)c);
    String id = null;

    if (isG0Page(c)) {
      id = getG0PageId();
      if (getG0Page().containsKey(key))
        return (Character)getG0Page().get(key);
    } else if (isG1Page(c)) {
      id = getG1PageId();
      if (getG1Page().containsKey(key))
        return (Character)getG1Page().get(key);
    }

    String message = "MARC character " + key + " (" + Integer.toHexString(c).toUpperCase() + " HEX) not found in characterSetId: " + id;
    logger.log(Level.WARNING, message);
    return new Character(unicodeUnknown);
  }


  private char[] getMARC8Chars(char unicodeChar) throws JaferException {


    if (unicodeChar == unicodeUnknown)
      return new char[] {marc8Unknown};

    Character key = new Character(unicodeChar);
    Hashtable page = null;
    String pageId;
    setPageG0(false);
    setPageG1(false);

    char[] c;
    String s;
    int i = 0;
    while (i < getLookUpList().size()) {
      pageId = (String)getLookUpList().get(i++);
      page = getPage(pageId, false);
      if (page.containsKey(key)) {
        moveToFront(pageId, getLookUpList()); // put pageId at front of list
        if (pageId.equals(EACC)) {
          c = toMultiByte((String)page.get(key));
          setPageG0(true);
          setG0PageId(pageId);
          setMultiByte(true);
          return c;
        } else {
          c = new char[1];
          c[0] = ((Character)page.get(key)).charValue();
          if (isG0Page(c[0])) {
            setPageG0(true);
            if (characterSetsMap.get(ASCII_TYPE_1).equals(characterSetsMap.get(pageId)))
              setG0PageId(ASCII_TYPE_1);
            else
              setG0PageId(pageId);
          } else if (isG1Page(c[0])) {
            setPageG1(true);
            if (characterSetsMap.get(ANSEL).equals(characterSetsMap.get(pageId)))
              setG1PageId(ANSEL);
            else
              setG1PageId(pageId);
          }
          setMultiByte(false);
          return c;
        }
      }
    }

    String message = "UnicodeCharacter " + key + " (" + Integer.toHexString(unicodeChar).toUpperCase() + " HEX) not found";
    logger.log(Level.WARNING, message);
    return new char[] {marc8Unknown};
  }

  private void buildLookUpList() {
    // build list of lookUp keys which map to unique values (CharacterSet files) for unicode to MARC lookUp
    lookUpList = new Vector();
    lookUpList.add(ASCII_TYPE_1);
    String key;
    Enumeration en = characterSetsMap.keys();
    while (en.hasMoreElements()) {
      key = (String)en.nextElement();
      int i = 0;
      boolean inList = false;
      while (!inList && i < lookUpList.size()) {
        if (characterSetsMap.get(key).equals(characterSetsMap.get(lookUpList.get(i++))))
          inList = true;
      }
      if (!inList)
        lookUpList.add(key);
    }

    // ensure EACC character set is last in list initially!
    if (lookUpList.contains(EACC))
      moveToBack(EACC, lookUpList);
  }

  private Vector getLookUpList() {

    return lookUpList;
  }

  private void moveToFront(String pageId, Vector lookUpList) {

    lookUpList.remove(pageId);
    lookUpList.add(0, pageId);
  }

  private void moveToBack(String pageId, Vector lookUpList) {

    lookUpList.remove(pageId);
    lookUpList.add(pageId);
  }
}
