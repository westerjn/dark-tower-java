/*
 * 07/24/2002 - 20:37:20
 *
 * DarkTower.java - The Dark Tower game for Java
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Component;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ChangeListener;
import javax.swing.WindowConstants;

public class DarkTower 
	implements ActionListener, MouseListener, MouseMotionListener
{
	private DarkTowerThread thread = null;
	private FlashThread flashThread = null;
	private JFrame frame = null;
	private DarkTowerPanel darkTowerPanel = null;
	private BoardPanel boardPanel = null;
	private JDesktopPane desktop = null;
	private ArrayList playerList = null;
	private ArrayList lastPlayerList = null;
	private JInternalFrame boardFrame = null;
	private JInternalFrame optionFrame = null;
	private JCheckBox[] enableCheckBox = null;
	private JCheckBox[] pcCheckBox = null;
	private JComboBox levelComboBox = null;
	private JComboBox battleComboBox = null;
	private JComboBox buildingComboBox = null;
	private JComboBox displayComboBox = null;
	private JLabel speedLabel = null;
	private Hashtable speedHashtable = null;
	private JSlider speedSlider = null;
	private JInternalFrame inventoryFrame = null;
	private JLabel[][] inventoryLabel = null;
	private JCheckBox[][] inventoryCheckBox = null;
	private JDialog aboutDialog = null;
	private int offset = 0;
	private int level = 0;

	public DarkTower()
	{
		System.out.println("init");

		// main frame
		frame = new JFrame("Dark Tower");
		frame.setIconImage(new ImageIcon(
			Util.class.getResource("images/darktower.gif")).getImage());
		frame.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					if ( flashThread != null )
						flashThread.interrupt();
					if ( thread != null )
						thread.interrupt();
					
					System.exit(0);
				}
			});
		desktop = new JDesktopPane();
		frame.setContentPane(desktop);

		
		// menu bar
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("New", KeyEvent.VK_N);
		menuItem.setActionCommand(Integer.toString(Button.NEW));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Board", KeyEvent.VK_B);
		menuItem.setActionCommand(Integer.toString(Button.BOARD));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Options", KeyEvent.VK_O);
		menuItem.setActionCommand(Integer.toString(Button.OPTIONS));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Inventory", KeyEvent.VK_I);
		menuItem.setActionCommand(Integer.toString(Button.INVENTORIES));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setActionCommand(Integer.toString(Button.EXIT));
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setActionCommand(Integer.toString(Button.ABOUT));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		
		// dark tower internal frame
		JInternalFrame darkTowerFrame = new JInternalFrame("Tower");
		darkTowerFrame.setFrameIcon(new ImageIcon(
			Util.class.getResource("images/darktower.gif")));
		Rectangle darkTowerRect = null;
		darkTowerFrame.getContentPane().setLayout(
			new BoxLayout(darkTowerFrame.getContentPane(), BoxLayout.Y_AXIS));
					
		// dark tower panel
		darkTowerPanel = new DarkTowerPanel();
		darkTowerPanel.setPreferredSize(new Dimension(180, 156));
		darkTowerFrame.getContentPane().add(darkTowerPanel);
		
		// button panel
		Font font = new Font("Arial", Font.PLAIN, 11);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 3));
		for (int i = 0; i < Button.getCount(); i++)
		{
			JButton button = Button.getButton(i + 1);
			Insets insets = button.getMargin();
			insets.left -= 3;
			insets.right -= 3;
			button.setMargin(insets);
			button.setActionCommand(Integer.toString(i + 1));
			button.setFont(font);
			button.addActionListener(this);
			buttonPanel.add(button);
		}
		darkTowerFrame.getContentPane().add(buttonPanel);
		darkTowerFrame.pack();
		darkTowerFrame.setResizable(false);
		darkTowerRect = darkTowerFrame.getBounds();
		desktop.add(darkTowerFrame);


		// board internal frame
		boardFrame = new JInternalFrame("Board",
			false, true, false, true);
		boardFrame.setFrameIcon(new ImageIcon(
			Util.class.getResource("images/board.gif")));
		boardFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		boardFrame.addInternalFrameListener(new InternalFrameAdapter()
			{
				public void internalFrameClosing(InternalFrameEvent e)
				{
					setCheckBoxEnabled( false );
					thread.reset();
					darkTowerPanel.requestFocus();
				}
			});
		Rectangle boardRect = null;
		
		// board panel
		boardPanel = new BoardPanel();
		boardPanel.setPreferredSize(new Dimension(500, 500));
		boardPanel.addMouseListener(this);
		boardPanel.addMouseMotionListener(this);
		boardFrame.setMinimumSize(new Dimension(282, 282));
		boardFrame.setContentPane(boardPanel);
		boardFrame.pack();
		boardFrame.setResizable(false);
		boardFrame.setLocation(darkTowerRect.width, 0);
		boardRect = boardFrame.getBounds();
		desktop.add(boardFrame);


		// init player list
		List territoryList = boardPanel.getTerritoryList();
		playerList = new ArrayList();
		playerList.add(new Player(0, playerList, territoryList, true, false, Player.NONEPC));
		playerList.add(new Player(1, playerList, territoryList, true, false, Player.PC));
		playerList.add(new Player(2, playerList, territoryList, false, false, Player.PC));
		playerList.add(new Player(3, playerList, territoryList, false, false, Player.PC));

		lastPlayerList = new ArrayList();
		lastPlayerList.add(new Player(0, playerList, territoryList, true, false, Player.NONEPC));
		lastPlayerList.add(new Player(1, playerList, territoryList, true, false, Player.PC));
		lastPlayerList.add(new Player(2, playerList, territoryList, false, false, Player.PC));
		lastPlayerList.add(new Player(3, playerList, territoryList, false, false, Player.PC));

		
		// options internal frame
		optionFrame = new JInternalFrame("Options",
			false, true, false, true);
		optionFrame.setFrameIcon(new ImageIcon(
			Util.class.getResource("images/darktower.gif")));
		optionFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		optionFrame.addInternalFrameListener(new InternalFrameAdapter()
			{
				public void internalFrameClosing(InternalFrameEvent e)
				{
					darkTowerPanel.requestFocus();
				}
			});

		// option panel
		JPanel optionPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		optionPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		enableCheckBox = new JCheckBox[4];
		pcCheckBox = new JCheckBox[4];
		for (int i = 0; i < 4; i++)
		{
			ImageIcon icon = MultiImage.getImageIcon(MultiImage.CLASSM);
			JLabel label = new JLabel(MultiImage.getSubImage(
				icon, MultiImage.CLASSM, MultiImage.PLAYER[i]));
			c.gridx = i;
			c.gridy = 0;
			gridbag.setConstraints(label, c);
			optionPanel.add(label);
			
			Player player = (Player) playerList.get(i);
//			enableCheckBox[i] = new JCheckBox("Player " + Integer.toString(i + 1));
			enableCheckBox[i] = new JCheckBox("Enabled");
			enableCheckBox[i].setFont(font);
			enableCheckBox[i].setSelected(player.isEnable());
			enableCheckBox[i].setActionCommand(Integer.toString(100 + i));
			enableCheckBox[i].addActionListener(this);
			c.gridx = i;
			c.gridy = 1;
			gridbag.setConstraints(enableCheckBox[i], c);
			optionPanel.add(enableCheckBox[i]);

			pcCheckBox[i] = new JCheckBox("Computer");
			pcCheckBox[i].setFont(font);
			pcCheckBox[i].setSelected(player.getPlayerType() == Player.PC);
			pcCheckBox[i].setActionCommand(Integer.toString(104 + i));
			pcCheckBox[i].addActionListener(this);
			c.gridx = i;
			c.gridy = 2;
			gridbag.setConstraints(pcCheckBox[i], c);
			optionPanel.add(pcCheckBox[i]);
		}

		JPanel comboBoxPanel = new JPanel();
		GridBagLayout comboBoxGridbag = new GridBagLayout();
		GridBagConstraints comboBoxC = new GridBagConstraints();
		comboBoxPanel.setLayout(comboBoxGridbag);

		JLabel label = new JLabel("Level");
		label.setFont(font);
		comboBoxC.anchor = GridBagConstraints.WEST;
		comboBoxC.insets = new Insets(0, 1, 0, 0);
		comboBoxC.gridx = 0;
		comboBoxC.gridy = 0;
		comboBoxGridbag.setConstraints(label, comboBoxC);
		comboBoxPanel.add(label);
		
		String[] levels = { "17 to 32 brigands to fight", "33 to 64 brigands to fight",
			"17 to 64 brigands to fight", "16 brigands to fight, all objects" };
		levelComboBox = new JComboBox(levels);
		levelComboBox.setFont(font);
		levelComboBox.setActionCommand(Integer.toString(Button.LEVEL));
		levelComboBox.addActionListener(this);
		comboBoxC.insets = new Insets(0, 4, 0, 0);
		comboBoxC.gridx = 1;
		comboBoxC.gridy = 0;
		comboBoxGridbag.setConstraints(levelComboBox, comboBoxC);
		comboBoxPanel.add(levelComboBox);

		label = new JLabel("Buildings");
		label.setFont(font);
		comboBoxC.insets = new Insets(0, 1, 0, 0);
		comboBoxC.gridx = 0;
		comboBoxC.gridy = 1;
		comboBoxGridbag.setConstraints(label, comboBoxC);
		comboBoxPanel.add(label);
		
		String[] buildings = { "Place buildings randomly", "Place buildings on original positions" };
		buildingComboBox = new JComboBox(buildings);
		buildingComboBox.setFont(font);
		buildingComboBox.setActionCommand(Integer.toString(Button.BUILDING));
		buildingComboBox.addActionListener(this);
		comboBoxC.insets = new Insets(0, 4, 0, 0);
		comboBoxC.gridx = 1;
		comboBoxC.gridy = 1;
		comboBoxGridbag.setConstraints(buildingComboBox, comboBoxC);
		comboBoxPanel.add(buildingComboBox);

		label = new JLabel("Battle");
		label.setFont(font);
		comboBoxC.insets = new Insets(0, 1, 0, 0);
		comboBoxC.gridx = 0;
		comboBoxC.gridy = 2;
		comboBoxGridbag.setConstraints(label, comboBoxC);
		comboBoxPanel.add(label);
		
		String[] battles = { "Easy", "Normal", "Difficult" };
		battleComboBox = new JComboBox(battles);
		battleComboBox.setFont(font);
		battleComboBox.setActionCommand(Integer.toString(Button.BATTLE));
		battleComboBox.addActionListener(this);
		comboBoxC.insets = new Insets(0, 4, 0, 0);
		comboBoxC.gridx = 1;
		comboBoxC.gridy = 2;
		comboBoxGridbag.setConstraints(battleComboBox, comboBoxC);
		comboBoxPanel.add(battleComboBox);

		label = new JLabel("Display");
		label.setFont(font);
		comboBoxC.insets = new Insets(0, 1, 0, 0);
		comboBoxC.gridx = 0;
		comboBoxC.gridy = 3;
		comboBoxGridbag.setConstraints(label, comboBoxC);
		comboBoxPanel.add(label);
		
		String[] displays = { "Show computer actions", "Don't show computer actions" };
		displayComboBox = new JComboBox(displays);
		displayComboBox.setFont(font);
		displayComboBox.setActionCommand(Integer.toString(Button.DISPLAY));
		displayComboBox.addActionListener(this);
		comboBoxC.insets = new Insets(0, 4, 0, 0);
		comboBoxC.gridx = 1;
		comboBoxC.gridy = 3;
		comboBoxGridbag.setConstraints(displayComboBox, comboBoxC);
		comboBoxPanel.add(displayComboBox);

		speedLabel = new JLabel("Speed");
		speedLabel.setFont(font);
		comboBoxC.anchor = GridBagConstraints.NORTHWEST;
		comboBoxC.insets = new Insets(4, 1, 0, 0);
		comboBoxC.gridx = 0;
		comboBoxC.gridy = 4;
		comboBoxGridbag.setConstraints(speedLabel, comboBoxC);
		comboBoxPanel.add(speedLabel);

		speedHashtable = new Hashtable();
		label = new JLabel("Normal");
		label.setFont(font);
		speedHashtable.put(new Integer(100), label);
		label = new JLabel("Middle");
		label.setFont(font);
		speedHashtable.put(new Integer(50), label);
		label = new JLabel("Fast");
		label.setFont(font);
		speedHashtable.put(new Integer(0), label);
		speedSlider = new JSlider(0, 100, 100);
		speedSlider.setFont(font);
		speedSlider.setLabelTable(speedHashtable);
		speedSlider.setInverted(true);
		speedSlider.setSnapToTicks(true);
		speedSlider.setMajorTickSpacing(25);
		speedSlider.setMinorTickSpacing(5);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		comboBoxC.insets = new Insets(0, 4, 0, 0);
		comboBoxC.gridx = 1;
		comboBoxC.gridy = 4;
		comboBoxGridbag.setConstraints(speedSlider, comboBoxC);
		comboBoxPanel.add(speedSlider);

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(comboBoxPanel, c);
		optionPanel.add(comboBoxPanel);

		optionFrame.getContentPane().add(optionPanel);
		optionFrame.pack();
		Insets insets = optionFrame.getInsets();
		offset = optionFrame.getMinimumSize().height - insets.top;
		optionFrame.setLocation(offset, offset);
		desktop.add(optionFrame);


		// inventory internal frame
		inventoryFrame = new JInternalFrame("Inventory",
			false, true, false, true);
		inventoryFrame.setFrameIcon(new ImageIcon(
			Util.class.getResource("images/board.gif")));
		inventoryFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		inventoryFrame.addInternalFrameListener(new InternalFrameAdapter()
			{
				public void internalFrameClosing(InternalFrameEvent e)
				{
					darkTowerPanel.requestFocus();
				}
			});

		// inventory panel
		JPanel inventoryPanel = new JPanel();
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		inventoryPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		inventoryLabel = new JLabel[5][3];
		inventoryCheckBox = new JCheckBox[4][8];
		for (int i = 0; i < 4; i++)
		{
			ImageIcon icon = MultiImage.getImageIcon(MultiImage.CLASSM);
			label = new JLabel(MultiImage.getSubImage(
				icon, MultiImage.CLASSM, MultiImage.PLAYER[i]));
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 0;
			gridbag.setConstraints(label, c);
			inventoryPanel.add(label);
			
			Player player = (Player) playerList.get(i);
			inventoryLabel[i][0] = new JLabel(Integer.toString(player.getWarriors()),
				SwingConstants.RIGHT);
			inventoryLabel[i][0].setFont(font);
			inventoryLabel[i][0].setPreferredSize(new Dimension(14, 21));
			c.gridwidth = 1;
			c.gridx = i * 2;
			c.gridy = 1;
			gridbag.setConstraints(inventoryLabel[i][0], c);
			inventoryPanel.add(inventoryLabel[i][0]);
			
			label = new JLabel(" Warriors");
			label.setFont(font);
			label.setPreferredSize(new Dimension(60, 21));
			c.gridwidth = 1;
			c.gridx = i * 2 + 1;
			c.gridy = 1;
			gridbag.setConstraints(label, c);
			inventoryPanel.add(label);


			inventoryLabel[i][1] = new JLabel(Integer.toString(player.getGold()),
				SwingConstants.RIGHT);
			inventoryLabel[i][1].setFont(font);
			inventoryLabel[i][1].setPreferredSize(new Dimension(14, 21));
			c.gridwidth = 1;
			c.gridx = i * 2;
			c.gridy = 2;
			gridbag.setConstraints(inventoryLabel[i][1], c);
			inventoryPanel.add(inventoryLabel[i][1]);
			
			label = new JLabel(" Gold");
			label.setFont(font);
			label.setPreferredSize(new Dimension(60, 21));
			c.gridwidth = 1;
			c.gridx = i * 2 + 1;
			c.gridy = 2;
			gridbag.setConstraints(label, c);
			inventoryPanel.add(label);


			inventoryLabel[i][2] = new JLabel(Integer.toString(player.getFood()),
				SwingConstants.RIGHT);
			inventoryLabel[i][2].setFont(font);
			inventoryLabel[i][2].setPreferredSize(new Dimension(14, 21));
			c.gridwidth = 1;
			c.gridx = i * 2;
			c.gridy = 3;
			gridbag.setConstraints(inventoryLabel[i][2], c);
			inventoryPanel.add(inventoryLabel[i][2]);
			
			label = new JLabel(" Food");
			label.setFont(font);
			label.setPreferredSize(new Dimension(60, 21));
			c.gridwidth = 1;
			c.gridx = i * 2 + 1;
			c.gridy = 3;
			gridbag.setConstraints(label, c);
			inventoryPanel.add(label);


			inventoryCheckBox[i][0] = new JCheckBox("Beast");
			inventoryCheckBox[i][0].setSelected(player.hasBeast());
			inventoryCheckBox[i][0].setFont(font);
			inventoryCheckBox[i][0].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][0].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 4;
			gridbag.setConstraints(inventoryCheckBox[i][0], c);
			inventoryPanel.add(inventoryCheckBox[i][0]);
			

			inventoryCheckBox[i][1] = new JCheckBox("Scout");
			inventoryCheckBox[i][1].setSelected(player.hasScout());
			inventoryCheckBox[i][1].setFont(font);
			inventoryCheckBox[i][1].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][1].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 5;
			gridbag.setConstraints(inventoryCheckBox[i][1], c);
			inventoryPanel.add(inventoryCheckBox[i][1]);


			inventoryCheckBox[i][2] = new JCheckBox("Healer");
			inventoryCheckBox[i][2].setSelected(player.hasHealer());
			inventoryCheckBox[i][2].setFont(font);
			inventoryCheckBox[i][2].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][2].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 6;
			gridbag.setConstraints(inventoryCheckBox[i][2], c);
			inventoryPanel.add(inventoryCheckBox[i][2]);


			inventoryCheckBox[i][3] = new JCheckBox("Sword");
			inventoryCheckBox[i][3].setSelected(player.hasDragonSword());
			inventoryCheckBox[i][3].setFont(font);
			inventoryCheckBox[i][3].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][3].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 7;
			gridbag.setConstraints(inventoryCheckBox[i][3], c);
			inventoryPanel.add(inventoryCheckBox[i][3]);


			inventoryCheckBox[i][4] = new JCheckBox("Pegasus");
			inventoryCheckBox[i][4].setSelected(player.hasPegasus());
			inventoryCheckBox[i][4].setFont(font);
			inventoryCheckBox[i][4].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][4].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 8;
			gridbag.setConstraints(inventoryCheckBox[i][4], c);
			inventoryPanel.add(inventoryCheckBox[i][4]);
			

			inventoryCheckBox[i][5] = new JCheckBox("Brass Key");
			inventoryCheckBox[i][5].setSelected(player.hasBrassKey());
			inventoryCheckBox[i][5].setFont(font);
			inventoryCheckBox[i][5].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][5].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 9;
			gridbag.setConstraints(inventoryCheckBox[i][5], c);
			inventoryPanel.add(inventoryCheckBox[i][5]);


			inventoryCheckBox[i][6] = new JCheckBox("Silver Key");
			inventoryCheckBox[i][6].setSelected(player.hasSilverKey());
			inventoryCheckBox[i][6].setFont(font);
			inventoryCheckBox[i][6].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][6].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 10;
			gridbag.setConstraints(inventoryCheckBox[i][6], c);
			inventoryPanel.add(inventoryCheckBox[i][6]);


			inventoryCheckBox[i][7] = new JCheckBox("Gold Key");
			inventoryCheckBox[i][7].setSelected(player.hasGoldKey());
			inventoryCheckBox[i][7].setFont(font);
			inventoryCheckBox[i][7].setActionCommand(Integer.toString(200));
			inventoryCheckBox[i][7].addActionListener(this);
			c.gridwidth = 2;
			c.gridx = i * 2;
			c.gridy = 11;
			gridbag.setConstraints(inventoryCheckBox[i][7], c);
			inventoryPanel.add(inventoryCheckBox[i][7]);
		}

		ImageIcon icon = MultiImage.getImageIcon(MultiImage.DRAGON);
		label = new JLabel(MultiImage.getSubImage(
			icon, MultiImage.DRAGON, 28));
		c.gridwidth = 2;
		c.gridx = 4 * 2;
		c.gridy = 0;
		gridbag.setConstraints(label, c);
		inventoryPanel.add(label);

		inventoryLabel[4][0] = new JLabel("0", SwingConstants.RIGHT);
		inventoryLabel[4][0].setFont(font);
		inventoryLabel[4][0].setPreferredSize(new Dimension(14, 21));
		c.gridwidth = 1;
		c.gridx = 4 * 2;
		c.gridy = 1;
		gridbag.setConstraints(inventoryLabel[4][0], c);
		inventoryPanel.add(inventoryLabel[4][0]);

		label = new JLabel(" Warriors");
		label.setFont(font);
		label.setPreferredSize(new Dimension(60, 21));
		c.gridwidth = 1;
		c.gridx = 4 * 2 + 1;
		c.gridy = 1;
		gridbag.setConstraints(label, c);
		inventoryPanel.add(label);


		inventoryLabel[4][1] = new JLabel("0", SwingConstants.RIGHT);
		inventoryLabel[4][1].setFont(font);
		inventoryLabel[4][1].setPreferredSize(new Dimension(14, 21));
		c.gridwidth = 1;
		c.gridx = 4 * 2;
		c.gridy = 2;
		gridbag.setConstraints(inventoryLabel[4][1], c);
		inventoryPanel.add(inventoryLabel[4][1]);

		label = new JLabel(" Gold");
		label.setFont(font);
		label.setPreferredSize(new Dimension(60, 21));
		c.gridwidth = 1;
		c.gridx = 4 * 2 + 1;
		c.gridy = 2;
		gridbag.setConstraints(label, c);
		inventoryPanel.add(label);


		inventoryFrame.getContentPane().add(inventoryPanel);
		inventoryFrame.pack();
		insets = inventoryFrame.getInsets();
		offset = inventoryFrame.getMinimumSize().height - insets.top;
		inventoryFrame.setLocation(offset * 2, offset * 2);
		desktop.add(inventoryFrame);


		frame.pack();
		insets = frame.getInsets();
		frame.setSize(darkTowerRect.width + boardRect.width + insets.left + insets.right,
			boardRect.height + insets.top + insets.bottom + menuBar.getHeight());
		frame.setResizable(true);

		
		// about dialog
		aboutDialog = new JDialog(frame, "About", true);
		aboutDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		aboutDialog.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					darkTowerPanel.requestFocus();
				}
			});

		// about panel
		JPanel aboutPanel = new JPanel();
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		aboutPanel.setLayout(gridbag);
		
		icon = new ImageIcon(Util.class.getResource("images/logo.gif"));
		label = new JLabel(icon, SwingConstants.CENTER);
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(label, c);
		aboutPanel.add(label);

		label = new JLabel();
		c.ipady = 8;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(label, c);
		aboutPanel.add(label);

		label = new JLabel("Dark Tower v1.02");
		label.setFont(font);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(label, c);
		aboutPanel.add(label);

		label = new JLabel("© 2002 Michael Bommer - m_bommer@yahoo.de");
		label.setFont(font);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(label, c);
		aboutPanel.add(label);

		label = new JLabel();
		c.ipady = 8;
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(label, c);
		aboutPanel.add(label);

		JButton button = new JButton("OK");
		button.setFont(font);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 5;
		gridbag.setConstraints(button, c);
		aboutPanel.add(button);
		button.addActionListener(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					aboutDialog.setVisible(false);
				}
			});
		aboutDialog.getRootPane().setDefaultButton(button);

		label = new JLabel();
		c.ipady = 4;
		c.gridx = 0;
		c.gridy = 6;
		gridbag.setConstraints(label, c);
		aboutPanel.add(label);

		aboutDialog.getContentPane().add(aboutPanel);
		aboutDialog.pack();
//		aboutDialog.setSize(aboutDialog.getWidth() + 8,
//			aboutDialog.getHeight() + 8);

		// init dark tower thread
		thread = new DarkTowerThread(this);
		thread.start();

		// init flash thread
		flashThread = new FlashThread(this);
		flashThread.start();

		// make visible
		boardFrame.setVisible(true);
		darkTowerFrame.setVisible(true);
		frame.setVisible(true);
	}

	public void setInventory()
	{
		for (int i = 0; i < 4; i++)
		{
			Player player = (Player) playerList.get(i);

			inventoryLabel[i][0].setText(Integer.toString(player.getWarriors()));
			inventoryLabel[i][1].setText(Integer.toString(player.getGold()));
			inventoryLabel[i][2].setText(Integer.toString(player.getFood()));

			inventoryCheckBox[i][0].setSelected(player.hasBeast());
			inventoryCheckBox[i][1].setSelected(player.hasScout());
			inventoryCheckBox[i][2].setSelected(player.hasHealer());
			inventoryCheckBox[i][3].setSelected(player.hasDragonSword());
			inventoryCheckBox[i][4].setSelected(player.hasPegasus());
			inventoryCheckBox[i][5].setSelected(player.hasBrassKey());
			inventoryCheckBox[i][6].setSelected(player.hasSilverKey());
			inventoryCheckBox[i][7].setSelected(player.hasGoldKey());
		}

		Dragon dragon = thread.getDragon();
		inventoryLabel[4][0].setText(Integer.toString(dragon.getWarriors()));
		inventoryLabel[4][1].setText(Integer.toString(dragon.getGold()));
	}

	public void setCheckBoxEnabled(boolean enabled)
	{
		pcCheckBox[0].setEnabled(enabled);
		pcCheckBox[1].setEnabled(enabled);
		pcCheckBox[2].setEnabled(enabled);
		pcCheckBox[3].setEnabled(enabled);
	}

	public void setSliderEnabled(boolean enabled)
	{
		speedLabel.setEnabled(enabled);
		speedSlider.setEnabled(enabled);

		JLabel label = null;
		Enumeration keys = speedHashtable.keys();
		while ( keys.hasMoreElements() )
		{
			label = (JLabel) speedHashtable.get(keys.nextElement());
			label.setEnabled(enabled);
		}
	}

	// action listener
	public void actionPerformed(ActionEvent e)
	{
		Player player =  null;
		int action = Util.getInteger(e.getActionCommand());

		if ( thread != null )
		{
			switch ( action )
			{
				// options
				case 100:
				case 101:
				case 102:
				case 103:
					player = (Player) playerList.get(action - 100);
					if ( ( player.getPlayerCount() != 1 ) || ( !player.isEnable() ) )
						player.setEnable(!player.isEnable());
					else
						enableCheckBox[action - 100].setSelected(true);
					break;
				case 104:
				case 105:
				case 106:
				case 107:
					player = (Player) playerList.get(action - 104);
					player.setPlayerType(player.getPlayerType() == Player.PC ?
						Player.NONEPC : Player.PC);
					break;
				case 200:
					setInventory();
					break;
				case Button.LEVEL:
					if ( getLevel() != level )
					{
						level = getLevel();
						for (int i = 0; i < 4; i++)
						{
							player = (Player) playerList.get(i);
							player.initObjectList(getLevel() == 3);
							player.initRiddleKeyList(getLevel() == 3);
							setInventory();
						}
					}
					break;
				case Button.BUILDING:
					break;
				case Button.BATTLE:
					break;
				case Button.DISPLAY:
					setSliderEnabled( getDisplay() == 0 );
					break;
				// menu
				case Button.NEW:
					thread.reset();
					break;
				case Button.BOARD:
					if ( !boardFrame.isVisible() )
					{
						boardFrame.setVisible( true );
						setCheckBoxEnabled( true );
						thread.reset();
					}
					break;
				case Button.OPTIONS:
					optionFrame.setVisible(true);
					break;
				case Button.INVENTORIES:
					inventoryFrame.setVisible(true);
					break;
				case Button.ABOUT:
					Point point = frame.getLocation();
					aboutDialog.setLocation(point.x + offset, point.y + offset);
					aboutDialog.pack();
					aboutDialog.setVisible(true);
					break;
				case Button.EXIT:
					if ( thread != null )
						thread.interrupt();
					System.exit(0);
					break;
				// dark tower
				default:
					thread.addAction(action);
					break;
			}
		}
	}

	// mouse listener
	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if ( thread != null )
			thread.addMouseAction(new MouseAction(
				MouseAction.CLICKED, e.getX(), e.getY()));
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
		if ( thread != null )
			thread.addMouseAction(new MouseAction(
				MouseAction.EXITED, e.getX(), e.getY()));
	}

	// mouse motion listener
	public void mouseDragged(MouseEvent e)
	{
	}
	
	public void mouseMoved(MouseEvent e)
	{
		if ( thread != null )
			thread.addMouseAction(new MouseAction(
				MouseAction.MOVED, e.getX(), e.getY()));
	}
	
	public List getPlayerList()
	{
		return playerList;
	}

	public List getLastPlayerList()
	{
		return lastPlayerList;
	}

	public DarkTowerPanel getDarkTowerPanel()
	{
		return darkTowerPanel;
	}

	public BoardPanel getBoardPanel()
	{
		return boardPanel;
	}

	public JInternalFrame getBoardFrame()
	{
		return boardFrame;
	}

	public int getLevel()
	{
		return levelComboBox.getSelectedIndex();
	}

	public int getBattle()
	{
		return battleComboBox.getSelectedIndex();
	}

	public int getDisplay()
	{
		return displayComboBox.getSelectedIndex();
	}

	public int getSpeed()
	{
		return speedSlider.getValue();
	}

	public boolean placeBuildingsRandomly()
	{
		return ( buildingComboBox.getSelectedIndex() == 0 );
	}
	
	public static void main(String[] args)
	{
		new DarkTower();
	}		
}
