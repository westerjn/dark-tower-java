/*
 * 07/24/2002 - 20:37:20
 *
 * Frontier.java
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

public class Frontier extends ActionEvent
{
	public Frontier(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());
		int relKingdomNo = player.getRelKingdomNo();

		thread.paintDarkTower("", Image.BLACK, Audio.FRONTIER);
		thread.sleep();

		// key missing?
		if ( ( ( relKingdomNo == 1 ) && ( !player.hasBrassKey() ) ) ||
			 ( ( relKingdomNo == 2 ) && ( !player.hasSilverKey() ) ) ||
			 ( ( relKingdomNo == 3 ) && ( !player.hasGoldKey() ) ) )
		{
			thread.paintDarkTower("", Image.KEYMISSING, Audio.BAZAARCLOSED);
			player.setMoveToPrevTerritory(true);
			thread.sleep();
		}
		else
		{
			player.setKingdomNo(player.getKingdomNo() + 1);
		}
			 
		// end turn
		thread.endTurn();
	}
}
