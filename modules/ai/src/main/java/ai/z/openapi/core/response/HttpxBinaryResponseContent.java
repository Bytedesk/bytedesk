package ai.z.openapi.core.response;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class HttpxBinaryResponseContent implements Closeable {

	private final retrofit2.Response<ResponseBody> response;

	public HttpxBinaryResponseContent(retrofit2.Response<ResponseBody> response) {
		if (response == null || response.body() == null) {
			throw new IllegalArgumentException("Response or ResponseBody cannot be null");
		}
		this.response = response;
	}

	public byte[] getContent() throws IOException {
		if (response.body() == null) {
			throw new IOException("ResponseBody is null");
		}
		try (BufferedSource source = response.body().source()) {
			return source.readByteArray();
		}
	}

	public String getText() throws IOException {
		if (response.body() == null) {
			throw new IOException("ResponseBody is null");
		}
		try (BufferedSource source = response.body().source()) {
			return source.readUtf8();
		}
	}

	public String getEncoding() {
		return response.body() != null && response.body().contentType() != null
				? Objects.requireNonNull(Objects.requireNonNull(response.body().contentType()).charset()).toString()
				: null;
	}

	public Iterator<byte[]> iterBytes(int chunkSize) throws IOException {
		if (response.body() == null) {
			throw new IOException("ResponseBody is null");
		}
		return new Iterator<byte[]>() {
			final BufferedSource source = response.body().source();

			final byte[] buffer = new byte[chunkSize];

			boolean hasMore = true;

			@Override
			public boolean hasNext() {
				try {
					if (source.exhausted()) {
						hasMore = false;
					}
				}
				catch (IOException e) {
					hasMore = false;
				}
				return hasMore;
			}

			@Override
			public byte[] next() {
				try {
					source.read(buffer);
				}
				catch (IOException e) {
					// Handle the exception
				}
				return buffer;
			}
		};
	}

	public Iterator<String> iterText(int chunkSize) throws IOException {
		if (response.body() == null) {
			throw new IOException("ResponseBody is null");
		}
		return new Iterator<String>() {
			final BufferedSource source = response.body().source();

			final byte[] buffer = new byte[chunkSize];

			boolean hasMore = true;

			@Override
			public boolean hasNext() {
				try {
					if (source.exhausted()) {
						hasMore = false;
					}
				}
				catch (IOException e) {
					hasMore = false;
				}
				return hasMore;
			}

			@Override
			public String next() {
				try {
					int bytesRead = source.read(buffer);
					return new String(buffer, 0, bytesRead);
				}
				catch (IOException e) {
					// Handle the exception
				}
				return "";
			}
		};
	}

	public void writeToFile(String file) throws IOException {
		if (response.body() == null) {
			throw new IOException("ResponseBody is null");
		}

		try (BufferedSource source = response.body().source(); FileOutputStream fos = new FileOutputStream(file)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = source.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
		}
	}

	public void streamToFile(String file, int chunkSize) throws IOException {
		if (response.body() == null) {
			throw new IOException("ResponseBody is null");
		}

		try (BufferedSource source = response.body().source(); FileOutputStream fos = new FileOutputStream(file)) {
			byte[] buffer = new byte[chunkSize];
			int bytesRead;
			while ((bytesRead = source.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (response.body() != null) {
			response.body().close();
		}
	}

}
