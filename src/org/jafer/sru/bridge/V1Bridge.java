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
package org.jafer.sru.bridge;

import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

/**
 * class that provides standard methods to all operation bridges
 */
public class V1Bridge
{

    /**
     * Creates a diagnostic response for the explain record for fatal errors
     *
     * @param code The diagnostic code
     * @param message The diagnostic message
     * @param details The diagnostic details (null will extract stack trace from
     *        exception)
     * @param exc The exception that caused the error
     * @return The explain response with the diagnostic
     */
    public String createExplainDiagnosticResponse(String code, String message, String details, Exception exc)
    {
        try
        {
            DiagnosticsType diagostics = createDiagnostics(code, message, details, exc);
            if (diagostics != null)
            {
                // obtain the serialiser to serialise diagnostic to XML to put
                // in
                // record packaging for explain
                QName diagQName = new QName("http://www.loc.gov/zing/srw/diagnostic/", "diagnostic");
                Serializer ser = DiagnosticType.getSerializer(null, DiagnosticType.class, diagQName);
                StringWriter diagWriter = new StringWriter();
                ser.serialize(diagQName, null, diagostics.getDiagnostic()[0], new SerializationContext(diagWriter));
                // create the message fragment
                MessageElement[] elements = { new MessageElement(diagQName, diagWriter.getBuffer().toString()) };
                StringOrXmlFragment fragment = new StringOrXmlFragment(elements);

                // create error record type
                RecordType record = new RecordType();
                record.setRecordSchema("info:srw/schema/1/diagnostics-v1.1");
                record.setRecordPacking("xml");
                record.setRecordData(fragment);
                // create empty reponse adding version and diagnostic
                ExplainResponseType response = new ExplainResponseType();
                response.setVersion("1.1");
                response.setDiagnostics(diagostics);
                response.setRecord(record);

                // obtain the serialiser to return this to XML
                QName explainQName = new QName("http://www.loc.gov/zing/srw/", "explainResponse");
                Serializer responseSer = ExplainResponseType.getSerializer(null, ExplainResponseType.class, explainQName);
                StringWriter responseWriter = new StringWriter();
                // switch pretty print off
                SerializationContext context = new SerializationContext(responseWriter);
                context.setPretty(false);
                responseSer.serialize(explainQName, null, response, context);
                return responseWriter.getBuffer().toString();
            }

        }
        catch (Exception e)
        {
            // these should never occur but if they do then return empty string
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Creates a diagnostic response for the search record for fatal errors
     *
     * @param code The diagnostic code
     * @param message The diagnostic message
     * @param details The diagnostic details (null will extract stack trace from
     *        exception)
     * @param exc The exception that caused the error
     * @return The search response with the diagnostic
     */
    public String createSearchDiagnosticResponse(String code, String message, String details, Exception exc)
    {
        try
        {
            DiagnosticsType diagostics = createDiagnostics(code, message, details, exc);
            if (diagostics != null)
            {
                SearchRetrieveResponseType response = new SearchRetrieveResponseType();
                response.setVersion("1.1");
                response.setDiagnostics(diagostics);
                response.setNumberOfRecords(new NonNegativeInteger("0"));
                // // obtain the serialiser to return this to XML
                QName searchQName = new QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponse");
                Serializer ser = SearchRetrieveResponseType.getSerializer(null, SearchRetrieveResponseType.class, searchQName);
                StringWriter responseWriter = new StringWriter();
                // switch pretty print off
                SerializationContext context = new SerializationContext(responseWriter);
                context.setPretty(false);
                ser.serialize(searchQName, null, response, context);
                return responseWriter.getBuffer().toString();
            }
        }
        catch (Exception e)
        {
            // these should never occur but if they do then return empty string
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Creates a diagnostic response for the scan record for fatal errors
     *
     * @param code The diagnostic code
     * @param message The diagnostic message
     * @param details The diagnostic details (null will extract stack trace from
     *        exception)
     * @param exc The exception that caused the error
     * @return The ccan response with the diagnostic
     */
    public String createScanDiagnosticResponse(String code, String message, String details, Exception exc)
    {
        try
        {
            DiagnosticsType diagostics = createDiagnostics(code, message, details, exc);
            if (diagostics != null)
            {
                ScanResponseType response = new ScanResponseType();
                response.setVersion("1.1");
                response.setDiagnostics(diagostics);

                // obtain the serialiser to return this to XML
                QName scanQName = new QName("http://www.loc.gov/zing/srw/", "scanResponse");
                Serializer ser = ScanResponseType.getSerializer(null, ScanResponseType.class, scanQName);
                StringWriter responseWriter = new StringWriter();
                // switch pretty print off
                SerializationContext context = new SerializationContext(responseWriter);
                context.setPretty(false);
                ser.serialize(scanQName, null, response, context);
                return responseWriter.getBuffer().toString();
            }
        }
        catch (Exception e)
        {
            // these should never occur but if they do then return empty string
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Creates a diagnostic response type
     *
     * @param code The diagnostic code
     * @param message The diagnostic message
     * @param details The diagnostic details (null will extract stack trace from
     *        exception)
     * @param exc The exception that caused the error
     * @return A diagnosticType object for embedding in the response
     */
    private DiagnosticsType createDiagnostics(String code, String message, String details, Exception exc)
    {
        try
        {
            URI uri = new URI("info:srw/diagnostic/" + code);
            // if details are null add stack trace
            if (details == null)
            {
                details = exc.getMessage();
            }

            // create the diagnostic type
            DiagnosticType diagnosticType = new DiagnosticType( details, message, uri);
            DiagnosticType[] diag = { diagnosticType };
            return new DiagnosticsType(diag);
        }
        catch (MalformedURIException exc1)
        {
            exc1.printStackTrace();
            return null;
        }

    }
}
