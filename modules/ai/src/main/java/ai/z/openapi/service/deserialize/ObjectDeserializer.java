package ai.z.openapi.service.deserialize;

import java.io.IOException;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {

	private static final long serialVersionUID = 1L;

	private final static ObjectDeserializer _instance = new ObjectDeserializer();

	private ObjectDeserializer() {
		super(ObjectNode.class, true);
	}

	public static ObjectDeserializer getInstance() {
		return _instance;
	}

	@Override
	public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		if (p.isExpectedStartObjectToken()) {
			return deserializeObject(p, ctxt, ctxt.getNodeFactory());
		}
		if (p.hasToken(JsonToken.FIELD_NAME)) {
			return deserializeObjectAtName(p, ctxt, ctxt.getNodeFactory());
		}
		// 23-Sep-2015, tatu: Ugh. We may also be given END_OBJECT (similar to
		// FIELD_NAME),
		// if caller has advanced to the first token of Object, but for empty Object
		if (p.hasToken(JsonToken.END_OBJECT)) {
			return ctxt.getNodeFactory().objectNode();
		}
		return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
	}

	/**
	 * Variant needed to support both root-level `updateValue()` and merging.
	 *
	 * @since 2.9
	 */
	@Override
	public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
		if (p.isExpectedStartObjectToken() || p.hasToken(JsonToken.FIELD_NAME)) {
			return (ObjectNode) updateObject(p, ctxt, (ObjectNode) node);
		}
		return (ObjectNode) ctxt.handleUnexpectedToken(ObjectNode.class, p);
	}

}
