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
 * Class that wraps an object to be sent as result for a webservice call.
 * 
 * @author teo
 *
 */
public class ObjectWrapper<T> extends BaseResult {

	private T object;

	/**
	 * Constructor.
	 */
	public ObjectWrapper() {
		success = true;
	}

	/**
	 * Constructor.
	 * 
	 * @param object
	 */
	public ObjectWrapper(T object) {
		this.object = object;
		success = true;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

}
