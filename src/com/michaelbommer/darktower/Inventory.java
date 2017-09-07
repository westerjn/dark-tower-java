/*
 * 07/24/2002 - 20:37:20
 *
 * Inventory.java
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

public class Inventory extends ActionEvent
{
	public Inventory(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().
			get(thread.getPlayerNo());

		player.getDisplayList().add(Image.WARRIOR);
		player.getDisplayList().add(Image.GOLD);
		player.getDisplayList().add(Image.FOOD);
		if ( player.hasBeast() )
			player.getDisplayList().add(Image.BEAST);
		if ( player.hasScout() )
			player.getDisplayList().add(Image.SCOUT);
		if ( player.hasHealer() )
			player.getDisplayList().add(Image.HEALER);
		if ( player.hasDragonSword() )
			player.getDisplayList().add(Image.DRAGONSWORD);
		if ( player.hasPegasus() )
			player.getDisplayList().add(Image.PEGASUS);
		if ( player.hasBrassKey() )
			player.getDisplayList().add(Image.BRASSKEY);
		if ( player.hasSilverKey() )
			player.getDisplayList().add(Image.SILVERKEY);
		if ( player.hasGoldKey() )
			player.getDisplayList().add(Image.GOLDKEY);

		// display items
		ActionEvent actionEvent = new Display(thread, true);
		actionEvent.run();

		// end turn
		thread.endTurn();
	}
}