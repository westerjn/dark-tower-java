/*
 * 07/24/2002 - 20:37:20
 *
 * Display.java
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

public class Display extends ActionEvent
{
	private boolean repeat = false;

	public Display(DarkTowerThread thread)
	{
		super(thread);
	}

	public Display(DarkTowerThread thread, boolean repeat)
	{
		super(thread);
		this.repeat = repeat;
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().
			get(thread.getPlayerNo());
		IntegerList displayList = player.getDisplayList();
		
		for (int i = 0; i < displayList.size(); i++)
		{
			int imageNo = displayList.getIntValue(i);
			int audioNo = Audio.BEEP;

			switch ( imageNo )
			{
				case Image.WARRIOR:
					thread.paintDarkTower(player.getWarriors(),	Image.WARRIOR, audioNo);
					thread.sleep();
					break;
				case Image.GOLD:
					thread.paintDarkTower(player.getGold(),	Image.GOLD, audioNo);
					thread.sleep();
					break;
				case Image.FOOD:
					thread.paintDarkTower(player.getFood(),	Image.FOOD, audioNo);
					thread.sleep();
					break;
				case Image.BEAST:
					thread.paintDarkTower("", Image.BEAST, audioNo);
					thread.sleep();
					break;
				case Image.SCOUT:
					thread.paintDarkTower("", Image.SCOUT, audioNo);
					thread.sleep();
					break;
				case Image.HEALER:
					thread.paintDarkTower("", Image.HEALER, audioNo);
					thread.sleep();
					break;
				case Image.DRAGONSWORD:
					thread.paintDarkTower("", Image.DRAGONSWORD, audioNo);
					thread.sleep();
					break;
				case Image.PEGASUS:
					if ( !repeat )
						audioNo = Audio.PEGASUS;
					thread.paintDarkTower("", Image.PEGASUS, audioNo);
					thread.sleep();
					break;
				case Image.BRASSKEY:
					thread.paintDarkTower("", Image.BRASSKEY, audioNo);
					thread.sleep();
					break;
				case Image.SILVERKEY:
					thread.paintDarkTower("", Image.SILVERKEY, audioNo);
					thread.sleep();
					break;
				case Image.GOLDKEY:
					thread.paintDarkTower("", Image.GOLDKEY, audioNo);
					thread.sleep();
					break;
				default:
					break;
			}
		}
	}
}