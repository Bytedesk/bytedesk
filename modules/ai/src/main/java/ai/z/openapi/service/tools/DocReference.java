package ai.z.openapi.service.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocReference implements Serializable {

	/** Document index */
	private Integer index;

	/** Document type */
	private String doc_type;

	/** Document name */
	private String doc_name;

}