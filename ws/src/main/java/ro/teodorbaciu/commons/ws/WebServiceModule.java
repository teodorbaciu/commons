/*
Copyright 2015 Teodor Baciu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package ro.teodorbaciu.commons.ws;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describes the methods that should be exposed by a webservice.
 * 
 * @author Teodor Baciu
 * 
 */
public abstract class WebServiceModule {

	public static final String OPERATION_PARAM_NAME = "op";

	/**
	 * The logger to be used.
	 */
	private static final Logger log = LoggerFactory.getLogger(WebServiceModule.class);

	/**
	 * Stores the webservice operations defined in this module.
	 */
	private Hashtable<String, WebserviceOperation> hashWsOperations = null;

	/**
	 * Constructor.
	 */
	public WebServiceModule() {
		hashWsOperations = new Hashtable<String, WebserviceOperation>();
		initializeWsOperations();

	}

	/**
	 * Intializes the ws operations available in this module. For each operation one should call "registerOperation(...)"
	 */
	protected abstract void initializeWsOperations();

	/**
	 * Returns the name of the module.
	 * 
	 * @return a String representing the name of the module.
	 */
	public abstract String getWebserviceModuleName();

	/**
	 * Sends the specified response down the wire to the browser.
	 * 
	 * @param result the text to send
	 * @param response the servlet response object
	 * @throws ServletException if an error occurs
	 * @throws IOException if an error occurs
	 */
	protected void writeResponse(String result, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pw = response.getWriter();
		pw.print(result);
		pw.flush();
		pw.close();

	}

	/**
	 * Returns the json error response
	 * @return the JSON representation of the error response.
	 */
	protected String formJsonErrorResponse(String errorMessage) {

		String escapedMessage = StringEscapeUtils.escapeJava(errorMessage);
		String result = "{" + "\"success\":false" + "," + "\"errorMessage\":" + "\"" + escapedMessage + "\"" + "}";

		return result;
	}

	/**
	 * Registers the specified webservice operation.
	 * 
	 * @param name
	 *            the name of the operation
	 * @param op
	 *            the operation to register
	 */
	public void registerOperation(String name, WebserviceOperation op) {

		// first check if the specified op is not already
		// registered

		if (hashWsOperations.get(name) != null) {
			throw new RuntimeException("Operation '" + name + "' is already registered !");
		}

		if (op == null) {
			throw new RuntimeException("Operation cannot be null");
		}

		hashWsOperations.put(name, op);

	}

	/**
	 * Dispatches the request for processing to this module.
	 * 
	 * @param request
	 *            the servlet request
	 * @param response
	 *            the servlet response
	 * @throws Exception
	 *             if an error occurs
	 */
	void dispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String operationName = request.getParameter(OPERATION_PARAM_NAME);

		if (StringUtils.isEmpty(operationName)) {

			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
					"[" + getWebserviceModuleName() + "]: " + "Please specify the 'op' http request parameter !");
			return;

		}

		//
		// Need to set the content type and the encoding
		//
		response.setContentType("application/json; charset=UTF-8");

		// Get the operation and execute it
		WebserviceOperation wsOperation = hashWsOperations.get(operationName);

		if (wsOperation == null) {
			response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,
					"[" + getWebserviceModuleName() + "]: " + "Webservice operation with name '" 
							+ operationName + "' not found !");
			return;

		}

		String jsonResult = "";

		try {

			jsonResult = wsOperation.execute(request, response);

		} catch (IOException ioe) {
			throw ioe;
		} catch (ServletException se) {
			throw se;
		} catch (Exception ex) {

			log.error("Unhandled exception caught while dispatching request", ex);
			jsonResult = formJsonErrorResponse("error" /* e.getMessage() */);

		}

		if (jsonResult != null) {

			// Send the response to the client
			if (StringUtils.isEmpty(jsonResult)) {
				jsonResult = formJsonErrorResponse("Could not execute the operation specified with name '" + operationName + " !");
			}

			String opIdentifier = "[" + getWebserviceModuleName() + ":" + operationName + "] ";
			log.debug(opIdentifier + jsonResult);

			writeResponse(jsonResult, response);

		}
	}

}
