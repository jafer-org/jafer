package org.jafer.zclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.logging.Logger;

import gov.loc.www.zing.srw.*;
import gov.loc.www.zing.srw.interfaces.SRWPort;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.jafer.exception.JaferException;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * This class binds an SRWSession to a host that only supports SRU
 */
public class SRUBinding implements SRWPort
{

    /**
     * Stores a reference to the XML HEADER
     */
    private static String XML_HEADER = "<?xml version=\"1.0\"?>";

    /**
     * Stores a reference to the required start xml of a SOAP envelope
     */
    private static String SOAP_START = "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>";

    /**
     * Stores a reference to the required end xml of a SOAP envelope
     */
    private static String SOAP_END = "</SOAP:Body></SOAP:Envelope>";

    /**
     * Stores a reference to the logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.zclient");

    /**
     * Stores a reference to url of the binding
     */
    private String url;

    /**
     * Constructor
     * 
     * @param url The url of the binding
     */
    public SRUBinding(String url)
    {
        this.url = url;
    }

    /**
     * This method takes an xml response, wraps it in a soap envelope and then
     * uses the axis derserialisation process to return an instance of the
     * specified response class
     * 
     * @param xml The xml to response to process
     * @param responseClass The response class type
     * @return An instance of the desired response class
     * @throws JaferException
     */
    private Object deserialiseResponse(String xml, Class responseClass) throws JaferException
    {
        try
        {
            // we need to extract any XML directives to add this xml to the SOAP
            // message. The simplest way to do this is to parse the xml so we
            // make sureit is valid first and then serialise it back out without
            // the xml header information so it can be put inside a SOAP message
            StringWriter writer = new StringWriter();
            writer.write(SOAP_START);
            XMLSerializer.out(DOMFactory.parse(xml).getDocumentElement(), true, writer);
            writer.write(SOAP_END);
            writer.flush();
            // create a message context and deserialsation context using the
            // soapMSG
            MessageContext msgContext = new MessageContext(new AxisServer());
            DeserializationContext dser = new DeserializationContext(new InputSource(new StringReader(writer.toString())),
                    msgContext, org.apache.axis.Message.RESPONSE);
            // parse the soap envelope
            dser.parse();
            // extract the parsed soap body
            SOAPEnvelope env = dser.getEnvelope();
            RPCElement rpcElem = (RPCElement) env.getFirstBody();
            // get the object value that will perform deserialisation of the
            // message
            return rpcElem.getObjectValue(responseClass);
        }
        catch (Exception exc)
        {
            throw new JaferException("Exception Parsing SRU Response: " + exc);
        }
    }

    /**
     * This method sends a GET request to the specified URL appending the
     * supplied request parameters. The resulting XML is returned to the caller
     * 
     * @param requestParameters The request paramaters string URLEncoded
     * @return The returned xml from the operation request sent
     * @throws IOException
     */
    private String sendRequest(String requestParameters) throws IOException
    {
        logger.fine("Sending operation request using SRU");

        StringBuffer xml = new StringBuffer();
        HttpURLConnection connection = null;

        try
        {
            // create the new session URL and open a new http connection
            URL sessionURL = new URL(url + requestParameters);
            connection = (HttpURLConnection) sessionURL.openConnection();
            // set the request method to post
            connection.setRequestMethod("GET");

            logger.fine("Sending GET to SRU url: " + url);
            connection.connect();
            logger.fine("Processing SRU result");

            // Read the input stream to get the returned XML
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            // loop round reading each line until non are left
            while ((inputLine = in.readLine()) != null)
            {
                // append the line to the xml buffer
                xml.append(inputLine);
                // add space instead of newline so not to corrupt XML
                xml.append(" ");
            }

            logger.fine("SRU result xml: " + xml);
        }
        finally
        {
            if (connection != null)
            {
                // ensure we close the connection
                connection.disconnect();
            }
            logger.fine("Sent and processed operation result using SRU");
        }
        return xml.toString();
    }

