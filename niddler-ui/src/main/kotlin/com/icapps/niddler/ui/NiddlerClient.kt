package com.icapps.niddler.ui

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import trikita.log.Log
import java.net.URI
import java.util.*

/**
 * Created by maartenvangiel on 14/11/2016.
 */
class NiddlerClient(serverURI: URI?) : WebSocketClient(serverURI) {

    private val listeners: MutableSet<MessageListener> = HashSet()

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("Connection succeeded: " + connection.remoteSocketAddress)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("Connection closed: " + reason)
    }

    override fun onMessage(message: String) {
        Log.d("Got message: " + message)
        synchronized(listeners) {
            listeners.forEach { it.onMessage(message) }
        }
    }

    override fun onError(ex: Exception?) {
        Log.d(ex.toString())
    }

    fun registerListener(listener: MessageListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun unregisterListener(listener: MessageListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

}

interface MessageListener {
    fun onMessage(msg: String)
}