/*
 * 07/24/2002 - 20:37:20
 *
 * Bazaar.java
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

public class Bazaar extends ActionEvent
{
	private static final int[] allowedHaggleActions =
		{ Button.YES, Button.NO, Button.REPEAT, Button.HAGGLE, Button.CLEAR };
	private static final int[] allowedBuyActions =
		{ Button.YES, Button.NO, Button.REPEAT, Button.CLEAR };
	private static final int[] allowedActions =
		{ Button.YES, Button.NO, Button.REPEAT, Button.CLEAR };

	public Bazaar(DarkTowerThread thread)
	{
		super(thread);
	}
	
	public void run()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = (Player) thread.getPlayerList().get(
			thread.getPlayerNo());

		boolean closed = false;
		boolean buyed = false;
		double random = 0.0;
		int action = Button.NA;
		int gold = player.getGold();
		int warriorsPrice = (int) (Math.random() * 7 + 4);
		int foodPrice = 1;
		int beastPrice = (int) (Math.random() * 11 + 15);
		int scoutPrice = (int) (Math.random() * 11 + 15);
		int healerPrice = (int) (Math.random() * 11 + 15);
		int requiredFood = 0;
		int timeToWait = 1500;
		int count = 0;

		requiredFood = player.requiredFood() * 5;
		if ( ( player.hasGoldKey() ) && ( player.getRelKingdomNo() == 0 ) )
			requiredFood = player.requiredFood() * 10;
		
		if ( player.isSinglePlayer() )
		{
			if ( ( !player.hasHealer() ) && ( player.getGold() >= healerPrice ) )
				thread.setItemNoToBuy(Image.HEALER);
			else if ( ( !player.hasBeast() ) && ( player.getGold() >= beastPrice ) )
				thread.setItemNoToBuy(Image.BEAST);
			else if ( ( !player.hasScout() ) && ( player.getGold() >= scoutPrice ) )
				thread.setItemNoToBuy(Image.SCOUT);
			else if ( player.getFood() <= requiredFood )
				thread.setItemNoToBuy(Image.FOOD);
			else
				thread.setItemNoToBuy(Image.WARRIOR);
		}
		else
		{
			if ( ( !player.hasBeast() ) && ( player.getGold() >= beastPrice ) )
				thread.setItemNoToBuy(Image.BEAST);
			else if ( ( !player.hasScout() ) && ( player.getGold() >= scoutPrice ) )
				thread.setItemNoToBuy(Image.SCOUT);
			else if ( ( !player.hasHealer() ) && ( player.getGold() >= healerPrice ) )
				thread.setItemNoToBuy(Image.HEALER);
			else if ( player.getFood() <= requiredFood )
				thread.setItemNoToBuy(Image.FOOD);
			else
				thread.setItemNoToBuy(Image.WARRIOR);
		}

		thread.paintDarkTower("", Image.BLACK, Audio.BAZAAR);
		thread.sleep(3000);
		do
		{
			// warriors
			// haggle
			do
			{
				thread.setPrice(warriorsPrice);
				thread.setItemNo(Image.WARRIOR);
				thread.paintDarkTower(warriorsPrice, Image.WARRIOR, Audio.BEEP);
				thread.sleep(timeToWait);
				thread.paintDarkTower("--", Image.BLACK, Audio.NA);
				action = thread.waitAction(allowedHaggleActions, true, false);
				if ( action == Button.HAGGLE )
				{
					if ( Math.random() >= 0.5 )
					{
						warriorsPrice = warriorsPrice - 1;
						if ( warriorsPrice < 4 )
							closed = true;
					}
					else
						closed = true;
				}
			}
			while ( ( !closed ) && ( action == Button.HAGGLE ) || ( action == Button.REPEAT ) );
			// buy
			while ( ( !closed ) && ( action == Button.YES ) || ( action == Button.REPEAT ) )
			{
				if ( action != Button.REPEAT )
				{
					if ( player.getGold() >= warriorsPrice )
					{
						count++;
						player.setWarriors(player.getWarriors() + 1);
						player.setGold(player.getGold() - warriorsPrice);
						thread.paintDarkTower(count, Image.BLACK, Audio.WRONG);
						thread.sleep(100);
						buyed = true;
					}
					else
					{
						player.setWarriors(player.getWarriors() - count);
						player.setGold(player.getGold() + warriorsPrice * count);
						closed = true;
					}
				}

				if ( !closed )
				{
					if ( !buyed )
						action = thread.waitAction(allowedBuyActions, true, false);
					else
						action = thread.waitAction(allowedActions, true, false);

					if ( action == Button.REPEAT )
					{
						thread.paintDarkTower(warriorsPrice, Image.WARRIOR, Audio.BEEP);
						thread.sleep(timeToWait);
						thread.paintDarkTower(count, Image.BLACK, Audio.NA);
					}
				}
			}
			
			// food
			// haggle
			if ( ( !closed ) && ( !buyed ) && 
				 ( action != Button.REPEAT ) && ( action != Button.CLEAR ) )
			{
				do
				{
					thread.setPrice(foodPrice);
					thread.setItemNo(Image.FOOD);
					thread.paintDarkTower(foodPrice, Image.FOOD, Audio.BEEP);
					thread.sleep(timeToWait);
					thread.paintDarkTower("--", Image.BLACK, Audio.NA);
					action = thread.waitAction(allowedHaggleActions, true, false);
					if ( action == Button.HAGGLE )
						closed = true;
				}
				while ( ( !closed ) && ( action == Button.HAGGLE ) || ( action == Button.REPEAT ) );
				// buy
				while ( ( !closed ) && ( action == Button.YES ) || ( action == Button.REPEAT ) )
				{
					if ( action != Button.REPEAT )
					{
						if ( player.getGold() >= foodPrice )
						{
							count++;
							player.setFood(player.getFood() + 1);
							player.setGold(player.getGold() - foodPrice);
							thread.paintDarkTower(count, Image.BLACK, Audio.WRONG);
							thread.sleep(100);
							buyed = true;
						}
						else
						{
							player.setFood(player.getFood() - count);
							player.setGold(player.getGold() + foodPrice * count);
							closed = true;
						}
					}

					if ( !closed )
					{
						if ( !buyed )
							action = thread.waitAction(allowedBuyActions, true, false);
						else
							action = thread.waitAction(allowedActions, true, false);

						if ( action == Button.REPEAT )
						{
							thread.paintDarkTower(foodPrice, Image.FOOD, Audio.BEEP);
							thread.sleep(timeToWait);
							thread.paintDarkTower(count, Image.BLACK, Audio.NA);
						}
					}
				}
			}

			// beast
			// haggle
			if ( ( !closed ) && ( !buyed ) && ( !player.hasBeast() ) &&
				 ( action != Button.CLEAR ) )
			{
				do
				{
					thread.setPrice(beastPrice);
					thread.setItemNo(Image.BEAST);
					thread.paintDarkTower(beastPrice, Image.BEAST, Audio.BEEP);
					thread.sleep(timeToWait);
					thread.paintDarkTower("--", Image.BLACK, Audio.NA);
					action = thread.waitAction(allowedHaggleActions, true, false);
					if ( action == Button.HAGGLE )
					{
						if ( Math.random() >= 0.5 )
						{
							beastPrice = beastPrice - 1;
							if ( beastPrice < 15 )
								closed = true;
						}
						else
							closed = true;
					}
					// buy
					if ( action == Button.YES )
					{
						if ( player.getGold() >= beastPrice )
						{
							player.setBeast(true);
							player.setGold(player.getGold() - beastPrice);
							action = Button.NO;
							buyed = true;
						}
						else
							closed = true;
					}
				}
				while ( ( !closed ) && ( !buyed ) && 
						( ( action == Button.HAGGLE ) || ( action == Button.YES ) || 
						  ( action == Button.REPEAT ) ) );
			}

			// scout
			// haggle
			if ( ( !closed ) && ( !buyed ) && ( !player.hasScout() ) &&
				 ( action != Button.CLEAR ) )
			{
				do
				{
					thread.setPrice(scoutPrice);
					thread.setItemNo(Image.SCOUT);
					thread.paintDarkTower(scoutPrice, Image.SCOUT, Audio.BEEP);
					thread.sleep(timeToWait);
					thread.paintDarkTower("--", Image.BLACK, Audio.NA);
					action = thread.waitAction(allowedHaggleActions, true, false);
					if ( action == Button.HAGGLE )
					{
						if ( Math.random() >= 0.5 )
						{
							scoutPrice = scoutPrice - 1;
							if ( scoutPrice < 15 )
								closed = true;
						}
						else
							closed = true;
					}
					// buy
					if ( action == Button.YES )
					{
						if ( player.getGold() >= scoutPrice )
						{
							player.setScout(true);
							player.setGold(player.getGold() - scoutPrice);
							action = Button.NO;
							buyed = true;
						}
						else
							closed = true;
					}
				}
				while ( ( !closed ) && ( !buyed ) && 
						( ( action == Button.HAGGLE ) || ( action == Button.YES ) ||
						  ( action == Button.REPEAT ) ) );
			}

			// healer
			// haggle
			if ( ( !closed ) && ( !buyed ) && ( !player.hasHealer() ) &&
				 ( action != Button.CLEAR ) )
			{
				do
				{
					thread.setPrice(healerPrice);
					thread.setItemNo(Image.HEALER);
					thread.paintDarkTower(healerPrice, Image.HEALER, Audio.BEEP);
					thread.sleep(timeToWait);
					thread.paintDarkTower("--", Image.BLACK, Audio.NA);
					action = thread.waitAction(allowedHaggleActions, true, false);
					if ( action == Button.HAGGLE )
					{
						if ( Math.random() >= 0.5 )
						{
							healerPrice = healerPrice - 1;
							if ( healerPrice < 15 )
								closed = true;
						}
						else
							closed = true;
					}
					// buy
					if ( action == Button.YES )
					{
						if ( player.getGold() >= healerPrice )
						{
							player.setHealer(true);
							player.setGold(player.getGold() - healerPrice);
							action = Button.NO;
							buyed = true;
						}
						else
							closed = true;
					}
				}
				while ( ( !closed ) && ( !buyed ) && 
						( ( action == Button.HAGGLE ) || ( action == Button.YES ) ||
						  ( action == Button.REPEAT ) ) );
			}
		}
		while ( ( !closed ) && ( !buyed ) && 
				( ( action == Button.REPEAT ) || ( action == Button.NO ) ) );

		thread.setItemNo(Image.NA);

		if ( closed )
		{
			thread.paintDarkTower("", Image.BAZAAR, Audio.BAZAARCLOSED);
			thread.sleep(timeToWait);
		}
		else if ( ( buyed ) && ( action != Button.CLEAR ) )
		{
			player.getDisplayList().add(Image.GOLD);
			// display items
			ActionEvent actionEvent = new Display(thread);
			actionEvent.run();
		}

		// end turn
		thread.endTurn(action);
	}
}