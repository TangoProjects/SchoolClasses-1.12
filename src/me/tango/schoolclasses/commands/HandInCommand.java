package me.tango.schoolclasses.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.tango.schoolclasses.Main;
import me.tango.schoolclasses.util.ConfigManager;

public class HandInCommand implements CommandExecutor{
	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Prefix"));
	String handin_already_submitted = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-already-submitted"));
	String handin_not_in_class = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-not-in-class"));
	String no_permission = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("no-permission"));


	public ArrayList<String> openclasses = new ArrayList<>();
	public ArrayList<String> startedclasses = new ArrayList<>();

	static ConfigManager conf = new ConfigManager(plugin);

	public Inventory inv;

	public void openHandin(Player p){
		inv = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Work hand-in");
		p.openInventory(inv);
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args){
		if (sender instanceof Player) {
			Player p = (Player)sender;
			if(args.length == 0 && cmd.getName().equalsIgnoreCase("handin")) {		

				if(p.hasPermission("class.handin") || p.hasPermission("class.student") || p.hasPermission("class.*")) {
					
					
					if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
						p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
						return true;
					}
					
					if(plugin.getInstance().getClass(p) != null) {
						if(plugin.getInstance().handin.containsKey(p.getUniqueId())) {
							p.sendMessage(prefix + handin_already_submitted);
							return true;
						}
						openHandin(p);
						return true;
					} else {
						p.sendMessage(prefix + handin_not_in_class);
					}
					return true;
				} else {
					p.sendMessage(prefix + no_permission);
					return true;
				}
			}
		}
		return false;
	}
}
