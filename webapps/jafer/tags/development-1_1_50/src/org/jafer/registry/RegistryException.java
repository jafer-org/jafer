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

package org.jafer.registry;

/**
 * This class defines the methods the user should be able to call to
 * determine information about the exception
 */
public abstract class RegistryException extends Exception
{
    /**
	 * Signifies that a particular publisher assertion (consisting of two businessKey values, and
	 * a keyed reference with three components) cannot be identified in a save or delete operation.
	 */
	public static final String E_assertionNotFound = "E_assertionNotFound";

	/**
	 * Signifies that the authentication token value passed in the authInfo argument
	 * is no longer valid because the token has expired.
	 */
	public static final String E_authTokenExpired = "E_authTokenExpired";


	/**
	 * Signifies that the authentication token value passed in the authInfo argument
	 * is either missing or is not valid.
	 */
	public static final String E_authTokenRequired = "E_authTokenRequired";

	/**
	 * Signifies that user account limits have been exceeded.
	 */
	public static final String E_accountLimitExceeded = "E_accountLimitExceeded";

	/**
	 * Signifies that the request cannot be processed at the current time.
	 */
	public static final String E_busy = "E_busy";

	/**
	 * Restrictions have been placed by the taxonomy provider on the types of
	 * information that should be included at that location within a specific
	 * taxonomy.  The validation routine chosen by the Operator Site has
	 * rejected this tModel for at least one specified category.
	 */
	public static final String E_categorizationNotAllowed = "E_categorizationNotAllowed";

	/**
	 * Signifies that a serious technical error has occurred while processing
	 * the request.
	 */
	public static final String E_fatalError= "E_fatalError";

	/**
	 * Signifies that the request cannot be satisfied because one or more uuid_key
	 * values specified is not a valid key value.  This will occur if a uuid_key
	 * value is passed in a tModel that does not match with any known tModel key.
	 */
	public static final String E_invalidKeyPassed = "E_invalidKeyPassed";

	/**
	 * Signifies that an attempt was made to save a businessEntity containing a service projection
	 * that does not match the businessService being projected. The serviceKey of at least one such
	 * businessService will be included in the dispositionReport
	 */
	public static final String E_invalidProjection = "E_invalidProjection";

	/**
	 * Signifies that the given keyValue did not correspond to a category within
	 * the taxonomy identified by a tModelKey value within one of the categoryBag elements provided.
	 */
	public static final String E_invalidCategory = "E_invalidCategory";

	/**
	 *  Signifies that one of assertion status values passed is unrecognized.  The completion status
	 *  that caused the problem will be clearly indicated in the error text.
	 */
	public static final String E_invalidCompletionStatus = "E_invalidCompletionStatus";

	/**
	 * An error occurred with one of the uploadRegister URL values.
	 */
	public static final String E_invalidURLPassed = "E_invalidURLPassed";

	/**
	 * A value that was passed in a keyValue attribute did not pass validation.  This applies to
	 * checked categorizations, identifiers and other validated code lists. The error text will
	 * clearly indicate the key and value combination that failed validation.
	 */
	public static final String E_invalidValue = "E_invalidValue";

	/**
	 * Signifies that the request cannot be satisfied because one or more uuid_key
	 * values specified has previously been hidden or removed by the requester.
	 * This specifically applies to the tModelKey values passed.
	 */
	public static final String E_keyRetired = "E_keyRetired";

	/**
	 * Signifies that an error was detected while processing elements that were annotated with
	 * xml:lang qualifiers. Presently, only the description and name elements support xml:lang
	 * qualifications.
	 */
	public static final String E_languageError = "E_languageError";

	/**
	 * Signifies that the message it too large.  The upper limit will be clearly indicated in the
	 * error text.
	 */
	public static final String E_messageTooLarge = "E_messageTooLarge";

