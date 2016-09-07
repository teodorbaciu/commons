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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.teodorbaciu.commons.ws.util.WebUtil;

/**
 * Checks if the requests are done from an authenticated session.
 * 
 * @author Teodor Baciu
 *
 */
public class AuthFilter implements Filter {

	/**
	 * The logger to be used.
	 */
	private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

	/**
	 * The address to the login page.
	 */
	private String loginPageUrl;

	/**
	 * Flag that instructs this filter to send http response codes instead of redirecting to the login 
	 * page when an unauthenticated request comes in.
	 */
	private String onlySendHttpResponseCodes;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		onlySendHttpResponseCodes = filterConfig.getInitParameter("only-send-http-response-codes");
		loginPageUrl = filterConfig.getInitParameter("login-page-url");

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestUri = httpRequest.getRequestURI();

		WebUser currentUser = WebUtil.getCurrentUser(httpRequest);
		if (currentUser == null) {

			request.setAttribute("targetRequestURI", requestUri);

			// forward to the login page
			if (StringUtils.isEmpty(onlySendHttpResponseCodes)) {
				request.getRequestDispatcher(loginPageUrl).forward(httpRequest, httpResponse);
			} else {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
			return;

		}

		// let the request pass
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
		log.info("Auth filter destroyed !");
	}

}
