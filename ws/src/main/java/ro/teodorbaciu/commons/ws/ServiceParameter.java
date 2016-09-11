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
@Repeatable(ServiceParameters.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceParameter {

	/** The name of this service parameter. */
	String name();
}

@Retention(RetentionPolicy.RUNTIME)
@interface ServiceParameters {
	public ServiceParameter[] value();
}
