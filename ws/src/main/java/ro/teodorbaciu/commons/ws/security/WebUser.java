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
package ro.teodorbaciu.commons.ws.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class representing a user in the application.
 * @author Teodor Baciu
 *
 */
public class WebUser {

	/**
	 * The username.
	 */
	private String username;

	/**
	 * The password.
	 */
	private String password;
	
	/**
	 * A list containing the name of the
	 * roles associated with the user.
	 */
	private List<String> listRoleNames;
	
	/**
	 * Contains the additional information stored with this
	 * instance.
	 */
	private Object additionalData;
	
	/**
	 * Constructor.
	 */
	public WebUser() {
		listRoleNames = new ArrayList<String>();
	}
	
	
	public Object getAdditionalData() {
		return additionalData;
	}


	public void setAdditionalData(Object additionalData) {
		this.additionalData = additionalData;
	}


	/**
	 * Adds the specified role name to the list
	 * of role names in the project.
	 * @param roleName
	 */
	public void addRoleName(String roleName) {
		listRoleNames.add(roleName);
	}
	
	/**
	 * Checks if the current user has the specified
	 * role.
	 * @param roleName the name of the role to check
	 * @return true if the user has the role, false otherwise
	 */
	public boolean hasRole(String roleName) {
		return listRoleNames.contains(roleName);
	}
	
	/**
	 * Returns an unmodifiable list of role names.
	 * @return a {@link List} containing role names.
	 */
	public List<String> getListRoleNames() {
		return Collections.unmodifiableList(listRoleNames);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
