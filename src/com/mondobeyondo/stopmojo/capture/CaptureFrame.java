/*
 * Created on Oct 21, 2003
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.prefs.Preferences;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.mondobeyondo.stopmojo.util.FieldPanel;
import com.mondobeyondo.stopmojo.util.FramePosSizeHandler;
import com.mondobeyondo.stopmojo.util.Project;
import com.mondobeyondo.stopmojo.util.SwingWorker;

/**
 * @author Derry Bryson
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CaptureFrame extends JFrame implements ChangeListener {
	private static final long serialVersionUID = 1L;

	private static final String PREF_VDIVLOC = "VDivLoc", PREF_HDIVLOC = "HDivLoc", PREF_CAPDEVNAME = "CapDevName",
			PREF_CAPRESOLUTION = "CapResolution", PREF_CAPFORMAT = "CapFormat", PREF_MIRRORON = "MirrorOn",
			PREF_GRIDON = "GridOn", PREF_GRIDNUMH = "GridNumH", PREF_GRIDNUMV = "GridNumV";

	private static final int FRAME_TIMEOUT = 250;

	private Timer m_timer;

	private Project m_prj = null;

	private JPanel m_monitorPanel, m_compPanel, m_roto1Panel, m_roto2Panel;

	private ImagePanel m_compImagePanel, m_roto1ImagePanel, m_roto2ImagePanel;

	private JLabel m_statusBarLabel;

	private JSlider m_prevFrameAlphaSlider, m_mainAlphaSlider;

	private JButton m_previewBut, m_capture1But, m_capture2But, m_capture3But, m_capture4But;

	private JSplitPane m_hSplitPane, m_vSplitPane;

	private JSpinner m_prevFrameOffsetSpinner, m_curFrameSpinner, m_gridHSpinner, m_gridVSpinner;

	private Action m_fileNewAction, m_fileOpenAction, m_fileCloseAction, m_capture1Action, m_capture2Action,
			m_capture3Action, m_capture4Action, m_projectPropAction, m_previewAction, m_exportAction,
			m_mirrorOnOffAction, m_gridOnOffAction;

	private JCheckBox m_gridCheckBox;

	private JCheckBox m_mirrorCheckBox;

	private Preferences m_pref;

	private boolean m_capturing = false;

	private Webcam m_webcam = null;

	private byte[] m_sound;

	public CaptureFrame(String prjFileName) {

		m_pref = Preferences.userNodeForPackage(this.getClass());

		setTitle("");
		setIconImage(Capture.s_stopmojoImage);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				onClose(evt);
			}
		});

		initActions();

		setJMenuBar(makeMenuBar());
		this.getContentPane().add(makeToolBar(), BorderLayout.NORTH);

		/*
		 * m_vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_roto1Panel
		 * = makeRoto1Panel(), m_roto2Panel = makeRoto2Panel());
		 * m_vSplitPane.setDividerLocation(m_pref.getInt(PREF_VDIVLOC, 250));
		 * 
		 * m_hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		 * m_compPanel = makeCompPanel(), m_vSplitPane);
		 * m_hSplitPane.setDividerLocation(m_pref.getInt(PREF_HDIVLOC, 250));
		 * 
		 * 
		 * getContentPane().add(m_hSplitPane, BorderLayout.CENTER);
		 */
		getContentPane().add(m_compPanel = makeCompPanel(), BorderLayout.CENTER);
		getContentPane().add(makeStatusBar(), BorderLayout.SOUTH);

		pack();

		setSize(500, 500);
		FramePosSizeHandler.restoreSizeAndPosition(this);

		// doLayout();

		setDefaultCloseOperation(HIDE_ON_CLOSE);

		updateUI();

		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("sounds/shutter1.wav");
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while (result != -1) {
				buf.write((byte) result);
				result = bis.read();
			}
			m_sound = buf.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!m_pref.get(PREF_CAPDEVNAME, "").equals(""))
			setCapDev(m_pref.get(PREF_CAPDEVNAME, ""), m_pref.get(PREF_CAPRESOLUTION, ""));

		if (prjFileName != null)
			doFileOpen(prjFileName);
	}

	public void setTitle(String s) {
		if (!s.trim().equals(""))
			s = " - " + s;
		super.setTitle(Capture.s_appName + ", Version " + Capture.s_appVersion + s);
	}

	public void setTitle(File file) {
		setTitle(file.getName());
	}

	private void setProject(Project prj) {
		m_prj = prj;
		if (m_prj != null) {
			setTitle(new File(m_prj.getFileName()));
			m_prevFrameAlphaSlider.setValue((int) (m_prj.getPrevFrameAlpha() * 100.0));
			m_compImagePanel.setAlpha(1, m_prj.getPrevFrameAlpha());
			// m_prevFrameOffsetSpinner.setValue(new
			// Integer(m_prj.getPrevFrameOffset()));
			m_prevFrameOffsetSpinner.setValue(new Integer(m_prj.getPrevFrameOffset()));
			setPrevFrameOffset(m_prj.getPrevFrameOffset());
			m_mainAlphaSlider.setValue((int) (m_prj.getMainAlpha() * 100.0));
			m_compImagePanel.setAlpha(0, m_prj.getMainAlpha());
			m_curFrameSpinner.setValue(new Integer(m_prj.getCurFrameNum()));
		}
		updateUI();
	}

	public Project getProject() {
		return m_prj;
	}

	private JPanel makeMonitorPanel() {
		JPanel p = new JPanel();

		p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Live"));
		p.setLayout(new BorderLayout());

		return p;
	}

	private JPanel makeCompPanel() {
		GridBagConstraints gbc;

		JPanel p = new JPanel();

		JLabel l;

		Hashtable<Integer, JLabel> labelTable;

		// p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
		// "Composite"));
		p.setLayout(new BorderLayout());

		JPanel ip = new JPanel();
		ip.setBorder(new EmptyBorder(5, 5, 5, 5));
		// ip.setBorder(new BevelBorder(BevelBorder.LOWERED));
		ip.setLayout(new BorderLayout());
		m_compImagePanel = new ImagePanel(3);
		m_compImagePanel.setAlpha(0, (float) 1.0);
		m_compImagePanel.setGridNumX(m_pref.getInt(PREF_GRIDNUMH, 10));
		m_compImagePanel.setGridNumY(m_pref.getInt(PREF_GRIDNUMV, 10));
		m_compImagePanel.showGrid(m_pref.getBoolean(PREF_GRIDON, false));
		ip.add(m_compImagePanel, BorderLayout.CENTER);
		p.add(ip, BorderLayout.CENTER);

		JPanel cp = new JPanel();
		cp.setLayout(new GridBagLayout());

		FieldPanel pfp = new FieldPanel();
		pfp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Previous Frame Overlay"));

		m_prevFrameAlphaSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		m_prevFrameAlphaSlider.addChangeListener(this);
		m_prevFrameAlphaSlider.setMajorTickSpacing(10);
		// m_prevFrameAlphaSlider.setMinorTickSpacing(1);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0"));
		labelTable.put(new Integer(50), new JLabel("50"));
		labelTable.put(new Integer(100), new JLabel("100"));
		m_prevFrameAlphaSlider.setLabelTable(labelTable);
		m_prevFrameAlphaSlider.setPaintTicks(true);
		m_prevFrameAlphaSlider.setPaintLabels(true);
		pfp.addField("Alpha (%):", m_prevFrameAlphaSlider, 100);

		m_prevFrameOffsetSpinner = new JSpinner(
				new SpinnerNumberModel(new Integer(-1), new Integer(-9999), new Integer(-1), new Integer(1)));
		m_prevFrameOffsetSpinner.addChangeListener(this);
		pfp.addField("Frame Offset:", m_prevFrameOffsetSpinner, 10);

		FieldPanel mp = new FieldPanel();
		mp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Video Capture"));

		m_mainAlphaSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		m_mainAlphaSlider.addChangeListener(this);
		m_mainAlphaSlider.setMajorTickSpacing(10);
		// m_mainAlphaSlider.setMinorTickSpacing(0);
		m_mainAlphaSlider.setPaintTicks(true);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0"));
		labelTable.put(new Integer(50), new JLabel("50"));
		labelTable.put(new Integer(100), new JLabel("100"));
		m_mainAlphaSlider.setLabelTable(labelTable);
		m_mainAlphaSlider.setPaintLabels(true);
		mp.addField("Alpha (%):", m_mainAlphaSlider, 100);

		int curFrame = 0;
		if (m_prj != null)
			curFrame = m_prj.getCurFrameNum();
		m_curFrameSpinner = new JSpinner(
				new SpinnerNumberModel(new Integer(curFrame), new Integer(0), new Integer(999999), new Integer(1)));
		m_curFrameSpinner.addChangeListener(this);
		mp.addField("Current Frame:", m_curFrameSpinner, 10);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		cp.add(pfp, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		cp.add(mp, gbc);

		JPanel butPanel = new JPanel();
		butPanel.setLayout(new GridBagLayout());

		m_capture1But = new JButton(m_capture1Action);
		m_capture1But.setText("1");
		m_capture1But.setMargin(new Insets(2, 2, 2, 2));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		butPanel.add(m_capture1But, gbc);

		m_capture2But = new JButton(m_capture2Action);
		m_capture2But.setText("2");
		m_capture2But.setMargin(new Insets(5, 5, 5, 5));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		butPanel.add(m_capture2But, gbc);

		m_capture3But = new JButton(m_capture3Action);
		m_capture3But.setText("3");
		m_capture3But.setMargin(new Insets(5, 5, 5, 5));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		butPanel.add(m_capture3But, gbc);

		m_capture4But = new JButton(m_capture4Action);
		m_capture4But.setText("4");
		m_capture4But.setMargin(new Insets(5, 5, 5, 5));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		butPanel.add(m_capture4But, gbc);

		m_previewBut = new JButton(m_previewAction);
		m_previewBut.setText("Preview");
		m_previewBut.setMargin(new Insets(5, 5, 5, 5));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 2.0;
		gbc.weighty = 1.0;
		butPanel.add(m_previewBut, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		cp.add(butPanel, gbc);

		p.add(cp, BorderLayout.SOUTH);
		doLayout();

		return p;
	}

	private JPanel makeRoto1Panel() {
		JPanel p = new JPanel();

		p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Roto 1"));

		return p;
	}

	private JPanel makeRoto2Panel() {
		JPanel p = new JPanel();

		p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Roto 2"));

		return p;
	}

	private JPanel makeStatusBar() {
		JPanel statusBar = new JPanel();

		statusBar.setLayout(new BorderLayout());
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		m_statusBarLabel = new JLabel(" ");
		m_statusBarLabel.setFont(m_statusBarLabel.getFont().deriveFont(Font.PLAIN, 10));
		statusBar.add(m_statusBarLabel, BorderLayout.WEST);

		return statusBar;
	}

	private void setStatusText(String text) {
		if (text.equals(""))
			text = " ";
		m_statusBarLabel.setText(text);
	}

	private JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar();

		JButton button;

		//JToggleButton tbutton;

		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		button = new JButton();
		button.setAction(m_fileNewAction);
		button.setText("");
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		button.setToolTipText("New Project");
		button.setIcon(Capture.s_bigNewIcon);
		button.setFocusable(false);
		toolBar.add(button);

		button = new JButton();
		button.setAction(m_fileOpenAction);
		button.setText("");
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		button.setToolTipText("Open Project");
		button.setIcon(Capture.s_bigOpenIcon);
		button.setFocusable(false);
		toolBar.add(button);

		button = new JButton();
		button.setAction(m_fileCloseAction);
		button.setText("");
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		button.setToolTipText("Close Project");
		button.setIcon(Capture.s_bigCloseIcon);
		button.setFocusable(false);
		toolBar.add(button);

		toolBar.addSeparator();

		m_mirrorCheckBox = new JCheckBox();
		m_mirrorCheckBox.setAction(m_mirrorOnOffAction);
		// m_mirrorCheckBox.setText("Mirror:");
		m_mirrorCheckBox.setHorizontalAlignment(JCheckBox.LEFT);
		m_mirrorCheckBox.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		m_mirrorCheckBox.setToolTipText("Rotate 180 degrees");
		m_mirrorCheckBox.setFocusable(false);
		m_mirrorCheckBox.setSelected(m_pref.getBoolean(PREF_MIRRORON, false));
		toolBar.add(new JLabel("Mirror:"));
		toolBar.add(m_mirrorCheckBox);

		m_gridCheckBox = new JCheckBox();
		m_gridCheckBox.setAction(m_gridOnOffAction);
		// m_gridCheckBox.setText("Grid:");
		m_gridCheckBox.setHorizontalAlignment(JCheckBox.LEFT);
		m_gridCheckBox.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		m_gridCheckBox.setToolTipText("Grid On/Off");
		m_gridCheckBox.setFocusable(false);
		m_gridCheckBox.setSelected(m_pref.getBoolean(PREF_GRIDON, false));
		toolBar.add(new JLabel("Grid:"));
		toolBar.add(m_gridCheckBox);

		m_gridHSpinner = new JSpinner(new SpinnerNumberModel(m_pref.getInt(PREF_GRIDNUMH, 10), 1, 100, 1));
		m_gridHSpinner.addChangeListener(this);
		m_gridHSpinner.setMaximumSize(new Dimension(50, 20));
		m_gridHSpinner.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		m_gridHSpinner.setToolTipText("Number of horizontal grid sections");
		m_gridHSpinner.setFocusable(false);
		m_gridHSpinner.setValue(new Integer(m_pref.getInt(PREF_GRIDNUMH, 10)));
		toolBar.add(new JLabel(" H:"));
		toolBar.add(m_gridHSpinner);

		m_gridVSpinner = new JSpinner(new SpinnerNumberModel(m_pref.getInt(PREF_GRIDNUMV, 10), 1, 100, 1));
		m_gridVSpinner.addChangeListener(this);
		m_gridVSpinner.setMaximumSize(new Dimension(50, 20));
		m_gridVSpinner.setFont(button.getFont().deriveFont(Font.PLAIN, 10));
		m_gridVSpinner.setToolTipText("Number of vertical grid sections");
		m_gridVSpinner.setFocusable(false);
		m_gridVSpinner.setValue(new Integer(m_pref.getInt(PREF_GRIDNUMV, 10)));
		toolBar.add(new JLabel(" V:"));
		toolBar.add(m_gridVSpinner);

		return toolBar;
	}

	private void initActions() {
		m_fileNewAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onFileNew();
			}
		};

		m_fileOpenAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onFileOpen();
			}
		};

		m_fileCloseAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onFileClose();
			}
		};

		m_projectPropAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onProjectProp();
			}
		};
		m_capture1Action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCapture(1);
			}
		};
		m_capture2Action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCapture(2);
			}
		};
		m_capture3Action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCapture(3);
			}
		};
		m_capture4Action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCapture(4);
			}
		};

		m_previewAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onPreview();
			}
		};

		m_exportAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onExport();
			}
		};
		m_mirrorOnOffAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onMirrorOnOff();
			}
		};
		m_gridOnOffAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onGridOnOff();
			}
		};
	}

	private void updateUI() {
		m_fileCloseAction.setEnabled(m_prj != null);
		m_capture1Action.setEnabled(m_prj != null && !m_prj.getImageFormat().equals("") && m_capturing);
		m_capture2Action.setEnabled(m_prj != null && !m_prj.getImageFormat().equals("") && m_capturing);
		m_capture3Action.setEnabled(m_prj != null && !m_prj.getImageFormat().equals("") && m_capturing);
		m_capture4Action.setEnabled(m_prj != null && !m_prj.getImageFormat().equals("") && m_capturing);
		m_projectPropAction.setEnabled(m_prj != null);
		m_previewAction.setEnabled(m_prj != null && m_prj.getNumFrames() > 0);
		m_exportAction.setEnabled(m_prj != null && m_prj.getNumFrames() > 0);

		m_prevFrameAlphaSlider.setEnabled(m_prj != null && m_capturing);
		m_prevFrameOffsetSpinner.setEnabled(m_prj != null && m_capturing);
		m_mainAlphaSlider.setEnabled(m_capturing);
		m_curFrameSpinner.setEnabled(m_prj != null && m_capturing);
	}

	private JMenuBar makeMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(makeFileMenu());
		menuBar.add(makeProjectMenu());
		menuBar.add(makeHelpMenu());

		return menuBar;
	}

	private JMenu makeFileMenu() {
		JMenu menu, menu1;

		JMenuItem menuItem;

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);

		menuItem = new JMenuItem(m_fileNewAction);
		menuItem.setText("New Project");
		menuItem.setMnemonic('n');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItem.setIcon(Capture.s_newIcon);
		menu.add(menuItem);

		menuItem = new JMenuItem(m_fileOpenAction);
		menuItem.setText("Open Project");
		menuItem.setMnemonic('o');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.setIcon(Capture.s_openIcon);
		menu.add(menuItem);

		menuItem = new JMenuItem(m_fileCloseAction);
		menuItem.setText("Close Project");
		menuItem.setMnemonic('c');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menuItem.setIcon(Capture.s_closeIcon);
		menu.add(menuItem);

		menu.addSeparator();

		class setCapAction implements java.awt.event.ActionListener {
			Webcam webcam;
			WebcamResolution resolution;

			public setCapAction(Webcam webcam, WebcamResolution resolution) {
				this.webcam = webcam;
				this.resolution = resolution;
			}

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onFileSelCapDev(webcam, resolution);
			}
		}
		;
		menu1 = new JMenu("Select Capture Device");
		menu1.setMnemonic(KeyEvent.VK_F);

		for (Webcam webcam : Webcam.getWebcams()) {
			menuItem = new JMenu(webcam.getName());
			menuItem.setEnabled(true); // TODO isOK
			for (WebcamResolution resolution : WebcamResolution.values()) {
				boolean ok = isOkForWebcam(webcam, resolution);
				if (ok) {
					JMenuItem menuItem2 = new JMenuItem(
							resolution.name() + " : " + resolution.getSize().width + "x" + resolution.getSize().height);
					menuItem2.addActionListener(new setCapAction(webcam, resolution));
					menuItem2.setEnabled(true);
					menuItem.add(menuItem2);
				} else {
					JMenu menu2 = new JMenu(
							resolution.name() + " : " + resolution.getSize().width + "x" + resolution.getSize().height);
					JMenuItem menuItem2 = new JMenuItem("Use unsupported resolution");
					menuItem2.addActionListener(new setCapAction(webcam, resolution));
					menuItem2.setEnabled(true);// TODO isOK
					menu2.add(menuItem2);
					menuItem.add(menu2);
				}
			}
			menu1.add(menuItem);
		}

		menu.add(menu1);
		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onFileExit(evt);
			}
		});
		menu.add(menuItem);

		return menu;
	}

	private boolean isOkForWebcam(Webcam cdi, WebcamResolution resolution) {
		for (Dimension d : cdi.getViewSizes()) {
			if (d.width == resolution.getSize().width && d.height == resolution.getSize().height) {
				return true;
			}
		}
		return false;
	}

	private JMenu makeProjectMenu() {
		JMenu menu;

		JMenuItem menuItem;

		menu = new JMenu("Project");
		menu.setMnemonic(KeyEvent.VK_F);

		menuItem = new JMenuItem(m_projectPropAction);
		menuItem.setText("Project Properties...");
		menuItem.setMnemonic('p');
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
		// ActionEvent.CTRL_MASK));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(m_capture1Action);
		menuItem.setText("Capture 1 Frame");
		menuItem.setMnemonic('c');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		menuItem = new JMenuItem(m_capture2Action);
		menuItem.setText("Capture 2 Frames");
		menuItem.setMnemonic('c');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		menuItem = new JMenuItem(m_capture3Action);
		menuItem.setText("Capture 3 Frames");
		menuItem.setMnemonic('c');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		menuItem = new JMenuItem(m_capture4Action);
		menuItem.setText("Capture 4 Frames");
		menuItem.setMnemonic('c');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(m_previewAction);
		menuItem.setText("Preview");
		menuItem.setMnemonic('p');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		menuItem = new JMenuItem(m_exportAction);
		menuItem.setText("Export");
		menuItem.setMnemonic('e');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		// menuItem.setIcon(EmailApp.s_checkEmailIcon);
		menu.add(menuItem);

		return menu;
	}

	private JMenu makeHelpMenu() {
		JMenu menu;

		JMenuItem menuItem;

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuItem = new JMenuItem("Help", KeyEvent.VK_H);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menuItem.setIcon(Capture.s_helpIcon);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setIcon(Capture.s_aboutIcon);
		menuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				onAbout();
			}
		});
		menu.add(menuItem);

		return menu;
	}

	private void doFrameClose() {
		// m_pref.putInt(PREF_VDIVLOC, m_vSplitPane.getDividerLocation());
		// m_pref.putInt(PREF_HDIVLOC, m_hSplitPane.getDividerLocation());

		m_pref.putInt(PREF_GRIDNUMH, ((Integer) m_gridHSpinner.getValue()).intValue());
		m_pref.putInt(PREF_GRIDNUMV, ((Integer) m_gridVSpinner.getValue()).intValue());
		m_pref.putBoolean(PREF_GRIDON, m_gridCheckBox.isSelected());

		doSaveProject();

		if (m_timer != null) {
			m_timer.stop();
			m_timer = null;
		}

		if (m_webcam != null) {
			m_webcam.close();
			m_webcam = null;
		}
		FramePosSizeHandler.saveSizeAndPosition(this);

		dispose();
		System.exit(0);
	}

	private void doSaveProject() {
		if (m_prj == null)
			return;

		try {
			m_prj.write();
		} catch (Exception e) {
			Capture.handleError(this, "Unable to write project!", "Error", e, false);
		}
		m_prj = null;
	}

	private void onFileExit(java.awt.event.ActionEvent evt) {
		doFrameClose();
	}

	private void onFileNew() {
		pauseCapture();

		NewProjectDialog d = new NewProjectDialog(this);

		if (d.showModal()) {
			if (m_prj != null) {
				doSaveProject();
				m_prj = null;
			}

			Project p = null;

			try {
				Capture.getPref().put(Capture.PREF_LASTPRJFOLDER, d.getFolder());
				p = new Project(d.getFolder(), d.getName());
				setProject(p);
				m_projectPropAction.actionPerformed(null);
			} catch (Exception e) {
				Capture.handleError(this, "Unable to create project!", "Error", e, false);
			}
		}
		unpauseCapture();
	}

	public void doFileOpen(String filename) {
		try {
			Project p = new Project(filename);

			if (p != null)
				setProject(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onFileOpen() {
		pauseCapture();

		doSaveProject();

		JFileChooser fc = new JFileChooser(Capture.getPref().get(Capture.PREF_LASTPRJFOLDER, ""));

		fc.addChoosableFileFilter(Project.getFileFilter());
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			Capture.getPref().put(Capture.PREF_LASTPRJFOLDER, fc.getSelectedFile().getParentFile().getAbsolutePath());
			try {
				doFileOpen(fc.getSelectedFile().getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		unpauseCapture();
	}

	public void onFileClose() {
		if (m_prj == null)
			return;

		doSaveProject();
		setProject(null);
		updateUI();
	}

	public void onProjectProp() {
		if (m_prj == null)
			return;

		ProjectPropDialog d = new ProjectPropDialog(this, m_prj);

		d.setVisible(true);
		updateUI();
	}

	private int getCurFrame() {
		return ((Integer) m_curFrameSpinner.getValue()).intValue();
	}

	private void setCurFrame(int frame) {
		m_curFrameSpinner.setValue(new Integer(frame));
		if (m_prj != null)
			m_prj.setCurFrameNum(frame);
	}

	public void onCapture(int frames) {
		if (m_prj == null)
			return;

		m_timer.stop();
		Image image = doCapture();
		if (image != null) {
			try {
				for (int i = 0; i < frames; i++)
					m_prj.putFrame(getCurFrame() + i, (BufferedImage) image);
				setCurFrame(getCurFrame() + frames);
				setPrevFrame(getCurFrame() + ((Integer) m_prevFrameOffsetSpinner.getValue()).intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		m_timer.start();
		updateUI();
	}

	private void setCapDev(String devName, String resName) {
		Cursor oldCursor = getCursor();
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		WebcamResolution resolution = null;
		for (WebcamResolution r : WebcamResolution.values()) {
			if (r.name().equalsIgnoreCase(resName)) {
				resolution = r;
				break;
			}
		}
		List<Webcam> webcams = Webcam.getWebcams();
		for (Webcam webcam : webcams) {
			if (devName.equalsIgnoreCase(webcam.getName())) {
				try {
					setCapturePlugin(webcam, resolution);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setCursor(oldCursor);
	}

	private void setCapturePlugin(Webcam webcam, WebcamResolution resolution) {
		Cursor oldCursor = getCursor();
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		m_compImagePanel.setImage(0, null);
		m_capturing = false;
		if (m_timer != null)
			m_timer.stop();
		if (m_webcam != null) {
			try {
				m_webcam.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		m_webcam = null;

		if (webcam != null) {
			if (resolution != null) {
				if (!isOkForWebcam(webcam, resolution)) {
					// force resolution
					Dimension[] sizes = new Dimension[] { resolution.getSize() };
					webcam.setCustomViewSizes(sizes);
				}
				webcam.setViewSize(resolution.getSize());
				m_pref.put(PREF_CAPRESOLUTION, resolution.name());
			}
			try {
				webcam.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
			m_webcam = webcam;
			setTimer(FRAME_TIMEOUT);
			m_capturing = true;
			updateUI();
			System.out.println("selected device: " + webcam.getName() + ", resolution: " + resolution);
			m_pref.put(PREF_CAPDEVNAME, webcam.getName());
		} else
			m_compImagePanel.setImage(0, null);

		setCursor(oldCursor);
	}

	private void onFileSelCapDev(Webcam webcam, WebcamResolution resolution) {
		if (m_timer != null) {
			m_timer.stop();
			m_timer = null;
		}
		setCapturePlugin(null, null);

		try {
			setCapturePlugin(webcam, resolution);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onClose(java.awt.event.WindowEvent evt) {
		doFrameClose();
	}

	private void onAbout() {
		Capture.onAbout(this);
	}

	private void onPreview() {
		PreviewDialog d = new PreviewDialog(this, m_prj, "Preview");

		d.setVisible(true);
		d.dispose();
	}

	private void onExport() {
		pauseCapture();
		if (m_prj != null && m_prj.getNumFrames() > 0) {
			try {
				ExportDialog d = new ExportDialog(this, m_prj);

				d.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		unpauseCapture();
	}

	private void onMirrorOnOff() {
		m_pref.putBoolean(PREF_MIRRORON, m_mirrorCheckBox.isSelected());
	}

	private void onGridOnOff() {
		m_compImagePanel.showGrid(m_gridCheckBox.isSelected());
	}

	public void setTimer(int delay) {
		if (m_timer != null) {
			m_timer.stop();
			m_timer = null;
		}

		if (delay > 0) {
			// System.out.println("setting timout to " + delay + " minutes");
			m_timer = new Timer(delay, new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					doSnapShot();
					m_timer.restart();
				}
			});
			m_timer.setRepeats(false);
			m_timer.start();
		}
	}

	private Image doCapture() {
		if (m_webcam != null) {
			playSound();
			try {
				return doMirror(m_webcam.getImage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private void playSound() {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(m_sound);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(bais);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doSnapShot() {
		if (m_webcam != null) {
			SwingWorker worker = new SwingWorker() {
				public Object construct() {
					try {
						return doMirror(m_webcam.getImage());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				public void finished() {
					m_compImagePanel.setImage(0, (BufferedImage) get());
				}
			};
			worker.start();
		}
	}

	private BufferedImage doMirror(BufferedImage image) {
		if (!m_mirrorCheckBox.isSelected()) {
			return image;
		}
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(-1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), -image.getHeight()));
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	private void pauseCapture() {
		if (m_capturing)
			m_timer.stop();
	}

	private void unpauseCapture() {
		if (m_capturing)
			m_timer.restart();
	}

	private void setPrevFrame(int frame) {
		if (m_prj != null) {
			// System.out.println("prevFrame = " + frame);
			try {
				Image img = m_prj.getFrame(frame);
				m_compImagePanel.setImage(1, img);
			} catch (Exception e) {
				m_compImagePanel.setImage(1, null);
				// e.printStackTrace();
			}
		}
	}

	private void setPrevFrameOffset(int offset) {
		// System.out.println("offset = " + offset);
		if (m_prj != null) {
			m_prj.setPrevFrameOffset(offset);
			setPrevFrame(m_prj.getCurFrameNum() + offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.
	 * ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == m_prevFrameAlphaSlider) {
			if (m_prj != null)
				m_prj.setPrevFrameAlpha(((float) m_prevFrameAlphaSlider.getValue()) / (float) 100.0);
			m_compImagePanel.setAlpha(1, ((float) m_prevFrameAlphaSlider.getValue()) / (float) 100.0);
		} else if (e.getSource() == m_mainAlphaSlider) {
			if (m_prj != null)
				m_prj.setMainAlpha(((float) m_mainAlphaSlider.getValue()) / (float) 100.0);
			m_compImagePanel.setAlpha(0, ((float) m_mainAlphaSlider.getValue()) / (float) 100.0);
		} else if (e.getSource() == m_prevFrameOffsetSpinner) {
			setPrevFrameOffset(((Integer) m_prevFrameOffsetSpinner.getValue()).intValue());
		} else if (e.getSource() == m_gridHSpinner) {
			m_compImagePanel.setGridNumX(((Integer) m_gridHSpinner.getValue()).intValue());
		} else if (e.getSource() == m_gridVSpinner) {
			m_compImagePanel.setGridNumY(((Integer) m_gridVSpinner.getValue()).intValue());
		}
	}
}
