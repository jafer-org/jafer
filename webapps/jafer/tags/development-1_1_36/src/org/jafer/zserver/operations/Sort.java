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



package org.jafer.zserver.operations;

import org.jafer.util.ConnectionException;

import org.jafer.util.PDUDriver;
import org.jafer.zserver.*;

import java.net.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;

import z3950.v3.*;
import asn1.*;


/**
 * <p>Runs a Z39.50 Sort - not implemented yet - throws java.lang.UnsupportedOperationException</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class Sort extends Operation {

  private PDU pduRequest;
  private PDU pduResponse;

  public Sort(Session session, PDU pduRequest) {

    super(session, "sort");
    this.pduRequest = pduRequest;
    this.pduResponse = new PDU();
  }

  public PDU runOp() throws Exception {

    throw new java.lang.UnsupportedOperationException("Method org.jafer.zserver.operation.Sort runOp not yet implemented.");
    /**@todo Implement this
    pduResponse.c_sortResponse = new SortResponse();
    pduResponse.c_sortResponse.s_referenceId = pduRequest.c_sortRequest.s_referenceId;
    return pduResponse;
    */
  }
}
