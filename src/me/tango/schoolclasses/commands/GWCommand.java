package me.tango.schoolclasses.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.tango.schoolclasses.Main;
import me.tango.schoolclasses.util.ConfigManager;

public class GWCommand implements CommandExecutor{
	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}
	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Prefix"));
	String no_permission = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("no-permission"));
	public Inventory inv;

	public ArrayList<String> openclasses = new ArrayList<>();
	public ArrayList<String> startedclasses = new ArrayList<>();

	static ConfigManager conf = new ConfigManager(plugin);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args){
		if (sender instanceof Player) {
			Player p = (Player)sender;
			if(args.length == 0 && cmd.getName().equalsIgnoreCase("gatherwork")) {	

				if(p.hasPermission("class.gatherwork") || p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId())) {
					
					if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
						p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
						return true;
					}
					
					if(!plugin.getInstance().students.containsKey(p.getUniqueId())) {
						p.sendMessage(prefix + ChatColor.RED + "You are currently not teaching a class!");
						return true;
					}

					openHandin(p);
					return true;
				} else {
					p.sendMessage(prefix + no_permission);
					return true;
				}
			}
		}
		return false;
	}

	public void openHandin(Player p){
		inv = Bukkit.createInventory(null, 45, ChatColor.AQUA + "Students work");
		for (Map.Entry<UUID, ItemStack> entry : plugin.getInstance().handin.entrySet()) {
			if(plugin.getInstance().getClass(Bukkit.getPlayer(entry.getKey())) != null && plugin.getInstance().getClass(Bukkit.getPlayer(entry.getKey())).equalsIgnoreCase(plugin.getInstance().getClass(p))) {
				ItemStack is = entry.getValue();
				is.setAmount(1);
				inv.addItem(is);
			}
		}

		p.openInventory(inv);
	}

}
