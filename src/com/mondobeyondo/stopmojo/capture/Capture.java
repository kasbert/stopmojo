/*
 * Created on Feb 4, 2005
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
package com.mondobeyondo.stopmojo.capture;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * @author derry
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Capture {
	public static final String s_appName = "StopMojo Capture", s_appVersion = "0.2", s_propFile = "capture.properties",
			PREF_LASTPRJFOLDER = "LastPrjFolder", PREF_LASTEXPORTFOLDER = "LastExportFolder";

	public static Image s_stopmojoImage;

	public static ImageIcon s_helpIcon, s_aboutIcon, s_okIcon, s_cancelIcon, s_prefIcon, s_exitIcon, s_newIcon,
			s_openIcon, s_saveIcon, s_bigNewIcon, s_bigOpenIcon, s_bigSaveIcon, s_bigGridIcon, s_playIcon, s_stopIcon,
			s_pauseIcon, s_rewindIcon, s_fastForwardIcon, s_beginIcon, s_endIcon, s_closeIcon, s_bigCloseIcon;

	private static Properties s_prop;

	private static String s_captureDevName = "";

	private static int s_captureFormatIndex = -1;

	private static Preferences s_pref;

	public static String getCaptureDevName() {
		return s_captureDevName;
	}

	public static void setCaptureDevName(String name) {
		s_captureDevName = name;
	}

	public static int getCaptureFormatIndex() {
		return s_captureFormatIndex;
	}

	public static void setCaptureFormatIndex(int format) {
		s_captureFormatIndex = format;
	}

	public static Preferences getPref() {
		return s_pref;
	}

	public static void main(String[] args) {
		s_pref = Preferences.userNodeForPackage(Capture.class);
		s_prop = new Properties();

		try {
			FileInputStream in = new FileInputStream(s_propFile);
			s_prop.load(in);
			in.close();
		} catch (Exception e) {
			// System.out.println("Unable to read properties from " +
			// s_propFile);
			// System.exit(1);
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		s_stopmojoImage = new ImageIcon(Capture.class.getResource("/images/stopmojo.gif")).getImage();
		s_helpIcon = new ImageIcon(Capture.class.getResource("/images/Help16.gif"));
		s_aboutIcon = new ImageIcon(Capture.class.getResource("/images/About16.gif"));
		s_prefIcon = new ImageIcon(Capture.class.getResource("/images/Options16.gif"));
		s_okIcon = new ImageIcon(Capture.class.getResource("/images/Check16.gif"));
		s_cancelIcon = new ImageIcon(Capture.class.getResource("/images/Cancel16.gif"));
		s_exitIcon = new ImageIcon(Capture.class.getResource("/images/Exit16.gif"));
		s_newIcon = new ImageIcon(Capture.class.getResource("/images/New16.gif"));
		s_openIcon = new ImageIcon(Capture.class.getResource("/images/OpenDoc16.gif"));
		s_saveIcon = new ImageIcon(Capture.class.getResource("/images/Save16.gif"));
		s_bigNewIcon = new ImageIcon(Capture.class.getResource("/images/New20.gif"));
		s_bigOpenIcon = new ImageIcon(Capture.class.getResource("/images/OpenDoc20.gif"));
		s_bigSaveIcon = new ImageIcon(Capture.class.getResource("/images/Save20.gif"));
		s_bigGridIcon = new ImageIcon(Capture.class.getResource("/images/Sheet20.gif"));
		s_playIcon = new ImageIcon(Capture.class.getResource("/images/VCRPlay.gif"));
		s_stopIcon = new ImageIcon(Capture.class.getResource("/images/VCRStop.gif"));
		s_pauseIcon = new ImageIcon(Capture.class.getResource("/images/VCRPause.gif"));
		s_rewindIcon = new ImageIcon(Capture.class.getResource("/images/VCRRewind.gif"));
		s_fastForwardIcon = new ImageIcon(Capture.class.getResource("/images/VCRFastForward.gif"));
		s_beginIcon = new ImageIcon(Capture.class.getResource("/images/VCRBegin.gif"));
		s_endIcon = new ImageIcon(Capture.class.getResource("/images/VCREnd.gif"));
		s_closeIcon = new ImageIcon(Capture.class.getResource("/images/DeleteDocument16.gif"));
		s_bigCloseIcon = new ImageIcon(Capture.class.getResource("/images/DeleteDocument20.gif"));

		String prjFileName = null;

		if (args.length > 0)
			prjFileName = args[0];

		CaptureFrame f = new CaptureFrame(prjFileName);

		f.setVisible(true);
	}

	public static Properties getProp() {
		return s_prop;
	}

	public static void onAbout(Frame parent) {
		JOptionPane.showMessageDialog(parent,
				Capture.s_appName + "\n" + "Version " + Capture.s_appVersion
						+ "\n\nCopyright (c) 2005 Derone Bryson.  All Rights Reserved.",
				"About " + Capture.s_appName, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void exit(int exitVal) {
		System.exit(exitVal);
	}

	public static void handleError(Component parent, String message, String title, Exception e, boolean fatal) {
		if (message == null)
			message = "";

		// message += "\nPlease try the operation again and notify your
		// system\n" +
		// "administrator if the problem persists.\n";

		if (fatal)
			message += "\nThe program will exit.\n";

		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(parent, message, s_appName + ": " + title, JOptionPane.ERROR_MESSAGE);

		if (fatal) {
			e.printStackTrace();
			exit(1);
		}
	}

	public static void errorMsg(Component parent, String message, String title) {
		JOptionPane.showMessageDialog(parent, message, s_appName + ": " + title, JOptionPane.ERROR_MESSAGE);
	}

	public static void databaseError(Component parent, Exception e, boolean fatal) {
		String mess = e.getMessage();

		if (mess != null && mess.indexOf('\n') != -1)
			mess = mess.substring(0, mess.indexOf('\n'));

		if (mess != null && mess.length() > 1000)
			mess = mess.substring(0, 1000);

		handleError(parent, mess, "Database Error", e, fatal);
	}

	public static void generalError(Component parent, Exception e, boolean fatal) {
		String mess = e.getMessage();

		if (mess != null && mess.indexOf('\n') != -1)
			mess = mess.substring(0, mess.indexOf('\n'));

		if (mess != null && mess.length() > 1000)
			mess = mess.substring(0, 1000);

		handleError(parent, mess, "General Error", e, fatal);
	}

	public static void generalError(Component parent, String title, Exception e, boolean fatal) {
		String mess = e.getMessage();

		if (mess != null && mess.indexOf('\n') != -1)
			mess = mess.substring(0, mess.indexOf('\n'));

		if (mess != null && mess.length() > 1000)
			mess = mess.substring(0, 1000);

		handleError(parent, mess, title, e, fatal);
	}
}
