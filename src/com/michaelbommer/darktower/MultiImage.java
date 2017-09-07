/*
 * 07/24/2002 - 20:37:20
 *
 * MultiImage.java
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

import java.lang.String;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MultiImage
{
	public static final int NA = 0;
	public static final int CLASSM = 1;
	public static final int DRAGON = 2;
	public static final int ISO = 3;
	
	public static final String[] IMAGE =
		{ "dg_classm32", "dg_dragon32", "dg_iso32" };
	public static final int[][] DIMENSION =
		{ {32, 32}, {32, 32}, {54, 49} };
	public static final int[][] INDEX =
		{ {16, 17, 18}, {0, 1, 33}, {39, 40, 43}, {9, 11, 12},
		  {10, 32}, {13, 14, 15} };

  	public static final int[] PLAYER =
		{ 11, 3, 19, 35 };
  	public static final int[] CITADEL =
		{ 210, 211, 212, 213 };
	public static final int[] BAZAAR =
		{ 182, 184, 183, 216 };
	public static final int[] SANCTUARY =
		{ 217, 220, 219, 218 };
	public static final int[] RUIN =
		{ 168, 168, 168, 168 };
	public static final int[] TOMB =
		{ 166, 164, 167, 165 };

	public static ImageIcon getImageIcon(int imageNo)
	{
		ImageIcon icon = null;
		
		if ( imageNo > NA )
			icon = new ImageIcon(
				Util.class.getResource("images/" + IMAGE[imageNo - 1] + ".gif"));
		
		return icon;
	}

	public static ImageIcon getTexture(ImageIcon icon, int imageNo, int indexNo, int dx, int dy)
	{
		int width = DIMENSION[imageNo - 1][0];
		int height = DIMENSION[imageNo - 1][1] - 23;
		
		int imagesPerRow = dx / width + 1;
		int imagesPerCol = dy / height * 2 + 2;
		
		int index = 0;
		double random = 0.0;

		BufferedImage bufferedImage = new BufferedImage(
			dx, dy, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		
		for (int row = -1; row < imagesPerCol; row++)
		{
			for (int col = -1; col < imagesPerRow; col++)
			{
				random = Math.random();
				index = INDEX[indexNo][(int) (random * INDEX[indexNo].length)];
				drawTexture(g, icon, imageNo, index, row / 2 + col, (row - 1) / 2 - col);
			}
		}
		
		return new ImageIcon(bufferedImage);
	}
	
	public static void drawTexture(Graphics g, ImageIcon icon, int imageNo, int index, int col, int row)
	{
		int width = DIMENSION[imageNo - 1][0];
		int height = DIMENSION[imageNo - 1][1];

		int x = (col - row ) * width / 2;
		int y = (col + row) * (height - 23) / 2 - 23;

		int imagesPerRow = icon.getIconWidth() / width;
		int srcX = (index % imagesPerRow) * width;
		int srcY = (index / imagesPerRow) * height;

		g.drawImage(icon.getImage(), 
			x, y, x + width, y + height, 
			srcX, srcY, srcX + width, srcY + height, null);
	}

	public static ImageIcon getSubImage(ImageIcon icon, int imageNo, int index)
	{
		int width = DIMENSION[imageNo - 1][0];
		int height = DIMENSION[imageNo - 1][1];
		
		int imagesPerRow = icon.getIconWidth() / width;
		int srcX = (index % imagesPerRow) * width;
		int srcY = (index / imagesPerRow) * height;

		BufferedImage bufferedImage = new BufferedImage(
			width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		
		g.drawImage(icon.getImage(),
			0, 0,
			width, height,
			srcX, srcY, 
			srcX + width, srcY + height, null);
		
		return new ImageIcon(bufferedImage);
	}

	public static void drawSubImage(Graphics g, ImageIcon icon, int imageNo, int index, int x, int y)
	{
		int width = DIMENSION[imageNo - 1][0];
		int height = DIMENSION[imageNo - 1][1];
		
		int imagesPerRow = icon.getIconWidth() / width;
		int srcX = (index % imagesPerRow) * width;
		int srcY = (index / imagesPerRow) * height;

		g.drawImage(icon.getImage(), 
			x - width / 2, y - height / 2, 
			x + width / 2, y + height / 2, 
			srcX, srcY, 
			srcX + width, srcY + height, null);
	}
}
