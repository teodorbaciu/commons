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

import java.util.Map;
import java.util.Optional;

import ro.teodorbaciu.commons.ws.transfer.beans.BaseResult;

/**
 * Represents an operation within a module to be executed.
 * 
 * @author Teodor Baciu
 */
@FunctionalInterface
public interface WebserviceOperation {

	/**
	 * Executes the operation.
	 */
	public Optional<BaseResult> execute(String operationName, Map<String, String> parameters);
	
}
