package me.tango.schoolclasses.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tango.schoolclasses.Main;
import me.tango.schoolclasses.util.ConfigManager;

public class RHCommand implements CommandExecutor{
	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Prefix"));
	String no_permission = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("no-permission"));
	String rh_out_of_class = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("rh-out-of-class"));
	String player_raised_hand = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("player-raised-hand"));

	public ArrayList<String> openclasses = new ArrayList<>();
	public ArrayList<String> startedclasses = new ArrayList<>();

	static ConfigManager conf = new ConfigManager(plugin);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args){
		if (sender instanceof Player) {
			Player p = (Player)sender;
			if(args.length == 0 && cmd.getName().equalsIgnoreCase("rh")) {			
				if(p.hasPermission("class.rh") || p.hasPermission("class.student") || p.hasPermission("class.*")) {
					if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
						p.sendMessage(prefix + rh_out_of_class);
						return true;
					}
					
					if(plugin.getInstance().disabledRH.contains(plugin.getInstance().getClass(p))) {
						p.sendMessage(prefix + ChatColor.RED + "You cannot raise your hand right now");
						return true;
					}
					
					for(Player all : Bukkit.getOnlinePlayers()) {
						for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							if(entry.getKey().equals(all.getUniqueId()) && entry.getValue().equalsIgnoreCase(plugin.getInstance().students.get(p.getUniqueId()))) {
								all.sendMessage(prefix + player_raised_hand.replace("%player%", p.getName()));
							}
						}
					}
				} else {
					p.sendMessage(prefix + no_permission);
					return true;
				}
				//all.playSound(p.getLocation(), Sound.NOTE_PLING, 0.5F, 0.6F);
			}
			return true;
		}
		return false;
	}


}
