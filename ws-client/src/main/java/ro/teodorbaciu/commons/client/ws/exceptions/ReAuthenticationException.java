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
 * Thrown after a failed attempt to authenticate again with
 * the server using the given webservice parameters.
 * Possible reason for throwing this exception are:
 * credentials changed on the server and we are still using the
 * wrong credentials.
 * @author Teodor Baciu
 *
 */
public class ReAuthenticationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public ReAuthenticationException(String message) {
		super(message);
	}

}
