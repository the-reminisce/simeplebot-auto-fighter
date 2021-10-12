package me.remie.xeros.combat;

import simple.api.wrappers.SimpleNpc;
import simple.api.wrappers.definitions.ItemDefinition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AutoFighterUI {

	public AutoFighter scombat;
	private JPanel contentPane;
	public JFrame frame;
	
	public JPanel generalPanel;
	public JComboBox<SFood> comboBoxFoods;
	public JSpinner spinnerHealAt;
	public JButton btnApplyNewSettings;
	public JButton btnCloseGui;
	public JList<String> listNearbyMonsters;
	public JLabel lblMonsterList;
	public JList<String> listSelectedMonsters;
	public JScrollPane scrollPaneNearbyMonsters;
	public JScrollPane scrollPaneSelectedMonsters;
	public JList<String> listLoot;
	public JScrollPane scrollPaneLoot;
	private JCheckBox chckbxEatforSpace;
	private JButton btnClearLoots;
	private JCheckBox chckbxTeleOnTask;
	private JCheckBox chbxQuickPrayers;
	
	public DefaultListModel<String> modelLoot = new DefaultListModel<String>();
	public DefaultListModel<String> modelNearbyMonsters = new DefaultListModel<String>();
	public DefaultListModel<String> modelSelectedMonsters = new DefaultListModel<String>();
	
	public AutoFighterUI(AutoFighter scombat) {
		try {
			this.scombat = scombat;
			SwingUtilities.invokeLater(new Runnable() {  public void run() {

			frame = new JFrame();
			frame.setResizable(false);
			frame.setTitle("AIO Combat");
			frame.setBounds(100, 100, 450, 460);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			frame.setContentPane(contentPane);
			contentPane.setLayout(null);
			frame.setLocationRelativeTo(scombat.ctx.mouse.getComponent());
			initComponents();
			frame.repaint();
			setupHerbArray();
			onRefreshNearbyMonsters();
			frame.setVisible(true);
				     }
			   });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initComponents() {
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 414, 369);
		contentPane.add(tabbedPane);
		
		generalPanel = new JPanel();
		generalPanel.setName("");
		tabbedPane.addTab("General", null, generalPanel, null);
		generalPanel.setLayout(null);
		
		btnApplyNewSettings = new JButton("Apply New Settings");
		btnApplyNewSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onApplyNewSettings();
			}
		});
		btnApplyNewSettings.setBounds(200, 391, 125, 23);
		contentPane.add(btnApplyNewSettings);
		
		btnCloseGui = new JButton("Close GUI");
		btnCloseGui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCloseGUI();
			}
		});
		btnCloseGui.setBounds(335, 391, 89, 23);
		contentPane.add(btnCloseGui);
		
		JLabel lblFoodType = new JLabel("Food Type:");
		lblFoodType.setBounds(10, 11, 81, 20);
		generalPanel.add(lblFoodType);
		
		spinnerHealAt = new JSpinner();
		spinnerHealAt.setModel(new SpinnerNumberModel(55, 1, 99, 1));
		spinnerHealAt.setBounds(276, 11, 47, 20);
		generalPanel.add(spinnerHealAt);
		
		comboBoxFoods = new JComboBox<SFood>(new DefaultComboBoxModel<SFood>(SFood.values()));
		comboBoxFoods.setBounds(93, 11, 91, 20);
		generalPanel.add(comboBoxFoods);
		
		JLabel lblHealAt = new JLabel("Heal At:");
		lblHealAt.setBounds(208, 11, 68, 20);
		generalPanel.add(lblHealAt);
		
		JLabel lblNearbyMonsters = new JLabel("Nearby Monsters");
		lblNearbyMonsters.setBounds(10, 44, 96, 19);
		generalPanel.add(lblNearbyMonsters);
		
		JButton btnRefreshNearbyMonsters = new JButton("Refresh");
		btnRefreshNearbyMonsters.setBounds(118, 44, 89, 23);
		generalPanel.add(btnRefreshNearbyMonsters);
		
		lblMonsterList = new JLabel("Monster List");
		lblMonsterList.setBounds(216, 44, 119, 19);
		generalPanel.add(lblMonsterList);
		
		scrollPaneNearbyMonsters = new JScrollPane();
		scrollPaneNearbyMonsters.setBounds(10, 74, 187, 124);
		generalPanel.add(scrollPaneNearbyMonsters);
		
		listNearbyMonsters = new JList<String>(modelNearbyMonsters);
		scrollPaneNearbyMonsters.setViewportView(listNearbyMonsters);
		listNearbyMonsters.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listNearbyMonsters.getSelectedIndex() == listNearbyMonsters.locationToIndex(e.getPoint()) && e.getClickCount() >= 2) {
					String element = listNearbyMonsters.getSelectedValue();
					modelNearbyMonsters.removeElement(element);
					modelSelectedMonsters.addElement(element);
				}
			}
		});
		
		listSelectedMonsters = new JList<String>(modelSelectedMonsters);
		listSelectedMonsters.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listSelectedMonsters.getSelectedIndex() == listSelectedMonsters.locationToIndex(e.getPoint()) && e.getClickCount() >= 2) {
					String element = listSelectedMonsters.getSelectedValue();
					modelSelectedMonsters.removeElement(element);
				}
			}
		});
		listSelectedMonsters.setBounds(193, 145, 206, 185);
		
		scrollPaneSelectedMonsters = new JScrollPane();
		scrollPaneSelectedMonsters.setBounds(207, 74, 192, 124);
		generalPanel.add(scrollPaneSelectedMonsters);
		scrollPaneSelectedMonsters.setViewportView(listSelectedMonsters);
		
		JLabel lblItemsToLoot = new JLabel("Items To Loot");
		lblItemsToLoot.setBounds(10, 206, 109, 14);
		generalPanel.add(lblItemsToLoot);
		
		JButton btnAddLoot = new JButton("+");
		btnAddLoot.setBounds(212, 246, 54, 23);
		generalPanel.add(btnAddLoot);
		btnAddLoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String input = JOptionPane.showInputDialog("Enter itemId");
				if (input.contains(",")) {
					String[] items = input.trim().split(",");
					for (String id : items) {
						ItemDefinition defs = scombat.ctx.definitions.getItemDefinition(Integer.parseInt(id));
						if (defs != null) {
							modelLoot.addElement("[" + defs.getName() + ", " + defs.getId() + "]");
						}
					}
				} else {
					ItemDefinition defs = scombat.ctx.definitions.getItemDefinition(Integer.parseInt(input));
					if (defs != null) {
						modelLoot.addElement("[" + defs.getName() + ", " + defs.getId() + "]");
					}
				}

			}
		});
		
		JButton btnRemoveLoot = new JButton("-");
		btnRemoveLoot.setBounds(212, 280, 54, 23);
		generalPanel.add(btnRemoveLoot);
		btnRemoveLoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelLoot.removeElement(listLoot.getSelectedValue());
			}
		});

		chbxQuickPrayers = new JCheckBox("Quick prayers");
		chbxQuickPrayers.setToolTipText("Drinks prayer pots in inv and enables quick prayers");
		chbxQuickPrayers.setBounds(210, 310, 200, 23);
		generalPanel.add(chbxQuickPrayers);
		
		scrollPaneLoot = new JScrollPane();
		scrollPaneLoot.setBounds(10, 223, 187, 107);
		generalPanel.add(scrollPaneLoot);
		
		listLoot = new JList<String>(modelLoot);
		scrollPaneLoot.setViewportView(listLoot);
		
		chckbxEatforSpace = new JCheckBox("Eat for Space");
		chckbxEatforSpace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scombat.eatForSpace = chckbxEatforSpace.isSelected();
			}
		});
		chckbxEatforSpace.setBounds(274, 280, 127, 23);
		generalPanel.add(chckbxEatforSpace);
		
		btnClearLoots = new JButton("Clear Loots");
		btnClearLoots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				modelLoot.clear();
			}
		});
		btnClearLoots.setBounds(290, 246, 109, 23);
		generalPanel.add(btnClearLoots);
		
		chckbxTeleOnTask = new JCheckBox("Tele on Task Finish");
		chckbxTeleOnTask.setBounds(210, 214, 189, 23);
		generalPanel.add(chckbxTeleOnTask);
		
		btnRefreshNearbyMonsters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						onRefreshNearbyMonsters();
					}
				});
			}
		});
	}
	
	public void onApplyNewSettings() {
		List<Integer> npcIdList = new ArrayList<Integer>();
		for (Object npc : modelSelectedMonsters.toArray()) {
			if (!(npc instanceof String)) {
				continue;
			}
			String npcInfo = (String) npc;
			npcIdList.add(Integer.valueOf(npcInfo.split(",")[2].replace(" ", "").replace("]", "")));
		}
		int[] npcIds = new int[npcIdList.size()];
		for (int i = 0; i < npcIds.length; i++) {
			npcIds[i] = npcIdList.get(i);
		}
		SFood foodType = (SFood) comboBoxFoods.getSelectedItem();
		if (foodType == SFood.NONE) {
			scombat.setupEating(null, 50);
		} else {
			scombat.setupEating(foodType.getItemId(), (Integer) spinnerHealAt.getValue());
		}
		int[] lootNames = new int[modelLoot.size()];
		if (modelLoot.size() > 0) {
			lootNames = new int[modelLoot.size()];
			for (int i = 0; i < lootNames.length; i++) {
				Pattern p = Pattern.compile("\\[(.*?),\\s(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher m = p.matcher(modelLoot.get(i));
				if (m.find()) {
					lootNames[i] = Integer.parseInt(m.group(2));
			    }
			}
			scombat.setupLooting(lootNames);
		}
		scombat.setupLooting(lootNames);
		scombat.setupAttacking(npcIds);
		scombat.eatForSpace = chckbxEatforSpace.isSelected();
		scombat.quickPrayers = chbxQuickPrayers.isSelected();
		scombat.started = true;
	}
	
	public void onCloseGUI() {
		frame.dispose();
	}
	
	public void onRefreshNearbyMonsters() {
		this.modelNearbyMonsters.clear();

		for (SimpleNpc npc : scombat.ctx.npcs.populate().filterHasAction("Attack")) {
			if (!this.modelNearbyMonsters.contains("[" + npc.getName() + ", " + npc.getCombatLevel() + ", " + npc.getId() + "]") && !this.modelSelectedMonsters.contains("[" + npc.getName() + ", " + npc.getCombatLevel() + ", " + npc.getId() + "]")) {
				this.modelNearbyMonsters.addElement("[" + npc.getName() + ", " + npc.getCombatLevel() + ", " + npc.getId() + "]");
			}
		}
	}
	
	public void setupHerbArray() {
		for (int i : UNIVERSAL_LOOT_IDS) {
			ItemDefinition defs = scombat.ctx.definitions.getItemDefinition(i);
			if (defs != null) {
				modelLoot.addElement("[" + defs.getName() + ", " + defs.getId() + "]");
			}
		}
		if (modelLoot.isEmpty()) {
			for (int i : UNIVERSAL_LOOT_IDS) {
				ItemDefinition defs = scombat.ctx.definitions.getItemDefinition(i);
				if (defs != null) {
					modelLoot.addElement("[" + defs.getName() + ", " + defs.getId() + "]");
				}
			}
		}
	}
	
	private static final int[] UNIVERSAL_LOOT_IDS = { 30084, 1626, 1628, 1630, 1624, 1622, 1620, 1618, 1632, 19677, 985, 987, 1147, 1373,
			1319, 1185, 454, 443, 1149, 1201, 1249, 1163, 568, 2362, 1514, 441, 1392, 4585, 4085, 12073, 11286, 5295,
			5300, 1347, 6812, 1359, 6809, 1215, 3140, 20736, 2360, 11840, 226, 1113, 4103, 1127, 1289, 1780, 533, 240,
			4111, 20727, 11902, 4151, 13265, 7979, 3202, 12655, 11905, 384, 402, 7945, 8779, 3052, 989, 12007, 12004,
			11908, 11235, 7185, 1079, 1305, 9193, 392, 452, 1516, 1748, 8783, 5316, 21817, 3025, 2364, 405, 12644, 6729,
			6562, 6739, 6139, 6141, 6731, 12645, 448, 6737, 6735, 2354, 6724, 6733, 21637, 6334, 2358, 445, 4857, 5304,
			5303, 5298, 5296, 12002, 11998, 4675, 9738, 3050, 1988, 20724, 20730, 21270, 6529, 9194, 6528, 6524, 6568,
			21298, 21301, 21304, 6526, 6522, 6523, 6525, 2722, 13233, 20718, 8921, 5516, 1128, 1290, 1164, 1202, 1398,
			1406, 3055, 576, 214, 2486, 216, 268, 2, 13273, 208, 21918, 22100, 22097, 11824, 11818, 11820, 11822, 246,
			11037, 10887, 961, 13307, 6686, 12696, 386, 13442, 8781, 990, 2367, 2369, 1374, 1320, 1186, 537, 450, 11212,
			21905, 1306, 4588, 1216, 1435, 21259, 12783, 12786, 6922, 6571, 12851, 6889, 6918, 6916, 6924, 4716, 4718,
			4720, 4722, 4753, 4755, 4757, 4759, 4708, 4710, 4712, 4714, 4745, 4747, 4749, 4751, 4724, 4726, 4728, 4730,
			4732, 4734, 4736, 4738, 6920, 21807, 21810, 21813, 22299, 22302, 22305, 11812, 11816, 11814, 11810, 22557,
			22552, 22542, 22547, 12789, 12849, 4109, 224, 1752, 11838, 238, 232, 2350, 11993, 1754, 1750, 6694, 6035,
			5975, 12603, 5315, 12746, 11940, 11920, 1976, 236, 13247, 570, 13249, 13231, 13229, 13227, 13245, 1776, 465,
			11995, 202, 200, 204, 206, 210, 212, 218, 11931, 11928, 2801, 4117, 12816, 12829, 9245, 7061, 5955, 12833,
			12823, 12827, 12819, 1093, 12651, 11785, 13256, 222, 11990, 11929, 11932, 19529, 19592, 19601, 19586, 19589,
			19610, 5317, 5314, 21748, 21739, 21736, 21730, 21745, 8788, 20595, 20517, 20520, 248, 11826, 11828, 11830,
			12650, 11832, 11834, 11836, 12646, 20544, 20545, 20546, 2677, 2971, 242, 9016, 3139, 12647, 6017, 7981,
			12885, 12653, 7980, 12549, 12652, 11791, 11992, 7400, 7399, 7398, 10602, 4107, 11335, 21802, 1432, 22103,
			13181, 2298, 11930, 11933, 19685, 19701, 13440, 1094, 21273, 13225, 13177, 12605, 21992, 22124, 1377, 11237,
			2425, 22006, 22106, 22111, 12921, 12934, 6290, 12938, 12922, 12932, 12927, 3204, 2999, 3001, 270, 4207,
			12936, 13200, 13201, 4587, 4087, 405 };
	
}
