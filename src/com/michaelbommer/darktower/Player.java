/*
 * 07/24/2002 - 20:37:20
 *
 * Player.java
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

import java.util.List;
import java.awt.Point;

public class Player extends Figure
{
	public static final int NA = 0;
	public static final int NONEPC = 1;
	public static final int PC = 2;

	private int playerType = NONEPC;
	private int playerNo = 0;
	private int kingdomNo = 0;
	private int lastBuildingNo = Territory.SANCTUARY;
	private int moves = 0;
	private boolean enable = false;
	private boolean cursed = false;
	private boolean usePegasus = false;
	private boolean placeDragon = false;

	private List playerList = null;
	private IntegerList displayList = null;
	private int[] riddleKeyList = null;
	private int[][] solveRiddleKeyList = null;
	
	private int warriors = 10;
	private int gold = 30;
	private int food = 25;
	private boolean beast = false;
	private boolean scout = false;
	private boolean healer = false;
	private boolean dragonSword = false;
	private boolean pegasus = false;
	private boolean brassKey = false;
	private boolean silverKey = false;
	private boolean goldKey = false;
	
	private Territory territory = null;
	private boolean moveToPrevTerritory = false;
	private boolean performAction = true;

	public Player(int playerNo, List playerList, List territoryList, boolean enable, 
		boolean isTeachingLevel, int playerType)
	{
		this.playerNo = playerNo;
		this.kingdomNo = playerNo;
		this.enable = enable;
		this.playerType = playerType;
		this.startTerritoryNo = Territory.CITADELLIST[playerNo];
		this.endTerritoryNo = Territory.CITADELLIST[playerNo];
		this.playerList = playerList;

		territory = (Territory) territoryList.get(startTerritoryNo - 1);

		initObjectList(isTeachingLevel);
		initRiddleKeyList(isTeachingLevel);

		displayList = new IntegerList();
	}

	public void initObjectList(boolean isTeachingLevel)
	{
		if ( isTeachingLevel )
		{
			warriors = 10;
			gold = 30;
			food = 25;
			beast = true;
			scout = true;
			healer = true;
			dragonSword = true;
			pegasus = true;
			brassKey = true;
			silverKey = true;
			goldKey = true;
		}
	}

	public void initRiddleKeyList(boolean isTeachingLevel)
	{
		riddleKeyList = new int[3];

		if ( isTeachingLevel )
		{
			// brass key
			riddleKeyList[2] = Image.BRASSKEY;
			// silver key
			riddleKeyList[1] = Image.SILVERKEY;
			// gold key
			riddleKeyList[0] = Image.GOLDKEY;
		}
		else
		{
			// brass key
			riddleKeyList[getRandomListPosition()] = Image.BRASSKEY;
			// silver key
			riddleKeyList[getRandomListPosition()] = Image.SILVERKEY;
			// gold key
			riddleKeyList[getRandomListPosition()] = Image.GOLDKEY;
		}

		solveRiddleKeyList = new int[3][3];
	}

	public void setPlayerType(int playerType)
	{
		this.playerType = playerType;
	}
	
	public int getPlayerType()
	{
		return playerType;
	}

	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}
	
	public boolean isEnable()
	{
		return enable;
	}

	public boolean isSinglePlayer()
	{
		int numberOfPlayers = 0;

		for (int i = 0; i < 4; i++)
		{
			Player player = (Player) playerList.get(i);
			if ( player.isEnable() )
				numberOfPlayers++;
		}

		return ( numberOfPlayers < 2 );
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

	public int getPlayerCount()
	{
		int count = 0;

		for (int i = 0; i < 4; i++)
		{
			Player player = (Player) playerList.get(i);
			if ( player.isEnable() )
				count++;
		}
		return count;
	}

	public void setKingdomNo(int kingdomNo)
	{
		this.kingdomNo = getKingdomNo(kingdomNo);
	}

	public int getKingdomNo()
	{
		return getKingdomNo(kingdomNo);
	}

	public int getKingdomNo(int kingdomNo)
	{
		if ( kingdomNo < 0 )
			return getKingdomNo(kingdomNo + 4);
		if ( kingdomNo > 3 )
			return getKingdomNo(kingdomNo - 4);
		return kingdomNo;
	}

	public int getRelKingdomNo()
	{
		return getKingdomNo(kingdomNo - playerNo);
	}

	public void setLastBuildingNo(int lastBuildingNo)
	{
		this.lastBuildingNo = lastBuildingNo;
	}

	public int getLastBuildingNo()
	{
		return lastBuildingNo;
	}
	
	public void setMoves(int moves)
	{
		this.moves = moves;
	}

	public int getMoves()
	{
		return moves;
	}

	public void setWarriors(int count)
	{
		warriors = count;
		if ( warriors > 99 )
			warriors = 99;
		if ( isSinglePlayer() )
		{
			if ( warriors < 0 )
				warriors = 0;
		}
		else
		{
			if ( warriors < 1 )
				warriors = 1;
		}
		setGold(gold);
	}

	public int getWarriors()
	{
		return warriors;
	}

	public void setGold(int count)
	{
		int maxgold = warriors * 6;
		if ( beast )
			maxgold = maxgold + 50;
		if ( maxgold > 99 )
			maxgold = 99;
		if ( count > maxgold )
			gold = maxgold;
		else
			gold = count;
		if ( gold < 0 )
			gold = 0;
	}

	public int getGold()
	{
		return gold;
	}

	public void setFood(int count)
	{
		food = count;
		if ( food > 99 )
			food = 99;
		if ( food < 0 )
			food = 0;
	}

	public int getFood()
	{
		return food;
	}

	public void setBeast(boolean has)
	{
		beast = has;
	}

	public boolean hasBeast()
	{
		return beast;
	}

	public void setScout(boolean has)
	{
		scout = has;
	}

	public boolean hasScout()
	{
		return scout;
	}

	public void setHealer(boolean has)
	{
		healer = has;
	}
	
	public boolean hasHealer()
	{
		return healer;
	}

	public void setDragonSword(boolean has)
	{
		dragonSword = has;
	}

	public boolean hasDragonSword()
	{
		return dragonSword;
	}

	public void setPegasus(boolean has)
	{
		pegasus = has;
		usePegasus = false;
	}

	public boolean hasPegasus()
	{
		return pegasus;
	}

	public void setBrassKey(boolean has)
	{
		brassKey = has;
	}

	public boolean hasBrassKey()
	{
		return brassKey;
	}

	public void setSilverKey(boolean has)
	{
		silverKey = has;
	}

	public boolean hasSilverKey()
	{
		return silverKey;
	}

	public void setGoldKey(boolean has)
	{
		goldKey = has;
	}

	public boolean hasGoldKey()
	{
		return goldKey;
	}

	public void setCursed(boolean is)
	{
		cursed = is;
	}

	public boolean isCursed()
	{
		return cursed;
	}

	public void setPerformAction(boolean performAction)
	{
		this.performAction = performAction;
	}
	
	public boolean isPerformAction()
	{
		return performAction;
	}

	public void setUsePegasus(boolean usePegasus)
	{
		this.usePegasus = usePegasus;
	}

	public boolean isUsePegasus()
	{
		return usePegasus;
	}

	public void setPlaceDragon(boolean placeDragon)
	{
		this.placeDragon = placeDragon;
	}

	public boolean isPlaceDragon()
	{
		return placeDragon;
	}

	public int[] getRiddleKeyList()
	{
		return riddleKeyList;
	}

	public int getRandomListPosition()
	{
		int listPosition = 0;
		
		do
		{
			listPosition = (int) (Math.random() * 3);
		}
		while ( riddleKeyList[listPosition] != 0 );
		
		return listPosition;
	}

	public void setRiddleKey(int keyImage, int action, boolean right, int riddleKeyListPos)
	{
		int key = 0;
		switch ( keyImage )
		{
			case Image.BRASSKEY:
				key = 0;
				break;
			case Image.SILVERKEY:
				key = 1;
				break;
			case Image.GOLDKEY:
				key = 2;
				break;
			default:
				break;
		}

		if ( action == Button.NO )
			solveRiddleKeyList[key][riddleKeyListPos] = right ? 1 : 3;
		else
			solveRiddleKeyList[key][riddleKeyListPos] = right ? 3 : 1;
		
		// columns
		for (int i = 0; i < 3; i++)
		{
			int sum = 0;
			for (int j = 0; j < 3; j++)
				sum += solveRiddleKeyList[j][i];
			for (int j = 0; j < 3; j++)
			{
				switch ( sum )
				{
					case 2:
						if ( solveRiddleKeyList[j][i] == 0 )
							solveRiddleKeyList[j][i] = 3;
						break;
					case 3:
					case 4:
						if ( solveRiddleKeyList[j][i] == 0 )
							solveRiddleKeyList[j][i] = 1;
						break;
					default:
						break;
				}
			}
		}
		// lines
		for (int i = 0; i < 3; i++)
		{
			int sum = 0;
			for (int j = 0; j < 3; j++)
				sum += solveRiddleKeyList[i][j];
			for (int j = 0; j < 3; j++)
			{
				switch ( sum )
				{
					case 2:
						if ( solveRiddleKeyList[i][j] == 0 )
							solveRiddleKeyList[i][j] = 3;
						break;
					case 3:
					case 4:
						if ( solveRiddleKeyList[i][j] == 0 )
							solveRiddleKeyList[i][j] = 1;
						break;
					default:
						break;
				}
			}
		}
	}

	public int getRiddleKeyAction(int keyImage, int riddleKeyListPos,
		boolean isTeachingLevel)
	{
		int key = 0;
		int sum = 0;

		switch ( keyImage )
		{
			case Image.BRASSKEY:
				key = 0;
				break;
			case Image.SILVERKEY:
				key = 1;
				break;
			case Image.GOLDKEY:
				key = 2;
				break;
			default:
				break;
		}

		for (int i = 0; i < 3; i++)
			sum += solveRiddleKeyList[i][riddleKeyListPos];

		if ( isTeachingLevel )
		{
			switch ( riddleKeyListPos )
			{
				case 0:
					if ( key == 2 )
						return Button.YES;
					return Button.NO;
				case 1:
					if ( key == 1 )
						return Button.YES;
					return Button.NO;
				case 2:
					if ( key == 0 )
						return Button.YES;
					return Button.NO;
				default:
					return Button.NO;
			}
		}
		else
		{
			if ( solveRiddleKeyList[key][riddleKeyListPos] == 3 )
				return Button.YES;
			if ( solveRiddleKeyList[key][riddleKeyListPos] == 1 )
				return Button.NO;
			if ( ( solveRiddleKeyList[key][riddleKeyListPos] == 0 ) && ( sum < 3 ) )
				return Button.YES;
			return Button.NO;
		}
	}

	public IntegerList getDisplayList()
	{
		return displayList;
	}

	public int requiredFood()
	{
		if ( (warriors >= 1) && (warriors <= 15) )
			return 1;
		if ( (warriors >= 16) && (warriors <= 30) )
			return 2;
		if ( (warriors >= 31) && (warriors <= 45) )
			return 3;
		if ( (warriors >= 46) && (warriors <= 60) )
			return 4;
		if ( (warriors >= 61) && (warriors <= 75) )
			return 5;
		if ( (warriors >= 76) && (warriors <= 90) )
			return 6;
		if ( (warriors >= 91) && (warriors <= 99) )
			return 7;
		return 1;
	}
	
	public void consumeFood()
	{
		if ( food < requiredFood() )
			setWarriors(warriors - 1);
		setFood(food - requiredFood());
	}

	public void setMoveToPrevTerritory(boolean moveToPrevTerritory)
	{
		this.moveToPrevTerritory = moveToPrevTerritory;
	}

	public boolean isMoveToPrevTerritory()
	{
		return moveToPrevTerritory;
	}

	public Object clone()
	{
		try
		{
			Player player = (Player) super.clone();
			player.riddleKeyList = (int[]) riddleKeyList.clone();
			player.solveRiddleKeyList = (int[][]) solveRiddleKeyList.clone();
			return player;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}
}
