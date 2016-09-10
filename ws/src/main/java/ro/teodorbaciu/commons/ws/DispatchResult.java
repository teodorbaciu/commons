package ro.teodorbaciu.commons.ws;

/**
 * Represents the result of dispatching a request.
 * @author Teodor Baciu
 *
 */
public class DispatchResult {

	/**
	 * The possible statuses of this dispatch result.
	 */
	public enum Status { DISPATCH_SUCCESS, MODULE_NOT_FOUND, MODULE_NAME_BLANK, OPERATION_NOT_FOUND }
	
	/**
	 * The result obtained by executing the operation.
	 */
	private ExecutionResult result;
	
	/**
	 * The status of this result.
	 */
	private Status status;

	/**
	 * Creates a new instance.
	 * @param status the dispatch status of this result.
	 */
	public DispatchResult(Status status) {
		this.status = status;
	}
	
	/**
	 * Creates a new instance of this class.
	 * @param result the result to store
	 * @param status the dispatch status associated with this result
	 */
	public DispatchResult(ExecutionResult result, Status status) {
		this.result = result;
		this.status = status;
	}

	public ExecutionResult getResult() {
		return result;
	}

	public Status getStatus() {
		return status;
	}
}
