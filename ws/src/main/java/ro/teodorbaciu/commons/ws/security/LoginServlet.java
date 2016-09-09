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

package ro.teodorbaciu.commons.ws.security;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.teodorbaciu.commons.ws.WsConstants;
import ro.teodorbaciu.commons.ws.util.WebUtil;

/**
 * <p>
 * Handles the login process. This servlet can be used for handling the login process initiated from an html form or for performing
 * programatic login, so the session can be used for calling webservice methods that require authentication.
 * </p>
 * <p>
 * We do a programatic login by specifying the <code>only-send-http-response</code> request parameter. The servlet will only send back the
 * corresponding http codes and will not try to redirect to the destination pages.
 * </p>
 * <p>
 * The servlet expects the <em>tfUsername</em> and <em>tfPassword</em> request parameters.
 * </p>
 * 
 * @author Teodor Baciu
 *
 */
public abstract class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * The logger to be used.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

	/**
	 * Specified in web xml as a servlet parameter. The servlet will redirect to this relative path in case of successful login.
	 */
	protected String loginSuccessfullDestination;

	/**
	 * Specified in web xml as a servlet parameter. The servlet will redirect to this relative path in case of error login.
	 */
	protected String loginErrorDestination;

	@Override
	public void init() throws ServletException {

		log.debug("Login Servlet initialized !");

		loginSuccessfullDestination = getServletConfig().getInitParameter("loginSuccessfullDestination");
		loginErrorDestination = getServletConfig().getInitParameter("loginErrorDestination");

	}

	@Override
	public void destroy() {
		log.debug("Login Servlet destroyed !");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		doPost(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String username = req.getParameter("tfUsername");
		String password = req.getParameter("tfPassword");
		String targetRequestUri = req.getParameter("targetRequestURI");
		String onlySendHttpResponseCodes = req.getParameter("only-send-http-response");

		if (!WebUtil.validateParam(username, "username", true, 45)) {
			req.getSession().setAttribute(WsConstants.KEY_LOGIN_ERROR_MSG, "Va rugam completati campul 'Nume utilizator'");

			if (!StringUtils.isEmpty(onlySendHttpResponseCodes)) {
				resp.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			} else {

				redirectTo(req, resp, loginErrorDestination);

			}

			return;
		}

		if (!WebUtil.validateParam(password, "password", true, 45)) {
			req.getSession().setAttribute(WsConstants.KEY_LOGIN_ERROR_MSG, "Va rugam completati campul 'Parola'");

			if (!StringUtils.isEmpty(onlySendHttpResponseCodes)) {
				resp.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			} else {
				redirectTo(req, resp, loginErrorDestination);
			}

			return;
		}

		try {
			// get the user
			WebUser user = findUser(username, password);
			if (user == null) {
				log.debug("user not found:" + username + " with specified password !");
				req.getSession().setAttribute(WsConstants.KEY_LOGIN_ERROR_MSG, "Nume utilizator sau parola incorecta !");

				if (!StringUtils.isEmpty(onlySendHttpResponseCodes)) {
					resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} else {
					redirectTo(req, resp, loginErrorDestination);
				}

				return;
			}

			// do post login processing and check the results
			if (!postLoginProcessing(user, req, resp)) {
				if (!StringUtils.isEmpty(onlySendHttpResponseCodes)) {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					redirectTo(req, resp, loginErrorDestination);
				}
				return;
			}

			log.info("successfully authenticated user:" + user.getUsername());
			req.getSession().setAttribute(WsConstants.KEY_CURRENT_USER, user);

			// everything OK, continue to the destination
			if (StringUtils.isEmpty(targetRequestUri)) {

				if (!StringUtils.isEmpty(onlySendHttpResponseCodes)) {
					resp.sendError(HttpServletResponse.SC_OK);
				} else {
					redirectTo(req, resp, loginSuccessfullDestination);
				}

			} else {

				// redirect to the original destination
				if (!StringUtils.isEmpty(onlySendHttpResponseCodes)) {
					resp.sendError(HttpServletResponse.SC_OK);
				} else {
					redirectTo(req, resp, targetRequestUri);
				}

			}

		} catch (Exception ex) {

			log.error("Error checking auth info", ex);
			req.getSession().setAttribute(WsConstants.KEY_LOGIN_ERROR_MSG, "Eroare de autentificare");
			redirectTo(req, resp, loginErrorDestination);
			return;

		}

	}

	/**
	 * Used to correctly redirect to the specified path. We need this complex redirection when accessing the web app through a reverse proxy
	 * over ssl.
	 */
	private void redirectTo(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {

		String protocol = "http";
		String remoteHost = null;
		int port = req.getLocalPort();
		String host = req.getRemoteHost();
		String referer = req.getHeader("Referer");

		// Try to use the referrer for more accurate info
		if (!StringUtils.isEmpty(referer)) {

			URL refererUrl = new URL(referer);

			protocol = refererUrl.getProtocol();
			host = refererUrl.getHost();
			port = refererUrl.getPort();// returns -1 when not present in the url

			log.debug("getting http protocol type, host and port from referer: " + referer);

		} else {

			// referrer not available, check to see if we have the
			// https_protocol custom flag set
			String httpsProtocol = req.getHeader("HTTPS_PROTOCOL_TYPE_CUSTOM");
			if (!StringUtils.isEmpty(httpsProtocol)) {
				// HTTPS is set
				protocol = "https";
				log.debug("https_protocol is true !");
			}

			// try to get the remote host header
			remoteHost = req.getHeader("REMOTE_HOST_CUSTOM");

		}

		String destinationUrl = protocol + "://" + host + req.getContextPath() + path;
		if (port != -1) {
			destinationUrl = protocol + "://" + host + ":" + port + req.getContextPath() + path;
		}

		// if the remote host is set, then use this
		if (!StringUtils.isEmpty(remoteHost)) {

			// if this is set, use this
			destinationUrl = protocol + "://" + remoteHost + req.getContextPath() + path;

		}

		log.debug("destination url: " + destinationUrl);
		resp.sendRedirect(destinationUrl);

	}

	/**
	 * Utility method to be overridden to perform additional processing after the login.
	 * 
	 * @param currentUser the user that was successfuly authenticated
	 * @return should return true on successful processing, false otherwise
	 */
	protected boolean postLoginProcessing(WebUser currentUser, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return true;
	}

	/**
	 * Searches for the user with the specified username.
	 * 
	 * @param username the username of the user :)
	 * @return an instance of {@link WtUser} or null if the user was not found.
	 */
	protected abstract WebUser findUser(String username, String password) throws Exception;
}
