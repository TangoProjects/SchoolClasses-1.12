package me.tango.schoolclasses.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tango.schoolclasses.Main;

public class ConfigManager {


    public FileConfiguration customConfig = null;
    public File customConfigFile = null;
   
    public ConfigManager(Main instance) {
        plugin = instance;
    }
    
	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}
   
    public void initCustomConfig() {

       
        this.getCustomConfig().options().copyDefaults(true);
        this.saveDefaultCustomConfig();
        this.saveCustomConfig();
       
    }
   
    public void reloadCustomConfig() {
        if (customConfigFile == null) {
        customConfigFile = new File(plugin.getDataFolder(), "classes.yml");
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(plugin.getInstance().getResource("classes.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(customConfigFile);
            customConfig.setDefaults(defConfig);
        }
    }
   
    public FileConfiguration getCustomConfig() {
        if (customConfig == null) {
            reloadCustomConfig();
        }
        return customConfig;
    }
   
    public void saveCustomConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getInstance().getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }
   
    public void saveDefaultCustomConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), "classes.yml");
        }
        if (!customConfigFile.exists()) {           
             plugin.getInstance().saveResource("classes.yml", false);
         }
    }
   
}
 