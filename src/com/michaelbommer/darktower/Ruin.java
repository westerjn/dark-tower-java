/*
 * 07/24/2002 - 20:37:20
 *
 * Ruin.java
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

public class Ruin extends ActionEvent
{
	public Ruin(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		double random = Math.random();

		if ( (random >= 0.0) && (random < 0.2) )
		{
			// nothing
			thread.paintDarkTower("", Image.BLACK, Audio.TOMBNOTHING);
			thread.sleep(9000);
		}
		if ( (random >= 0.2) && (random < 0.3) )
		{
			// treasure
			thread.paintDarkTower("", Image.BLACK, Audio.TOMB);
			thread.sleep(4200);
			ActionEvent actionEvent = new Treasure(thread);
			actionEvent.run();
		}
		if ( (random >= 0.3) && (random <= 1.0) )
		{
			// battle
			int brigands = thread.getBrigands(player.getWarriors());
			thread.paintDarkTower("", Image.BLACK, Audio.TOMBBATTLE);
			thread.sleep(6000);
			ActionEvent actionEvent = new Battle(thread, brigands);
			actionEvent.run();
		}

		// end turn
		thread.endTurn();
	}
}
