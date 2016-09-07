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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatches web requests to the corresponding modules.
 * 
 * @author Teodor Baciu
 *
 */
public class WsDispatcher {

	/**
	 * The logger to be used.
	 */
	private static final Logger log = LoggerFactory.getLogger(WsDispatcher.class);

	/**
	 * Contains the modules defined in this dispatcher.
	 */
	private List<WebServiceModule> listModules;

	/**
	 * A short name associated with this dispatcher, useful for logging.
	 */
	private String name;

	/**
	 * Constructor.
	 */
	public WsDispatcher(String name) {

		this.name = name;

		log.debug("Successfully created web service dispatcher " + name);

	}

	/**
	 * Dispatches the web request to the appropriate module.
	 * 
	 * @param request
	 *            the servlet request
	 * @param response
	 *            the servlet response
	 * @throws ServletException
	 *             if an error occurs
	 * @throws IOException
	 *             if an error occurs
	 */
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String moduleName = request.getParameter("module");

		// check input
		if (StringUtils.isEmpty(moduleName)) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
					"[" + name + "]: " + "Please specify the 'module' http request parameter !");
			return;
		}

		WebServiceModule module = findModule(moduleName);

		if (module == null) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
					"[" + name + "]: " + "Could not find module with name '" + moduleName + "' !");
			return;
		}

		module.dispatch(request, response);
	}

	/**
	 * Finds the module with the specified name.
	 * 
	 * @param moduleName
	 *            the name of the module
	 * @return the module or null if no module with the specified name was found
	 */
	protected WebServiceModule findModule(String moduleName) {

		for (WebServiceModule module : listModules) {
			if (moduleName.equals(module.getWebserviceModuleName())) {
				return module;
			}
		}

		return null;
	}

	public void setListModules(List<WebServiceModule> listModules) {
		this.listModules = listModules;
	}

	@Override
	public String toString() {
		return "web service dispatcher: " + name;
	}
}
