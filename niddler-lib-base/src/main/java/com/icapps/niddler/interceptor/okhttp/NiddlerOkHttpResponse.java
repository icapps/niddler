package com.icapps.niddler.interceptor.okhttp;

import com.icapps.niddler.core.NiddlerRequest;
import com.icapps.niddler.core.NiddlerResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author Maarten Van Giel
 * @author Nicola Verbeeck
 */
final class NiddlerOkHttpResponse implements NiddlerResponse {

	private final Response mResponse;
	private final String mRequestId;
	private final String mMessageId;
	private final long mTimestamp;
	@Nullable
	private final NiddlerRequest mActualNetworkRequest;
	@Nullable
	private final NiddlerResponse mActualNetworkReply;
	private final int mWriteTime;
	private final int mReadTime;
	private final int mWaitTime;
	@Nullable
	private final Map<String, String> mExtraHeaders;

	NiddlerOkHttpResponse(final Response response,
			final String requestId,
			@Nullable final NiddlerRequest actualNetworkRequest,
			@Nullable final NiddlerResponse actualNetworkReply,
			final int writeTime,
			final int readTime,
			final int waitTime,
			@Nullable final Map<String, String> extraHeaders) {
		mResponse = response;
		mRequestId = requestId;
		mActualNetworkRequest = actualNetworkRequest;
		mActualNetworkReply = actualNetworkReply;
		mWriteTime = writeTime;
		mReadTime = readTime;
		mWaitTime = waitTime;
		mMessageId = UUID.randomUUID().toString();
		mTimestamp = System.currentTimeMillis();
		mExtraHeaders = extraHeaders;
	}

	@Override
	public String getMessageId() {
		return mMessageId;
	}

	@Override
	public String getRequestId() {
		return mRequestId;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	@Override
	public Map<String, List<String>> getHeaders() {
		final Map<String, List<String>> finalHeaders = mResponse.headers().toMultimap();
		if (mExtraHeaders != null) {
			for (final Map.Entry<String, String> keyValueEntry : mExtraHeaders.entrySet()) {
				if (!finalHeaders.containsKey(keyValueEntry.getKey())) {
					finalHeaders.put(keyValueEntry.getKey(), Collections.singletonList(keyValueEntry.getValue()));
				}
			}
		}
		return finalHeaders;
	}

	@Override
	public Integer getStatusCode() {
		return mResponse.code();
	}

	@Nullable
	@Override
	public NiddlerRequest actualNetworkRequest() {
		return mActualNetworkRequest;
	}

	@Nullable
	@Override
	public NiddlerResponse actualNetworkReply() {
		return mActualNetworkReply;
	}

	@NonNull
	@Override
	public String getStatusLine() {
		return mResponse.message();
	}

	@NonNull
	@Override
	public String getHttpVersion() {
		return NiddlerOkHttpRequest.httpVersion(mResponse.protocol());
	}

	@Override
	public int getWriteTime() {
		return mWriteTime;
	}

	@Override
	public int getReadTime() {
		return mReadTime;
	}

	@Override
	public int getWaitTime() {
		return mWaitTime;
	}

	@Override
	public long estimateBodySize() {
		final ResponseBody body = mResponse.body();
		if (body == null) {
			return 0;
		}
		final long size = body.contentLength();
		return size;
	}

	@Override
	public void writeBody(final OutputStream stream) {
		final ResponseBody body = mResponse.body();
		try {
			if (body != null) {
				final BufferedSource source = body.source();
				source.request(Long.MAX_VALUE); // Buffer entire body

				final Buffer buffer = source.buffer();
				stream.write(buffer.clone().readByteArray());
				stream.flush();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Nullable
	@Override
	public StackTraceElement[] getErrorStackTrace() {
		return null;
	}
}
