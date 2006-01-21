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

package org.jafer.zclient.operations;

import org.jafer.transport.ConnectionException;
import org.jafer.zclient.ZSession;
import org.jafer.transport.PDUDriver;

import java.util.logging.Logger;
import java.util.logging.Level;

import z3950.v3.*;
import asn1.*;

public class Init {

  private ZSession session;
  private PDUDriver pduDriver;
  private String targetInfo;
  private int targetVersion;

  public Init(ZSession session) {

    this.session = session;
    this.pduDriver = session.getPDUDriver();
  }

  public void init(String group, String username, String password) throws ConnectionException {

    InitializeRequest init = new InitializeRequest();

    boolean version[] = new boolean[3];
    version[0] = true;
    version[1] = true; // Z39.50 version 2
    version[2] = true; // Z39.50 version 3

    init.s_protocolVersion = new ProtocolVersion();
    init.s_protocolVersion.value = new ASN1BitString(version);

    boolean options[] = new boolean[15]; // mappings from http://lcweb.loc.gov/z3950/agency/options.html
    options[0] = true; // search
    options[1] = true; // present
    options[2] = true;  // delete result set
//    options[3] = true; // resource-report
//    options[4] = false; // trigger-resource-control
//    options[5] = false; // resource-control
//    options[6] = false; // access-control
//    options[7] = true; // scan
//    options[8] = false; // sort
//    options[9] = false; // unused
//    options[10] = false; // extended-services
//    options[11] = true; // level 1 segmentation
//    options[12] = false; // level 2 segmentation
//    options[13] = false; // concurrent operations
//    options[14] = true; // named result sets

    init.s_options = new Options();
    init.s_options.value = new ASN1BitString(options);
    init.s_preferredMessageSize = new ASN1Integer(128 * 1024);
    init.s_exceptionalRecordSize = new ASN1Integer(256 * 1024);
    init.s_implementationId = new InternationalString();
    init.s_implementationId.value = new ASN1GeneralString("1");
    init.s_implementationName = new InternationalString();
    init.s_implementationName.value = new ASN1GeneralString("JAFER ZClient");
    init.s_implementationVersion = new InternationalString();
    init.s_implementationVersion.value = new ASN1GeneralString("1.00");

    if (username != null) {
      init.s_idAuthentication = new IdAuthentication();
      init.s_idAuthentication.c_idPass = new IdAuthentication_idPass();
      init.s_idAuthentication.c_idPass.s_userId = new InternationalString();
      init.s_idAuthentication.c_idPass.s_userId.value = new ASN1GeneralString(username);

      if (password != null) {
        init.s_idAuthentication.c_idPass.s_password = new InternationalString();
        init.s_idAuthentication.c_idPass.s_password.value = new ASN1GeneralString(password);
      }
      if (group != null) {
        init.s_idAuthentication.c_idPass.s_groupId = new InternationalString();
        init.s_idAuthentication.c_idPass.s_groupId.value = new ASN1GeneralString(group);
      }
    }

    PDU pduOut = new PDU();
    pduOut.c_initRequest = init;
    pduDriver.sendPDU(pduOut);
    PDU pduIn = pduDriver.getPDU();

    InitializeResponse initResp = pduIn.c_initResponse;
    if (initResp == null) {
        throw new ConnectionException("Init failed");
    }

      if (initResp.s_implementationName != null) {
	targetInfo = initResp.s_implementationName.toString();
	if (initResp.s_implementationVersion != null)
          targetInfo += " - " + initResp.s_implementationVersion.toString();
      } else
        targetInfo = "server";

      if (initResp.s_protocolVersion != null) {
	for (int n = 0; n < initResp.s_protocolVersion.value.get().length; n++) {
	  if (initResp.s_protocolVersion.value.get()[n])
	    targetVersion = n + 1;
	}
	targetInfo += " (Version " + targetVersion + ")";

      } else
        targetInfo += " (Version unknown)";

      if (initResp.s_userInformationField != null) {
        if (initResp.s_userInformationField.c_singleASN1type != null)
          targetInfo += "\n" + initResp.s_userInformationField.c_singleASN1type.toString();
      }

      if (initResp.s_otherInfo != null)
        targetInfo += "\n" + initResp.s_otherInfo.toString();

      targetInfo = targetInfo.replaceAll("\"", "");

    if(!initResp.s_result.get())
      throw new ConnectionException("Init failed");
  }

  public String getTargetInfo() {

    return targetInfo;
  }

  public int getTargetVersion() {

    return targetVersion;
  }
}
