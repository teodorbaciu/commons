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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ro.teodorbaciu.commons.ws.transfer.beans.BaseResult;

/**
 * Represents an operation within a module to be executed.
 * 
 * @author Teodor Baciu
 */
public abstract class ServiceOperation {

	/**
	 * The name of the operation.
	 */
	private String operationName;
	
	/**
	 * Contains the names of the parameters that this operation
	 * should receive when execute(...) is being called.
	 */
	private List<String> listParameterNames;
	
	/**
	 * Constructor.
	 * @param operationName the name of the operation
	 */
	public ServiceOperation(String operationName) {
		this.operationName = operationName;
		
		collectDefinedParameterNames();
	}

	/**
	 * Returns the name of the operation.
	 */
	public String getName() {
		return operationName;
	}

	/**
	 * Returns the names of the parameters this operation requires.
	 * @return a {@link List} containing the paramter names
	 */
	public List<String> getParameterNames() {
		return Collections.unmodifiableList(listParameterNames);
	}
	
	/**
	 * Executes the operation.
	 * @param parameters key value based map containing the parameters received by this operation
	 * @return an {@link Optional} containg an instance of {@link BaseResult}
	 */
	public abstract Optional<BaseResult> execute(Map<String, Object> parameters);

	/**
	 * Obtains the names of the parameters that have been defined using
	 * {@link OperationParameter} annotations.
	 */
	protected void collectDefinedParameterNames() {
		
		listParameterNames = new ArrayList<>();
		
		OperationParameter[] parameters = this.getClass().getAnnotationsByType(OperationParameter.class);
		for ( OperationParameter parameter:parameters ) {
			listParameterNames.add( parameter.name() );
		}
	}
}
