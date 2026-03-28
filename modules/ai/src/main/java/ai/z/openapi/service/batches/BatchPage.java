package ai.z.openapi.service.batches;

import lombok.Data;

import java.util.List;

@Data
public class BatchPage {

	private String object;

	private List<Batch> data;

}
