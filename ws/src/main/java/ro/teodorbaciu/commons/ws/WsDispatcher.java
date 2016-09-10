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
import org.apache.commons.lang3.builder.ToStringBuilder;

import ro.teodorbaciu.commons.ws.DispatchResult.Status;

/**
 * Dispatches web requests to the corresponding modules.
 * 
 * @author Teodor Baciu
 *
 */
public class WsDispatcher {

	/**
	 * Contains the modules defined in this dispatcher.
	 */
	private HashMap<String, WebServiceModule> mapModules;

	/**
	 * A short name associated with this dispatcher, useful for logging.
	 */
	private String name;

	/**
	 * Constructor.
	 */
	public WsDispatcher(String name) {

		this.name = name;
		mapModules = new HashMap<>();
	}

	/**
	 * Dispatches request to the appropriate module.
	 * @param moduleName the name of the module which contains the operation to execute
	 * @param operationName the name of the operation to be executed
	 * @param parameters a map containining the name value pairs
	 */
	public DispatchResult dispatch(String moduleName, String operationName, Map<String, String> parameters)  {
		
		if ( StringUtils.isBlank( moduleName ) ) {
			return new DispatchResult(Status.MODULE_NAME_BLANK);
		}
		
		Optional<WebServiceModule> optModule = findModule(moduleName);
		if ( !optModule.isPresent() ) { //module not found
			return new DispatchResult(DispatchResult.Status.MODULE_NOT_FOUND);
		}
		
		ExecutionResult executionResult = optModule.get().executeOperation(operationName, parameters);
		return new DispatchResult(executionResult, Status.DISPATCH_SUCCESS);
		
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).toString();
	}

	/**
	 * Finds the module with the specified name.
	 * 
	 * @param moduleName the name of the module
	 * @return an {@link Optional} containing the module if one was found
	 */
	protected Optional<WebServiceModule> findModule(String moduleName) {

		WebServiceModule module = mapModules.get(moduleName);
		if (mapModules == null) {
			return Optional.of(module);
		}
		return Optional.empty();
	}

}
