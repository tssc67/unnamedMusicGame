package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import me.sunchiro.game.Game;
import me.sunchiro.game.engine.ConfigurationOption;

public class ConfigFrame extends JFrame {

	public ConfigFrame() throws Exception {
		setTitle("in_the_Box");
		setLayout(new GridBagLayout());
		JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 0;
		Class cls = Class.forName("main.ConfigFrame");
		ClassLoader cLoader = cls.getClassLoader();
		add(new JLabel(new ImageIcon(cLoader.getResource("textures/InTheBox300.png"))),c);
		JButton launch = new JButton("Launch!");

		c.gridy = 5;
		container.add(launch,c);
		JLabel limitLbl = new JLabel("Object Limit :");
		JLabel widthLbl = new JLabel("Width :");
		JLabel heightLbl = new JLabel("Height :");
		JLabel godLbl = new JLabel("God Mode :");
		c.gridwidth =1;
		c.gridy = 1;
		container.add(limitLbl,c);
		c.gridy = 2;
		container.add(widthLbl,c);
		c.gridy = 3;
		container.add(heightLbl,c);
		c.gridy = 4;
		container.add(godLbl,c);
		
		
		JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(1500,500,3000,100));
		JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(800,600,3840,128));
		JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(600,480,2160,128));
		JCheckBox godCB = new JCheckBox();
		c.gridx = 1;
		c.gridy = 1;
		container.add(limitSpinner,c);
		c.gridy = 2;
		container.add(widthSpinner,c);
		c.gridy = 3;
		container.add(heightSpinner,c);
		c.gridy = 4;
		container.add(godCB,c);
		
		c.insets = new Insets(20, 20, 20, 20);
		add(container,c);
		launch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationOption.godMode = godCB.isSelected();
				ConfigurationOption.screenHeight = (int)heightSpinner.getValue();
				ConfigurationOption.screenWidth = (int)widthSpinner.getValue();
				ConfigurationOption.objectCount = (int)limitSpinner.getValue();
				setVisible(false);
				new Game();
			}
		});
	}
}
