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

package ro.teodorbaciu.commons.client.ws.exceptions;

/**
 * Thrown in the webservice methods when authentication is
 * required for completing a certain webservice call.
 * @author Teodor Baciu
 *
 */
public class AuthorizationRequiredException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param msg the message of the exception
	 */
	public AuthorizationRequiredException(String msg) {
		super(msg);
	}
}
