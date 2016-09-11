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

import java.util.Collection;
import java.util.Collections;
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
public class ServiceDispatcher {

	/**
	 * Contains the modules defined in this dispatcher.
	 */
	private HashMap<String, ServiceModule> mapModules;

	/**
	 * A short name associated with this dispatcher, useful for logging.
	 */
	private String name;

	/**
	 * Constructor.
	 */
	public ServiceDispatcher(String name) {

		this.name = name;
		mapModules = new HashMap<>();
	}

	/**
	 * Dispatches request to the appropriate module.
	 * 
	 * @param moduleName the name of the module which contains the operation to execute
	 * @param operationName the name of the operation to be executed
	 * @param parameters a map containining the name value pairs
	 */
	public DispatchResult dispatch(String moduleName, String operationName, Map<String, String> parameters) {

		if (StringUtils.isBlank(moduleName)) {
			return new DispatchResult(Status.MODULE_NAME_BLANK);
		}

		Optional<ServiceModule> optModule = findModule(moduleName);
		if (!optModule.isPresent()) { // module not found
			return new DispatchResult(DispatchResult.Status.MODULE_NOT_FOUND);
		}

		ExecutionResult executionResult = optModule.get().executeOperation(operationName, parameters);
		return new DispatchResult(executionResult, Status.DISPATCH_SUCCESS);

	}

	/**
	 * Adds the specified module to the dispatcher.
	 * 
	 * @param module the module to add
	 */
	public void addModule(ServiceModule module) {

		if (module == null) {
			throw new NullPointerException("Cannot add null module");
		}

		// check if a module with the specified name is already added
		if (mapModules.get(module.getModuleName()) != null) {
			throw new IllegalArgumentException("A module with this name is already added");
		}
		mapModules.put(module.getModuleName(), module);
	}

	/**
	 * Removes the module with the specified name.
	 * 
	 * @param moduleName the name of the module to remove
	 * @return an {@link Optional} containing the removed module or an empty optional if no module with the specified name was found
	 */
	public Optional<ServiceModule> removeModule(String moduleName) {

		if (StringUtils.isBlank(moduleName)) {
			throw new IllegalArgumentException("moduleName parameter cannot be blank");
		}

		ServiceModule module = mapModules.remove(moduleName);
		if (module == null) {
			return Optional.empty();
		}

		return Optional.of(module);
	}

	/**
	 * Returns the list modules defined in this dispatcher.
	 * 
	 * @return an unmodifiable collection containing the modules from the dispatcher
	 */
	public Collection<ServiceModule> getModules() {
		return Collections.unmodifiableCollection(mapModules.values());
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
	protected Optional<ServiceModule> findModule(String moduleName) {

		ServiceModule module = mapModules.get(moduleName);
		if (mapModules == null) {
			return Optional.of(module);
		}
		return Optional.empty();
	}

}