	/**
	 * Signifies that the partial name value passed exceeds the maximum name length designated by
	 * the policy of an implementation or Operator Site.
	 */
	public static final String E_nameTooLong = "E_nameTooLong";

	/**
	 * Signifies that one or more of the uuid_key values passed refers to data
	 * that is not controlled by the Operator Site that received the request for processing.
	 */
	public static final String E_operatorMismatch = "E_operatorMismatch";

	/**
	 * Signifies that the target publisher cancelled the custody transfer operation.
	 */
	public static final String E_publisherCancelled = "E_publisherCancelled";

	/**
	 * Signifies that a custody transfer request has been refused.
	 */
	public static final String E_requestDenied = "E_requestDenied";

	/**
	 * Signifies that the request could not be carried out because a needed validate_values service did not
	 * respond in a reasonable amount of time.
	 */
	public static final String E_requestTimeout = "E_requestTimeout";

	/**
	 * Signifies that the target publisher was unable to match the shared secret and the five (5)
	 * attempt limit was exhausted. The target operator automatically cancelled the transfer
	 * operation.
	 */
	public static final String E_secretUnknown = "E_secretUnknown";

	/**
	 * Signifies no failure occurred. This return code is used with the dispositionReport for
	 * reporting results from requests with no natural response document.
	 */
	public static final String E_success = "E_success";

	/**
	 * Signifies that too many or incompatible arguments were passed. The error text will clearly
	 * indicate the nature of the problem.
	 */
	public static final String E_tooManyOptions = "E_tooManyOptions";

	/**
	 * Signifies that a custody transfer request will not succeed.
	 */
	public static final String E_transferAborted = "E_transferAborted";

	/**
	 * Signifies that the value of the generic attribute passed is unsupported by the Operator
	 * Instance being queried.
	 */
	public static final String E_unrecognizedVersion = "E_unrecognizedVersion";

	/**
	 * Signifies that the user ID and password pair passed in a get_authToken message is not known
	 * to the Operator Site or is not valid.
	 */
	public static final String E_unknownUser = "E_unknownUser";

	/**
	 * Signifies that the implementer does not support a feature or API.
	 */
	public static final String E_unsupported = "E_unsupported";

	/**
	 * Signifies that an attempt was made to reference a taxonomy or identifier system in a
	 * keyedReference whose tModel is categorized with the unvalidatable categorization.
	 */
	public static final String E_unvalidatable = "E_unvalidatable";

	/**
	 * Signifies that one or more of the uuid_key values passed refers to data
	 * that is not controlled by the individual who is represented by the authentication token.
	 */
	public static final String E_userMismatch = "E_userMismatch";

	/**
	 * Signifies that a value did not pass validation because of contextual issues. The value may
	 * be valid in some contexts, but not in the context used. The error text may contain information
	 * about the contextual problem.
	 */
	public static final String E_valueNotAllowed = "E_valueNotAllowed";
	/**
     * Constructor supplying a message
     * 
     * @param message
     */
    protected RegistryException(String message)
    {
        super(message);
    }

    /**
     * Constructor supplying a message
     * 
     * @param exc
     */
    protected RegistryException(Exception exc)
    {
        super(exc);
    }

    /**
     * Constructor supplying a message and exception
     * 
     * @param message
     * @param exc
     */
    protected RegistryException(String message, Exception exc)
    {
        super(message, exc);
    }

    /**
     * Returns the UDDI defined error number
     * @return The erro rnumber
     */
    public abstract String getErrorNumber();
    /**
     * Returns the UDDI defined code
     * @return The code
     */
    public abstract String getErrorCode();
    /**
     * Returns the UDDI defined error text
     * @return The error Text
     */
    public abstract String getErrorText();
    
    /**
     * This method returns the error message with a full stack trace. This is
     * done by calling printStackTrace on Exception that internally calls
     * toString() to get our message.
     * 
     * @return The full stack trace string of the exception
     */
    public abstract String getStackTraceString();
}
