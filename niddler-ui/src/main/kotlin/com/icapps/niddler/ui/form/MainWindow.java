package com.icapps.niddler.ui.form;

import javax.swing.*;
import java.awt.*;

/**
 * @author Nicola Verbeeck
 * @date 10/11/16.
 */
public class MainWindow {
	private JPanel rootPanel;
	private JTable messages;
	private JToolBar toolbar;
	private JToggleButton buttonTimeline;
	private JToggleButton buttonLinkedMode;
	private JButton buttonClear;
	private JPanel detailPanel;
	private JSplitPane splitPane;
	private JLabel statusText;
	private JPanel statusBar;
	private JButton connectButton;

	public JButton getConnectButton() {
		return connectButton;
	}

	public JPanel getStatusBar() {
		return statusBar;
	}

	public JLabel getStatusText() {
		return statusText;
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

	public JTable getMessages() {
		return messages;
	}

	public JButton getButtonClear() {
		return buttonClear;
	}

	public JPanel getDetailPanel() {
		return detailPanel;
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

	{
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout(0, 0));
		rootPanel.setMinimumSize(new Dimension(300, 300));
		rootPanel.setPreferredSize(new Dimension(1000, 600));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		rootPanel.add(panel1, BorderLayout.NORTH);
		connectButton = new JButton();
		connectButton.setText("Connect");
		panel1.add(connectButton);
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout(0, 0));
		rootPanel.add(panel2, BorderLayout.WEST);
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setMargin(new Insets(0, 4, 0, 4));
		toolbar.setOrientation(1);
		panel2.add(toolbar, BorderLayout.NORTH);
		buttonTimeline = new JToggleButton();
		buttonTimeline.setFocusPainted(true);
		buttonTimeline.setHideActionText(false);
		buttonTimeline.setIcon(new ImageIcon(getClass().getResource("/ic_chronological.png")));
		buttonTimeline.setInheritsPopupMenu(false);
		buttonTimeline.setMargin(new Insets(0, 2, 0, 2));
		buttonTimeline.setMaximumSize(new Dimension(32, 32));
		buttonTimeline.setMinimumSize(new Dimension(32, 32));
		buttonTimeline.setPreferredSize(new Dimension(32, 32));
		buttonTimeline.setSelected(true);
		buttonTimeline.setText("");
		toolbar.add(buttonTimeline);
		buttonLinkedMode = new JToggleButton();
		buttonLinkedMode.setFocusPainted(true);
		buttonLinkedMode.setIcon(new ImageIcon(getClass().getResource("/ic_link.png")));
		buttonLinkedMode.setMargin(new Insets(0, 2, 0, 2));
		buttonLinkedMode.setMaximumSize(new Dimension(32, 32));
		buttonLinkedMode.setMinimumSize(new Dimension(32, 32));
		buttonLinkedMode.setPreferredSize(new Dimension(32, 32));
		buttonLinkedMode.setText("");
		toolbar.add(buttonLinkedMode);
		buttonClear = new JButton();
		buttonClear.setIcon(new ImageIcon(getClass().getResource("/ic_delete.png")));
		buttonClear.setLabel("");
		buttonClear.setMaximumSize(new Dimension(32, 32));
		buttonClear.setMinimumSize(new Dimension(32, 32));
		buttonClear.setPreferredSize(new Dimension(32, 32));
		buttonClear.setText("");
		toolbar.add(buttonClear);
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		rootPanel.add(splitPane, BorderLayout.CENTER);
		final JScrollPane scrollPane1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane1);
		messages = new JTable();
		messages.setFillsViewportHeight(false);
		messages.setRowHeight(32);
		messages.setShowHorizontalLines(true);
		messages.setShowVerticalLines(false);
		scrollPane1.setViewportView(messages);
		detailPanel = new JPanel();
		detailPanel.setLayout(new BorderLayout(0, 0));
		detailPanel.setMinimumSize(new Dimension(100, 100));
		splitPane.setRightComponent(detailPanel);
		statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout(0, 0));
		rootPanel.add(statusBar, BorderLayout.SOUTH);
		statusBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
		statusText = new JLabel();
		statusText.setFocusable(false);
		statusText.setText("");
		statusText.setVerifyInputWhenFocusTarget(false);
		statusText.putClientProperty("html.disable", Boolean.FALSE);
		statusBar.add(statusText, BorderLayout.CENTER);
		ButtonGroup buttonGroup;
		buttonGroup = new ButtonGroup();
		buttonGroup.add(buttonTimeline);
		buttonGroup.add(buttonLinkedMode);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
