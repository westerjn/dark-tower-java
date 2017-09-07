/*
 * 07/24/2002 - 20:37:20
 *
 * DarkTowerThread.java
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

import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import java.applet.AudioClip;
import java.awt.Polygon;
import javax.swing.ImageIcon;

public class DarkTowerThread extends Thread
{
	private static final int[] allowedActions =
		{ Button.NO, Button.REPEAT, Button.CLEAR };

	private DarkTower darkTower = null;
	private BoardPanel boardPanel = null;
	private List territoryList = null;
	private List playerList = null;
	private ArrayList actionQueue = null;
	private ArrayList mouseActionQueue = null;
	private String label = null;
	private ActionEvent actionEvent = null;
	private int playerNo = 0;
	private int imageNo = Image.NA;
	private int audioNo = 0;
	private int price = 0;
	private int itemNo = Image.NA;
	private int itemNoToBuy = Image.NA;
	private int cursedPlayerNo = 0;
	private int riddleKeyListPos = 0;
	private int warriors = 0;
	private int brigands = 0;
	private Dragon dragon = null;
	private Dragon lastDragon = null;
	private boolean reset = false;
	private AudioClip audio = null;

	public DarkTowerThread(DarkTower darkTower)
	{
		this.darkTower = darkTower;
		this.playerList = darkTower.getPlayerList();
		this.boardPanel = darkTower.getBoardPanel();
		this.territoryList = boardPanel.getTerritoryList();
		initQueues();
		initPlayerList();
	}

	public void initQueues()
	{
		actionQueue = new ArrayList();
		mouseActionQueue = new ArrayList();
		dragon = new Dragon();
		lastDragon = new Dragon();

		playerNo = 0;
		reset = false;
	}

	public void initTerritoryPlaces()
	{
		Player player = null;

		boardPanel.createTerritoryPlaces(darkTower.placeBuildingsRandomly());
		for (int i = 0; i < 4; i++)
		{
			player = (Player) playerList.get(i);
			playerList.set(i, new Player(i, playerList, territoryList,
				player.isEnable(), (getLevel() == 3), player.getPlayerType()));
		}
	}

	public void initPlayerList()
	{
		setPlayerNo(0);
		boardPanel.setPlayerList(playerList);
		boardPanel.setPlayerNo(playerNo);

		paintDarkTowerEx(Integer.toString(playerNo + 1),
			Image.BLACK, Audio.NA, true, false);
		paintBoardEx(0);
	}

	public void init()
	{
		initQueues();
		initTerritoryPlaces();
		initPlayerList();
		setInventory();
	}

	public void run()
	{
		try
		{
			while ( !interrupted() )
			{
				Player player = (Player) playerList.get(playerNo);
				getDarkTowerPanel().setFlash(true);

				try
				{
					if ( !player.isEnable() )
						throw new DisableException();

					storePlayers();
					if ( !isBoardVisible() )
					{
						if ( !isActionQueueEmpty() )
							performAction(getAction());
						else if ( reset )
							init();
					}
					else
					{
						// none pc
						if ( player.getPlayerType() == Player.NONEPC )
						{
							if ( !isMouseActionQueueEmpty() )
								performMouseAction(getMouseAction());
							else if ( !isActionQueueEmpty() )
								performAction(getAction());
							else if ( reset )
								init();
						}
						// pc
						else
						{
							pcRun();
						}
					}
				}
				catch ( DisableException e )
				{
					int territoryNo = player.getEndTerritoryNo();
					int dragonTerritoryNo = dragon.getEndTerritoryNo();

					restorePlayers();
					player = (Player) playerList.get(playerNo);

					if ( territoryNo != 0 )
						player.setStartTerritoryNo(territoryNo);
					if ( dragon.getEndTerritoryNo() != 0 )
						dragon.setStartTerritoryNo(dragonTerritoryNo);
					do
					{
						paintBoardEx(0);
					}
					while ( ( player.getStartTerritoryNo() != player.getEndTerritoryNo() ) ||
							( dragon.getStartTerritoryNo() != dragon.getEndTerritoryNo() ) );

					player.setLastTerritoryNo(player.getEndTerritoryNo());

					setInventory();
					getBoardPanel().setPlayerNo(playerNo + 1);
					setPlayerNo(playerNo + 1);
					paintDarkTowerEx(Integer.toString(playerNo + 1), 
						Image.BLACK, Audio.NA, true, false);
				}
				catch ( ResetException e )
				{
					init();
				}
			}
		}
		catch ( InterruptedException e )
		{}
	}
	
	public void pcRun()
		throws InterruptedException, ResetException, DisableException
	{
		int territoryNo = getPCTerritoryNo();
		Territory territory = (Territory) territoryList.get(territoryNo - 1);

		Player player = getPlayerListItem(playerNo);
		Search search = new Search();
		player.setDestTerritoryNo(territoryNo);

		if ( player.hasPegasus() )
		{
			if ( player.isUsePegasus() )
				player.setPegasus(false);
			else
			{
				if ( hasToUsePegasus(territoryNo) )
				{
					if ( territory.getType() == Territory.FRONTIER )
						player.setUsePegasus(true);
					else
						player.setPegasus(false);
				}
			}
		}
		else
		{
			// System.out.print("P" + (player.getPlayerNo() + 1) + ": from " + player.getEndTerritoryNo() + " to " + territoryNo);
			getBoardPanel().deepFirstSearch(
				search, player.getEndTerritoryNo(), territoryNo);
			territoryNo = search.getTerritoryNo();
			// System.out.println(" step " + territoryNo);
		}
		player.setEndTerritoryNo(territoryNo);
		do
		{
			paintBoard(0);
		}
		while ( player.getStartTerritoryNo() != player.getEndTerritoryNo() );
		performTerritoryAction(playerNo);
	}
	
	public int getPCTerritoryNo()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);
		Territory territory = (Territory) territoryList.get(player.getEndTerritoryNo() - 1);
		Search search = new Search();
		int territoryNo = 0;

		if ( player.isSinglePlayer() )
		{
			if ( ( !player.hasHealer() ) && ( player.getGold() >= 25 ) )
				return Territory.BAZAARLIST[player.getKingdomNo()];
		}
		else
		{
			if ( ( !player.hasBeast() ) && ( player.getGold() >= 25 ) )
				return Territory.BAZAARLIST[player.getKingdomNo()];
		}

		if ( ( ( !player.hasBrassKey() ) && ( player.getRelKingdomNo() == 0 ) ) ||
			 ( ( player.hasBrassKey() ) && ( player.getRelKingdomNo() == 1 ) ) ||
			 ( ( player.hasSilverKey() ) && ( player.getRelKingdomNo() == 2 ) ) ||
			 ( ( player.hasGoldKey() ) && ( player.getRelKingdomNo() == 3 ) ) )
		{
			if ( ( !player.isSinglePlayer() ) || ( player.getWarriors() > 4 ) )
			{
				if ( territory.getType() != Territory.FRONTIER )
					return Territory.FRONTIERLIST[player.getKingdomNo()];
			}
		}

		if ( ( player.getWarriors() <= 4 ) || ( player.getGold() <= 7 ) ||
			 ( player.getFood() <= 5 ) )
		{
			if ( player.getGold() >= 30 )
				return Territory.BAZAARLIST[player.getKingdomNo()];
			else
			{
				getBoardPanel().deepFirstSearch(search, 
					player.getEndTerritoryNo(), Territory.SANCTUARYLIST[player.getKingdomNo()]);
				int sanctuaryDistance = search.getDistance();
				getBoardPanel().deepFirstSearch(search, 
					player.getEndTerritoryNo(), Territory.CITADELLIST[player.getKingdomNo()]);
				int citadelDistance = search.getDistance();

				int fromSanctuaryDistance = 0;
				int fromCitadelDistance = 0;
				if ( player.isPerformAction() )
				{
					Dragon dragonClone = (Dragon) dragon.clone();
					Player playerClone = (Player) player.clone();
					territoryNo = Territory.SANCTUARYLIST[player.getKingdomNo()];
					player.setPerformAction(false);
					player.setStartTerritoryNo(territoryNo);
					player.setEndTerritoryNo(territoryNo);
					performTerritoryAction(playerNo);

					territoryNo = getPCTerritoryNo();
					getBoardPanel().deepFirstSearch(search, 
						Territory.SANCTUARYLIST[player.getKingdomNo()], territoryNo);
					fromSanctuaryDistance = search.getDistance();
					getBoardPanel().deepFirstSearch(search, 
						Territory.CITADELLIST[player.getKingdomNo()], territoryNo);
					fromCitadelDistance = search.getDistance();

					dragon = dragonClone;
					player = playerClone;
					setPlayerListItem(playerNo, player);
					boardPanel.setDragon(dragon);
				}

				if ( ( sanctuaryDistance + fromSanctuaryDistance > citadelDistance + fromCitadelDistance ) &&
					 ( player.getRelKingdomNo() == 0 ) )
					return Territory.CITADELLIST[player.getKingdomNo()];
				return Territory.SANCTUARYLIST[player.getKingdomNo()];
			}
		}

		if ( ( player.hasGoldKey() ) && ( player.getRelKingdomNo() == 0 ) )
		{
			if( player.getWarriors() >= getMinBrigands() )
				return Territory.DARKTOWERLIST[player.getKingdomNo()];
			else if ( player.getLastBuildingNo() != Territory.SANCTUARY )
				return Territory.CITADELLIST[player.getKingdomNo()];
			else if ( ( player.getFood() < player.requiredFood() * 10 ) &&
					  ( player.getGold() >= player.requiredFood() * 10 ) )
				return Territory.BAZAARLIST[player.getKingdomNo()];
		}

		if ( player.getGold() >= 25 )
			return Territory.BAZAARLIST[player.getKingdomNo()];

		getBoardPanel().deepFirstSearch(search, 
			player.getEndTerritoryNo(), Territory.RUINLIST[player.getKingdomNo()]);
		int ruinDistance = search.getDistance();
		getBoardPanel().deepFirstSearch(search, 
			player.getEndTerritoryNo(), Territory.TOMBLIST[player.getKingdomNo()]);
		int tombDistance = search.getDistance();

		int fromRuinDistance = 0;
		int fromTombDistance = 0;
		if ( player.isPerformAction() )
		{
			Dragon dragonClone = (Dragon) dragon.clone();
			Player playerClone = (Player) player.clone();
			territoryNo = Territory.RUINLIST[player.getKingdomNo()];
			player.setPerformAction(false);
			player.setStartTerritoryNo(territoryNo);
			player.setEndTerritoryNo(territoryNo);
			performTerritoryAction(playerNo);

			territoryNo = getPCTerritoryNo();
			getBoardPanel().deepFirstSearch(search, 
				Territory.RUINLIST[player.getKingdomNo()], territoryNo);
			fromRuinDistance = search.getDistance();
			getBoardPanel().deepFirstSearch(search, 
				Territory.TOMBLIST[player.getKingdomNo()], territoryNo);
			fromTombDistance = search.getDistance();

			dragon = dragonClone;
			player = playerClone;
			setPlayerListItem(playerNo, player);
			boardPanel.setDragon(dragon);
		}
		
		if ( ruinDistance + fromRuinDistance > tombDistance + fromTombDistance)
			return Territory.TOMBLIST[player.getKingdomNo()];
		return Territory.RUINLIST[player.getKingdomNo()];
	}

	public void endTurn()
		throws InterruptedException, ResetException, DisableException
	{
		endTurn(Button.NA);
	}
	
	public void endTurn(int action)
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);

		if ( player.isPerformAction() )
		{
			if ( player.isMoveToPrevTerritory() )
			{
				player.setMoveToPrevTerritory(false);
				if ( player.getLastTerritoryNo() != 0 )
					player.setEndTerritoryNo(player.getLastTerritoryNo());
				do
				{
					paintBoard(0);
				}
				while ( player.getStartTerritoryNo() != player.getEndTerritoryNo() );
			}

			// loose?
			if ( player.getWarriors() < 1 )
			{
				getDarkTowerPanel().setFlash(true);
				paintDarkTower("00", Image.BLACK, Audio.NA, true, false);
				while ( true )
					sleep();
			}

			if ( action != Button.CLEAR )
			{
				do
				{
					getDarkTowerPanel().setFlash(true);
					paintDarkTower("-" + Integer.toString(playerNo + 1), 
						Image.BLACK, Audio.NA);
					if ( player.isPlaceDragon() )
						placeDragon();
					action = waitAction(allowedActions, true, false);
					if ( action == Button.REPEAT )
					{
						// display items
						ActionEvent actionEvent = new Display(this, true);
						actionEvent.run();
						if ( player.getDisplayList().size() == 0 )
						{
							paintDarkTower(null, Image.BLACK, Audio.BEEP);
							sleep(100);
						}
					}
				}
				while ( action == Button.REPEAT );
			}

			if ( action == Button.CLEAR )
			{
				int territoryNo = player.getEndTerritoryNo();
				int dragonTerritoryNo = dragon.getEndTerritoryNo();

				restorePlayers();
				player = (Player) playerList.get(playerNo);

				paintDarkTower("", Image.BLACK, Audio.CLEAR);
				player = getPlayerListItem(playerNo);
				if ( territoryNo != 0 )
					player.setStartTerritoryNo(territoryNo);
				if ( dragon.getEndTerritoryNo() != 0 )
					dragon.setStartTerritoryNo(dragonTerritoryNo);
				do
				{
					paintBoard(0);
				}
				while ( ( player.getStartTerritoryNo() != player.getEndTerritoryNo() ) ||
						( dragon.getStartTerritoryNo() != dragon.getEndTerritoryNo() ) );
				sleep();
			}

			player.setLastTerritoryNo(player.getEndTerritoryNo());

			if ( action == Button.NO )
			{
				paintDarkTower("", Image.BLACK, Audio.ENDTURN, true, true);
				sleep(1000);
			}

			getBoardPanel().setPlayerNo(playerNo + 1);
			setPlayerNo(playerNo + 1);
			resetMouseActionQueue();
			resetActionQueue();
			paintDarkTower(Integer.toString(playerNo + 1), 
				Image.BLACK, Audio.NA, true, false);
			sleep(500);
		}
	}

	//
	// verify player position
	//
	public void verifyPlayerPosition(int action)
		throws ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);
		Territory territory = (Territory) territoryList.get(player.getEndTerritoryNo() - 1);
		int territoryNo = player.getEndTerritoryNo();
		int kingdomNo = player.getKingdomNo();

		switch ( action )
		{
			case Button.BAZAAR:
				if ( territory.getType() != Territory.BAZAAR )
					territoryNo =Territory.BAZAARLIST[kingdomNo];
				break;
			case Button.RUIN:
				if ( ( territory.getType() != Territory.RUIN ) &&
					 ( territory.getType() != Territory.TOMB ) )
					territoryNo =Territory.RUINLIST[kingdomNo];
				break;
			case Button.SANCTUARY:
				if ( ( territory.getType() != Territory.SANCTUARY ) &&
					 ( territory.getType() != Territory.CITADEL ) )
					territoryNo =Territory.SANCTUARYLIST[kingdomNo];
				break;
			case Button.DARKTOWER:
				if ( territory.getType() != Territory.DARKTOWER )
					territoryNo =Territory.DARKTOWERLIST[kingdomNo];
				break;
			case Button.FRONTIER:
				if ( ( territory.getType() != Territory.FRONTIER ) ||
					 ( Territory.FRONTIERLIST[kingdomNo] != territoryNo ) )
					territoryNo =Territory.FRONTIERLIST[kingdomNo];
				break;
			default:
				break;
		}

		if ( player.getEndTerritoryNo() != territoryNo )
		{
			player.setEndTerritoryNo(territoryNo);
			do
			{
				paintBoard(0);
			}
			while ( player.getStartTerritoryNo() != player.getEndTerritoryNo() );
		}
	}

	//
	// perform action
	//
	public void performAction(int action)
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);
		player.getDisplayList().clear();
		getDarkTowerPanel().setFlash(false);

		int food = player.getFood();
		player.setMoves(player.getMoves() + 1);
		player.consumeFood();
		// player has enough food?
		if ( player.getFood() < (player.requiredFood() * 4) )
		{
			switch ( action )
			{
				case Button.BAZAAR:
				case Button.RUIN:
				case Button.MOVE:
				case Button.SANCTUARY:
				case Button.CITADEL:
				case Button.DARKTOWER:
				case Button.FRONTIER:
				case Button.INVENTORY:
					if ( food < player.requiredFood() )
					{
						paintDarkTower("", Image.BLACK, Audio.PLAGUE);
						sleep(3500);
					}
					else
					{
						paintDarkTower("", Image.BLACK, Audio.STARVING);
						sleep();
					}
				default:
					break;
			}
		}
		
		// player cursed?
		if ( player.isCursed() )
		{
			player.setCursed(false);
			paintDarkTower("", Image.CURSED, Audio.PLAGUE);
			sleep(3000);
			
			player.getDisplayList().add(Image.WARRIOR);
			player.getDisplayList().add(Image.GOLD);
			// display items
			ActionEvent actionEvent = new Display(this);
			actionEvent.run();
			
			player.setMoveToPrevTerritory(true);
			endTurn();
			return;
		}

		// loose?
		if ( player.getWarriors() < 1 )
		{
			endTurn();
			return;
		}

		verifyPlayerPosition(action);

		switch ( action )
		{
			case Button.BAZAAR:
				actionEvent = new Bazaar(this);
				actionEvent.run();
				player.setLastBuildingNo(Territory.BAZAAR);
				break;
			case Button.RUIN:
				actionEvent = new Ruin(this);
				actionEvent.run();
				player.setLastBuildingNo(Territory.RUIN);
				break;
			case Button.MOVE:
				actionEvent = new Move(this);
				actionEvent.run();
				break;
			case Button.SANCTUARY:
				actionEvent = new Sanctuary(this);
				actionEvent.run();
				player.setLastBuildingNo(Territory.SANCTUARY);
				break;
			case Button.CITADEL:
				actionEvent = new Citadel(this);
				actionEvent.run();
				player.setLastBuildingNo(Territory.SANCTUARY);
				break;
			case Button.DARKTOWER:
				actionEvent = new Tower(this);
				actionEvent.run();
				player.setLastBuildingNo(Territory.DARKTOWER);
				break;
			case Button.FRONTIER:
				actionEvent = new Frontier(this);
				actionEvent.run();
				player.setLastBuildingNo(Territory.FRONTIER);
				break;
			case Button.INVENTORY:
				actionEvent = new Inventory(this);
				actionEvent.run();
				break;
			default:
				play(Audio.WRONG);
				sleep(100);
				break;
		}
	}

	//
	// perform mouse action
	//
	public void performMouseAction(MouseAction action)
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);
		Territory territory = null;
		int territoryNo = 0;

		if ( action != null )
		{
			switch ( action.getType() )
			{
				case MouseAction.EXITED:
					paintBoard(0);
					break;
				case MouseAction.MOVED:
					territoryNo = findTerritoryNo(action.getX(), action.getY());
					if ( isNeigbor(territoryNo) )
						paintBoard(territoryNo);
					else
						paintBoard(0);
					break;
				case MouseAction.CLICKED:
					territoryNo = findTerritoryNo(action.getX(), action.getY());
					territory = (Territory) territoryList.get(territoryNo - 1);
					if ( isNeigbor(territoryNo) )
					{
						if ( player.isUsePegasus() )
							player.setPegasus(false);
						else
						{
							if ( hasToUsePegasus(territoryNo) )
							{
								if ( territory.getType() == Territory.FRONTIER )
									player.setUsePegasus(true);
								else
									player.setPegasus(false);
							}
						}
						player.setEndTerritoryNo(territoryNo);
						do
						{
							paintBoard(0);
						}
						while ( player.getStartTerritoryNo() != player.getEndTerritoryNo() );
						performTerritoryAction(playerNo);
					}
					break;
				default:
					break;
			}
		}
	}

	public void performTerritoryAction(int playerNo)
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);
		Territory territory = (Territory) territoryList.get(player.getEndTerritoryNo() - 1);

		switch ( territory.getType() )
		{
			case Territory.BAZAAR:
				performAction(Button.BAZAAR);
				break;
			case Territory.RUIN:
			case Territory.TOMB:
				performAction(Button.RUIN);
				break;
			case Territory.SANCTUARY:
				performAction(Button.SANCTUARY);
				break;
			case Territory.CITADEL:
				performAction(Button.CITADEL);
				break;
			case Territory.DARKTOWER:
				performAction(Button.DARKTOWER);
				break;
			case Territory.FRONTIER:
				performAction(Button.FRONTIER);
				break;
			default:
				performAction(Button.MOVE);
				break;
		}
	}

	//
	// place dragon
	//
	public void placeDragon()
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);
		Dragon dragon = getDragon();
		int territoryNo = 0;

		player.setPlaceDragon(false);

		if ( !isBoardVisible() )
			return;

		// none pc
		if ( player.getPlayerType() == Player.NONEPC )
		{
			while ( true )
			{
				if ( reset )
					throw new ResetException();
				if ( !player.isEnable() )
					throw new DisableException();
				if ( ( player.getPlayerCount() < 2 ) || ( isBoardIcon() ) )
				{
					if ( dragon.getEndTerritoryNo() != player.getEndTerritoryNo() )
					{
						dragon.setEndTerritoryNo(player.getEndTerritoryNo());
						while ( dragon.getStartTerritoryNo() != dragon.getEndTerritoryNo() )
							paintBoard(0);
						paintDarkTower("-" + Integer.toString(playerNo + 1), 
							Image.BLACK, Audio.BEEP);
						sleep(100);
					}
					break;
				}
				if ( player.getPlayerType() == Player.PC )
				{
					pcPlaceDragon();
					break;
				}
				if ( !isMouseActionQueueEmpty() )
				{
					MouseAction action = getMouseAction();
					if ( action != null )
					{
						if ( action.getType() == MouseAction.MOVED )
						{
							do
							{
								territoryNo = findTerritoryNo(action.getX(), action.getY());
								if ( !isMouseAction(MouseAction.MOVED) )
									break;
								if ( !isMouseActionQueueEmpty() )
									action = getMouseAction();
							}
							while ( !isMouseActionQueueEmpty() );

							if ( isAllowedDragonTerritory(territoryNo) )
							{
								dragon.setEndTerritoryNo(territoryNo);
								while ( dragon.getStartTerritoryNo() != dragon.getEndTerritoryNo() )
									paintBoard(0);
							}
						}
						else if ( action.getType() == MouseAction.CLICKED )
						{
							paintDarkTower("-" + Integer.toString(playerNo + 1), 
								Image.BLACK, Audio.BEEP);
							sleep(100);
							break;
						}
					}
				}
				else if ( !isActionQueueEmpty() )
				{
					int action = getAction();
					paintDarkTower("-" + Integer.toString(playerNo + 1), 
						Image.BLACK, Audio.WRONG);
					sleep(100);
				}
			}
		}
		// pc
		else
			pcPlaceDragon();
	}

	public void pcPlaceDragon()
		throws InterruptedException, ResetException, DisableException
	{
		Search search = new Search();
		Player player = getPlayerListItem(playerNo);
		int victimPlayerNo = playerNo;
		int currentPlayerNo = playerNo;
		int victimKingdomNo = 0;
		int victimStartTerritoryNo = 0;
		int victimEndTerritoryNo = 0;
		int victimTerritoryNo = 0;

		if ( player.getPlayerCount() > 1 )
		{
			do
			{
				victimPlayerNo = player.getPlayerNo(victimPlayerNo + 1);
				if ( player.getPlayerNo() == victimPlayerNo )
					victimPlayerNo = player.getPlayerNo(victimPlayerNo + 1);
			}
			while ( Math.random() < 0.5 );
			
			setPlayerNo(victimPlayerNo);
			Player victimPlayer = getPlayerListItem(victimPlayerNo);
			victimKingdomNo = victimPlayer.getKingdomNo();
			victimStartTerritoryNo = victimPlayer.getEndTerritoryNo();
			victimEndTerritoryNo = victimPlayer.getEndTerritoryNo();
			victimTerritoryNo = getPCTerritoryNo();

			do
			{
				getBoardPanel().deepFirstSearch(
					search, victimEndTerritoryNo, victimTerritoryNo);
				victimTerritoryNo = search.getTerritoryNo();
				if ( victimTerritoryNo == victimEndTerritoryNo )
					victimTerritoryNo = getAllowedDragonTerritory(victimKingdomNo, 
						victimStartTerritoryNo);
				victimEndTerritoryNo = victimTerritoryNo;
			}
			while ( !isAllowedDragonTerritory(victimTerritoryNo) );

			setPlayerNo(currentPlayerNo);
			dragon.setEndTerritoryNo(victimTerritoryNo);
			while ( dragon.getStartTerritoryNo() != dragon.getEndTerritoryNo() )
				paintBoard(0);
			paintDarkTower("-" + Integer.toString(playerNo + 1), 
				Image.BLACK, Audio.BEEP);
			sleep(100);
		}
	}

	//
	// action queue functions
	//
	public void addAction(int action)
	{
		Player player = getPlayerListItem(playerNo);
		// none pc
		if ( ( player.getPlayerType() == Player.NONEPC ) || ( !isBoardVisible() ) )
			actionQueue.add(new Integer(action));
	}

	public int getAction()
	{
		int action = 0;
		
		if ( !isActionQueueEmpty() )
		{
			action = ((Integer) actionQueue.get(0)).intValue();
			actionQueue.remove(0);
		}
		
		return action;
	}

	public int waitAction(int[] allowedActions)
		throws InterruptedException, ResetException, DisableException
	{
		return waitAction(allowedActions, true, true);
	}

	public int waitAction(int[] allowedActions, boolean muteAllowed, boolean muteWrong)
		throws InterruptedException, ResetException, DisableException
	{
		int action = Button.NA;
		Player player = getPlayerListItem(playerNo);
		getDarkTowerPanel().setFlash(true);

		while ( true )
		{
			// none pc
			if ( ( player.getPlayerType() == Player.NONEPC ) || ( !isBoardVisible() ) )
			{
				if ( !isActionQueueEmpty() )
				{
					action = getAction();
					if ( Util.contains(allowedActions, action) )
					{
						if ( !muteAllowed )
						{
							play(Audio.BEEP);
							sleep(100);
						}
						break;
					}
					if ( !muteWrong )
					{
						play(Audio.WRONG);
						sleep(100);
					}
				}
			}
			// pc
			else
			{
				action = pcWaitAction(allowedActions);
				if ( !muteAllowed )
				{
					play(Audio.BEEP);
					sleep(100);
				}
				break;
			}

			sleep(100);
		}

		getDarkTowerPanel().setFlash(false);
		return action;
	}

	public int pcWaitAction(int[] allowedActions)
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);

		int action = Button.NA;
		int timeToWait = 500;
		double random = Math.random();

		switch ( getItemNo() )
		{
			// bazaar
			case Image.WARRIOR:
				// haggle?
				if ( getItemNoToBuy() == Image.WARRIOR )
				{
					if ( ( Util.contains(allowedActions, Button.HAGGLE) ) &&
						 ( getPrice() > 8 ) )
						action = Button.HAGGLE;
					// buy if enough gold
					else if ( player.getGold() >= getPrice() )
						action = Button.YES;
					// leave
					else
						action = Button.NO;
				}
				else
					action = Button.NO;
				sleep(timeToWait);
				break;
			case Image.FOOD:
				if ( getItemNoToBuy() == Image.FOOD )
				{
					// buy if enough gold
					if ( ( player.getGold() >= getPrice() ) && ( player.getFood() < player.requiredFood() * 20 ) )
						action = Button.YES;
					// leave
					else
						action = Button.NO;
				}
				else
					action = Button.NO;
				sleep(timeToWait);
				break;
			case Image.BEAST:
				if ( getItemNoToBuy() == Image.BEAST )
				{
					// buy if enough gold
					if ( player.getGold() >= getPrice() )
						action = Button.YES;
					// haggle
					else
						action = Button.HAGGLE;
				}
				else
					action = Button.NO;
				sleep(1000);
				break;
			case Image.SCOUT:
				if ( getItemNoToBuy() == Image.SCOUT )
				{
					// buy if enough gold
					if ( player.getGold() >= getPrice() )
						action = Button.YES;
					// haggle
					else
						action = Button.HAGGLE;
				}
				else
					action = Button.NO;
				sleep(1000);
				break;
			case Image.HEALER:
				if ( getItemNoToBuy() == Image.HEALER )
				{
					// buy if enough gold
					if ( player.getGold() >= getPrice() )
						action = Button.YES;
					// haggle
					else
						action = Button.HAGGLE;
				}
				else
					action = Button.NO;
				sleep(1000);
				break;
			// wizard
			case Image.WIZARD:
				if ( label.equals("C" + Integer.toString(getCursedPlayerNo() + 1)) )
					action = Button.YES;
				else
					action = Button.NO;
				sleep(1000);
				break;
			// dark tower
			case Image.BRASSKEY:
			case Image.SILVERKEY:
			case Image.GOLDKEY:
				action = player.getRiddleKeyAction(getItemNo(),
					getRiddleyKeyListPos(), (getLevel() == 3));
				sleep(1000);
				break;
			default:
				action = allowedActions[0];
				sleep(1000);
				break;
		}
		
		return action;
	}
	
	public int readAction(int[] allowedActions)
	{
		int action = Button.NA;
		Player player = getPlayerListItem(playerNo);

		// none pc
		if ( ( player.getPlayerType() == Player.NONEPC ) || ( !isBoardVisible() ) )
		{
			while ( !isActionQueueEmpty() )
			{
				action = getAction();
				if ( Util.contains(allowedActions, action) )
					break;
				action = Button.NA;
			}
		}
		// pc
		else
		{
			action = pcReadAction(allowedActions);
		}
		
		return action;
	}

	public int pcReadAction(int[] allowedActions)
	{
		Player player = getPlayerListItem(playerNo);

		int action = Button.NA;

		switch ( imageNo )
		{
			// battle
			case Image.BRIGANDS:
			case Image.BLACK:
				if ( Util.contains(Territory.FRONTIERLIST, player.getDestTerritoryNo()) )
					action = Button.NO;
				else if ( ( !Util.contains(Territory.DARKTOWERLIST, player.getEndTerritoryNo()) ) &&
						  ( player.hasBrassKey() ) &&
						  ( player.hasSilverKey() ) &&
						  ( player.hasGoldKey() ) &&
						  ( player.getWarriors() >= getMinBrigands() ) )
					action = Button.NO;
				else if ( getWinningChance(getWarriors(), getBrigands()) < 0.25 )
					action = Button.NO;
				else if ( player.getWarriors() == 2 )
					action = Button.NO;
				break;
			default:
				break;
		}

		return action;
	}
			
	public boolean isActionQueueEmpty()
	{
		return actionQueue.isEmpty();
	}
	
	public void resetActionQueue()
	{
		actionQueue = new ArrayList();
	}

	//
	// mouse action queue functions
	//
	public void addMouseAction(MouseAction action)
	{
		Player player = getPlayerListItem(playerNo);
		// none pc
		if ( player.getPlayerType() == Player.NONEPC )
			mouseActionQueue.add(action);
	}
	
	public MouseAction getMouseAction()
	{
		MouseAction action = null;
		
		if ( !isMouseActionQueueEmpty() )
		{
			action = (MouseAction) mouseActionQueue.get(0);
			mouseActionQueue.remove(0);
		}
		
		return action;
	}

	public boolean isMouseAction(int type)
	{
		MouseAction action = null;
		
		if ( !isMouseActionQueueEmpty() )
		{
			action = (MouseAction) mouseActionQueue.get(0);
			if ( action.getType() == type )
				return true;
		}
		
		return false;
	}

	public boolean isMouseActionQueueEmpty()
	{
		return mouseActionQueue.isEmpty();
	}
	
	public void resetMouseActionQueue()
	{
		MouseAction action = null;
		
		if ( !isMouseActionQueueEmpty() )
		{
			action = (MouseAction) mouseActionQueue.get(
				mouseActionQueue.size() - 1);
			mouseActionQueue = new ArrayList();
			mouseActionQueue.add(action);
		}
	}

	//
	// dark tower panel functions
	//
	public void paintDarkTower(int count, int imageNo, int audioNo)
		throws ResetException, DisableException
	{
		paintDarkTower(count, imageNo, audioNo, false, false);
	}

	public void paintDarkTower(int count, int imageNo, int audioNo,
		boolean forcePaint, boolean forcePlay)
		throws ResetException, DisableException
	{
		String label = Integer.toString(count);
		if ( count < 10 )
			label = "0" + label;
		paintDarkTower(label, imageNo, audioNo, forcePaint, forcePlay);
	}
	
	public void paintDarkTower(String label, int imageNo, int audioNo)
		throws ResetException, DisableException
	{
		paintDarkTower(label, imageNo, audioNo, false, false);
	}

	public void paintDarkTower(String label, int imageNo, int audioNo,
		boolean forcePaint, boolean forcePlay)
		throws ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);

		if ( reset )
			throw new ResetException();
		if ( !player.isEnable() )
			throw new DisableException();

		paintDarkTowerEx(label, imageNo, audioNo, forcePaint, forcePlay);
	}

	public void paintDarkTowerEx(String label, int imageNo, int audioNo,
		boolean forcePaint, boolean forcePlay)
	{
		Player player = getPlayerListItem(playerNo);

		boolean enabled = false;
		if ( ( player.getPlayerType() == Player.NONEPC ) ||
			 ( darkTower.getDisplay() == 0 ) || ( forcePaint ) )
			enabled = true;

		if ( player.isPerformAction() )
		{
			getDarkTowerPanel().setEnabled(enabled);

			if ( enabled )
			{
				getDarkTowerPanel().setColor(Territory.COLORLIST[playerNo]);

				if ( label != null )
					getDarkTowerPanel().setLabel(label);

				ImageIcon image = Image.getImageIcon(imageNo);
				if ( image != null )
					getDarkTowerPanel().setImage(image);
			}

			play(audioNo, forcePlay);
		}

		this.label = label;
		this.imageNo = imageNo;
		this.audioNo = audioNo;
	}

	public void play(int audioNo)
	{
		play(audioNo, false);
	}

	public void play(int audioNo, boolean forcePlay)
	{
		Player player = getPlayerListItem(playerNo);

		boolean enabled = false;
		if ( ( player.getPlayerType() == Player.NONEPC ) ||
			 ( darkTower.getDisplay() == 0 ) || ( forcePlay ) )
			enabled = true;

		if ( ( audioNo != Audio.NA ) && ( audio != null ) )
			audio.stop();
		audio = Audio.getAudioClip(audioNo);
		if ( ( audio != null ) && ( enabled ) )
			audio.play();
	}

	//
	// board panel functions
	//
	public void paintBoard(int highlightedTerritoryNo)
		throws ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);

		if ( reset )
			throw new ResetException();
		if ( !player.isEnable() )
			throw new DisableException();

		paintBoardEx(highlightedTerritoryNo);
	}

	public void paintBoardEx(int highlightedTerritoryNo)
	{
		Player player = getPlayerListItem(playerNo);

		if ( player.isPerformAction() )
		{
			BoardPanel boardPanel =	getBoardPanel();
			boardPanel.setHighlightedTerritoryNo(highlightedTerritoryNo);
			boardPanel.setDragon(dragon);
			if ( ( isBoardVisible() ) && ( !isBoardIcon() ) )
				boardPanel.repaint();
			else
			{
				// move player
				if ( playerList != null )
				{
					for (int i = 0; i < playerList.size(); i++)
					{
						player = (Player) playerList.get(i);
						boardPanel.nextCoord(player);
					}
				}
				// move dragon
				if ( dragon != null )
				{
					if ( dragon.getStartTerritoryNo() != 0 )
						boardPanel.nextCoord(dragon);
					else if ( dragon.getEndTerritoryNo() != 0 )
						dragon.setStartTerritoryNo(dragon.getEndTerritoryNo());
				}
			}
		}
	}

	public int findTerritoryNo(int x, int y)
	{
		Territory territory = null;
		Polygon polygon = null;
		List territoryList = getBoardPanel().getTerritoryList();
		
		for (int i = 0; i < territoryList.size(); i++)
		{
			territory = (Territory) territoryList.get(i);
			polygon = territory.getPolygon();
			if ( polygon.contains(x, y) )
				return territory.getTerritoryNo();
		}
		return 0;
	}

	public boolean isNeigbor(int territoryNo)
	{
		if ( territoryNo > 0 )
		{
			List territoryList = getBoardPanel().getTerritoryList();
			Territory territory = (Territory) territoryList.get(territoryNo - 1);
			Player player = getPlayerListItem(playerNo);
			
			if ( ( player.getKingdomNo() == territory.getKingdomNo() ) &&
				 ( territoryNo != dragon.getEndTerritoryNo() ) )
			{
				if ( !( ( territory.getType() == Territory.FRONTIER ) &&
						( player.getRelKingdomNo() == 0 ) &&
						( player.hasBrassKey() ) &&
						( player.hasSilverKey() ) &&
						( player.hasGoldKey() ) ) )
				{
					if ( !( ( territory.getType() == Territory.CITADEL ) &&
							( player.getRelKingdomNo() != 0 ) ) )
					{
						if ( territory.getNeigborList().contains(new Integer(player.getStartTerritoryNo())) )
							return true;
						if ( player.getStartTerritoryNo() == territoryNo )
							return true;
						if ( player.hasPegasus() )
						{
							if ( ( player.isUsePegasus() ) &&
								 ( territory.getType() == Territory.FRONTIER ) )
								return false;
							else
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean hasToUsePegasus(int territoryNo)
	{
		if ( territoryNo > 0 )
		{
			List territoryList = getBoardPanel().getTerritoryList();
			Territory territory = (Territory) territoryList.get(territoryNo - 1);
			Player player = getPlayerListItem(playerNo);
			
			if ( player.getKingdomNo() == territory.getKingdomNo() )
			{
				if ( !( ( territory.getType() == Territory.CITADEL ) &&
						( player.getRelKingdomNo() != 0 ) ) )
				{	   
					if ( territory.getNeigborList().contains(new Integer(player.getStartTerritoryNo())) )
						return false;
					if ( player.getStartTerritoryNo() == territoryNo )
						return false;
				}
			}
		}
		return true;
	}

	public boolean isAllowedDragonTerritory(int territoryNo)
	{
		Player player = null;

		if ( territoryNo > 0 )
		{
			List territoryList = getBoardPanel().getTerritoryList();
			Territory territory = (Territory) territoryList.get(territoryNo - 1);
			if ( territory.getType() != Territory.STANDARD )
				return false;
			for (int i = 0; i < playerList.size(); i++)
			{
				player = (Player) playerList.get(i);
				if ( player.getEndTerritoryNo() == territoryNo )
					return false;
			}
			return true;
		}
		return false;
	}

	public int getAllowedDragonTerritory(int kingdomNo, int territoryNo)
	{
		Territory territory = null;
		int neigborTerritoryNo = 0;
		int count = 0;
		int no = 0;
		
		if ( territoryNo > 0 )
		{
			territory = (Territory) territoryList.get(territoryNo - 1);
			List neigborList = territory.getNeigborList();
			do
			{
				count++;
				no = (int) (Math.random() * neigborList.size());
				neigborTerritoryNo = ((Integer) neigborList.get(no)).intValue();
				if ( isAllowedDragonTerritory(neigborTerritoryNo) )
					return neigborTerritoryNo;
			}
			while ( count < 256 );
		}

		do
		{
			neigborTerritoryNo = (int) (Math.random() * territoryList.size()) + 1;
			territory = (Territory) territoryList.get(neigborTerritoryNo - 1);
		}
		while ( ( !isAllowedDragonTerritory(neigborTerritoryNo) ) ||
				( territory.getKingdomNo() != kingdomNo ) );

		return neigborTerritoryNo;
	}

	//
	// general functions
	//
	public DarkTower getDarkTower()
	{
		return darkTower;
	}

	public DarkTowerPanel getDarkTowerPanel()
	{
		return darkTower.getDarkTowerPanel();
	}

	public BoardPanel getBoardPanel()
	{
		return darkTower.getBoardPanel();
	}

	public void setInventory()
	{
		darkTower.setInventory();
	}

	public List getPlayerList()
	{
		return playerList;
	}

	public void setPlayerListItem(int index, Player player)
	{
		darkTower.getPlayerList().set(index, player);
	}

	public Player getPlayerListItem(int index)
	{
		return (Player) playerList.get(index);
	}

	public void setLastPlayerListItem(int index, Player player)
	{
		darkTower.getLastPlayerList().set(index, player);
	}

	public Player getLastPlayerListItem(int index)
	{
		return (Player) darkTower.getLastPlayerList().get(index);
	}

	public void storePlayers()
	{
		lastDragon = (Dragon) dragon.clone();
		for (int i = 0; i < playerList.size(); i++)
		{
			Player player = getPlayerListItem(i);
			setLastPlayerListItem(i, (Player) player.clone());
		}
	}

	public void restorePlayers()
	{
		dragon = (Dragon) lastDragon.clone();
		boardPanel.setDragon(dragon);
		for (int i = 0; i < playerList.size(); i++)
		{
			Player player = getPlayerListItem(i);
			Player lastPlayer = (Player) getLastPlayerListItem(i).clone();
			lastPlayer.setPlayerType(player.getPlayerType());
			lastPlayer.setEnable(player.isEnable());
			lastPlayer.setFood(player.getFood());
			setPlayerListItem(i, (Player) lastPlayer);
		}
	}

	public void setPlayerNo(int playerNo)
	{
		this.playerNo = getPlayerNo(playerNo);
	}

	public int getPlayerNo()
	{
		return playerNo;
	}

	public int getPlayerNo(int playerNo)
	{
		if ( playerNo < 0 )
			return getPlayerNo(playerNo + playerList.size());
		if ( playerNo >= playerList.size() )
			return getPlayerNo(playerNo - playerList.size());
		Player player = (Player) playerList.get(playerNo);
		if ( !player.isEnable() )
			return getPlayerNo(playerNo + 1);
		return playerNo;
	}

	public void setDragon(Dragon dragon)
	{
		this.dragon = dragon;
	}

	public Dragon getDragon()
	{
		return dragon;
	}

	public void setPrice(int price)
	{
		this.price = price;
	}

	public int getPrice()
	{
		return price;
	}

	public void setItemNo(int itemNo)
	{
		this.itemNo = itemNo;
	}

	public int getItemNo()
	{
		return itemNo;
	}

	public void setItemNoToBuy(int itemNoToBuy)
	{
		this.itemNoToBuy = itemNoToBuy;
	}

	public int getItemNoToBuy()
	{
		return itemNoToBuy;
	}

	public void setCursedPlayerNo(int cursedPlayerNo)
	{
		this.cursedPlayerNo = cursedPlayerNo;
	}

	public int getCursedPlayerNo()
	{
		return cursedPlayerNo;
	}

	public void setRiddleyKeyListPos(int riddleKeyListPos)
	{
		this.riddleKeyListPos = riddleKeyListPos;
	}

	public int getRiddleyKeyListPos()
	{
		return riddleKeyListPos;
	}

	public void setWarriors(int warriors)
	{
		this.warriors = warriors;
	}

	public int getWarriors()
	{
		return warriors;
	}

	public void setBrigands(int brigands)
	{
		this.brigands = brigands;
	}

	public int getBrigands()
	{
		return brigands;
	}

	public int getBrigands(int warriors)
	{
		int brigands = warriors;

		switch ( darkTower.getBattle() )
		{
			case 0:
				brigands = (warriors - 3) + (int) (Math.random() * 7);
				break;
			case 1:
				brigands = warriors + (int) (Math.random() * 6);
				break;
			case 2:
				brigands = warriors + (int) (Math.random() * 11 + 5);
				break;
			default:
				break;
		}

		if ( brigands < 3 )
			brigands = (int) (Math.random() * 4 + 3);
		if ( brigands > 99 )
			brigands = 99;
		
		return brigands;
	}

	public int getMinBrigands()
	{
		int brigands = 17;

		switch ( darkTower.getLevel() )
		{
			case 0:
				brigands = 17;
				break;
			case 1:
				brigands = 33;
				break;
			case 2:
				brigands = 17;
				break;
			case 3:
				brigands = 16;
				break;
			default:
				break;
		}

		return brigands;
	}

	public double getWinningChance(int warriors, int brigands)
	{
		if ( warriors > brigands )
			return 0.75 - ( brigands / ( 4.0 * warriors) );
		return 0.25 + ( warriors / ( 4.0 * brigands) );
	}

	public int getLevel()
	{
		return darkTower.getLevel();
	}

	public void setImageNo(int imageNo)
	{
		this.imageNo = imageNo;
	}

	public boolean isBoardVisible()
	{
		return darkTower.getBoardFrame().isVisible();
	}
	
	public boolean isBoardIcon()
	{
		return darkTower.getBoardFrame().isIcon();
	}

	public void reset()
	{
		reset = true;
	}

	public void sleep()
		throws InterruptedException, ResetException, DisableException
	{
		sleep(2000);
	}

	public void sleep(int millis)
		throws InterruptedException, ResetException, DisableException
	{
		Player player = getPlayerListItem(playerNo);

		if ( player.getPlayerType() == Player.PC )
		{
			if ( darkTower.getDisplay() == 0 )
				millis = millis * darkTower.getSpeed() / 100;
			else
				millis = 0;
		}

		if ( player.isPerformAction() )
		{
			setInventory();

			for (int i = 0; i < 100; i++)
			{
				sleep((long) (millis / 100));
				if ( reset )
					throw new ResetException();
				if ( !player.isEnable() )
					throw new DisableException();
			}
		}
	}
}
