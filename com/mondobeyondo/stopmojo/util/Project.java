/*
 * Created on Feb 12, 2005
 *
 * Copyright (c) 2005 Derone Bryson
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

package com.mondobeyondo.stopmojo.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 * @author derry
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Project {
	private static final String PROJECT_HEADER = "StopMojo Project File", PROJECT_ID = "StopMojo",
			PROJECT_VERSION = "1.0", PROP_ID = "ID", PROP_VERSION = "Version", PROP_OUTPUTDIR = "OutputDir",
			PROP_CURFRAMENUM = "CurFrameNum", PROP_NUMFRAMES = "NumFrames", PROP_PREVFRAMEOFFSET = "PrevFrameOffset",
			PROP_FPS = "Fps", PROP_FRAMESKIP = "FrameSkip", PROP_MAINALPHA = "MainAlpha",
			PROP_PREVFRAMEALPHA = "PrevFrameAlpha", PROP_ROTO1ALPHA = "Roto1Alpha", PROP_ROTO2ALPHA = "Roto2Alpha",
			PROP_ROTO1FILENAME = "Roto1FileName", PROP_ROTO2FILENAME = "Roto2FileName",
			PROP_IMAGEFORMAT = "ImageFormat", EXTENSION = "smp", FRAME_DIR = "Frames", FRAME_BASE = "Frame";

	private Properties m_prop = null;

	private String m_fileName = "";

	private int m_numFrames = 0;

	public Project(String folder, String name) throws Exception {
		File ff = new File(folder, name), pf = new File(ff.getAbsoluteFile(), name + "." + EXTENSION);

		m_prop = new Properties();
		m_prop.setProperty(PROP_ID, PROJECT_ID);
		m_prop.setProperty(PROP_VERSION, PROJECT_VERSION);

		if (!ff.exists())
			ff.mkdirs();

		write(pf.getAbsolutePath());
		createFrameDir();
	}

	public Project(String fileName) throws Exception {
		m_fileName = fileName;
		read(fileName);
		countFrames();
	}

	public boolean read() throws Exception {
		return read(m_fileName);
	}

	public boolean read(String fileName) throws Exception {
		m_fileName = fileName;

		m_prop = new Properties();

		FileInputStream in = new FileInputStream(fileName);
		m_prop.load(in);
		if (!m_prop.getProperty(PROP_ID, "").equals(PROJECT_ID))
			throw new InvalidProjectException("ID Invalid");
		if (m_prop.getProperty(PROP_VERSION, "").compareTo(PROJECT_VERSION) > 0)
			throw new InvalidProjectException("Unknown Version");
		in.close();

		return true;
	}

	public boolean write() throws Exception {
		return write(m_fileName);
	}

	public boolean write(String fileName) throws Exception {
		m_fileName = fileName;

		boolean retval = false;

		FileOutputStream out = new FileOutputStream(fileName);
		m_prop.store(out, PROJECT_HEADER);
		out.close();
		retval = true;

		return retval;
	}

	private boolean createFrameDir() {
		boolean retval = false;

		File f = new File(getFrameDir());

		if (!f.exists())
			f.mkdirs();

		return f.isDirectory();
	}

	private void countFrames() {
		int baselen = FRAME_BASE.length(), max = 0;

		String files[] = getFrameFile().list(new ExtensionFileFilter(getImageFormat(), ""));

		for (int i = 0; i < files.length; i++) {
			int f = Util.atoi(files[i].substring(baselen));

			if (f > max)
				max = f;
		}

		m_numFrames = max;
	}

	public String getFileName() {
		return m_fileName;
	}

	public void setFileName(String fileName) {
		m_fileName = fileName;
	}

	public File getFrameFile() {
		File f = new File(m_fileName);

		return new File(f.getParent(), FRAME_DIR);
	}

	public String getFrameDir() {
		return getFrameFile().getAbsolutePath();
	}

	public int getCurFrameNum() {
		return Integer.parseInt(m_prop.getProperty(PROP_CURFRAMENUM, "1"));
	}

	public void setCurFrameNum(int frameNum) {
		m_prop.setProperty(PROP_CURFRAMENUM, Integer.toString(frameNum));
	}

	public int getNumFrames() {
		return m_numFrames;
	}

	public int getPrevFrameOffset() {
		return Integer.parseInt(m_prop.getProperty(PROP_PREVFRAMEOFFSET, "-1"));
	}

	public void setPrevFrameOffset(int offset) {
		m_prop.put(PROP_PREVFRAMEOFFSET, Integer.toString(offset));
	}

	public float getFps() {
		return Float.parseFloat(m_prop.getProperty(PROP_FPS, "30"));
	}

	public void setFps(float fps) {
		m_prop.put(PROP_FPS, Float.toString(fps));
	}

	public int getFrameSkip() {
		return Integer.parseInt(m_prop.getProperty(PROP_FRAMESKIP, "0"));
	}

	public void setFrameSkip(int frameSkip) {
		m_prop.put(PROP_FRAMESKIP, Integer.toString(frameSkip));
	}

	public float getMainAlpha() {
		return Float.parseFloat(m_prop.getProperty(PROP_MAINALPHA, "1.0"));
	}

	public void setMainAlpha(float alpha) {
		m_prop.put(PROP_MAINALPHA, Float.toString(alpha));
	}

	public float getPrevFrameAlpha() {
		return Float.parseFloat(m_prop.getProperty(PROP_PREVFRAMEALPHA, "0.5"));
	}

	public void setPrevFrameAlpha(float alpha) {
		m_prop.put(PROP_PREVFRAMEALPHA, Float.toString(alpha));
	}

	public float getRoto1Alpha() {
		return Float.parseFloat(m_prop.getProperty(PROP_ROTO1ALPHA, "0.5"));
	}

	public void setRoto1Alpha(float alpha) {
		m_prop.put(PROP_ROTO1ALPHA, Float.toString(alpha));
	}

	public float getRoto2Alpha() {
		return Float.parseFloat(m_prop.getProperty(PROP_ROTO2ALPHA, "0.5"));
	}

	public void setRoto2Alpha(float alpha) {
		m_prop.put(PROP_ROTO2ALPHA, Float.toString(alpha));
	}

	public String getImageFormat() {
		return m_prop.getProperty(PROP_IMAGEFORMAT, "jpg");
	}

	public void setImageFormat(String imageFormat) {
		m_prop.put(PROP_IMAGEFORMAT, imageFormat);
	}

	private String getFrameName(int frameNum) {
		return FRAME_BASE + Util.formatNumber("000000", frameNum) + "." + getImageFormat();
	}

	public File getFrameFile(int frameNum) {
		return new File(getFrameDir(), getFrameName(frameNum));
	}

	public BufferedImage getFrame(int frameNum) throws Exception {
		// System.out.println("reading frame " + frameNum + ", " +
		// getFrameFile(frameNum).getAbsolutePath());
		return ImageIO.read(getFrameFile(frameNum));
	}

	public void putFrame(int frameNum, BufferedImage img) throws Exception {
		ImageIO.write(img, getImageFormat(), getFrameFile(frameNum));
		if (frameNum > m_numFrames)
			m_numFrames = frameNum;
	}

	public static FileFilter getFileFilter() {
		return (FileFilter) new ExtensionFileFilter(EXTENSION, "StopMojo Project (*." + EXTENSION + ")");
	}
}
