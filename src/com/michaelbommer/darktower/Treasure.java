/*
 * 07/24/2002 - 20:37:20
 *
 * Treasure.java
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

public class Treasure extends ActionEvent
{
	private static final int[] allowedActions =
		{ Button.YES, Button.NO };
		
	public Treasure(DarkTowerThread thread)
	{
		super(thread);
	}

	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		int action = Button.NA;
		boolean found = false;
		double random = 0.0;

		int cursedPlayerNo = player.getPlayerNo();
		if ( player.getPlayerCount() > 1 )
		{
			do
			{
				cursedPlayerNo = player.getPlayerNo(cursedPlayerNo + 1);
				if ( player.getPlayerNo() == cursedPlayerNo )
					cursedPlayerNo = player.getPlayerNo(cursedPlayerNo + 1);
				thread.setCursedPlayerNo(cursedPlayerNo);
			}
			while ( Math.random() < 0.5 );
		}

		do
		{
			random = Math.random();

//			if ( (random >= 0.0) && (random < 0.05) )
//			{
//				// nothing
//				found = true;
//			}
			if ( (random >= 0.0) && (random < 0.3) )
			{
				// gold
				int gold = (int) (Math.random() * 11 + 10);
				player.setGold(player.getGold() + gold);
				player.getDisplayList().add(Image.GOLD);
				found = true;
			}
			if ( (random >= 0.3) && (random < 0.5) )
			{
				// dragon sword
				if ( !player.hasDragonSword() )
				{
					player.setDragonSword(true);
					player.getDisplayList().add(Image.DRAGONSWORD);
					found = true;
				}
			}
			if ( (random >= 0.5) && (random < 0.6) )
			{
				// wizard
				if ( player.getPlayerCount() > 1 )
				{
					int p = player.getPlayerNo();

					thread.setItemNo(Image.WIZARD);
					thread.paintDarkTower(player.getGold(),	Image.GOLD, Audio.BEEP);
					thread.sleep();
					thread.paintDarkTower("", Image.WIZARD, Audio.BEEP);
					thread.sleep(1500);
					do
					{
						p = player.getPlayerNo(p + 1);
						if ( player.getPlayerNo() == p )
							p = player.getPlayerNo(p + 1);
						thread.paintDarkTower("C" + Integer.toString(p + 1),
							Image.BLACK, Audio.NA);
						action = thread.waitAction(allowedActions, false, false);
					}
					while ( action != Button.YES );

					thread.setItemNo(Image.NA);
					thread.paintDarkTower("", Image.BLACK, Audio.NA);
					thread.sleep(1500);

					Player destPlayer = (Player) thread.getPlayerList().get(p);
					int warriors = (int) (Math.round(destPlayer.getWarriors() * 0.25));
					int gold = (int) (Math.round(destPlayer.getGold() * 0.25));
					if ( player.isPerformAction() )
					{
						destPlayer.setCursed(true);
						destPlayer.setWarriors(destPlayer.getWarriors() - warriors);
						destPlayer.setGold(destPlayer.getGold() - gold);
					}
					player.setWarriors(player.getWarriors() + warriors);
					player.setGold(player.getGold() + gold);
					player.getDisplayList().clear();
					player.getDisplayList().add(Image.WARRIOR);
					player.getDisplayList().add(Image.GOLD);
					found = true;
				}
			}
			if ( (random >= 0.6) && (random < 0.7) )
			{
				// pegasus
				if ( !player.hasPegasus() )
				{
					player.setPegasus(true);
					player.getDisplayList().add(Image.PEGASUS);
					found = true;
				}
			}
			if ( (random >= 0.7) && (random <= 1.0) )
			{
				// key
				switch ( player.getRelKingdomNo() )
				{
					case 0:
						break;
					case 1:
						if ( !player.hasBrassKey() )
						{
							player.setBrassKey(true);
							player.getDisplayList().add(Image.BRASSKEY);
							found = true;
						}
						break;
					case 2:
						if ( !player.hasSilverKey() )
						{
							player.setSilverKey(true);
							player.getDisplayList().add(Image.SILVERKEY);
							found = true;
						}
						break;
					case 3:
						if ( !player.hasGoldKey() )
						{
							player.setGoldKey(true);
							player.getDisplayList().add(Image.GOLDKEY);
							found = true;
						}
						break;
					default:
						break;
				}		
			}
		}
		while ( !found );

		// display items
		ActionEvent actionEvent = new Display(thread);
		actionEvent.run();
	}
}
