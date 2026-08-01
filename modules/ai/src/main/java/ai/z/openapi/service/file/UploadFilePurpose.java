package ai.z.openapi.service.file;

public enum UploadFilePurpose {

	BATCH("batch"), FILE_EXTRACT("file-extract"), CODE_INTERPRETER("code-interpreter"), AGENT("agent"),
	VOICE_CLONE_INPUT("voice-clone-input");

	private final String value;

	UploadFilePurpose(final String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

}
