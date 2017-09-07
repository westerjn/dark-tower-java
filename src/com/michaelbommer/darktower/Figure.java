/*
 * 07/24/2002 - 20:37:20
 *
 * Figure.java
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

public class Figure implements Cloneable
{
	protected int lastTerritoryNo = 0;
	protected int startTerritoryNo = 0;
	protected int endTerritoryNo = 0;
	protected int destTerritoryNo = 0;
	protected int step = 0;

	public Figure()
	{
	}

	public void setLastTerritoryNo(int lastTerritoryNo)
	{
		this.lastTerritoryNo = lastTerritoryNo;
	}

	public int getLastTerritoryNo()
	{
		return lastTerritoryNo;
	}

	public void setStartTerritoryNo(int startTerritoryNo)
	{
		this.startTerritoryNo = startTerritoryNo;
	}

	public int getStartTerritoryNo()
	{
		return startTerritoryNo;
	}

	public void setEndTerritoryNo(int endTerritoryNo)
	{
		this.lastTerritoryNo = this.endTerritoryNo;
		this.endTerritoryNo = endTerritoryNo;
	}

	public int getEndTerritoryNo()
	{
		return endTerritoryNo;
	}

	public void setDestTerritoryNo(int destTerritoryNo)
	{
		this.destTerritoryNo = destTerritoryNo;
	}

	public int getDestTerritoryNo()
	{
		return destTerritoryNo;
	}

	public void setStep(int step)
	{
		this.step = step;
	}

	public int getStep()
	{
		return step;
	}
}
