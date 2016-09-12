package ro.teodorbaciu.commons.ws.test;

import java.util.HashMap;
import java.util.Map;
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
import ro.teodorbaciu.commons.ws.DispatchResult;
import ro.teodorbaciu.commons.ws.OperationParameter;
import ro.teodorbaciu.commons.ws.transfer.beans.BaseResult;

public class ServiceDispatcherTestCase {

	private static final String SERVICE_MODULE_PRODUCTS = "ws-module-products";
	private static final String SERVICE_MODULE_USERS = "ws-module-users";

	private static final Logger log = LoggerFactory.getLogger(ServiceDispatcherTestCase.class);
	
	ServiceDispatcher dispatcher;
	private WsModuleUsers wsModuleUsers;
	private WsModuleProducts wsModuleProducts;

	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		
		dispatcher = new ServiceDispatcher("dispatcher");
		wsModuleUsers = new WsModuleUsers(SERVICE_MODULE_USERS);
		wsModuleProducts = new WsModuleProducts(SERVICE_MODULE_PRODUCTS);
		
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
	public void testAddRemoveModules() {

		// add
		dispatcher.addModule(wsModuleUsers);
		dispatcher.addModule(wsModuleProducts);
		Assert.assertEquals(2, dispatcher.getModules().size());

		// try to remove inexistent module
		Optional<ServiceModule> inexistentModule = dispatcher.removeModule("inexistent-module");
		Assert.assertFalse(inexistentModule.isPresent());

		// Now remove the users module
		Optional<ServiceModule> usersModule = dispatcher.removeModule(SERVICE_MODULE_USERS);
		Assert.assertTrue(usersModule.isPresent());

		// Check if the returned module is the same with the one we added
		Assert.assertSame(wsModuleUsers, usersModule.get());

		// Check the number of modules
		Assert.assertEquals(1, dispatcher.getModules().size());
	}
	
	@Test( expected = NullPointerException.class)
	public void testAddNullModule() {
		dispatcher.addModule(null);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void testRemoveModuleBlankName() {
		dispatcher.removeModule("   ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModuleAlreadyAdded() {

		dispatcher.addModule(wsModuleProducts);
		dispatcher.addModule(wsModuleUsers);
		dispatcher.addModule(wsModuleProducts);// exception thrown: module already added

	}

	@Test
	public void testDispatch() {
		
		//Dispatch to inexistent module
		DispatchResult dispatchResult = dispatcher.dispatch(SERVICE_MODULE_USERS, "op-add-user", new HashMap<>());
		Assert.assertSame(DispatchResult.Status.MODULE_NOT_FOUND, dispatchResult.getStatus());
	
		//Now dispatch to valid module
		dispatcher.addModule(wsModuleUsers);
		dispatchResult = dispatcher.dispatch(SERVICE_MODULE_USERS, "op-add-user", new HashMap<>());
		Assert.assertSame(DispatchResult.Status.DISPATCH_SUCCESS, dispatchResult.getStatus());
		
		//Dispatch to blank module name
		dispatchResult = dispatcher.dispatch("   " , "op-add-user", new HashMap<>());
		Assert.assertSame(DispatchResult.Status.MODULE_NAME_BLANK, dispatchResult.getStatus());
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
		
		@OperationParameter(name = "username", mandatory = true)
		@OperationParameter(name = "password", mandatory = true)
		class OpAddUser extends ServiceOperation {

			public OpAddUser() {
				super("op-add-user");
			}

			@Override
			public Optional<BaseResult> execute(Map<String, Object> parameters) {
				return Optional.empty();
			}

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
