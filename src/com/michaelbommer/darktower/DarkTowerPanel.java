/*
 * 07/24/2002 - 20:37:20
 *
 * DarkTowerPanel.java
 * Copyright (C) 2002 Michael Bommer
 * m_bommer@yahoo.de
 * www.well-of-souls.com/tower
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.michaelbommer.darktower;

import javax.swing.JComponent;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class DarkTowerPanel extends JComponent
{
	private String label = null;
	private ImageIcon image = null;
	private Color color = new Color(255, 0, 0);
	private boolean flash = false;
	private int flashInterval = 0;
	private boolean enabled = false;
	
	public DarkTowerPanel()
	{
		label = "1";
		image = Image.getImageIcon(Image.BLACK);
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public void setLabel(String label)
	{
		this.label = label;
		repaint();
	}
	
	public void setImage(ImageIcon image)
	{
		this.image = image;
		repaint();
	}

	public void setFlash(boolean flash)
	{
		this.flash = flash;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;
		
		Font font = new Font("Arial", Font.BOLD, 20);
		Rectangle2D fontrec = font.getStringBounds(label, g2D.getFontRenderContext());
//		Rectangle2D standardrec = font.getStringBounds("00", g2D.getFontRenderContext());

//		int labelx = ( getWidth() + (int) standardrec.getWidth() ) / 2 - (int) fontrec.getWidth() + 1;
		int labelx = ( getWidth() - (int) fontrec.getWidth() ) / 2;
		int labely = 20;
		int imagex = ( getWidth() - image.getIconWidth() ) / 2;
		int imagey = 26;

		if ( flash )
			flashInterval++;
		else
			flashInterval = 0;
		
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, getWidth() - 2, getHeight());
		g.setFont(font);
		g.setColor(color);
		if ( flashInterval % 2 == 0 )
			g.drawString(label, labelx, labely);
		if ( enabled )
			g.drawImage(image.getImage(), imagex, imagey, this);
	}
}