    /**
     * This method extracts the extra data parameters and creates a URL string
     * (?key=value&key2=value2) that can be appended to the URL for a
     * scan/search GET request. As all the construction is contained in this
     * method static strings are not defined.<br>
     * <br>
     * see http://www.loc.gov/standards/sru/extra-data.html for param details
     * 
     * @param extraData The extra data to be processed
     * @return The URL parameter string for the extra data
     * @throws UnsupportedEncodingException
     */
    private String constructExtraRequestData(ExtraDataType extraData) throws UnsupportedEncodingException
    {
        StringBuffer params = new StringBuffer();
        // get the extra request data
        MessageElement[] elements = extraData.get_any();
        // make sure we have message elements that represent a node in the XML
        if (elements != null)
        {
            // loop round and process each extra request data element
            for (int index = 0; index < elements.length; index++)
            {
                // This area is difficult to test as its not used by
                // jafer.
                // Hence it may need to be checked later on if this is
                // incoporated into the code base as something that can
                // be
                // set.
                //
                // At the time of coding and according to specification
                // http://www.loc.gov/standards/sru/extra-data.html the
                // format of extra request data for sru is:
                //
                // x-info-<NNN>-<ELEMENTLOCALNAME>=<ELEMENTVALUE>
                //
                // where nnn is is the 'srw:info' authority string if
                // present. It's not very clear on how to set this but
                // it is
                // important for it to be unique hence interpretation
                // here
                // is to set it to:
                // 
                // <currentIndex>-<FIRSTPREFIXIFSET>

                String namespace = "-";
                Iterator iter = elements[index].getNamespacePrefixes();
                if (iter.hasNext())
                {
                    namespace = "-" + (String) iter.next() + "-";
                }

                params.append("&x-info-").append(Integer.toString(index));
                params.append(namespace);
                params.append(elements[index].getLocalName());
                params.append(URLEncoder.encode(elements[index].getValue(), "UTF-8"));
            }
        }
        return params.toString();
    }

    /**
     * This method extracts the scan request parameters and creates a URL string
     * (?key=value&key2=value2) that can be appended to the URL for a scan GET
     * request. As all the construction is contained in this method static
     * strings are not defined.<br>
     * <br>
     * see http://www.loc.gov/standards/sru/scan/index.html for param details<br>
     * <br>
     * <b>Note - String parameters must be URL Encoded<b>
     * 
     * @param request The ScanRequestType of request parameters
     * @return The URL parameter string for the scan operation
     * @throws JaferException
     */
    private String constructURLScanRequestParameters(ScanRequestType request) throws JaferException
    {
        logger.fine("Constructing scan operation SRU parameters ");

        // As we have a lot of string concatenation to do use a string
        // buffer
        StringBuffer params = new StringBuffer();
        try
        {
            // Note format needs to conform to the following format and any
            // values that are strings must be URL encoded:
            // ?key=value&key2=value2&key3=value3

            // these keys are mandatory and must be checked before we move
            // forward
            if (request.getVersion() == null || request.getVersion().length() == 0)
            {
                throw new JaferException("Version is mandatory when sending scan SRU request");
            }
            if (request.getScanClause() == null || request.getScanClause().length() == 0)
            {
                throw new JaferException("Scan clause is mandatory when sending scan SRU request");
            }

            // these keys are mandatory
            params.append("?operation=scan");
            params.append("&version=").append(URLEncoder.encode(request.getVersion(), "UTF-8"));
            params.append("&scanClause=").append(URLEncoder.encode(request.getScanClause(), "UTF-8"));

            // these keys are optional and are only added if the value is not
            // null and in the case of strings the value is not an empty string
            if (request.getResponsePosition() != null)
            {
                params.append("&responsePosition=").append(request.getResponsePosition());
            }
            if (request.getMaximumTerms() != null)
            {
                params.append("&maximumTerms=").append(request.getMaximumTerms());
            }
            if (request.getStylesheet() != null)
            {
                params.append("&stylesheet=").append(request.getStylesheet());
            }
            if (request.getExtraRequestData() != null)
            {
                params.append(constructExtraRequestData(request.getExtraRequestData()));
            }
        }
        catch (UnsupportedEncodingException exc)
        {
            logger.severe(exc.getMessage());
            throw new JaferException("Bad URL encoding scheme for SRU scan: " + exc);
        }

        logger.fine("Scan operation SRU parameters: " + params.toString());

        return params.toString();
    }

