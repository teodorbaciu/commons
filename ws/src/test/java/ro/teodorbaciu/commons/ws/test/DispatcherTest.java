package ro.teodorbaciu.commons.ws.test;

import java.util.HashMap;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.teodorbaciu.commons.ws.ServiceDispatcher;
import ro.teodorbaciu.commons.ws.ServiceModule;
import ro.teodorbaciu.commons.ws.ServiceOperation;

public class DispatcherTest {

	private static final Logger log = LoggerFactory.getLogger(DispatcherTest.class);
	
	ServiceDispatcher dispatcher;
	private WsModuleUsers wsModuleUsers;
	private WsModuleProducts wsModuleProducts;

	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		
		dispatcher = new ServiceDispatcher("dispatcher");
		wsModuleUsers = new WsModuleUsers("ws-module-users");
		wsModuleProducts = new WsModuleProducts("ws-module-products");
		
		log.debug("DispatcherTest set up");
	}

	/**
	 * Frees resources.
	 */
	@After
	public void tearDown() throws Exception {
		
		dispatcher = null;
		wsModuleUsers = null;
		wsModuleProducts = null;
		log.debug("DispatcherTest teared down");
		
	}

	@Test
	public void testDispatcherOperations() {

		// add
		dispatcher.addModule(wsModuleUsers);
		dispatcher.addModule(wsModuleProducts);
		Assert.assertEquals(2, dispatcher.getModules().size());

		// try to remove inexistent module
		Optional<ServiceModule> inexistentModule = dispatcher.removeModule("inexistent-module");
		Assert.assertFalse(inexistentModule.isPresent());

		// Now remove the users module
		Optional<ServiceModule> usersModule = dispatcher.removeModule("ws-module-users");
		Assert.assertTrue(usersModule.isPresent());

		// Check if the returned module is the same with the one we added
		Assert.assertSame(wsModuleUsers, usersModule.get());

		// Check the number of modules
		Assert.assertEquals(1, dispatcher.getModules().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModuleAlreadyAdded() {

		dispatcher.addModule(wsModuleProducts);
		dispatcher.addModule(wsModuleUsers);
		dispatcher.addModule(wsModuleProducts);// exception thrown: module already added

	}

	@Test
	public void testOperations() {

		/*dispatcher.addModule(wsModuleUsers);
		HashMap<String, String> mapParameters = new HashMap<>();
		mapParameters.put("username", "teo");
		mapParameters.put("password", "pwd");
		dispatcher.dispatch(wsModuleUsers.getModuleName(), "op-add-user", mapParameters);
		*/
	}

	/**
	 * Webservice module for simulating user operations.
	 *
	 */
	static class WsModuleUsers extends ServiceModule {
		public WsModuleUsers(String moduleName) {
			super(moduleName);
			
			//add the various operations defined in this module
			ServiceOperation opAddUser = new OpAddUser();
			log.info( opAddUser.getParameterNames().toString() );
			addOperation( opAddUser );
		}
	}

	/**
	 * WebserviceModule for simulating product operations.
	 *
	 */
	static class WsModuleProducts extends ServiceModule {
		public WsModuleProducts(String moduleName) {
			super(moduleName);
		}
	}

}
