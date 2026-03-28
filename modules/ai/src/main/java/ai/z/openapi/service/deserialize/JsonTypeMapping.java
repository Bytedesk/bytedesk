package ai.z.openapi.service.deserialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTypeMapping {

	Class<?>[] value();

}