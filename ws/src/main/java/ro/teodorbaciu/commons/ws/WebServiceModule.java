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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.teodorbaciu.commons.ws.ExecutionResult.Status;
import ro.teodorbaciu.commons.ws.transfer.beans.BaseResult;

/**
 * Describes the methods that should be exposed by a webservice.
 * 
 * @author Teodor Baciu
 * 
 */
public abstract class WebServiceModule {

	/**
	 * The logger to be used.
	 */
	private static final Logger log = LoggerFactory.getLogger(WebServiceModule.class);

	/**
	 * Stores the webservice operations defined in this module.
	 */
	private HashMap<String, WebserviceOperation> mapOperations = null;

	/**
	 * The name of the module.
	 */
	private String moduleName;

	/**
	 * Constructor.
	 */
	public WebServiceModule(String moduleName) {
		this.moduleName = moduleName;
		mapOperations = new HashMap<String, WebserviceOperation>();
	}
	
	/**
	 * Executes the operation with the specified name.
	 * @param operationName the name of the operation to execute.
	 * @param parameters the parameters to pass to the operation
	 * @return an instance of type {@link ExecutionResult}
	 */
	public ExecutionResult executeOperation(String operationName, Map<String, String> parameters) {
		
		if ( StringUtils.isBlank( operationName ) ) {
			return new ExecutionResult(Status.OPERATION_NAME_BLANK);
		}
		
		WebserviceOperation operation = mapOperations.get(operationName);
		if ( operation == null ) {
			return new ExecutionResult(Status.OPERATION_NOT_FOUND);
		}
		
		Optional<BaseResult> optExecutionValue = operation.execute(operationName, parameters);
		if ( !optExecutionValue.isPresent() ) {
			return new ExecutionResult(Status.INVALID);
		}
		
		return new ExecutionResult(optExecutionValue.get(), Status.INVALID);
	}

	/**
	 * Returns the name of the module.
	 * 
	 * @return a String representing the name of the module.
	 */
	public String getModuleName() {
		return moduleName;
	}

}
