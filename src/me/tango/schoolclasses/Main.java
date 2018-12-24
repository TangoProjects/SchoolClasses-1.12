package me.tango.schoolclasses;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;

import me.tango.schoolclasses.commands.HandInCommand;
import me.tango.schoolclasses.commands.ClassCommands;
import me.tango.schoolclasses.commands.GWCommand;
import me.tango.schoolclasses.commands.RHCommand;
import me.tango.schoolclasses.events.ItemCheck;
import me.tango.schoolclasses.events.MagicCast;
import me.tango.schoolclasses.util.ConfigManager;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	public String configVersion = "2.0";
	static ConfigManager conf;
	public ArrayList<String> disabledRH = new ArrayList<>();
	public ArrayList<String> readyclasses = new ArrayList<>();
	public ArrayList<String> openclasses = new ArrayList<>();
	public ArrayList<String> startedclasses = new ArrayList<>();
	public ArrayList<String> mutedclasses = new ArrayList<>();
	public ArrayList<String> wand_disabled_classes = new ArrayList<>();
	public ArrayList<UUID> called = new ArrayList<>();
	public ArrayList<UUID> professors = new ArrayList<>();
	public ArrayList<UUID> helpers = new ArrayList<>();
	public Map<UUID,String> students = new HashMap<>();
	public Map<UUID,ItemStack> handin = new HashMap<>();
	public Map<UUID,Location> previouslocs = new HashMap<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		
        if (!getConfig().getString("version").equals(configVersion)) {
            getLogger().info("[SchoolClasses+] Your configuration file was not up to date. Updating it now...");
            //updateConfig();
            getConfig().options().copyDefaults(true);
            getConfig().set("version", configVersion);
            saveConfig();
            getLogger().info("[SchoolClasses+] Configuration file updated.");
        }
        
		if(Bukkit.getPluginManager().getPlugin("Magic") != null) {
			getServer().getPluginManager().registerEvents(new MagicCast(), this);
			System.out.println("Magic integration successful");
		} else {
			System.out.println("Magic integration unsuccessful");
		}
		
		if (!setupEconomy()) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		loadConfig();
		
		getServer().getPluginManager().registerEvents(new ItemCheck(), this);
		getCommand("handin").setExecutor((CommandExecutor) new HandInCommand());
		getCommand("rh").setExecutor((CommandExecutor) new RHCommand()); 
		getCommand("class").setExecutor((CommandExecutor) new ClassCommands()); 
		getCommand("gatherwork").setExecutor((CommandExecutor) new GWCommand()); 
		
		conf = new ConfigManager(this);
		conf.initCustomConfig();
		conf.getCustomConfig();
		
	}
	
	
    MagicAPI getMagicAPI() {
        Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
          if (magicPlugin == null || !(magicPlugin instanceof MagicAPI)) {
              return null;
          }
        return (MagicAPI) magicPlugin;
    }
    
	public static Economy economy = null;

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) economy = economyProvider.getProvider();
		return (economy != null);
	}

	
	
	@Override
	public void onDisable() {
		returnPlayers();
	}
	
	public void returnPlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(students.containsKey(p.getUniqueId()) && previouslocs.containsKey(p.getUniqueId())) {
				Location s = previouslocs.get(p.getUniqueId());
				p.teleport(s);
			}
		}
	}
	
	public String getClass(Player p) {
		for (Map.Entry<UUID, String> entry : students.entrySet()) {
			if(p.getUniqueId() == entry.getKey()) {
				return students.get(p.getUniqueId());
			}
		}
		return null;
	}
	
	private void loadConfig() {
		try {
			if (!getDataFolder().exists()) getDataFolder().mkdirs();
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) saveDefaultConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void updateConfig() {
        HashMap<String, Object> newConfig = getConfigVals();
        FileConfiguration c = getConfig();
        for (String var : c.getKeys(false)) {
            newConfig.remove(var);
        }
        if (newConfig.size()!=0) {
            for (String key : newConfig.keySet()) {
                c.set(key, newConfig.get(key));
            }
            try {
                c.set("version", configVersion);
                c.save(new File(getDataFolder(), "config.yml"));
            } catch (IOException e) {}
        }
    }
    
    public HashMap<String, Object> getConfigVals() {
        HashMap<String, Object> var = new HashMap<>();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(stringFromInputStream(Main.class.getResourceAsStream("/config.yml")));
        } catch (InvalidConfigurationException e) {}
        for (String key : config.getKeys(false)) {
            var.put(key, config.get(key));
        }
        return var;
    }
    public String stringFromInputStream(InputStream in) {
        return new Scanner(in).useDelimiter("\\A").next();
    }
	
}
