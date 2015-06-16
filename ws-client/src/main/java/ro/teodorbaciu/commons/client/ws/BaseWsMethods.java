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
package ro.teodorbaciu.commons.client.ws;



import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.teodorbaciu.commons.client.ws.exceptions.AuthorizationRequiredException;
import ro.teodorbaciu.commons.client.ws.exceptions.InvalidLoginCredentialsException;
import ro.teodorbaciu.commons.client.ws.exceptions.InvalidWsParamsException;
import ro.teodorbaciu.commons.client.ws.exceptions.MaxFailedLoginsExceededException;
import ro.teodorbaciu.commons.client.ws.exceptions.OperationForbiddenException;
import ro.teodorbaciu.commons.client.ws.exceptions.ReAuthenticationException;
import ro.teodorbaciu.commons.client.ws.progress.MultipartEntityWithProgressMonitoring;
import ro.teodorbaciu.commons.client.ws.progress.WriteListener;
import ro.teodorbaciu.commons.client.ws.util.WebClientDevWrapper;

/**
 * Base class for webservice methods.
 * @author Teodor Baciu
 *
 */
public abstract class BaseWsMethods {

	/**
	 * The logger to be used.
	 */
	private final static Logger log = LoggerFactory.getLogger(BaseWsMethods.class);
	
	
	/**
	 * Stores the name of the host that provides the
	 * webservices.
	 */
	protected String webserviceHost;
	
	/**
	 * The http client used for webservice calls.
	 */
	protected DefaultHttpClient wsHttpClient;
	
	/**
	 * The http context for which the calls are made.
	 */
	protected HttpContext httpContext;
		
	/**
	 * Ignores https warnings and allows the communication with a server
	 * that provides untrusted certificates.
	 */
	protected boolean allowUntruestedHttpsServer;
	
	protected String authenticatedWebserviceUri;
	
	protected String publicWebserviceUri;
	
	/**
	 * Constructor.
	 */
	public BaseWsMethods() {
		
		publicWebserviceUri = "/pws";
		authenticatedWebserviceUri = "/aws";
		
	}
	
	/**
	 * Calls the specified webservice that is available the address specified with the publicWebserviceUri,
	 *  without trying to re-authenticate if an authorization required is returned on the server.
	 * @param moduleName the name of the module
	 * @param op the operation within that module
	 * @param a list containing the parameters
	 * @return a String representing the response
	 */
	protected String callPublicWsOperation(String moduleName, String op, List<NameValuePair> wsParamsList)
			throws AuthorizationRequiredException, OperationForbiddenException,
			UnsupportedEncodingException,  ClientProtocolException, IOException  {
		
		//form the request target
		String targetUrl = webserviceHost + publicWebserviceUri;
		
		try {
			
			return callServer(moduleName, op, targetUrl, wsParamsList);
			
		} finally {
			currentRequest = null;
		}
	}

	/**
	 * Executes an Http get and returns the contents of the
	 * call as a byte array.
	 * @param uri
	 * @return
	 */
	public byte[] executeHttpGet(String uri) throws Exception {
		
		String targetUrl = webserviceHost + uri;
		HttpGet get = new HttpGet(targetUrl);
		
		HttpResponse response = wsHttpClient.execute(get);
		int statusCode = response.getStatusLine().getStatusCode(); 
		String reasonPhrase = response.getStatusLine().getReasonPhrase();
		
		if( statusCode == 401 ) {
			
			get.abort();//release resources
			throw new AuthorizationRequiredException("You need to authenticate first !");
			
		} else if( statusCode == 412) {
			
			get.abort();
			throw new InvalidWsParamsException("Error calling http get because of invalid parameters ! Server returned: " + reasonPhrase);
			
		} else if( statusCode == 403) {
			
			get.abort();
			throw new OperationForbiddenException("The server refused to execute the specified operation ! Server returned: " + reasonPhrase);
			
		} else if( statusCode != 200 ) {
			
			get.abort();//release resources
			throw new RuntimeException("Could not get http response ! Status:" + statusCode + " Server returned: " + reasonPhrase);
			
		}

		//get the response content
		HttpEntity entity = response.getEntity();
		if( entity == null )
			throw new RuntimeException("Could not get http response ! Server returned: " + reasonPhrase);
		
		return EntityUtils.toByteArray(entity);
		
	}
	
