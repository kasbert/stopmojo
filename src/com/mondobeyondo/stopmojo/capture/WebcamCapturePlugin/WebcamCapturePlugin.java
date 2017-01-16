/*
 * Created on Apr 30, 2005
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
package com.mondobeyondo.stopmojo.capture.WebcamCapturePlugin;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.mondobeyondo.stopmojo.plugin.capture.CapturePlugin;
import com.mondobeyondo.stopmojo.plugin.capture.CapturePluginException;
import com.mondobeyondo.stopmojo.util.CDSWrapper;
import com.mondobeyondo.stopmojo.util.Util;

/**
 * @author derry
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WebcamCapturePlugin implements CapturePlugin {
	private static final String ID = "WebcamCapture1", DESC = "Webcam Capture", VENDOR = "StopMojo Project", VERSION = "1.0";

	private boolean m_ok = false;

	private String m_devName = null;

	private Webcam m_webcam = null;

	private WebcamPanel m_panel = null;

	private JFrame m_parent;

	public WebcamCapturePlugin() {
		int devCount = 0;

		//
		// do a simple check to see if JMF is installed and we have some
		// capture devices available.
		try {
			List<Webcam> webcams = Webcam.getWebcams();
			for (Webcam webcam : webcams) {
				devCount++;
			}
			m_ok = devCount > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mondobeyondo.stopmojo.capture.CapturePlugin#selectCaptureDevice(java.
	 * lang.String, boolean)
	 */
	public boolean selectCaptureDevice(JFrame parent, String dev, boolean showDialog) throws CapturePluginException {
		boolean retval = false;

		dispose();

		m_devName = dev;
		m_parent = parent;

		if (showDialog) {
			try {
				CaptureDeviceDialog d = new CaptureDeviceDialog(m_parent, dev);

				if (d.showModal())
					m_devName = d.getDevName();
				else
					m_devName = "";
			} catch (Exception e) {
				throw new CapturePluginException("Unable to select capture device!", e);
			}
		}

		if (!m_devName.trim().equals("")) {
			Cursor oldCursor = m_parent.getCursor();
			m_parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));

			String devName = "";

			int formatIndex = -1;

			StringTokenizer st = new StringTokenizer(m_devName, CaptureDeviceDialog.DELIMITER);

			if (st.hasMoreTokens())
				devName = st.nextToken();
			if (st.hasMoreTokens())
				formatIndex = Util.atoi(st.nextToken());

			dispose();
			try {
				retval = setCapDev(devName, formatIndex);
			} catch (CapturePluginException e1) {
				m_parent.setCursor(oldCursor);
				throw e1;
			} catch (Exception e2) {
				m_parent.setCursor(oldCursor);
				throw new CapturePluginException("Unable to set capture device!", e2);
			}

			m_parent.setCursor(oldCursor);
		}
		return retval;
	}

	private boolean setCapDev(String devName, int format) throws Exception {
		WebcamResolution resolution = null;
		for (WebcamResolution r : WebcamResolution.values()) {
			if (r.ordinal() == format) {
				resolution = r;
				break;
			}
		}
		List<Webcam> webcams = Webcam.getWebcams();
		for (Webcam webcam : webcams) {
			if (devName.equals(webcam.getName())) {
				if (resolution != null) {
					webcam.setViewSize(resolution.getSize());
				}
				System.out.println("devName = '" + devName + "', format = " +resolution);
				
				m_webcam = webcam;
				m_webcam.open();
				//m_panel = new WebcamPanel(m_webcam);
				//m_panel.setFPSDisplayed(true);
				//m_panel.setDisplayDebugInfo(true);
				//m_panel.setImageSizeDisplayed(true);
				//m_panel.setMirrored(true);
				//m_panel.start();
				//JFrame window = new JFrame("Test webcam panel");
				//window.add(panel);
				//window.setResizable(true);
				//window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//window.pack();
				//window.setVisible(true);
				return true;
			}
		}
		return false;
	}

	public String getCaptureDeviceName() {
		return m_devName;
	}

	public void startCapture() throws CapturePluginException {
		if (m_panel != null) {
			m_panel.start();
		}
	}

	public void stopCapture() throws CapturePluginException {
		if (m_panel != null) {
			m_panel.stop();
		}
	}

	public BufferedImage grabPreviewImage() throws CapturePluginException {
		return grabImage();
	}

	public BufferedImage grabImage() throws CapturePluginException {
		BufferedImage image = m_webcam.getImage();
		return image;
	}

	public String getID() {
		return ID;
	}

	public String getDesc() {
		return DESC;
	}

	public String getVendor() {
		return VENDOR;
	}

	public String getVersion() {
		return VERSION;
	}

	public boolean isOk() {
		return m_ok;
	}

	public void dispose() {
		if (m_panel != null) {
			m_panel.stop();
			m_panel = null;
		}

		if (m_webcam != null) {
			m_webcam.close();
			m_webcam = null;
		}
	}
}
