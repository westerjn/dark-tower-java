/*
 * 07/24/2002 - 20:37:20
 *
 * Tower.java
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

public class Tower extends ActionEvent
{
	private static final int[] allowedActions =
		{ Button.YES, Button.NO, Button.REPEAT, Button.CLEAR };

	public Tower(DarkTowerThread thread)
	{
		super(thread);
	}

	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		boolean wrongKey = false;
		int riddleKeyListPos = 0;
		int keyImage = Image.NA;
		int action = Button.NA;

		thread.paintDarkTower("", Image.BLACK, Audio.DARKTOWER);
		thread.sleep(3500);

		if ( ( player.hasBrassKey() ) && ( player.hasSilverKey() ) && 
		     ( player.hasGoldKey() ) )
		{
			if ( player.getRelKingdomNo() == 0 )
			{
				int[] list = player.getRiddleKeyList();

				while ( ( !wrongKey ) && ( riddleKeyListPos < 2 ) )
				{
					int keyNo = (int) (Math.random() * 3);
					do
					{
						switch ( keyNo )
						{
							case 0:
								keyImage = Image.BRASSKEY;
								break;
							case 1:
								keyImage = Image.SILVERKEY;
								break;
							case 2:
								keyImage = Image.GOLDKEY;
								break;
							default:
								break;
						}

						action = Button.NO;
						if ( ( riddleKeyListPos == 0 ) ||
							 ( list[riddleKeyListPos - 1] != keyImage ) )
						{
							do
							{
								thread.setRiddleyKeyListPos(riddleKeyListPos);
								thread.paintDarkTower("", keyImage, Audio.BEEP);
								thread.sleep(1500);
								thread.paintDarkTower(Integer.toString(riddleKeyListPos + 1), 
									Image.BLACK, Audio.NA);
								thread.setItemNo(keyImage);
								action = thread.waitAction(allowedActions, true, false);
								thread.setItemNo(Image.NA);
							}
							while ( action == Button.REPEAT );
						}

						keyNo = (keyNo + 1) % 3;
					}
					while ( action == Button.NO );

					if ( action != Button.CLEAR )
					{
						if ( ( action == Button.YES ) && 
							 ( list[riddleKeyListPos] == keyImage ) )
						{
							// right
							player.setRiddleKey(keyImage, action, true, riddleKeyListPos);
							riddleKeyListPos = riddleKeyListPos + 1;
						}
						else
						{
							// wrong
							player.setRiddleKey(keyImage, action, false, riddleKeyListPos);
							thread.paintDarkTower("", Image.BLACK, Audio.BAZAARCLOSED);
							thread.sleep();
							wrongKey = true;
						}
					}
					else
					{
						// clear
						thread.paintDarkTower("", Image.BLACK, Audio.CLEAR);
						thread.sleep();
						wrongKey = true;
					}
				}
				
				if ( !wrongKey )
				{
					// battle
					int brigands = 0;
					switch ( thread.getLevel() )
					{
						case 0:
							brigands = (int) (Math.random() * 16 + 17);
							break;
						case 1:
							brigands = (int) (Math.random() * 32 + 33);
							break;
						case 2:
							brigands = (int) (Math.random() * 48 + 17);
							break;
						case 3:
							brigands = 16;
							break;
						default:
							break;
					}
					thread.paintDarkTower("", Image.BLACK, Audio.BATTLE);
					thread.sleep();
					ActionEvent actionEvent = new Battle(thread, brigands, true);
					actionEvent.run();
				}
			}
			// wrong kingdom
			else
			{
				thread.paintDarkTower("", Image.BLACK, Audio.BAZAARCLOSED);
				thread.sleep();
				player.setMoveToPrevTerritory(true);
			}
		}
		// key missing
		else
		{
			thread.paintDarkTower("", Image.KEYMISSING, Audio.BAZAARCLOSED);
			thread.sleep();		
			player.setMoveToPrevTerritory(true);
		}

		// end turn
		thread.endTurn();
	}	
}