	/**
	 * Does a multipart server request.
	 * @param filePath
	 * @return the devyn id of the uploaded file
	 * @throws Exception
	 */
	public String callWsOperationMultipartPost(String moduleName, String op, List<NameValuePair> wsParamsList, File fileToUpload, WriteListener writeListener) 
	throws AuthorizationRequiredException, OperationForbiddenException, ReAuthenticationException,
			UnsupportedEncodingException,  ClientProtocolException, IOException  {
		
		String targetUrl = webserviceHost + authenticatedWebserviceUri;
		
		try {
			
			return callServerMultipartPost(moduleName, op, targetUrl, wsParamsList, fileToUpload, writeListener);
			
		} catch(AuthorizationRequiredException uae) {
			
			//if this thrown, it means that the authorized
			//session has expired on the server and we need to authenticate
			//again
			if( reAuthenticate() ) {
				
				//re-authentication successful, call the operation again
				return callServerMultipartPost(moduleName, op, targetUrl, wsParamsList, fileToUpload, writeListener);
				
			} else {
				
				//could not authenticate again - are the credentials still good ?
				throw new ReAuthenticationException("Could not authenticate again for calling multipart ws operation !");
				
			}
		} finally {
			currentRequest = null;
		}
		
	}
	
	/**
	 * Cancels the current execution. Useful for cancelling uploads.
	 */
	public void cancelCurrentRequest() {
		currentRequest.abort();
	}
	
	/**
	 * Calls a webservice that might have required authentication first.
	 * @param moduleName the name of the module
	 * @param op the operation to call
	 * @return the json result of the call
	 */
	protected String callWsOperation(String moduleName, String op, List<NameValuePair> wsParamsList)
	throws AuthorizationRequiredException, OperationForbiddenException, ReAuthenticationException,
			UnsupportedEncodingException,  ClientProtocolException, IOException {
		
		//form the request target
		String targetUrl = webserviceHost + authenticatedWebserviceUri;
		
		try {
			
			return callServer(moduleName, op, targetUrl, wsParamsList);
			
		} catch(AuthorizationRequiredException uae) {
			
			//if this thrown, it means that the authorized
			//session has expired on the server and we need to authenticate
			//again
			if( reAuthenticate() ) {
				
				//re-authentication successful, call the operation again
				return callServer(moduleName, op, targetUrl, wsParamsList);
				
			} else {
				
				//could not authenticate again - are the credentials still good ?
				throw new ReAuthenticationException("Could not authenticate again for calling ws operation !");
				
			}
		} finally {
			currentRequest = null;
		}
	}
	
	/**
	 * Returns a list of name/value pairs that will be sent as authentication
	 * parameters.
	 * @return a {@link List} containing {@link NameValuePair} objects.
	 */
	protected abstract List<NameValuePair> getAuthenticationParams();
	
	/**
	 * Tries to authenticate again this client session based
	 * on the previous authentication exception. This method catches
	 * any exception that might arise.
	 * @return true if successfully authenticated, false otherwise
	 */
	protected boolean reAuthenticate() {
		
		try {
			
			List<NameValuePair> authParams = getAuthenticationParams();
			return authenticate(authParams);
			
		} catch(Exception e) {
			log.error("Exception thrown during re-authentication", e);
			return false;
		}
	}
	
	/**
	 * Tries to authenticate application on the server.
	 * @param username the name of the user
	 * @param password the password of the user
	 * @param workingPointId the id of the user selected working point
	 * @throws Exception if an error occurs
	 */
	public boolean authenticate(List<NameValuePair> authenticationParams)
	throws InvalidLoginCredentialsException, MaxFailedLoginsExceededException, 
	UnsupportedEncodingException, ClientProtocolException, IOException {

		httpContext =  new BasicHttpContext();
		
		//form the request
		String targetUrl = webserviceHost + "/login";
		HttpPost post = new HttpPost(targetUrl);
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.addAll(authenticationParams);
		
		paramsList.add( new BasicNameValuePair("only-send-http-response", "true"));
		
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsList, "UTF-8");
		post.setEntity(formEntity);

		//get the response
		HttpResponse response = wsHttpClient.execute(post, httpContext);
		int statusCode = response.getStatusLine().getStatusCode(); 
		if( statusCode != 200 ) {
			
			post.abort();
			
			if( statusCode == 401) {
				throw new InvalidLoginCredentialsException("The username/password combination was not valid ");
			}
			
			return false;
		}
		
		post.abort();//close the connection

