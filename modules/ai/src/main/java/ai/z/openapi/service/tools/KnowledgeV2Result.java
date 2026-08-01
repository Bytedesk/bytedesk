package ai.z.openapi.service.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/** Knowledge base retrieval V2 result */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeV2Result implements Serializable {

	/** List of data sources */
	private List<Content> contents;

	/** Query rewrite result */
	private RewrittenQuery rewritten_query;

	private String type;

	/** Data source */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Content {

		/** Chunk ID (uuid) */
		private String id;

		/** Modality type: text, image_url, video_url */
		private String type;

		/** Text content */
		private String text;

		/** List of media files in text */
		private List<Media> medias;

		/** Image URL object */
		private ImageUrl image_url;

		/** Video URL object */
		private VideoUrl video_url;

		/** Recall position (rankIndex) */
		private Integer index;

		/** Recall score (rankScore) */
		private Double score;

		/** Rerank position (rerankIndex) */
		private Integer rerank_index;

		/** Rerank score (rerankScore) */
		private Double rerank_score;

		/** Metadata */
		private MetadataDTO metadata;

	}

	/** Media file */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Media {

		/** Image ID */
		private String id;

		/** Image URL */
		private String url;

		/** Image description */
		private String description;

	}

	/** Image URL object */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ImageUrl {

		/** URL */
		private String url;

	}

	/** Video URL object */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VideoUrl {

		/** URL */
		private String url;

	}

	/** Metadata */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MetadataDTO {

		/** Chunk ID */
		private String _id;

		/** Knowledge base ID */
		private String know_id;

		/** Document ID */
		private String doc_id;

		/** Document type (dtype) */
		private String doc_type;

		/** Document name (filename) */
		private String doc_name;

		/** Document URL */
		private String doc_url;

		/** Chunk index */
		private Integer index;

		/** Document page number */
		private Integer page_index;

		/** Video clip index */
		private Integer clip_index;

		/** Start frame timestamp */
		private Long start_time;

		/** End frame timestamp */
		private Long end_time;

		/** Video clip duration */
		private Long duration;

		/** Key frame list */
		private List<Object> frames;

	}

	/** Query rewrite result */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RewrittenQuery {

		/** Primary query */
		private String primary_query;

		/** Alternative query list */
		private List<String> multi_queries;

	}

}
