package com.application.configmanage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.awt.*;

class AppSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, String> settings;
	private String lastModified;
	
	public AppSettings() {
		settings = new HashMap<>();
		lastModified = java.time.LocalDateTime.now().toString();
	}
	
	public void setSetting(String key, String value) {
		settings.put(key, value);
		lastModified = java.time.LocalDateTime.now().toString();
	}
	
	public String getSetting(String key) {
		return settings.get(key);
	}
	
	public String getLastModified() {
		return lastModified;
	}
}

public class ConfigurationManager extends JFrame {
	private AppSettings settings;
	private JTextField themeField, fontSizeField, languageField;
	private static final String SAVE_FILE = "app_config.dat";
	
	public ConfigurationManager() {
		settings = loadSettings();
		if(settings == null) {
			settings = new AppSettings();
		}
		
		setTitle("Application Configuration Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));
		
		JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		inputPanel.add(new JLabel("Theme:"));
		themeField = new JTextField(settings.getSetting("theme"));
		inputPanel.add(themeField);
		
		inputPanel.add(new JLabel("Font Size:"));
		fontSizeField = new JTextField(settings.getSetting("fontSize"));
		inputPanel.add(fontSizeField);
		
		inputPanel.add(new JLabel("Language:"));
        languageField = new JTextField(settings.getSetting("language"));
        inputPanel.add(languageField);
        
        inputPanel.add(new JLabel("Last Modified:"));
        JLabel lastModifiedLabel = new JLabel(settings.getLastModified());
        inputPanel.add(lastModifiedLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Settings");
        JButton loadButton = new JButton("Reload Settings");
        
        saveButton.addActionListener(e -> saveSettings());
        loadButton.addActionListener(e -> reloadSettings());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setSize(400, 250);
        setLocationRelativeTo(null);
	}
	
	private void saveSettings() {
        settings.setSetting("theme", themeField.getText());
        settings.setSetting("fontSize", fontSizeField.getText());
        settings.setSetting("language", languageField.getText());

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(settings);
            JOptionPane.showMessageDialog(this, 
                "Settings saved successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving settings: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
	
	private AppSettings loadSettings() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            return (AppSettings) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
	
	private void reloadSettings() {
        AppSettings loaded = loadSettings();
        if (loaded != null) {
            settings = loaded;
            themeField.setText(settings.getSetting("theme"));
            fontSizeField.setText(settings.getSetting("fontSize"));
            languageField.setText(settings.getSetting("language"));
            JOptionPane.showMessageDialog(this, 
                "Settings reloaded successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConfigurationManager().setVisible(true);
        });
    }
}
