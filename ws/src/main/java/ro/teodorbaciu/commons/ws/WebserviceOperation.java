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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Represents an operation to be executed by a webservice module.
 * 
 * @author Teodor Baciu
 * 
 */
public interface WebserviceOperation {

	/**
	 * Executes the operation.
	 * @param request the servlet request
	 * @param response the servlet response
	 * @throws ServletException
	 * @throws IOException
	 * @return the json response as String to be written
	 * to the client
	 */
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
