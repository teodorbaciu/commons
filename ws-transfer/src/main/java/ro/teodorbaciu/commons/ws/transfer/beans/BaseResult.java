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
package ro.teodorbaciu.commons.ws.transfer.beans;

/**
 * Base class for all the beans to be sent as responses.
 * @author teo
 *
 */
public abstract class BaseResult {

	/**
	 * Flag that indicates if the result is successful or not.
	 */
	protected boolean success;

	/**
	 * Stores an optional message in case of errors.
	 */
	private String errorMessage;
	
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
