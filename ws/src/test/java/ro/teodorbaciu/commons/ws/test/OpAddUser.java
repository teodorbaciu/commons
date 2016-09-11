package ro.teodorbaciu.commons.ws.test;

import java.util.Map;
import java.util.Optional;

import ro.teodorbaciu.commons.ws.ServiceOperation;
import ro.teodorbaciu.commons.ws.ServiceParameter;
import ro.teodorbaciu.commons.ws.transfer.beans.BaseResult;

@ServiceParameter(name = "username")
@ServiceParameter(name = "password")
class OpAddUser extends ServiceOperation {

	public OpAddUser() {
		super("op-add-user");
	}

	@Override
	public Optional<BaseResult> execute(Map<String, String> parameters) {
		return Optional.empty();
	}

}