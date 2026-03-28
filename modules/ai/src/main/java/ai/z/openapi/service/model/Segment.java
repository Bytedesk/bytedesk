package ai.z.openapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Segment {

	private Integer id;

	private Double start;

	private Double end;

	private String text;

}
