/*
 * 07/24/2002 - 20:37:20
 *
 * Move.java
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

public class Move extends ActionEvent
{
	public Move(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		Dragon dragon = thread.getDragon();
		boolean battle = false;
		boolean extraturn = false;
		double random = Math.random();

		if ( (random >= 0.0) && (random < 0.5) )
		{
			// nothing
			thread.paintDarkTower(null, Image.BLACK, Audio.BEEP);
			thread.sleep(100);
		}
		if ( (random >= 0.5) && (random < 0.6) )
		{
			// dragon attack
			dragon.setEndTerritoryNo(player.getEndTerritoryNo());
			while ( dragon.getStartTerritoryNo() != dragon.getEndTerritoryNo() )
				thread.paintBoard(0);

			if ( player.hasDragonSword() )
			{
				thread.paintDarkTower("", Image.DRAGON, Audio.DRAGONKILL);
				thread.sleep(1100);

				player.setDragonSword(false);
				player.setWarriors(player.getWarriors() + dragon.getWarriors());
				player.setGold(player.getGold() + dragon.getGold());
				dragon.setWarriors(0);
				dragon.setGold(0);

				thread.paintDarkTower("", Image.DRAGONSWORD, Audio.NA);
				thread.sleep(3500);
				player.getDisplayList().add(Image.WARRIOR);
				player.getDisplayList().add(Image.GOLD);

				dragon.setStartTerritoryNo(0);
				dragon.setEndTerritoryNo(0);
				thread.paintBoard(0);
			}
			else
			{
				thread.paintDarkTower("", Image.DRAGON, Audio.DRAGON);
				thread.sleep();

				int warriors = (int) (Math.round(player.getWarriors() * 0.25));
				int gold = (int) (Math.round(player.getGold() * 0.25));
				player.setWarriors(player.getWarriors() - warriors);
				player.setGold(player.getGold() - gold);
				dragon.setWarriors(dragon.getWarriors() + warriors);
				dragon.setGold(dragon.getGold() + gold);

				player.getDisplayList().add(Image.WARRIOR);
				player.getDisplayList().add(Image.GOLD);
				player.setPlaceDragon(true);
			}
		}
		if ( (random >= 0.6) && (random < 0.7) )
		{
			// lost
			if ( player.hasScout() )
			{
				thread.paintDarkTower("", Image.LOST, Audio.BEEP);
				thread.sleep();
				thread.paintDarkTower("", Image.SCOUT, Audio.BEEP);
				thread.sleep();
				thread.getDarkTowerPanel().setFlash(true);
				thread.paintDarkTower(Integer.toString(thread.getPlayerNo() + 1),
					Image.BLACK, Audio.NA, true, false);
				thread.sleep();
				return;
			}
			else
			{
				thread.paintDarkTower("", Image.LOST, Audio.LOST);
				thread.sleep();
				player.setMoveToPrevTerritory(true);
			}
		}
		if ( (random >= 0.7) && (random < 0.8) )
		{
			// plague
			if ( player.hasHealer() )
			{
				thread.paintDarkTower("", Image.PLAGUE, Audio.BEEP);
				thread.sleep();
				thread.paintDarkTower("", Image.HEALER, Audio.BEEP);
				player.setWarriors(player.getWarriors() + 2);
				thread.sleep();
			}
			else
			{
				thread.paintDarkTower("", Image.PLAGUE, Audio.PLAGUE);
				thread.sleep(4000);
				player.setWarriors(player.getWarriors() - 2);
			}
			player.getDisplayList().add(Image.WARRIOR);
		}
		if ( (random >= 0.8) && (random <= 1.0) )
		{
			// battle
			battle = true;
			int brigands = thread.getBrigands(player.getWarriors());
			thread.paintDarkTower("", Image.BLACK, Audio.BATTLE);
			thread.sleep();
			ActionEvent actionEvent = new Battle(thread, brigands);
			actionEvent.run();
		}

		// display items
		if ( !battle )
		{
			ActionEvent actionEvent = new Display(thread);
			actionEvent.run();
		}

		// end turn
		thread.endTurn();
	}
}
