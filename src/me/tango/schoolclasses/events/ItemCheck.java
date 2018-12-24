package me.tango.schoolclasses.events;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.tango.schoolclasses.Main;
import me.tango.schoolclasses.util.ConfigManager;

public class ItemCheck implements Listener {

	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Prefix"));
	String handin_already_submitted = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-already-submitted"));
	String handin_too_much = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-too-much"));
	String handin_successful = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-successful"));
	String professor_prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Professor-prefix"));
	String helper_prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Helper-prefix"));
	String student_prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Student-prefix"));


	static ConfigManager conf = new ConfigManager(plugin);

	public int getAmount(ItemStack[] s) {
		ItemStack[] items = s;
		int has = 0;
		for (ItemStack item : items) {
			if ((item != null) && (item.getAmount() > 0)) {
				has += item.getAmount();
			}
		}
		return has;
	}


	@EventHandler(priority=EventPriority.HIGH)
	public void InventoryE(InventoryCloseEvent e){

		Inventory inv = e.getInventory();
		Player p = (Player) e.getPlayer();

		if(e.getInventory().getName().equalsIgnoreCase(ChatColor.AQUA + "Work hand-in")){

			ItemStack[] closed = e.getInventory().getContents();

			if(getAmount(closed) == 0) return;

			if(plugin.getInstance().handin.containsKey(p.getUniqueId())) {
				for (ItemStack i : closed) {
					try {
						if (i.getType() != null) {
							p.getInventory().addItem(i);
						}
					} catch(NullPointerException npe){
					}
				}
				p.sendMessage(prefix + handin_already_submitted);
				return;
			}

			if(getAmount(closed) > 1) {
				for (ItemStack i : closed) {
					try {
						if (i.getType() != null) {
							p.getInventory().addItem(i);
						}
					} catch(NullPointerException npe){
					}
				}

				p.sendMessage(prefix + handin_too_much);
				return;
			}

			for (ItemStack i : closed) {
				try {
					if (i.getType() != null) {
						if (i.getType().equals(Material.WRITTEN_BOOK) || i.getType().equals(Material.BOOK_AND_QUILL)) {
							ItemMeta iMeta = i.getItemMeta();
							iMeta.setDisplayName(p.getName() + "'s work");
							i.setItemMeta(iMeta);


							plugin.getInstance().handin.put(p.getUniqueId(), i);
							p.sendMessage(prefix + handin_successful);
							playPing(p);

						} else {
							p.getInventory().addItem(i);
						}

					}
				} catch(NullPointerException npe){
				}
			}
		}
	}