    /**
     * This method extracts the search request parameters and creates a URL
     * string (?key=value&key2=value2) that can be appended to the URL for a
     * search GET request.As all the construction is contained in this method
     * static strings are not defined.<br>
     * <br>
     * see http://www.loc.gov/standards/sru/sru-spec.html for param details <br>
     * <br>
     * <b>Note - String parameters must be URL Encoded<b>
     * 
     * @param request The SearchRetrieveRequestType of request parameters
     * @return The URL parameter string for the search retrieve operation
     * @throws JaferException
     */
    private String constructURLSearchRequestParameters(SearchRetrieveRequestType request) throws JaferException
    {
        logger.fine("Constructing search retrieve operation SRU parameters ");

        // As we have a lot of string concatenation to do use a string
        // buffer
        StringBuffer params = new StringBuffer();
        try
        {
            // Note format needs to conform to the following format and any
            // values that are strings must be URL encoded:
            // ?key=value&key2=value2&key3=value3

            // these keys are mandatory and must be checked before we move
            // forward
            if (request.getVersion() == null || request.getVersion().length() == 0)
            {
                throw new JaferException("Version is mandatory when sending search and retrieve SRU request");
            }
            if (request.getQuery() == null || request.getQuery().length() == 0)
            {
                throw new JaferException("Query is mandatory when sending search and retrieve SRU request");
            }

            // these keys are mandatory
            params.append("?operation=searchRetrieve");
            params.append("&version=").append(URLEncoder.encode(request.getVersion(), "UTF-8"));
            params.append("&query=").append(URLEncoder.encode(request.getQuery(), "UTF-8"));

            // these keys are optional and are only added if the value is not
            // null and in the case of strings the value is not an empty string
            if (request.getStartRecord() != null)
            {
                params.append("&startRecord=").append(request.getStartRecord());
            }
            if (request.getMaximumRecords() != null)
            {
                params.append("&maximumRecords=").append(request.getMaximumRecords());
            }
            if (request.getRecordPacking() != null && request.getRecordPacking().length() > 0)
            {
                params.append("&recordPacking=");
                params.append(URLEncoder.encode(request.getRecordPacking(), "UTF-8"));
            }
            if (request.getRecordSchema() != null && request.getRecordSchema().length() > 0)
            {
                params.append("&recordSchema=");
                params.append(URLEncoder.encode(request.getRecordSchema(), "UTF-8"));
            }
            if (request.getRecordXPath() != null && request.getRecordXPath().length() > 0)
            {
                params.append("&recordXPath=");
                params.append(URLEncoder.encode(request.getRecordXPath(), "UTF-8"));
            }
            if (request.getSortKeys() != null && request.getSortKeys().length() > 0)
            {
                params.append("&sortKeys=");
                params.append(URLEncoder.encode(request.getSortKeys(), "UTF-8"));
            }
            if (request.getResultSetTTL() != null)
            {
                params.append("&resultSetTTL=").append(request.getResultSetTTL());
            }
            if (request.getStylesheet() != null)
            {
                params.append("&stylesheet=").append(request.getStylesheet());
            }
            if (request.getExtraRequestData() != null)
            {
                params.append(constructExtraRequestData(request.getExtraRequestData()));
            }
        }
        catch (UnsupportedEncodingException exc)
        {
            logger.severe(exc.getMessage());
            throw new JaferException("Bad URL encoding scheme for SRU search: " + exc);
        }

        logger.fine("Search retrieve operation SRU parameters: " + params.toString());

        return params.toString();
    }

    /**
     * This method performs the scanOperation connecting to the host using SRU.
     * It takes the scan request and converts it to an SRU url to send to the
     * server. When the response is returned the XML is converted back to the
     * ScanResponseType message object.
     * 
     * @param body The ScanRequestType message
     * @return The ScanResponseType message
     * @throws RemoteException
     */
    public ScanResponseType scanOperation(ScanRequestType request) throws RemoteException
    {
        logger.fine("Executing scan operation using SRU");
        ScanResponseType response = null;

        try
        {
            // construct the URLEncoded parameter string to add to the default
            // URL and send the request to the server
            String xml = sendRequest(constructURLScanRequestParameters(request));
            logger.fine("Deserialising SRU scan operation Result");
            // take the xml response and deserialise it to response object
            response = (ScanResponseType) deserialiseResponse(xml, ScanResponseType.class);
        }
        catch (IOException exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("IO ERROR performing SRU scan: " + exc);
        }
        catch (JaferException exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("Jafer Exception performing SRU scan: " + exc);
        }
        finally
        {
            logger.fine("Completed scan operation using SRU");
        }
        return response;

    }

    /**
     * This method performs the searchRetrieveOperation connecting to the host
     * using SRU. It takes the searchRetrieve request and converts it to an SRU
     * url to send to the server. When the response is returned the XML is
     * converted back to the SearchRetrieveResponseType message object.
     * 
     * @param body The SearchRetrieveRequestType message
     * @return The SearchRetrieveResponseType message
     * @throws RemoteException
     */
    public SearchRetrieveResponseType searchRetrieveOperation(SearchRetrieveRequestType request) throws RemoteException
    {
        logger.fine("Executing search and retrieve operation using SRU");
        SearchRetrieveResponseType response = null;
        try
        {
            // construct the URLEncoded parameter string to add to the default
            // URL and send the request to the server
            String xml = sendRequest(constructURLSearchRequestParameters(request));

            logger.fine("Deserialising SRU search and retrieve operation Result");
            // take the xml response and deserialise it to response object
            response = (SearchRetrieveResponseType) deserialiseResponse(xml, SearchRetrieveResponseType.class);
        }
        catch (IOException exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("IO ERROR performing SRU search: " + exc);
        }
        catch (JaferException exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("Jafer Exception performing SRU search: " + exc);
        }
        finally
        {
            logger.fine("Completed search and retrieve operation using SRU");
        }

        return response;
    }
}
