package me.tango.schoolclasses.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.elmakers.mine.bukkit.api.event.PreCastEvent;

import me.tango.schoolclasses.Main;

public class MagicCast implements Listener {
	
	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Prefix"));
	
	@EventHandler(priority = EventPriority.HIGH)
	public void wandUse(PreCastEvent e) {
		if(plugin.getInstance().wand_disabled_classes.contains(plugin.getInstance().getClass(e.getMage().getPlayer()))) {
			e.getSpell().cancel();
			e.setCancelled(true);
			e.getMage().getPlayer().sendMessage(prefix + ChatColor.RED + "Wands currently disabled for this class!");
		}
	}
}
