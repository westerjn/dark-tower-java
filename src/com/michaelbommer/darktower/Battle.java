/*
 * 07/24/2002 - 20:37:20
 *
 * Battle.java
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

public class Battle extends ActionEvent
{
	private static final int[] allowedActions =
		{ Button.NO };
		
	private int brigands = 0;
	private boolean finalBattle = false;
	
	public Battle(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public Battle(DarkTowerThread thread, int brigands)
	{
		super(thread);
		this.brigands = brigands;
	}

	public Battle(DarkTowerThread thread, int brigands, boolean finalBattle)
	{
		super(thread);
		this.brigands = brigands;
		this.finalBattle = finalBattle;
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		int audioNo = Audio.NA;
		int warriors = player.getWarriors();
		int action = Button.NO;
		int minWarriors = 0;
		int score = 0;
		boolean win = false;
		boolean loose = false;

		thread.paintDarkTower(brigands,	Image.BRIGANDS, Audio.BEEP);
		thread.sleep(1500);
		thread.paintDarkTower("", Image.BLACK, Audio.NA);
		thread.sleep(1000);
		thread.setImageNo(Image.BRIGANDS);
		thread.setWarriors(warriors);
		thread.setBrigands(brigands);

		if ( thread.getLevel() != 3 )
			score = 140 - ( player.getMoves() + player.getWarriors() );
		if ( score > 99 )
			score = 99;
		if ( score < 0 )
			score = 0;
		
		minWarriors = 1;
		if ( player.isSinglePlayer() )
			minWarriors = 0;

		if ( warriors > minWarriors + 1 )
		{
			do
			{
				action = thread.readAction(allowedActions);
				if ( action == Button.NA )
				{
					if ( Math.random() < thread.getWinningChance(warriors, brigands) )
					{
						// warriors win
						if ( brigands > 0 )
						{
							brigands = (int) (brigands / 2);
							audioNo = Audio.ENIMYHIT;
						}
						if ( brigands < 1 )
						{
							win = true;
						}
					}
					else
					{
						// brigands win
						if ( warriors > minWarriors )
						{
							warriors = warriors - 1;
							player.setWarriors(warriors);
							audioNo = Audio.PLAYERHIT;
						}
						if ( warriors < 1 + minWarriors * 2)
						{
							loose = true;
						}
					}
					thread.paintDarkTower(warriors,	Image.WARRIORS, Audio.BEEP);
					thread.sleep(1500);
					thread.paintDarkTower(brigands, Image.BRIGANDS, Audio.BEEP);
					thread.sleep(1500);
					thread.paintDarkTower("", Image.BLACK, audioNo);
					thread.sleep();
				}
			}
			while ( (!win) && (!loose) && (action == Button.NA) );
		}

		// retreat
		if ( ( loose ) || ( action == Button.NO ) )
		{
			warriors = warriors - 1;
			player.setWarriors(warriors);
		}

		// win
		if ( win )
		{
			if ( finalBattle )
			{
				thread.getDarkTowerPanel().setFlash(true);
				thread.paintDarkTower(score, Image.VICTORY, Audio.INTRO, true, true);
				while ( true )
					thread.sleep();
			}
			else
			{
				player.getDisplayList().add(Image.GOLD);
				ActionEvent actionEvent = new Treasure(thread);
				actionEvent.run();
			}
		}
		else
		{
			thread.paintDarkTower("", Image.WARRIORS, Audio.PLAGUE);
			thread.sleep(4000);
		}
	}
}