	@EventHandler
	public void onInv(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		Inventory top = event.getView().getTopInventory();
		Inventory bottom = event.getView().getBottomInventory();

		if(top.getTitle().equalsIgnoreCase(ChatColor.AQUA + "Students work") && bottom.getType() == InventoryType.PLAYER){
			if(event.getCurrentItem() != null) {
				if(event.getCurrentItem().getType() != Material.AIR) {
					if(event.getCurrentItem().getType() == Material.WRITTEN_BOOK || event.getCurrentItem().getType() == Material.BOOK_AND_QUILL) {
						if(event.getClickedInventory() == top) {

							try {
								if(!event.getCurrentItem().getItemMeta().getDisplayName().toString().contains("'s work")) return;
							} catch(NullPointerException npe) {
								return;
							}

							ItemStack is = event.getCurrentItem();
							ItemMeta iMeta = is.getItemMeta();
							is.setItemMeta(iMeta);
							String student = iMeta.getDisplayName().replace("'s work", "");
							plugin.getInstance().handin.remove(Bukkit.getPlayer(student).getUniqueId());


							for(Player viewers : Bukkit.getOnlinePlayers()) {
								if(viewers.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase(ChatColor.AQUA + "Students work") && !event.getWhoClicked().getName().equals(viewers.getName())) {
									viewers.getOpenInventory().getTopInventory().remove(is);
									viewers.updateInventory();
								}
							}

							p.getInventory().addItem(event.getCurrentItem());
							p.setItemOnCursor(null);

							event.getClickedInventory().remove(event.getCurrentItem());
							event.getCurrentItem().setType(Material.AIR);
							if (Bukkit.getVersion().contains("1.8")) p.playSound(p.getEyeLocation(), Sound.valueOf("ITEM_PICKUP"), 0.6F, 1F);
							if (Bukkit.getVersion().contains("1.9")) p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.6F, 1F);
							if (Bukkit.getVersion().contains("1.10")) p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.6F, 1F);
							if (Bukkit.getVersion().contains("1.11")) p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.6F, 1F);
							if (Bukkit.getVersion().contains("1.12")) p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.6F, 1F);
							if (Bukkit.getVersion().contains("1.13")) p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.6F, 1F);
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();

		if(!plugin.getInstance().students.containsKey(p.getUniqueId())) {
			for (Map.Entry<UUID,String> entry : plugin.getInstance().students.entrySet()) {
				e.getRecipients().remove(Bukkit.getPlayer(entry.getKey()));
			}
		}

		if(plugin.getInstance().getClass(p) != null && plugin.getInstance().students.containsKey(p.getUniqueId()) && !plugin.getInstance().openclasses.contains(plugin.getInstance().getClass(p))) {
			e.getRecipients().clear();
			for (Map.Entry<UUID,String> entry : plugin.getInstance().students.entrySet()) {
				if(plugin.getInstance().startedclasses.contains(entry.getValue())) {
					e.getRecipients().add(Bukkit.getPlayer(entry.getKey()));
				}
			}
		}

		if((plugin.getInstance().professors.contains(p.getUniqueId())) 
				&& !plugin.getInstance().openclasses.contains(plugin.getInstance().getClass(p))
				&& plugin.getInstance().getClass(p) != null) {
			String dn = p.getDisplayName();
			p.setDisplayName(prefix + professor_prefix + dn);
			e.setMessage(ChatColor.RED + e.getMessage());
		}
		
		if(((plugin.getInstance().helpers.contains(p.getUniqueId())) 
				|| p.hasPermission("class.helper"))
				&& !plugin.getInstance().professors.contains(p.getUniqueId())
				&& !plugin.getInstance().openclasses.contains(plugin.getInstance().getClass(p))
				&& plugin.getInstance().getClass(p) != null) {
			String dn = p.getDisplayName();
			p.setDisplayName(prefix + helper_prefix + dn);
			e.setMessage(ChatColor.RED + e.getMessage());
		}
		
		if(((!plugin.getInstance().helpers.contains(p.getUniqueId())))
				&& !p.hasPermission("class.helper")
				&& !plugin.getInstance().professors.contains(p.getUniqueId())
				&& !plugin.getInstance().openclasses.contains(plugin.getInstance().getClass(p))
				&& plugin.getInstance().getClass(p) != null) {
			String dn = p.getDisplayName();
			p.setDisplayName(prefix + student_prefix + dn);
			e.setMessage(ChatColor.RED + e.getMessage());
		}


		if(plugin.getInstance().professors.contains(p.getUniqueId())) return;
		if((!p.hasPermission("class.start") || !plugin.getInstance().professors.contains(p.getUniqueId())) 
				&& plugin.getInstance().mutedclasses.contains(plugin.getInstance().getClass(p)) 
				&& !plugin.getInstance().called.contains(p.getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(plugin.getInstance().students.containsKey(p.getUniqueId())) {
			for (Map.Entry<UUID,Location> entry : plugin.getInstance().previouslocs.entrySet()) {
				if(p.getUniqueId() == entry.getKey()) {
					if(plugin.getInstance().getClass(p) != null) {
						Location s = entry.getValue();
						p.teleport(s);
					}
				}
			}

			plugin.getInstance().students.remove(p.getUniqueId());
			//plugin.getInstance().handin.remove(p.getUniqueId());
			plugin.getInstance().called.remove(p.getUniqueId());

		}
	}

	public void playPing(Player p) {		
		if (Bukkit.getVersion().contains("1.8")) p.playSound(p.getEyeLocation(), Sound.valueOf("NOTE_PLING") , 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.9")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.10")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.11")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.12")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.13")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"), 0.6F, 1F);
	}
}
