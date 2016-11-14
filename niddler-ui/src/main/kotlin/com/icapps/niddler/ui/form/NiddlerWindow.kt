package com.icapps.niddler.ui.form

import com.google.gson.Gson
import com.icapps.niddler.ui.MessageListener
import com.icapps.niddler.ui.NiddlerClient
import com.icapps.niddler.ui.adb.ADBBootstrap
import com.icapps.niddler.ui.model.NiddlerMessage
import se.vidstige.jadb.JadbDevice
import java.net.URI
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * @author Nicola Verbeeck
 * @date 14/11/16.
 */
class NiddlerWindow : JFrame(), MessageListener {

    private val windowContents = MainWindow()
    private val adbConnection = ADBBootstrap()

    private lateinit var devices: MutableList<JadbDevice>
    private var selectedSerial: String? = null

    fun init() {
        add(windowContents.rootPanel)
        devices = adbConnection.bootStrap().devices

        devices.forEach {
            windowContents.adbTargetSelection.addItem(it.serial)
        }
        windowContents.adbTargetSelection.addActionListener {
            onDeviceSelectionChanged()
        }

        pack()
        isVisible = true
    }

    private fun onDeviceSelectionChanged() {
        selectedSerial = windowContents.adbTargetSelection.selectedItem.toString()
        initNiddlerOnDevice()
    }

    private var niddlerClient: NiddlerClient? = null

    private fun initNiddlerOnDevice() {
        niddlerClient?.close()
        niddlerClient?.unregisterListener(this)
        if (niddlerClient != null) {
            //TODO Remove previous mapping
        }
        windowContents.dummyContentPanel.text = ""
        val device = devices.find { it.serial == selectedSerial }
        adbConnection.extend(device)?.fowardTCPPort(6555, 6555)
        niddlerClient = NiddlerClient(URI.create("ws://127.0.0.1:6555"))
        niddlerClient?.registerListener(this)
        niddlerClient?.connectBlocking()
    }

    override fun onMessage(msg: String) {
        val message = Gson().fromJson(msg, NiddlerMessage::class.java)
        SwingUtilities.invokeLater {
            windowContents.dummyContentPanel.append("\n\n${message.method} : ${message.url} - ${message.requestId}")
        }
    }

}