package ro.teodorbaciu.commons.ws;

import ro.teodorbaciu.commons.ws.transfer.beans.BaseResult;

/**
 * Represents the result of executing a service operation.
 * @author Teodor Baciu
 *
 */
public class ExecutionResult {

	public enum Status { VALID, OPERATION_NAME_BLANK, OPERATION_NOT_FOUND, INVALID }
	
	/**
	 * The result of executing the operation.
	 */
	private BaseResult value;
	
	/**
	 * The status of the excution.
	 */
	private Status status;
	
	/**
	 * Creates a new instance of this class.
	 * @param value the result of this execution
	 * @param status the status associated with the execution
	 */
	public ExecutionResult(BaseResult value, Status status) {
		this.value = value;
		this.status = status;
	}

	/**
	 * Creates a new instance with the specified status. Usefull for error results.
	 * @param status the status to associate with this {@link ExecutionResult} instance
	 */
	public ExecutionResult(Status status) {
		this(null, status);
	}
	
	public BaseResult getValue() {
		return value;
	}

	public Status getStatus() {
		return status;
	}
}
