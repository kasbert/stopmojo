/*
 * Created on Oct 23, 2003
 *
 * This program is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software 
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this software; see the file COPYING. If not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * As a special exception, Derone Bryson and the StopMojo Project gives 
 * permission for additional uses of the text contained in its release of 
 * StopMojo.
 *
 * The exception is that, Derone Bryson and the the StopMojo Project hereby 
 * grants permission for non-GPL compatible modules (jar files, libraries, 
 * codecs, etc.) to be used and distributed together with StopMojo. This 
 * permission is above and beyond the permissions granted by the GPL license 
 * StopMojo is covered by.
 *
 * This exception does not however invalidate any other reasons why the 
 * executable file might be covered by the GNU General Public License.
 *
 * This exception applies only to the code released by Derone Bryson and/or the
 * StopMojo Project under the name StopMojo. If you copy code from other Free 
 * Software Foundation releases into a copy of StopMojo, as the General Public 
 * License permits, the exception does not apply to the code that you add in 
 * this way. To avoid misleading anyone as to the status of such modified files, 
 * you must delete this exception notice from them.
 *
 * If you write modifications of your own for StopMojo, it is your choice 
 * whether to permit this exception to apply to your modifications. If you do 
 * not wish that, delete this exception notice.  
 */
package com.mondobeyondo.stopmojo.webcam;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.mondobeyondo.stopmojo.util.BasicDialog;
import com.mondobeyondo.stopmojo.util.FieldPanel;
import com.mondobeyondo.stopmojo.util.Util;

/**
 * @author Derry Bryson
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CaptureDeviceDialog extends BasicDialog implements ActionListener {
  private static final long serialVersionUID = 1L;

  public static final String DELIMITER = "~";

	private boolean m_retval;

	private JButton m_helpBut, m_okBut, m_cancelBut;

	private JComboBox m_devComboBox, m_formatComboBox;

	private String m_devName = "", m_formatEncoding;

	private Dimension m_size;

	private int m_format = -1, m_formatIndexes[];

	List<Webcam> m_devices;

	public CaptureDeviceDialog(JFrame parent, String dev) throws Exception {
		super(parent, true);
		init(dev);
	}

	private void init(String dev) throws Exception {
		int i;

		if (!dev.trim().equals("")) {
			StringTokenizer st = new StringTokenizer(dev, DELIMITER);

			if (st.hasMoreTokens())
				m_devName = st.nextToken();
			if (st.hasMoreTokens())
				m_format = Util.atoi(st.nextToken());
		}

		m_retval = false;
		// System.out.println("devName = '" + m_devName + "', format = " +
		// m_format);

		GridBagConstraints gbc;

		setTitle("Select/Configure Capture Device");

		m_devices = Webcam.getWebcams();

		JPanel padPanel = new JPanel();
		padPanel.setLayout(new GridBagLayout());
		getContentPane().add(padPanel, BorderLayout.CENTER);
		JScrollPane sp = new JScrollPane(makeFieldPanel());
		sp.setBorder(null);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		padPanel.add(sp, gbc);

		JPanel butPanel = new JPanel();
		butPanel.setLayout(new GridBagLayout());
		m_helpBut = new JButton("Help");
		m_helpBut.setMnemonic(KeyEvent.VK_H);
		// m_helpBut.setIcon(Capture.s_helpIcon);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 10, 10, 5);
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		butPanel.add(m_helpBut, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 5, 10, 5);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		butPanel.add(new JPanel(), gbc);
		m_okBut = new JButton("Ok");
		m_okBut.setMnemonic(KeyEvent.VK_O);
		// m_okBut.setIcon(Capture.s_okIcon);
		m_okBut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onOk();
			}
		});
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 5, 10, 10);
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		butPanel.add(m_okBut, gbc);
		m_cancelBut = new JButton("Cancel");
		m_cancelBut.setMnemonic(KeyEvent.VK_C);
		// m_cancelBut.setIcon(Capture.s_cancelIcon);
		m_cancelBut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();
			}
		});
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 5, 10, 10);
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		butPanel.add(m_cancelBut, gbc);

		getContentPane().add(butPanel, java.awt.BorderLayout.SOUTH);

		getRootPane().setDefaultButton(m_okBut);

		pack();
		centerOnParent();
	}

	private JPanel makeFieldPanel() {
		JLabel label;

		GridBagConstraints gbc;

		int sel = -1;

		FieldPanel fieldPanel = new FieldPanel();

		if (m_devices != null && m_devices.size() != 0) {
			String[] devices = new String[m_devices.size()];
			int i = 0;
			for (Webcam dev : m_devices) {
				devices[i] = dev.getName();
				if (dev.getName().equals(m_devName))
					sel = i;
        i++;
			}
			m_devComboBox = new JComboBox(devices);

		} else
			m_devComboBox = new JComboBox();
		m_devComboBox.setEditable(false);
		m_devComboBox.setSelectedIndex(sel);
		m_devComboBox.addActionListener(this);
		fieldPanel.addField("Device:", m_devComboBox, 50);

		m_formatComboBox = new JComboBox(new Vector());
		m_formatComboBox.setEditable(false);
		fieldPanel.addField("Format:", m_formatComboBox, 50);

		fillFormatComboBox();

		fieldPanel.done();

		return fieldPanel;
	}

	private void fillFormatComboBox() {
		int i, j;

		m_formatComboBox.removeAllItems();
		m_formatIndexes = new int[WebcamResolution.values().length];
		if (m_devComboBox.getSelectedIndex() != -1) {
			Webcam cdi = (Webcam) m_devices.get(m_devComboBox.getSelectedIndex());
			i = j = 0;
			for (WebcamResolution resolution : WebcamResolution.values()) {
				if (isOkForWebcam(cdi, resolution)) {
					m_formatComboBox.addItem(resolution.getSize().width +"x"+resolution.getSize().height  + " : " + resolution.name());
					m_formatIndexes[j++] = resolution.ordinal();
				}
			}

			i = 0;
			if (cdi.getName().equals(m_devName)) {
				for (i = 0; i < j; i++)
					if (m_formatIndexes[i] == m_format)
						break;
				if (i == j)
					i = 0;
			}
			m_formatComboBox.setSelectedIndex(i);
		}
	}

	private boolean isOkForWebcam(Webcam cdi, WebcamResolution resolution) {
		Dimension[] predefined = cdi.getViewSizes();
		Dimension[] custom = cdi.getCustomViewSizes();
		boolean ok = false;
		for (Dimension d : predefined) {
			if (d.width == resolution.getSize().width && d.height == resolution.getSize().height) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			for (Dimension d : custom) {
				if (d.width == resolution.getSize().width && d.height == resolution.getSize().height) {
					ok = true;
					break;
				}
			}
		}
		return ok;
	}

	public boolean showModal() {
		show();
		return m_retval;
	}

	private void onOk() {
		m_retval = true;

		if (m_devices != null && m_devices.size() != 0 && m_devComboBox.getSelectedIndex() != -1) {
			m_devName = ((Webcam) m_devices.get(m_devComboBox.getSelectedIndex())).getName();
			m_format = m_formatIndexes[m_formatComboBox.getSelectedIndex()];
		}
		System.out.println("devName = '" + m_devName + "', format = " + m_format);

		dispose();
	}

	public String getDevName() {
		return m_devName + DELIMITER + m_format;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_devComboBox) {
			fillFormatComboBox();
		}
	}
}