/*
 * 07/24/2002 - 20:37:20
 *
 * Image.java
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

public class Image
{
	public static final int NA = 0;
	public static final int BAZAAR = 1;
	public static final int BEAST = 2;
	public static final int BLACK = 3;
	public static final int BRASSKEY = 4;
	public static final int BRIGANDS = 5;
	public static final int CURSED = 6;
	public static final int DRAGON = 7;
	public static final int FOOD = 8;
	public static final int GOLD = 9;
	public static final int GOLDKEY = 10;
	public static final int HEALER = 11;
	public static final int KEYMISSING = 12;
	public static final int LOST = 13;
	public static final int PEGASUS = 14;
	public static final int PLAGUE = 15;
	public static final int SCOUT = 16;
	public static final int SILVERKEY = 17;
	public static final int DRAGONSWORD = 18;
	public static final int VICTORY = 19;
	public static final int WARRIOR = 20;
	public static final int WARRIORS = 21;
	public static final int WIZARD = 22;
	
	public static final String[] IMAGE =
		{ "bazaar", "beast", "black", "brasskey", "brigands", 
		  "cursed", "dragon", "food", "gold", "goldkey", 
		  "healer", "keymissing", "lost", "pegasus", "plague",
		  "scout", "silverkey", "sword", "victory", "warrior",
		  "warriors", "wizard" };

	public static ImageIcon getImageIcon(int imageNo)
	{
		ImageIcon icon = null;
		
		if ( imageNo > NA )
			icon = new ImageIcon(
				Util.class.getResource("images/" + IMAGE[imageNo - 1] + ".jpg"));
		
		return icon;
	}
}
