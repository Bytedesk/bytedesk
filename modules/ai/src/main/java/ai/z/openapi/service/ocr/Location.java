package ai.z.openapi.service.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

	private int left;

	private int top;

	private int width;

	private int height;

}