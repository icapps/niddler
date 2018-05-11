package com.icapps.niddler.core;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.icapps.niddler.core.debug.NiddlerDebugger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maarten Van Giel
 * @author Nicola Verbeeck
 */
class NiddlerServer extends WebSocketServer {

	private static final String LOG_TAG = NiddlerServer.class.getSimpleName();

	private final String mPackageName;
	private final WebSocketListener mListener;
	private final List<ServerConnection> mConnections;
	private final String mPassword;
	private final NiddlerDebuggerImpl mNiddlerDebugger;
	private final NiddlerServerAnnouncementManager mServerAnnouncementManager;

	private NiddlerServer(final String password, final InetSocketAddress address, final String packageName,
			final WebSocketListener listener) {
		super(address);
		mPackageName = packageName;
		mListener = listener;
		mPassword = password;
		mConnections = new LinkedList<>();
		mNiddlerDebugger = new NiddlerDebuggerImpl();
		mServerAnnouncementManager = new NiddlerServerAnnouncementManager(packageName, this);
	}

	NiddlerServer(final String password, final int port, final String packageName,
			final WebSocketListener listener) throws UnknownHostException {
		this(password, new InetSocketAddress(port), packageName, listener);
	}

	@Override
	public void start() {
		mServerAnnouncementManager.stop();
		super.start();
	}

	@Override
	public void stop() throws IOException, InterruptedException {
		mServerAnnouncementManager.stop();
		super.stop();
	}

	@Override
	public final void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		Log.d(LOG_TAG, "New socket connection: " + handshake.getResourceDescriptor());

		final ServerConnection connection = new ServerConnection(conn);
		synchronized (mConnections) {
			mConnections.add(connection);
		}
		if (TextUtils.isEmpty(mPassword)) {
			connection.noAuth();
			authSuccess(conn);
		} else {
			connection.sendAuthRequest(mPackageName);
		}
	}

	@Override
	public final void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		Log.d(LOG_TAG, "Connection closed: " + conn);

		synchronized (mConnections) {
			final Iterator<ServerConnection> iterator = mConnections.iterator();
			while (iterator.hasNext()) {
				final ServerConnection connection = iterator.next();
				mNiddlerDebugger.onConnectionClosed(connection);
				connection.closed();
				if (connection.isFor(conn)) {
					iterator.remove();
				}
			}
		}
	}

	@Override
	public void onStart() {
		mServerAnnouncementManager.stop();
		mServerAnnouncementManager.start();
	}

	private static final String MESSAGE_AUTH = "authReply";
	private static final String MESSAGE_START_DEBUG = "startDebug";
	private static final String MESSAGE_END_DEBUG = "endDebug";
	private static final String MESSAGE_DEBUG_CONTROL = "controlDebug";

	@Override
	public final void onMessage(final WebSocket conn, final String message) {
		final ServerConnection connection = getConnection(conn);
		if (connection == null) {
			conn.close();
			return;
		}

		try {
			final JSONObject object = new JSONObject(message);
			final String type = object.optString("type", null);
			switch (type) {
				case MESSAGE_AUTH:
					if (!connection.checkAuthReply(MessageParser.parseAuthReply(object), mPassword)) {
						Log.w(LOG_TAG, "Client sent wrong authentication code!");
						return;
					}
					authSuccess(conn);
					break;
				case MESSAGE_START_DEBUG:
					if (connection.canReceiveData()) {
						mNiddlerDebugger.onDebuggerAttached(connection);
					}
					break;
				case MESSAGE_END_DEBUG:
					mNiddlerDebugger.onDebuggerConnectionClosed();
					break;
				case MESSAGE_DEBUG_CONTROL:
					mNiddlerDebugger.onControlMessage(object, connection);
					break;
				default:
					Log.w(LOG_TAG, "Received unsolicited message from client: " + message);
			}
		} catch (final JSONException e) {
			Log.w(LOG_TAG, "Received non-json message from server: " + message, e);
		}
	}

	private ServerConnection getConnection(final WebSocket conn) {
		synchronized (mConnections) {
			for (final ServerConnection connection : mConnections) {
				if (connection.isFor(conn)) {
					return connection;
				}
			}
		}
		return null;
	}

	private void authSuccess(final WebSocket conn) {
		if (mListener != null) {
			mListener.onConnectionOpened(conn);
		}
	}

	@Override
	public final void onError(final WebSocket conn, final Exception ex) {
		Log.e(LOG_TAG, "WebSocket error", ex);

		final ServerConnection connection = getConnection(conn);
		if (connection != null) {
			mNiddlerDebugger.onConnectionClosed(connection);
			connection.closed();
		}
	}

	/**
	 * Sends a String message to all sockets
	 *
	 * @param message the message to be sent
	 */
	final synchronized void sendToAll(final String message) {
		synchronized (mConnections) {
			for (final ServerConnection connection : mConnections) {
				try {
					if (connection.canReceiveData()) {
						connection.send(message);
					}
				} catch (final NotYetConnectedException ignored) {
					//Nothing to do, wait for the connection to complete
				} catch (final IllegalArgumentException ignored) {
					Log.e(LOG_TAG, "WebSocket error", ignored);
				}
			}
		}
	}

	@NonNull
	NiddlerDebugger debugger() {
		return mNiddlerDebugger;
	}

	interface WebSocketListener {
		void onConnectionOpened(final WebSocket conn);
	}

}
