/*
 * 07/24/2002 - 20:37:20
 *
 * Search.java
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

public class Search
{
	private int territoryNo = 0;
	private int distance = 0;
	
	public Search()
	{
	}

	public Search(int territoryNo, int distance)
	{
		this.territoryNo = territoryNo;
		this.distance = distance;
	}

	public void setTerritoryNo(int territoryNo)
	{
		this.territoryNo = territoryNo;
	}

	public int getTerritoryNo()
	{
		return territoryNo;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public int getDistance()
	{
		return distance;
	}
}
