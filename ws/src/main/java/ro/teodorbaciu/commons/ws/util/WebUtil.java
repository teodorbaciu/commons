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
package ro.teodorbaciu.commons.ws.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.teodorbaciu.commons.ws.WsConstants;
import ro.teodorbaciu.commons.ws.security.WebUser;


/**
 * Contains various utility methods to be used on
 * the web tier.
 * @author Teodor Baciu
 *
 */
public class WebUtil {

	/**
	 * The logger to be used.
	 */
	private final static Logger log = LoggerFactory.getLogger(WebUtil.class);
	
	/**
	 * Validates the specified input parameter.
	 * 
	 * @param param
	 *            the param to validate
	 * @param required
	 *            when true, the specified parameter must be not null and non
	 *            empty
	 * @param maxLength
	 *            the maximum allowable length
	 * @return true if the parameter is valid, false otherwise
	 */
	public static boolean validateParam(String param, String paramName,
			boolean required, int maxLength) {

		if (required) {

			if (StringUtils.isEmpty(param)) {
				
				log.debug("Parameter '" + paramName + "' is empty !");
				return false;

			}
		}

		if (param != null && param.length() > maxLength) {
			
			log.debug("Parameter '" + paramName + "' exceeds maximum length ! Received value:"+param);
			return false;

		}

		return true;
	}
	
	/**
	 * Returns the current user or null if no user is authenticated.
	 * @param request the servlet request
	 * @return an instance of {@link WtUser} or null
	 */
	public static WebUser getCurrentUser(HttpServletRequest request) {
		
		HttpSession session = request.getSession(true);
		
		return (WebUser)session.getAttribute(WsConstants.KEY_CURRENT_USER);
		
	}
	
	public static String formatDate(Date d) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MMMMMMMMM/yyyy HH:mm");
		return df.format(d);
	}
}
