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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

/**
 * @author derry
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Image m_images[];

	private AlphaComposite m_alphas[];

	private Color m_gridColor;

	private int m_numGridX, m_numGridY;

	private boolean m_gridOn;

	public ImagePanel(int numImages) {
		m_images = new Image[numImages];
		m_alphas = new AlphaComposite[numImages];
		for (int i = 0; i < numImages; i++) {
			m_images[i] = null;
			m_alphas[i] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0);
		}
		m_gridColor = Color.WHITE;
		m_numGridX = 10;
		m_numGridY = 10;
		m_gridOn = true;
		setOpaque(true);
	}

	public void setImage(int index, Image image, float alpha) {
		m_images[index] = image;
		m_alphas[index] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		repaint();
	}

	public void setImage(int index, Image image) {
		m_images[index] = image;
		repaint();
	}

	public void setAlpha(int index, float alpha) {
		m_alphas[index] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		repaint();
	}

	public void setGridColor(Color c) {
		m_gridColor = c;
		repaint();
	}

	public Color getGridColor() {
		return m_gridColor;
	}

	public void setGridNumX(int numX) {
		m_numGridX = numX;
		repaint();
	}

	public int getGridNumX() {
		return m_numGridX;
	}

	public void setGridNumY(int numY) {
		m_numGridY = numY;
		repaint();
	}

	public int getGridNumY() {
		return m_numGridY;
	}

	public void showGrid(boolean on) {
		m_gridOn = on;
		repaint();
	}

	public boolean isShowingGrid() {
		return m_gridOn;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		int w = getSize().width, h = getSize().height;

		double gw = w, gh = h;

		double windowRatio = (double) w / (double) h;

		// g2d.setClip(null);
		// g2d.setTransform(new AffineTransform());

		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, w, h);

		for (int i = 0; i < m_images.length; i++) {
			Image img = m_images[i];

			if (img != null) {
				double iw = img.getWidth(null), ih = img.getHeight(null), x = 0, y = 0,
						imageRatio = (double) img.getWidth(null) / (double) img.getHeight(null), scale;

				if (windowRatio < imageRatio)
					scale = (double) w / iw;
				else
					scale = (double) h / ih;

				gw = (int) (iw * scale + 0.5);
				gh = (int) (ih * scale + 0.5);

				AffineTransform at = new AffineTransform(), iat = new AffineTransform();

				iat.scale(scale, scale);
				if (scale * iw < w)
					x = (w - (scale * iw)) / 2.0;
				if (scale * ih < h)
					y = (h - (scale * ih)) / 2.0;

				at.translate(x, y);
				g2d.setTransform(at);
				if (i == 0) {
					g2d.setColor(Color.BLACK);
					g2d.fillRect(0, 0, (int) (iw * scale), (int) (ih * scale));
				}
				g2d.setComposite(m_alphas[i]);
				g2d.drawImage(img, iat, null);
			}
		}

		if (m_gridOn) {
			double i, spacing;

			g2d.setComposite(AlphaComposite.SrcOver);
			g2d.setColor(m_gridColor);
			spacing = gh / m_numGridY;
			for (i = spacing; i < gh; i += spacing)
				g2d.drawLine(0, (int) (i + 0.5), (int) (gw - 1 + 0.5), (int) (i + 0.5));
			spacing = gw / m_numGridX;
			for (i = spacing; i < gw; i += spacing)
				g2d.drawLine((int) (i + 0.5), 0, (int) (i + 0.5), (int) (gh - 1 + 0.5));
		}
		g2d.dispose();
	}
}
