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

import java.util.List;

/**
 * Class that wraps a list of objects to be sent
 * as a result for a webservice call.
 * @author Teodor Baciu
 *
 */
public class ListWrapper<T> extends BaseResult {
	
	/**
	 * Stores the list to be wrapped.
	 */
	private List<T> list;

	/**
	 * Constructor.
	 */
	public ListWrapper() {
		success = true;
	}
	
	/**
	 * Constructor.
	 * @param list
	 */
	public ListWrapper(List<T> list) {
		this.list = list;
		success = true;
	}
	
	public List<T> getList() {
		return list;
	}
	
}
