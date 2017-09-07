/*
 * 07/24/2002 - 20:37:20
 *
 * Sanctuary.java
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

public class Citadel extends ActionEvent
{
	public Citadel(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		int warriors = (int) (Math.random() * 3 + 5);
		int gold = (int) (Math.random() * 6 + 10);
		int food = (int) (Math.random() * 6 + 10);
		
		thread.paintDarkTower("", Image.BLACK, Audio.SANCTUARY);
		thread.sleep(2500);

		if ( ( player.hasBrassKey() ) && 
			 ( player.hasSilverKey() ) && ( player.hasGoldKey() ) &&
			 ( player.getRelKingdomNo() == 0 ) &&
			 ( player.getLastBuildingNo() != Territory.SANCTUARY ) &&
			 ( player.getWarriors() >= 5 ) && ( player.getWarriors() <= 24 ) )
		{
			player.setWarriors(player.getWarriors() * 2);
			player.getDisplayList().add(Image.WARRIOR);
		}
		else
		{
			if ( player.getWarriors() <= 4 )
			{
				player.setWarriors(player.getWarriors() + warriors);
				player.getDisplayList().add(Image.WARRIOR);
			}
			if ( player.getGold() <= 7 )
			{
				player.setGold(player.getGold() + gold);
				player.getDisplayList().add(Image.GOLD);
			}
			if ( player.getFood() <= 5 )
			{
				player.setFood(player.getFood() + food);
				player.getDisplayList().add(Image.FOOD);
			}
		}

		// display items
		ActionEvent actionEvent = new Display(thread);
		actionEvent.run();

		// end turn
		thread.endTurn();
	}
}