		//getCookie();
		return true;
		
	}
	
	/**
	 * Returns the cookie associated with the current webservice session.
	 * @return null if no cookie could be found
	 */
	public Cookie getCookie() {
		
		CookieStore cookieStore = wsHttpClient.getCookieStore();
		if( cookieStore == null ) {
			return null;
		}

		List<Cookie> listCookies = cookieStore.getCookies();
		if( listCookies.size() == 0 )
			return null;
		
		return listCookies.get( listCookies.size() -1 );
		
	}

	/**
	 * The current request being executed.
	 */
	private HttpUriRequest currentRequest;
	
	/**
	 * Handles the communication details with the server.
	 * @param moduleName the name of the module
	 * @param op the operation to call
	 * @param targetUrl the url to post the call
	 * @param wsParamsList contains the parameters to submit for the webservice call
	 * @return the result of the call
	 * @throws Exception if an error occurs
	 */
	private String callServerMultipartPost(String moduleName, String op, String targetUrl, List<NameValuePair> wsParamsList, File fileToUpload, WriteListener writeListener)
	throws AuthorizationRequiredException, OperationForbiddenException, 
	UnsupportedEncodingException, ClientProtocolException, IOException {
		
		if( currentRequest != null ) {
			throw new RuntimeException( "Another webservice request is still executing !" );
		}
		
		String postUrl = targetUrl + "?module="+moduleName + "&op="+op;
		HttpPost post = new HttpPost(postUrl);
		

		MultipartEntity reqEntity = null;
		if( writeListener != null ) {
			reqEntity = new MultipartEntityWithProgressMonitoring(writeListener);
		} else {
			reqEntity = new MultipartEntity();
		}
		
		for(NameValuePair pair:wsParamsList) {
			reqEntity.addPart(pair.getName(), new StringBody(pair.getValue()));
		}
		reqEntity.addPart("data", new FileBody(fileToUpload));
		post.setEntity(reqEntity);

		return processServerPost(post);
	}

	/**
	 * Executes the http post action and interprets the results.
	 * @param post the http post to execute
	 * @return the String received from the server.
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws AuthorizationRequiredException
	 * @throws InvalidWsParamsException
	 * @throws OperationForbiddenException
	 */
	private String processServerPost(HttpPost post) throws IOException,
			ClientProtocolException, AuthorizationRequiredException,
			InvalidWsParamsException, OperationForbiddenException {
		
		//get the response
		currentRequest = post;
		HttpResponse response = wsHttpClient.execute(post);
		int statusCode = response.getStatusLine().getStatusCode(); 
		String reasonPhrase = response.getStatusLine().getReasonPhrase();
		
		if( statusCode == 401 ) {
			
			post.abort();//release resources
			throw new AuthorizationRequiredException("You need to authenticate first !");
			
		} else if( statusCode == 412) {
			
			
			post.abort();
			throw new InvalidWsParamsException("Error calling webservice because of invalid parameters ! Server returned: " + reasonPhrase);
			
		} else if( statusCode == 403) {
			
			post.abort();
			throw new OperationForbiddenException("The server refused to execute the specified operation !  Server returned: " + reasonPhrase);
			
		} else if( statusCode != 200 ) {
			
			post.abort();//release resources
			throw new RuntimeException("Could not get webservice response ! Status:" + statusCode + " Server returned: " + reasonPhrase);
			
		}

		//get the response content
		HttpEntity entity = response.getEntity();
		if( entity == null )
			throw new RuntimeException("Could not get webservice response !  Server returned: " + reasonPhrase);
		
	
		return EntityUtils.toString(entity, "UTF-8");
	}
	
	/**
	 * Handles the communication details with the server.
	 * @param moduleName the name of the module
	 * @param op the operation to call
	 * @param targetUrl the url to post the call
	 * @param wsParamsList contains the parameters to submit for the webservice call
	 * @return the result of the call
	 */
	private String callServer(String moduleName, String op, String targetUrl, List<NameValuePair> wsParamsList)
	throws AuthorizationRequiredException, OperationForbiddenException,
		UnsupportedEncodingException,  ClientProtocolException, IOException {
		
		HttpPost post = new HttpPost(targetUrl);
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add( new BasicNameValuePair("module", moduleName));
		paramsList.add( new BasicNameValuePair("op", op));
		paramsList.addAll(wsParamsList);
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsList, "UTF-8");
		post.setEntity(formEntity);

		return processServerPost(post);
		
	}
	
	public void setWsHttpClient(DefaultHttpClient wsHttpClient) {
		
		if( allowUntruestedHttpsServer ) {
			
			this.wsHttpClient = (DefaultHttpClient)WebClientDevWrapper.wrapClient(wsHttpClient);
			
		} else {
			
			this.wsHttpClient = wsHttpClient;
		}
	}

	public void setWebserviceHost(String webserviceHost) {
		this.webserviceHost = webserviceHost;
	}

	public String getWebserviceHost() {
		return webserviceHost;
	}

	public void setAllowUntruestedHttpsServer(boolean allowUntruestedHttpsServer) {
		this.allowUntruestedHttpsServer = allowUntruestedHttpsServer;
	}

	public String getAuthenticatedWebserviceUri() {
		return authenticatedWebserviceUri;
	}

	public void setAuthenticatedWebserviceUri(String authenticatedWebserviceUri) {
		this.authenticatedWebserviceUri = authenticatedWebserviceUri;
	}

	public boolean isAllowUntruestedHttpsServer() {
		return allowUntruestedHttpsServer;
	}
	
	
}
