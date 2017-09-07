/*
 * 07/24/2002 - 20:37:20
 *
 * Audio.java
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

import java.lang.String;
import java.applet.Applet;
import java.applet.AudioClip;

public class Audio
{
	public static final int NA = 0;
	public static final int BATTLE = 1;
	public static final int BAZAAR = 2;
	public static final int BAZAARCLOSED = 3;
	public static final int BEEP = 4;
	public static final int CLEAR = 5;
	public static final int DARKTOWER = 6;
	public static final int DRAGON = 7;
	public static final int DRAGONKILL = 8;
	public static final int ENDTURN = 9;
	public static final int ENIMYHIT = 10;
	public static final int FRONTIER = 11;
	public static final int INTRO = 12;
	public static final int LOST = 13;
	public static final int PEGASUS = 14;
	public static final int PLAGUE = 15;
	public static final int PLAYERHIT = 16;
	public static final int SANCTUARY = 17;
	public static final int TOMB = 18;
	public static final int TOMBBATTLE = 19;
	public static final int TOMBNOTHING = 20;
	public static final int WRONG = 21;
	public static final int STARVING = 22;

	public static final String[] AUDIO =
		{ "battle", "bazaar", "bazaar-closed", "beep", "clear", 
		  "darktower", "dragon", "dragon-kill", "end-turn", "enemy-hit",
		  "frontier", "intro", "lost", "pegasus", "plague",
		  "player-hit", "sanctuary", "tomb", "tomb-battle",
		  "tomb-nothing", "wrong", "starving" };
		
	public static AudioClip getAudioClip(int audioNo)
	{
		AudioClip audio = null;
		
		if ( audioNo > NA )
			audio = Applet.newAudioClip(
				Util.class.getResource("audio/" + AUDIO[audioNo - 1] + ".wav"));
		
		return audio;
	}

	public static void play(int audioNo)
	{
		AudioClip audio = getAudioClip(audioNo);
		if ( audio != null )
			audio.play();	
	}
}
