/*
 * 07/24/2002 - 20:37:20
 *
 * Button.java
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
import javax.swing.JButton;

public class Button
{
	public static final int NA = 0;
	public static final int YES = 1;
	public static final int REPEAT = 2;
	public static final int NO = 3;
	public static final int HAGGLE = 4;
	public static final int BAZAAR = 5;
	public static final int CLEAR = 6;
	public static final int RUIN = 7;
	public static final int MOVE = 8;
	public static final int SANCTUARY = 9;
	public static final int DARKTOWER = 10;
	public static final int FRONTIER = 11;
	public static final int INVENTORY = 12;
	public static final int NEW = 13;
	public static final int BOARD = 14;
	public static final int OPTIONS = 15;
	public static final int INVENTORIES = 16;
	public static final int EXIT = 17;
	public static final int ABOUT = 18;
	public static final int LEVEL = 19;
	public static final int BUILDING = 20;
	public static final int BATTLE = 21;
	public static final int CITADEL = 22;
	public static final int DISPLAY = 23;
	
	public static final String[] BUTTON =
		{ "Yes / Buy", "Repeat", "No / End",
		  "Haggle", "Bazaar", "Clear",
		  "Ruin", "Move", "Sanctuary",
		  "Dark Tower", "Frontier", "Inventory" };

	public static JButton getButton(int buttonNo)
	{
		JButton button = null;
		
		if ( buttonNo > NA )
			button = new JButton(BUTTON[buttonNo - 1]);
		
		return button;
	}

	public static int getCount()
	{
		return INVENTORY;
	}
}