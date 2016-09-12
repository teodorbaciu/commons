package ro.teodorbaciu.commons.ws;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for specifying the names of the parameters a {@link ServiceOperation} requires.
 * 
 * @author Teodor Baciu
 *
 */
@Repeatable(OperationParameters.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationParameter {

	/** The name of this service parameter. */
	String name();
	
	/** Returns if the named parameter is mandatory or not */
	boolean mandatory();
	
	/** Returns if the parameter is integer or not */
	boolean integer() default false;
	
	/** Returns if the parameter should be considered of type floating point or not */
	boolean floatingPoint() default false; 
}

@Retention(RetentionPolicy.RUNTIME)
@interface OperationParameters {
	public OperationParameter[] value();
}
