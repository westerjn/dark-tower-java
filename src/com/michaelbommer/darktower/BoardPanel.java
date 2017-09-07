/*
 * 07/24/2002 - 20:37:20
 *
 * BoardPanel.java
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

import javax.swing.JComponent;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Dimension;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class BoardPanel extends JComponent
{
	private List territoryList = null;
	private BufferedImage boardImage = null;
	private BufferedImage grayBoardImage = null;
	private List playerList = null;
	private int playerNo = 0;
	private int highlightedTerritoryNo = 0;
	private Dragon dragon = null;
	private ImageIcon classmImageIcon = null;
	private ImageIcon dragonImageIcon = null;
	private ImageIcon isoImageIcon = null;
	private ImageIcon kingdomImageIcon[] = null;
	private ImageIcon frontierImageIcon = null;
	private ImageIcon darkTowerImageIcon = null;

	public BoardPanel()
	{
		classmImageIcon = MultiImage.getImageIcon(MultiImage.CLASSM);
		dragonImageIcon = MultiImage.getImageIcon(MultiImage.DRAGON);
		isoImageIcon = MultiImage.getImageIcon(MultiImage.ISO);
		
		territoryList = newTerritoryList();
	}

	public void setPreferredSize(Dimension preferredSize)
	{
		super.setPreferredSize(preferredSize);
		setSize(preferredSize);
		createTexture();
		createTerritories();
		createNeigbors();
		createTerritoryPlaces(true);
		createBoard();
	}

	public void createTexture()
	{
		kingdomImageIcon = new ImageIcon[4];
		for (int i = 0; i < 4; i++)
			kingdomImageIcon[i] = MultiImage.getTexture(
				isoImageIcon, MultiImage.ISO, i, getWidth(), getHeight());

		frontierImageIcon = MultiImage.getTexture(
			isoImageIcon, MultiImage.ISO, 4, getWidth(), getHeight());
		darkTowerImageIcon = MultiImage.getTexture(
			isoImageIcon, MultiImage.ISO, 5, getWidth(), getHeight());
	}

	public void createTerritories()
	{
		int number = 8;
		Polygon polygon = null;
		Territory territory = null;
		List aboveList = null;
		List belowList = null;
		int r = getTerritoryRadius();

		for (int i = 0; i < 4; i++)
		{
			polygon = getDarkTowerPolygon(4, r, 225 + (i * 90));
			territory = (Territory) territoryList.get(i);
			territory.setPolygon(polygon);
		}

		for (int i = 0; i < 4; i++)
		{
			polygon = getFrontierPolygon(5, r, 310 + (i * 90));
			territory = (Territory) territoryList.get(i + 4);
			territory.setPolygon(polygon);
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				aboveList = getPointList(4 + j, r + (j * r), 230 + (i * 90), 310 + (i * 90));
				belowList = getPointList(5 + j, (2 * r) + (j * r), 230 + (i * 90), 310 + (i * 90));
				for (int k = 0; k < 4 + j; k++)
				{
					polygon = getTerritoryPolygon(k, aboveList, belowList);
					territory = (Territory) territoryList.get(number);
					territory.setPolygon(polygon);
					number += 1;
				}
			}
		}
	}

	public void createNeigbors()
	{
		Territory srcTerritory = null;
		ArrayList srcNeigborList = null;
		Territory destTerritory = null;
		Integer destNumber = null;

		for (int i = 0; i < territoryList.size(); i++)
		{
			srcTerritory = (Territory) territoryList.get(i);
			srcNeigborList = new ArrayList();
			for (int j = 0; j < territoryList.size(); j++)
			{
				if ( i != j )
				{
					destTerritory = (Territory) territoryList.get(j);
					destNumber = new Integer( destTerritory.getTerritoryNo() );
					if ( srcTerritory.intersects(destTerritory) )
					{
						if ( !srcNeigborList.contains(destNumber) )
						{
							srcNeigborList.add(destNumber);
						}
					}
				}
			}
			srcTerritory.setNeigborList(srcNeigborList);
		}
	}

	public void createTerritoryPlaces(boolean placeBuildingsRandomly)
	{
		Territory territory = null;

		do
		{
			for (int i = 8; i < territoryList.size(); i++)
			{
				territory = (Territory) territoryList.get(i);
				territory.setType(Territory.STANDARD);
			}
		}
		while (	!createTerritoryTypes(placeBuildingsRandomly) );
	}

	public boolean createTerritoryTypes(boolean placeBuildingsRandomly)
	{
		Territory territory = null;
		int territoryNo = 0;
		int count = 0;

		if ( placeBuildingsRandomly )
		{
			for (int i = 0; i < 4; i++)
			{
				for (int j = 0; j < 5; j++)
				{
					count = 0;
					do
					{
						territoryNo = (int) (Territory.KINGDOMLIST[i] + Math.random() * 30);
						count++;
						if ( count > 256 )
							return false;
					}
					while ( isOccupied(territoryNo) );

					territory =	(Territory) territoryList.get(territoryNo - 1);
					switch ( j )
					{
						case 0:
							Territory.CITADELLIST[i] = territoryNo;
							territory.setType(Territory.CITADEL);
							break;
						case 1:
							Territory.SANCTUARYLIST[i] = territoryNo;
							territory.setType(Territory.SANCTUARY);
							break;
						case 2:
							Territory.BAZAARLIST[i] = territoryNo;
							territory.setType(Territory.BAZAAR);
							break;
						case 3:
							Territory.RUINLIST[i] = territoryNo;
							territory.setType(Territory.RUIN);
							break;
						case 4:
							Territory.TOMBLIST[i] = territoryNo;
							territory.setType(Territory.TOMB);
							break;
						default:
							break;
					}
				}
			}
		}
		else
		{
			Territory.CITADELLIST = new int[] { 34, 64, 94, 124 };
			Territory.SANCTUARYLIST = new int[] { 29, 59, 89, 119 };
			Territory.TOMBLIST = new int[] { 25, 55, 85, 115 };
			Territory.RUINLIST = new int[] { 21, 51, 81, 111 };
			Territory.BAZAARLIST = new int[] { 14, 44, 74, 104 };

			for (int i = 0; i < 4; i++)
			{
				territory =	(Territory) territoryList.get(Territory.CITADELLIST[i] - 1);
				territory.setType(Territory.CITADEL);
				territory =	(Territory) territoryList.get(Territory.SANCTUARYLIST[i] - 1);
				territory.setType(Territory.SANCTUARY);
				territory =	(Territory) territoryList.get(Territory.TOMBLIST[i] - 1);
				territory.setType(Territory.TOMB);
				territory =	(Territory) territoryList.get(Territory.RUINLIST[i] - 1);
				territory.setType(Territory.RUIN);
				territory =	(Territory) territoryList.get(Territory.BAZAARLIST[i] - 1);
				territory.setType(Territory.BAZAAR);
			}
		}
		return true;
	}
	
	public boolean isOccupied(int territoryNo)
	{
		Territory territory = (Territory) territoryList.get(territoryNo - 1);
		List neigborList = territory.getNeigborList();
		Territory neigborTerritory = null;
		int neigborTerritoryNo = 0;

		if ( territory.getType() != Territory.STANDARD )
			return true;

		for (int i = 0; i < neigborList.size(); i++)
		{
			neigborTerritoryNo = ((Integer) neigborList.get(i)).intValue();
			neigborTerritory = (Territory) territoryList.get(neigborTerritoryNo - 1);

			if ( neigborTerritory.getType() != Territory.STANDARD )
				return true;
		}

		return false;
	}

	public void createBoard()
	{
		boardImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		Territory territory = null;
		Graphics2D g = boardImage.createGraphics();
		
		// draw territories
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int i = 0; i < territoryList.size(); i++)
		{
			territory = (Territory) territoryList.get(i);
			switch ( territory.getType() )
			{
				case Territory.DARKTOWER:
					g.setClip(territory.getPolygon());
					darkTowerImageIcon.paintIcon(this, g, 0, 0);
					break;
				case Territory.FRONTIER:
					g.setClip(territory.getPolygon());
					frontierImageIcon.paintIcon(this, g, 0, 0);
					g.setClip(0, 0, getWidth(), getHeight());
					g.setColor(new Color(0, 0, 0));
					g.drawPolygon(territory.getPolygon());
					break;
				default:
					g.setClip(territory.getPolygon());
					kingdomImageIcon[territory.getKingdomNo()].paintIcon(this, g, 0, 0);
					g.setClip(0, 0, getWidth(), getHeight());
					g.setColor(new Color(0, 0, 0));
					g.drawPolygon(territory.getPolygon());
					break;
			}
			
//			g.setColor(new Color(255, 255, 255));
//			Point point = territory.getCentre();
//			g.drawString(Integer.toString(territory.getTerritoryNo()), 
//				point.x - 4, point.y + 4);
//			g.setColor(new Color(0, 0, 0));
//			g.drawString(Integer.toString(territory.getTerritoryNo()), 
//				point.x - 3, point.y + 3);
		}

		RenderingHints renderingHints = g.getRenderingHints();
		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp colorConvertOp = new ColorConvertOp(colorSpace, renderingHints);
		grayBoardImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		colorConvertOp.filter(boardImage, grayBoardImage);
	}

	public Point getCartCoord(int radius, double angDeg)
	{
		double angRad = Math.toRadians(angDeg);
		int x = (int) ( getWidth() / 2 + radius * Math.cos(angRad) );
		int y = (int) ( getHeight() / 2 - radius * Math.sin(angRad) );
		return new Point(x, y);
	}

	public List getPointList(int territoryCount, int radius, double startAngDeg, double endAngDeg)
	{
		double angEvenDeg = startAngDeg;
		double angOddDeg = startAngDeg;
		int listSize = territoryCount * 2 - 1;
		ArrayList list = new ArrayList();

		for (int i = 0; i < listSize; i++)
		{
			if ( (i % 2) == 0 )
			{
				list.add( getCartCoord(radius, angEvenDeg) );
				angEvenDeg += ( (endAngDeg - startAngDeg) / (territoryCount - 1) );
			}
			else
			{
				angOddDeg += ( (endAngDeg - startAngDeg) / territoryCount );
				list.add( getCartCoord(radius, angOddDeg) );
			}
		}

		return list;
	}

	public int getStepExtension()
	{
		return (int) (getMinExtension() * getMinExtension() / 40000);
	}

	public int getMinExtension()
	{
		return ( getWidth() < getHeight() ) ? getWidth() : getHeight();
	}

	public int getTerritoryRadius()
	{
		return (int) (getMinExtension() * 0.08) + 1;
	}

	public List newTerritoryList()
	{
		int number = 9;
		int type = Territory.STANDARD;
		Territory territory = null;
		Color color = null;
		ArrayList territoryList = new ArrayList();

		for (int i = 0; i < 4; i++)
		{
			color = new Color(0, 0, 0);
			territory = new Territory(i + 1, i, Territory.DARKTOWER, color);
			territoryList.add(territory);
		}

		for (int i = 0; i < 4; i++)
		{
			color = new Color(250, 190, 110);
			territory = new Territory(i + 5, i, Territory.FRONTIER, color);
			territoryList.add(territory);
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				for (int k = 0; k < 4 + j; k++)
				{
					type = Territory.STANDARD;
					color = new Color(0, (int) (100 + Math.random() * 30), 0);
					// if ( Util.contains(Territory.SANCTUARYLIST, number) )
					// 	type = Territory.SANCTUARY;
					// else if ( Util.contains(Territory.TOMBLIST, number) )
					// 	type = Territory.TOMB;
					// else if ( Util.contains(Territory.RUINLIST, number) )
					// 	type = Territory.RUIN;
					// else if ( Util.contains(Territory.BAZAARLIST, number) )
					// 	type = Territory.BAZAAR;
					// else if ( Util.contains(Territory.CITADELLIST, number) )
					// 	type = Territory.CITADEL;

					territory = new Territory(number, i, type, color);
					territoryList.add(territory);
					number += 1;
				}
			}
		}
		return territoryList;
	}

	public Polygon getTerritoryPolygon(int territoryNo, List abovePointList, 
		List belowPointList)
	{
		int territoryCount = (abovePointList.size() + 1) / 2;
		int aboveIndex = territoryNo;
		int belowIndex = territoryNo * 2;

		if ( territoryNo > 1 )
			aboveIndex = territoryNo * 2 - 1;

		Point point = null;
		Polygon polygon = new Polygon();
		point = (Point) abovePointList.get(aboveIndex);
		polygon.addPoint(point.x, point.y);
		point = (Point) abovePointList.get(aboveIndex + 1);
		polygon.addPoint(point.x, point.y);
		if ( ( territoryNo > 0 ) && ( territoryNo < territoryCount - 1) )
		{
			point = (Point) abovePointList.get(aboveIndex + 2);
			polygon.addPoint(point.x, point.y);
		}

		point = (Point) belowPointList.get(belowIndex + 2);
		polygon.addPoint(point.x, point.y);
		point = (Point) belowPointList.get(belowIndex + 1);
		polygon.addPoint(point.x, point.y);
		point = (Point) belowPointList.get(belowIndex);
		polygon.addPoint(point.x, point.y);

		return polygon;
	}

	public Polygon getFrontierPolygon(int territoryCount, int radius,
		double startAngDeg)
	{
		int territoryHeight = getTerritoryRadius();
		double frontierAngDeg = 10.0;
		Point point = null;
		Polygon polygon = new Polygon();

		for (int i = 0; i < territoryCount + 1; i++)
		{
			point = getCartCoord(radius + (i * territoryHeight), 
				startAngDeg + frontierAngDeg);
			polygon.addPoint(point.x, point.y);
		}
		for (int i = territoryCount; i >= 0; i--)
		{
			point = getCartCoord(radius + (i * territoryHeight), startAngDeg);
			polygon.addPoint(point.x, point.y);
		}

		return polygon;
	}

	public Polygon getDarkTowerPolygon(int territoryCount, int radius,
		double startAngDeg)
	{
		List belowPointList = getPointList(territoryCount + 1, radius,
			startAngDeg + 5, startAngDeg + 90 - 5);
		List extraPointList = getPointList(territoryCount + 1, radius,
			startAngDeg, startAngDeg + 90);
		Point point = null;
		Polygon polygon = new Polygon();

		point = getCartCoord(0, 0.0);
		polygon.addPoint(point.x, point.y);
		point = (Point) extraPointList.get(extraPointList.size() - 1);
		polygon.addPoint(point.x, point.y);
		for (int i = belowPointList.size() - 1; i >= 0; i--)
		{
			point = (Point) belowPointList.get(i);
			polygon.addPoint(point.x, point.y);
		}
		point = (Point) extraPointList.get(0);
		polygon.addPoint(point.x, point.y);

		return polygon;
	}

	public List getTerritoryList()
	{
		return territoryList;
	}

	public void setPlayerList(List playerList)
	{
		this.playerList = playerList;
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

	public void setHighlightedTerritoryNo(int territoryNo)
	{
		highlightedTerritoryNo = territoryNo;
	}

	public void setDragon(Dragon dragon)
	{
		this.dragon = dragon;
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;
		Player player = null;
		Territory territory = null;
		Color color = null;
		Point point = null;
		int r = (int) (getTerritoryRadius() * 0.7);
		
		g.drawImage(boardImage, 0, 0, this);

		// draw highlighted territory
		if ( highlightedTerritoryNo > 0 )
		{
			territory = (Territory) territoryList.get(highlightedTerritoryNo - 1);
			g.setColor(Territory.COLORLIST[playerNo]);
			g.fillPolygon(territory.getPolygon());

			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			g.setClip(territory.getPolygon());
			g2D.setComposite(ac);
			g.drawImage(grayBoardImage, 0, 0, this);
			g.setClip(0, 0, getWidth(), getHeight());
			g2D.setComposite(AlphaComposite.SrcOver);

			g.setColor(new Color(0, 0, 0));
			g.drawPolygon(territory.getPolygon());
		}

		// draw buildings
		for (int i = 0; i < territoryList.size(); i++)
		{
			territory = (Territory) territoryList.get(i);
			point = territory.getCentre();
			switch ( territory.getType() )
			{
				case Territory.BAZAAR:
					MultiImage.drawSubImage(g, isoImageIcon, MultiImage.ISO, 
						MultiImage.BAZAAR[territory.getKingdomNo()], point.x, point.y);
					break;
				case Territory.SANCTUARY:
					MultiImage.drawSubImage(g, isoImageIcon, MultiImage.ISO, 
						MultiImage.SANCTUARY[territory.getKingdomNo()], point.x, point.y);
					break;
				case Territory.RUIN:
					MultiImage.drawSubImage(g, isoImageIcon, MultiImage.ISO, 
						MultiImage.RUIN[territory.getKingdomNo()], point.x, point.y);
					break;
				case Territory.TOMB:
					MultiImage.drawSubImage(g, isoImageIcon, MultiImage.ISO, 
						MultiImage.TOMB[territory.getKingdomNo()], point.x, point.y);
					break;
				default:
					break;
			}
		}
		MultiImage.drawSubImage(g, isoImageIcon, MultiImage.ISO, 
			221, getWidth() / 2, getHeight() / 2);

		// draw citatel
		for (int i = 0; i < 4; i++)
		{
			territory = (Territory) territoryList.get(Territory.CITADELLIST[i] - 1);
			point = territory.getCentre();
			MultiImage.drawSubImage(g, isoImageIcon, MultiImage.ISO, 
				MultiImage.CITADEL[i], point.x, point.y);
		}

		// draw player
		if ( playerList != null )
		{
			for (int i = 0; i < playerList.size(); i++)
			{
				player = (Player) playerList.get(i);
				point = nextCoord(player);
				MultiImage.drawSubImage(g, classmImageIcon, MultiImage.CLASSM, 
					MultiImage.PLAYER[i], point.x, point.y);
			}
		}

		// draw dragon
		if ( dragon != null )
		{
			if ( dragon.getStartTerritoryNo() != 0 )
			{
				point = nextCoord(dragon);
				MultiImage.drawSubImage(g, dragonImageIcon, MultiImage.DRAGON, 28, point.x, point.y);
			}
			else if ( dragon.getEndTerritoryNo() != 0 )
			{
				territory = (Territory) territoryList.get(dragon.getEndTerritoryNo() - 1);
				point = territory.getCentre();
				MultiImage.drawSubImage(g, dragonImageIcon, MultiImage.DRAGON, 28, point.x, point.y);
				dragon.setStartTerritoryNo(dragon.getEndTerritoryNo());
			}
		}
	}

	public Point nextCoord(Figure figure)
	{
		Point point = null;
		Territory territory = null;
		
		int step = figure.getStep();
		territory = (Territory) territoryList.get(figure.getStartTerritoryNo() - 1);
		int x1 = territory.getCentre().x;
		int y1 = territory.getCentre().y;
		territory = (Territory) territoryList.get(figure.getEndTerritoryNo() - 1);
		int x2 = territory.getCentre().x;
		int y2 = territory.getCentre().y;
		point = new Point(x2, y2);
		
		if ( figure.getStartTerritoryNo() != figure.getEndTerritoryNo() )
		{
			int dx = Math.abs(x2 - x1);
			int dy = Math.abs(y2 - y1);
			int incx = (x1 > x2) ? -1 : 1;
			int incy = (y1 > y2) ? -1 : 1;

			if (dx > dy)
			{
				int e = dx / 2;
				while (Math.abs(x2 - x1) > 0)
				{
					x1 += incx;
					e += dy;
					if (e >= dx)
					{
						y1 += incy;
						e -= dx;
					}
					if ( step == 0 )
					{
						point = new Point(x1, y1);
						break;
					}
					step--;
				}
			}
			else
			{
				int e = dy / 2;
				while (Math.abs(y2 - y1) > 0)
				{
					y1 += incy;
					e += dx;
					if (e >= dy)
					{
						x1 += incx;
						e -= dy;
					}
					if ( step == 0 )
					{
						point = new Point(x1, y1);
						break;
					}
					step--;
				}
			}

			figure.setStep(figure.getStep() + getStepExtension());
			if ( step > 0 )
			{
				figure.setStep(0);
				figure.setStartTerritoryNo(figure.getEndTerritoryNo());
			}
		}
		
		return point;
	}

	public void deepFirstSearch(Search search, int fromTerritoryNo, int toTerritoryNo)
	{
		Player player = (Player) playerList.get(playerNo);
		Territory fromTerritory = (Territory) territoryList.get(fromTerritoryNo - 1);
		Territory toTerritory = (Territory) territoryList.get(toTerritoryNo - 1);
		Territory neigborTerritory = null;
		List neigborList = fromTerritory.getNeigborList();
		int neigborTerritoryNo = 0;
		int dragonTerritoryNo = 0;

		if ( dragon != null )
			dragonTerritoryNo = dragon.getEndTerritoryNo();

		if ( fromTerritoryNo != toTerritoryNo )
		{
			search.setDistance(Integer.MAX_VALUE);
			for (int i = 0; i < neigborList.size(); i++)
			{
				neigborTerritoryNo = ((Integer) neigborList.get(i)).intValue();
				neigborTerritory = (Territory) territoryList.get(neigborTerritoryNo - 1);
				if ( ( neigborTerritoryNo != dragonTerritoryNo ) &&
					 ( ( neigborTerritory.getType() != Territory.FRONTIER ) ||
					   ( toTerritory.getType() == Territory.FRONTIER ) ) &&
					 ( ( neigborTerritory.getType() != Territory.DARKTOWER ) ||
					   ( toTerritory.getType() == Territory.DARKTOWER ) ) &&
					 ( ( neigborTerritory.getType() != Territory.CITADEL ) ||
					   ( player.getRelKingdomNo() == 0 ) ) &&
					 ( ( neigborTerritory.getType() == Territory.STANDARD ) ||
					   ( neigborTerritoryNo == toTerritoryNo ) ) )
				{
					int dist = search(neigborTerritoryNo, toTerritoryNo, 0);
					if ( ( dist < search.getDistance() ) ||
						 ( dist == search.getDistance() ) && ( Math.random() > 0.5 ) )
					{
						search.setTerritoryNo(neigborTerritoryNo);
						search.setDistance(dist);
					}
				}
			}
		}
		else
		{
			search.setTerritoryNo(toTerritoryNo);
			search.setDistance(0);
		}
	}

	public int search(int fromTerritoryNo, int toTerritoryNo, int deep)
	{
		Player player = (Player) playerList.get(playerNo);
		Territory fromTerritory = (Territory) territoryList.get(fromTerritoryNo - 1);
		Territory toTerritory = (Territory) territoryList.get(toTerritoryNo - 1);
		Territory neigborTerritory = null;
		List neigborList = fromTerritory.getNeigborList();
		int neigborTerritoryNo = 0;
		int dragonTerritoryNo = 0;

		if ( dragon != null )
			dragonTerritoryNo = dragon.getEndTerritoryNo();

		if ( ( deep > 3 ) || ( fromTerritoryNo == toTerritoryNo ) )
			return getDistance(fromTerritoryNo, toTerritoryNo) + deep;

		int minDist = Integer.MAX_VALUE;
		for (int i = 0; i < neigborList.size(); i++)
		{
			neigborTerritoryNo = ((Integer) neigborList.get(i)).intValue();
			neigborTerritory = (Territory) territoryList.get(neigborTerritoryNo - 1);
			if ( ( neigborTerritoryNo != dragonTerritoryNo ) &&
				 ( ( neigborTerritory.getType() != Territory.FRONTIER ) ||
				   ( toTerritory.getType() == Territory.FRONTIER ) ) &&
				 ( ( neigborTerritory.getType() != Territory.DARKTOWER ) ||
				   ( toTerritory.getType() == Territory.DARKTOWER ) ) &&
				 ( ( neigborTerritory.getType() != Territory.CITADEL ) ||
				   ( player.getRelKingdomNo() == 0 ) ) &&
				 ( ( neigborTerritory.getType() == Territory.STANDARD ) ||
				   ( neigborTerritoryNo == toTerritoryNo ) ) )
			{
				int dist = search(neigborTerritoryNo, toTerritoryNo, deep + 1);
				if ( dist < minDist)
					minDist = dist;
			}
		}
		return minDist + deep;
	}

	public int getDistance(int fromTerritoryNo, int toTerritoryNo)
	{
		Territory fromTerritory = (Territory) territoryList.get(fromTerritoryNo - 1);
		Territory toTerritory = (Territory) territoryList.get(toTerritoryNo - 1);
		Point fromCentre = fromTerritory.getCentre();
		Point toCentre = toTerritory.getCentre();
		int dx = toCentre.x - fromCentre.x;
		int dy = toCentre.y - fromCentre.y;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}
}
