package me.tango.schoolclasses.commands;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.tango.schoolclasses.Main;
import me.tango.schoolclasses.util.ConfigManager;
import mkremins.fanciful.FancyMessage;

public class ClassCommands implements CommandExecutor {

	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("Prefix"));
	String class_joined = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-joined"));
	String no_permission = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("no-permission"));
	String class_ended = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-ended"));
	String class_started = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-started"));
	String class_full = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-full"));
	String class_already_started = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-already-started"));
	String class_not_opened = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-not-opened"));
	String left_queue = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("left-queue"));
	String joined_queue = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("joined-queue"));
	String joined_queue_info = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("joined-queue-info"));
	String class_doesnt_exist = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-doesnt-exist"));
	String class_muted = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-muted"));
	String class_unmuted = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-unmuted"));
	String work_returned = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("work-returned"));
	String queue_kicked = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("queue-kicked"));
	String left_class = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("left-class"));
	String class_kicked = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-kicked"));
	String class_kicked_info = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-kicked-info"));
	String class_leave = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-leave"));
	String class_leave_info = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-leave-info"));
	String class_leave_goodbye = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-leave-goodbye"));
	String invalid_usage = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("invalid-usage"));
	String player_raised_hand = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("player-raised-hand"));
	String rh_out_of_class = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("rh-out-of-class"));
	String handin_already_submitted = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-already-submitted"));
	String handin_not_in_class = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("handin-not-in-class"));
	String player_called = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("player-called"));
	String player_uncalled = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("player-uncalled"));
	String class_notify = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("class-notify"));

	public Inventory inv;

	static ConfigManager conf = new ConfigManager(plugin);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args){
		if (sender instanceof Player) {
			Player p = (Player)sender;
			if(cmd.getName().equalsIgnoreCase("class")) {
				if(args.length == 2 && args[0].equalsIgnoreCase("ready")){	
					if(p.hasPermission("class.open") || p.hasPermission("class.*")) {
						if(conf.getCustomConfig().contains("Classes." + args[1])) {
							if(plugin.getInstance().readyclasses.contains(args[1]) || plugin.getInstance().openclasses.contains(args[1]) || plugin.getInstance().startedclasses.contains(args[1])) {
								p.sendMessage(prefix + ChatColor.RED + class_already_started.replace("%class%", args[1]));
								return true;
							}

							double x = conf.getCustomConfig().getDouble("Classes." + args[1] + ".x");
							double y = conf.getCustomConfig().getDouble("Classes." + args[1] + ".y");
							double z = conf.getCustomConfig().getDouble("Classes." + args[1] + ".z");
							float pitch = (float) conf.getCustomConfig().getDouble("Classes." + args[1] + ".pitch");
							float yaw = (float) conf.getCustomConfig().getDouble("Classes." + args[1] + ".yaw");
							World w = Bukkit.getWorld(conf.getCustomConfig().getString("Classes." + args[1] + ".world"));

							Location sp = new Location(w,x,y,z,yaw,pitch);

							plugin.getInstance().readyclasses.add(args[1]);
							plugin.getInstance().students.put(p.getUniqueId(), args[1]);
							plugin.getInstance().professors.add(p.getUniqueId());

							for(Player all : Bukkit.getOnlinePlayers()) {
								all.sendMessage(prefix + class_notify.replace("%professor%", p.getName()).replace("%class%", args[1]));
								if(p.getName() != all.getName()) {
									new FancyMessage(prefix)
									.then("§a§lCLICK HERE")
									.command("/class join " + args[1])
									.tooltip("§bClick to join the queue")
									.color(ChatColor.AQUA)
									.then(" to join the queue for this class!")
									.color(ChatColor.AQUA)
									.send(all);
									playPing(all);
								}

								all.sendTitle(ChatColor.GREEN + "Class " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is ready!", "");

							}
							p.teleport(sp);
							p.sendMessage("");
							p.sendMessage(prefix + "§bYou are the professor of this class!");
							p.sendMessage(prefix +"§eWhen you are ready, be sure to open the class and let students in");
							p.sendMessage("");
							return true;
						}

						p.sendMessage(prefix + ChatColor.RED + "Class " + args[1] + " does not exist!");
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				if(args.length == 3 && args[0].equalsIgnoreCase("create")){
					if(p.hasPermission("class.create") || p.hasPermission("class.*")) {
						if(isInt(args[2])) {

							boolean alreadyCreated = true;
							
							if(conf.getCustomConfig().contains("Classes." + args[1])) {
								p.sendMessage(prefix + ChatColor.GREEN + "Class " + args[1] + " location updated");
								alreadyCreated = false;
							}
							
							conf.getCustomConfig().set("Classes." + args[1] + ".world", p.getWorld().getName());
							conf.getCustomConfig().set("Classes." + args[1] + ".x", p.getLocation().getX());
							conf.getCustomConfig().set("Classes." + args[1] + ".y", p.getLocation().getY());
							conf.getCustomConfig().set("Classes." + args[1] + ".z", p.getLocation().getZ());
							conf.getCustomConfig().set("Classes." + args[1] + ".pitch", p.getLocation().getPitch());
							conf.getCustomConfig().set("Classes." + args[1] + ".yaw", p.getLocation().getYaw());
							conf.getCustomConfig().set("Classes." + args[1] + ".max_students", Integer.parseInt(args[2]));

							conf.saveCustomConfig();
							playPing(p);

							if(alreadyCreated) p.sendMessage(prefix + ChatColor.GREEN + "Class " + args[1] + " has been created!");
							
							p.sendMessage(ChatColor.AQUA + "1) " + ChatColor.YELLOW + "You can open the queue for this class using: " + ChatColor.AQUA + "/class ready " + args[1]);
							p.sendMessage(ChatColor.AQUA + "2) " + ChatColor.YELLOW + "TP students to the classroom using " + ChatColor.AQUA + "/class open " + args[1]);
							p.sendMessage(ChatColor.AQUA + "3) " + ChatColor.YELLOW + "Start the class by using " + ChatColor.AQUA + "/class start " + args[1]);

							return true;
						} else {
							p.sendMessage(prefix + ChatColor.RED + "max-students field must be an integer!");
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				} else if(args.length == 2 && args[0].equalsIgnoreCase("create")) {
					p.sendMessage(prefix + ChatColor.RED + "Please input max-students field!");
					return true;
				}

				if(args.length == 1 && args[0].equalsIgnoreCase("magic")){

					if(p.hasPermission("class.helper") || p.hasPermission("class.*") || p.hasPermission("class.magic") || plugin.getInstance().helpers.contains(p.getUniqueId())) {
						if(plugin.getInstance().getClass(p) == null){
							p.sendMessage(prefix + ChatColor.RED + "You are currently not in a class");
							return true;
						}
						if(Bukkit.getServer().getPluginManager().getPlugin("Magic") != null) {
							if(plugin.getInstance().wand_disabled_classes.contains(plugin.getInstance().getClass(p))) {
								plugin.getInstance().wand_disabled_classes.remove(plugin.getInstance().getClass(p));
								p.sendMessage(prefix + ChatColor.GREEN + "Magic enabled");
								return true;
							} else {
								plugin.getInstance().wand_disabled_classes.add(plugin.getInstance().getClass(p));
								p.sendMessage(prefix + ChatColor.RED + "Magic disabled");
								return true;
							}
						} else {
							p.sendMessage(prefix + ChatColor.RED + "Magic plugin not found!");
							return true;
						} 	
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				if(args.length == 1 && (args[0].equalsIgnoreCase("rhtoggle") || args[0].equalsIgnoreCase("togglerh") || args[0].equalsIgnoreCase("mrh"))){
					if(p.hasPermission("class.helper") || p.hasPermission("class.*") || p.hasPermission("class.togglerh") || plugin.getInstance().helpers.contains(p.getUniqueId())) {
						if(plugin.getInstance().getClass(p) == null){
							p.sendMessage(prefix + ChatColor.RED + "You are currently not in a class");
							return true;
						}
						
						if(!plugin.getInstance().disabledRH.contains(plugin.getInstance().getClass(p))){
							plugin.getInstance().disabledRH.add(plugin.getInstance().getClass(p));
							p.sendMessage(prefix + ChatColor.AQUA + "Students can no longer raise hands");
							return true;
						} else {
							plugin.getInstance().disabledRH.remove(plugin.getInstance().getClass(p));
							p.sendMessage(prefix + ChatColor.AQUA + "Students can now raise hands");
							return true;
						}
						
						
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}

				}

				if(args.length == 2 && args[0].equalsIgnoreCase("join")){
					int count = conf.getCustomConfig().getInt("Classes." + args[1] + ".max_students");
					if(p.hasPermission("class.join") || p.hasPermission("class.student") || p.hasPermission("class.*")) {

						/*   AUTO START DISABLED 
						if(studentCount(args[1]) > (count - 1) && !plugin.getInstance().startedclasses.contains(args[1])) {
							for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
								if(entry.getValue().equalsIgnoreCase(args[1]) && !p.getUniqueId().equals(entry.getKey())) {
									Player student = Bukkit.getPlayer(entry.getKey());
									plugin.getInstance().previouslocs.put(student.getUniqueId(), student.getLocation());
								}
							}

							for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
								if(entry.getValue().equalsIgnoreCase(args[1])) {

									Player student = Bukkit.getPlayer(entry.getKey());

									double x = conf.getCustomConfig().getDouble("Classes." + args[1] + ".x");
									double y = conf.getCustomConfig().getDouble("Classes." + args[1] + ".y");
									double z = conf.getCustomConfig().getDouble("Classes." + args[1] + ".z");
									float pitch = (float) conf.getCustomConfig().getDouble("Classes." + args[1] + ".pitch");
									float yaw = (float) conf.getCustomConfig().getDouble("Classes." + args[1] + ".yaw");
									World w = Bukkit.getWorld(conf.getCustomConfig().getString("Classes." + args[1] + ".world"));

									Location sp = new Location(w,x,y,z,yaw,pitch);

									if(student.getName() != p.getName()) {
										student.teleport(sp);
										student.getInventory().addItem(new ItemStack(Material.BOOK_AND_QUILL, 1));
									}

									playPing(student);
									/*
									student.sendMessage("");
									student.sendMessage(prefix + ChatColor.GREEN + "Class has started!");
									student.sendMessage("");

								}
							}

							plugin.getInstance().openclasses.remove(args[1]);
							//plugin.getInstance().startedclasses.add(args[1]);
							return true;

						}
						 */
						
						if(!conf.getCustomConfig().contains("Classes." + args[1] + ".max_students")) {
							count = 40;
						}
						
						if(plugin.getInstance().students.containsKey(p.getUniqueId())) {
							plugin.getInstance().students.remove(p.getUniqueId());
							p.sendMessage(prefix + left_queue);
							return true;
						}

						if((studentCount(args[1])) > (count)) {
							p.sendMessage(prefix + class_full);
							return true;
						}

						if(plugin.getInstance().startedclasses.contains(args[1])) {
							p.sendMessage(prefix + class_already_started.replace("%class%", args[1]));
							return true;
						}

						if(!plugin.getInstance().readyclasses.contains(args[1]) && !plugin.getInstance().openclasses.contains(args[1]) && !plugin.getInstance().startedclasses.contains(args[1])) {
							p.sendMessage(prefix + class_not_opened.replace("%class%", args[1]));
							return true;
						}

						if(plugin.getInstance().openclasses.contains(args[1])) {
							
							plugin.getInstance().previouslocs.put(p.getUniqueId(), p.getLocation());
							plugin.getInstance().students.put(p.getUniqueId(), args[1]);

							double x = conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".x");
							double y = conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".y");
							double z = conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".z");
							float pitch = (float) conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".pitch");
							float yaw = (float) conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".yaw");
							World w = Bukkit.getWorld(conf.getCustomConfig().getString("Classes." + plugin.getInstance().getClass(p) + ".world"));

							Location sp = new Location(w,x,y,z,yaw,pitch);
							p.sendMessage(prefix + ChatColor.AQUA + "Class is about to begin!");
							
							p.getInventory().addItem(new ItemStack(Material.BOOK_AND_QUILL, 1));



							playPing(p);
							
							p.teleport(sp);
							return true;
						}

						if(conf.getCustomConfig().contains("Classes." + args[1])) {
							p.sendMessage("");
							p.sendMessage(prefix + joined_queue);
							p.sendMessage(prefix + joined_queue_info);
							p.sendMessage("");
							playPing(p);
							plugin.getInstance().students.put(p.getUniqueId(), args[1]);


							if((studentCount(args[1])) > (count)) {
								for(UUID id : plugin.getInstance().professors) {
									Player prof = Bukkit.getPlayer(id);
									prof.sendMessage(prefix + ChatColor.AQUA + "Class " + ChatColor.GOLD + args[1] + ChatColor.AQUA + " is now full!");
								}
								return true;
							}

							return true;
						} else {
							p.sendMessage(prefix + class_doesnt_exist.replace("%class%", args[1]));
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				if(args.length == 2 && args[0].equalsIgnoreCase("open")){		

					if(p.hasPermission("class.start") || p.hasPermission("class.*")) {
						/*
						if(plugin.getInstance().startedclasses.contains(args[1])) {
							p.sendMessage(prefix + ChatColor.RED + "Class "  + args[1] + " has already been opened");
							return true;
						}
						 */


						if(!conf.getCustomConfig().getConfigurationSection("Classes").contains(args[1])) {
							p.sendMessage(prefix + ChatColor.RED + "Class " + args[1] + " does not exist");
							return true;
						}

						if(plugin.getInstance().openclasses.contains(args[1])) {
							p.sendMessage(prefix + ChatColor.RED + "This class has already been opened to students!");
							return true;
						}

						if(!plugin.getInstance().readyclasses.contains(args[1])) {
							p.sendMessage(prefix + ChatColor.RED + "You must ready the class before you can open to students! "  + ChatColor.GRAY + "please use /class ready <name>");
							return true;
						}

						int count = plugin.getInstance().getConfig().getInt("Min-Students");
						if(studentCount(args[1]) <= (count - 1)) {
							p.sendMessage(prefix + ChatColor.RED + "There are not enough students to begin the class!");
							return true;
						}


						plugin.getInstance().readyclasses.remove(args[1]);
						plugin.getInstance().openclasses.add(args[1]);

						for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							if(entry.getValue().equalsIgnoreCase(args[1])) { 
								Player student = Bukkit.getPlayer(entry.getKey());
								plugin.getInstance().previouslocs.put(student.getUniqueId(), student.getLocation());
							}
						}

						for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							if(entry.getValue().equalsIgnoreCase(args[1])) {
								Player student = Bukkit.getPlayer(entry.getKey());

								double x = conf.getCustomConfig().getDouble("Classes." + args[1] + ".x");
								double y = conf.getCustomConfig().getDouble("Classes." + args[1] + ".y");
								double z = conf.getCustomConfig().getDouble("Classes." + args[1] + ".z");
								float pitch = (float) conf.getCustomConfig().getDouble("Classes." + args[1] + ".pitch");
								float yaw = (float) conf.getCustomConfig().getDouble("Classes." + args[1] + ".yaw");
								World w = Bukkit.getWorld(conf.getCustomConfig().getString("Classes." + args[1] + ".world"));

								Location sp = new Location(w,x,y,z,yaw,pitch);

								if(student.getName() != p.getName()) {
									student.teleport(sp);
									student.getInventory().addItem(new ItemStack(Material.BOOK_AND_QUILL, 1));
								} 


								playPing(student);
								/*
								student.sendMessage("");
								student.sendMessage(prefix + class_started);
								student.sendMessage("");
								 */

							}
						}

						ArrayList<UUID> inClass = new ArrayList<>();
						for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							if(entry.getValue().equalsIgnoreCase(args[1])) {
								inClass.add(entry.getKey());
							}
						}

						for(Player all : Bukkit.getOnlinePlayers()) {
							if(inClass.contains(all.getUniqueId())) {
								for(int i = 0; i < studentCount(args[1]); i++) {
									if(!plugin.getInstance().professors.contains(Bukkit.getPlayer(inClass.get(i)))) all.sendMessage(prefix + class_joined.replace("%player%", Bukkit.getPlayer(inClass.get(i)).getName()));
								}
								if(!plugin.getInstance().professors.contains(all.getUniqueId())) {
									all.sendMessage("");
									all.sendMessage(prefix + ChatColor.AQUA + "Please wait for the professor to start the class");
									all.sendMessage("");
								} else {
									all.sendMessage("");
									all.sendMessage(prefix + ChatColor.AQUA + "Be sure to start the class once you are ready!");
									all.sendMessage("");
								}
							}

						}


						//plugin.getInstance().openclasses.remove(args[1]);
						//plugin.getInstance().startedclasses.add(args[1]);
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				/* CUSTOM COMMAND ENABLE
				if(args.length == 2 && args[0].equalsIgnoreCase("kickallcc")){
					if(p.hasPermission("class.*") || p.hasPermission("class.kick")) {
						if(conf.getCustomConfig().contains("Classes." + args[1])) {
							if(!plugin.getInstance().startedclasses.contains(args[1]) && !plugin.getInstance().openclasses.contains(args[1])) {
								p.sendMessage(prefix + ChatColor.RED + "Unable to end class " + args[1]);
								return true;
							}

							for(Player current : Bukkit.getOnlinePlayers()) {
								if(plugin.getInstance().getClass(current) != null && plugin.getInstance().getClass(current).equalsIgnoreCase(args[1])) {
									current.sendMessage("");
									current.sendMessage(prefix + class_ended);
									if(!plugin.getInstance().professors.contains(current.getUniqueId())) current.sendMessage(prefix + ChatColor.GREEN + "You have been awarded 1 CC");
									current.sendMessage("");
									if(!plugin.getInstance().professors.contains(current.getUniqueId())) plugin.getInstance().economy.depositPlayer(current, 1);
									playPing(current);
								}

								if(plugin.getInstance().called.contains(current.getUniqueId()) && plugin.getInstance().getClass(current) != null && plugin.getInstance().getClass(current).equalsIgnoreCase(args[1])) {
									plugin.getInstance().called.remove(current.getUniqueId());
								}

								if(plugin.getInstance().previouslocs.containsKey(current.getUniqueId())) {
									if(isInClass(current, args[1])) {
										Location s = plugin.getInstance().previouslocs.get(current.getUniqueId());
										current.teleport(s);
										plugin.getInstance().previouslocs.remove(p.getUniqueId());
									}
								}
							}

							for (Entry<UUID, String> s : plugin.getInstance().students.entrySet()) {
								if(s.getValue().contains(plugin.getInstance().getClass(p))) {
									plugin.getInstance().students.remove(s.getKey());
								}
							}

							for (Entry<UUID, ItemStack> s : plugin.getInstance().handin.entrySet()) {
								if(plugin.getInstance().getClass(Bukkit.getPlayer(s.getKey())) != null && plugin.getInstance().getClass(p) != null && plugin.getInstance().getClass(p).equalsIgnoreCase(plugin.getInstance().getClass(Bukkit.getPlayer(s.getKey())))){
									plugin.getInstance().handin.remove(s.getKey());
								}
							}

							plugin.getInstance().mutedclasses.remove(plugin.getInstance().getClass(p));
							plugin.getInstance().professors.remove(p.getUniqueId());
							plugin.getInstance().openclasses.remove(args[1]);
							plugin.getInstance().startedclasses.remove(args[1]);
							return true;
						} else {
							p.sendMessage(prefix + ChatColor.RED + "Class not found!");
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}
	*/

				if(args.length == 2 && args[0].equalsIgnoreCase("end")){
					if(p.hasPermission("class.*") || p.hasPermission("class.end")) {
						if(conf.getCustomConfig().contains("Classes." + args[1])) {
							if(!plugin.getInstance().startedclasses.contains(args[1]) && !plugin.getInstance().openclasses.contains(args[1])) {
								p.sendMessage(prefix + ChatColor.RED + "Unable to end class " + args[1]);
								return true;
							}

							for(Player current : Bukkit.getOnlinePlayers()) {
								if(plugin.getInstance().getClass(current) != null && plugin.getInstance().getClass(current).equalsIgnoreCase(args[1])) {
									current.sendMessage("");
									current.sendMessage(prefix + class_ended);
									current.sendMessage("");
									playPing(current);
								}

								if(plugin.getInstance().called.contains(current.getUniqueId()) && plugin.getInstance().getClass(current) != null && plugin.getInstance().getClass(current).equalsIgnoreCase(args[1])) {
									plugin.getInstance().called.remove(current.getUniqueId());
								}

								if(plugin.getInstance().previouslocs.containsKey(current.getUniqueId())) {
									if(isInClass(current, args[1])) {
										Location s = plugin.getInstance().previouslocs.get(current.getUniqueId());
										current.teleport(s);
										plugin.getInstance().previouslocs.remove(p.getUniqueId());
									}
								}
							}

							for (Entry<UUID, String> s : plugin.getInstance().students.entrySet()) {
								if(s.getValue().contains(plugin.getInstance().getClass(p))) {
									plugin.getInstance().students.remove(s.getKey());
								}
							}

							for (Entry<UUID, ItemStack> s : plugin.getInstance().handin.entrySet()) {
								if(plugin.getInstance().getClass(Bukkit.getPlayer(s.getKey())) != null && plugin.getInstance().getClass(p) != null && plugin.getInstance().getClass(p).equalsIgnoreCase(plugin.getInstance().getClass(Bukkit.getPlayer(s.getKey())))){
									plugin.getInstance().handin.remove(s.getKey());
								}
							}

							plugin.getInstance().mutedclasses.remove(plugin.getInstance().getClass(p));
							plugin.getInstance().professors.remove(p.getUniqueId());
							plugin.getInstance().openclasses.remove(args[1]);
							plugin.getInstance().startedclasses.remove(args[1]);
							return true;
						} else {
							p.sendMessage(prefix + ChatColor.RED + "Class not found!");
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				if(args.length == 2 && args[0].equalsIgnoreCase("addplayer")){

					if(p.hasPermission("class.addplayer") || p.hasPermission("class.*")) {

						if(Bukkit.getPlayer(args[1]) == null) {
							p.sendMessage(prefix + ChatColor.RED + "Player " + args[1] + " is not online!");
							return true;
						}

						if(plugin.getInstance().getClass(p) == null) {
							p.sendMessage(prefix + ChatColor.RED + "You are not in a class!");
							return true;
						}

						if(plugin.getInstance().students.containsKey(Bukkit.getPlayer(args[1]).getUniqueId())) {
							p.sendMessage(prefix + ChatColor.RED + "Student " + args[1] + " is already in the class! ");
							return true;
						}
						plugin.getInstance().previouslocs.put(Bukkit.getPlayer(args[1]).getUniqueId(), Bukkit.getPlayer(args[1]).getLocation());
						plugin.getInstance().students.put(Bukkit.getPlayer(args[1]).getUniqueId(), plugin.getInstance().getClass(p));

						double x = conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".x");
						double y = conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".y");
						double z = conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".z");
						float pitch = (float) conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".pitch");
						float yaw = (float) conf.getCustomConfig().getDouble("Classes." + plugin.getInstance().getClass(p) + ".yaw");
						World w = Bukkit.getWorld(conf.getCustomConfig().getString("Classes." + plugin.getInstance().getClass(p) + ".world"));

						Location sp = new Location(w,x,y,z,yaw,pitch);
						Bukkit.getPlayer(args[1]).teleport(sp);

						p.sendMessage(prefix + ChatColor.AQUA + args[1] + " has been added to your class");
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				if(args.length == 1 && args[0].equalsIgnoreCase("mute")){
					if(p.hasPermission("class.mute") || p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId())) {

						if(plugin.getInstance().getClass(p) == null) {
							p.sendMessage(prefix + ChatColor.RED + "You are not currently in a class!");
							return true;
						}

						if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
							p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
							return true;
						}

						if(plugin.getInstance().mutedclasses.contains(plugin.getInstance().getClass(p))){
							plugin.getInstance().mutedclasses.remove(plugin.getInstance().getClass(p));
							for(Player students : Bukkit.getOnlinePlayers()) {
								if(plugin.getInstance().getClass(students) != null && plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
									students.sendMessage("");
									students.sendMessage(prefix + class_unmuted);
									students.sendMessage("");
									playPing(students);
								}
							}
							return true;
						} else {
							plugin.getInstance().mutedclasses.add(plugin.getInstance().getClass(p));
							for(Player students : Bukkit.getOnlinePlayers()) {
								if(plugin.getInstance().getClass(students) != null && plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
									students.sendMessage("");
									students.sendMessage(prefix + class_muted);
									students.sendMessage("");
									playPing(students);
								}
							}
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				if(args.length == 1 && args[0].equalsIgnoreCase("give")){
					if(p.hasPermission("class.give") || p.hasPermission("class.*")) {

						if(plugin.getInstance().getClass(p) == null) {
							p.sendMessage(prefix + ChatColor.RED + "You are not currently in a class!");
							return true;
						}

						if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
							p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
							return true;
						}

						if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
							p.sendMessage(prefix + ChatColor.RED + "There is no item in your main hand");
							return true;
						}

						ItemStack is = p.getItemInHand();

						for (Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							if(entry.getValue().equalsIgnoreCase(plugin.getInstance().getClass(p))) {
								Player students = Bukkit.getPlayer(entry.getKey());
								if(!plugin.getInstance().professors.contains(students.getUniqueId())) {
									students.sendMessage(prefix + ChatColor.GRAY + "The professor has handed out an item!");
									if(students.getInventory().firstEmpty() != -1) {
										students.getInventory().addItem(is);
									} else {
										Bukkit.getWorld(students.getWorld().getName()).dropItemNaturally(students.getLocation().add(0.5,1,0.5), is);
										students.sendMessage(prefix + ChatColor.RED + "Your inventory was full so the item was dropped by your feet");
									}
								} else {
									p.sendMessage(prefix + ChatColor.AQUA + "Item handed out to students!");
								}
								playPing(students);
							}
						}
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				if(args.length == 2 && args[0].equalsIgnoreCase("start")){
					if(p.hasPermission("class.*") || p.hasPermission("class.start")) {

						if(plugin.getInstance().startedclasses.contains(args[1])) {
							p.sendMessage(prefix + ChatColor.RED + "Class "  + args[1] + " has already been started");
							return true;
						}

						if(!plugin.getInstance().openclasses.contains(args[1])) {
							p.sendMessage(prefix + ChatColor.RED + "You must begin class "  + args[1] + " first!");
							return true;
						}

						for(Player students : Bukkit.getOnlinePlayers()) {
							if(plugin.getInstance().getClass(students) != null && plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
								students.sendMessage("");
								students.sendMessage(prefix + class_started);
								students.sendMessage("");
								students.sendTitle(ChatColor.GREEN + "Class has started!", "");
								playPing(students);
							}
						}

						plugin.getInstance().openclasses.remove(args[1]);
						plugin.getInstance().startedclasses.add(args[1]);
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				if(args.length == 1 && args[0].equalsIgnoreCase("list")){
					if(p.hasPermission("class.*") || p.hasPermission("class.list")) {

						//if(!conf.getCustomConfig().contains("Classes"))
						
						if(!conf.getCustomConfig().contains("Classes")) {
							p.sendMessage(prefix + ChatColor.RED + "No classes found!");
							return true;
						}

						p.sendMessage(ChatColor.GREEN + "Classes:");
						for(String s : conf.getCustomConfig().getConfigurationSection("Classes").getKeys(false)) {
							p.sendMessage(ChatColor.GRAY + "  - " + ChatColor.BLUE + s);
						}
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				if(args.length == 2 && (args[0].equalsIgnoreCase("call") || args[0].equalsIgnoreCase("pick") || args[0].equalsIgnoreCase("prh")) ){
					if(p.hasPermission("class.call") || p.hasPermission("class.*")) {

						if(Bukkit.getPlayer(args[1]) == null){
							p.sendMessage(prefix + ChatColor.RED + "Player is not online!");
							return true;
						}

						if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
							p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
							return true;
						}

						if(plugin.getInstance().getClass(Bukkit.getPlayer(args[1])) == null){
							p.sendMessage(prefix + ChatColor.RED + "Player is not in a class!");
							return true;
						}

						if(plugin.getInstance().called.contains(Bukkit.getPlayer(args[1]).getUniqueId())) {
							plugin.getInstance().called.remove(Bukkit.getPlayer(args[1]).getUniqueId());
							p.sendMessage(prefix + ChatColor.AQUA + Bukkit.getPlayer(args[1]).getName() + " is no longer called upon");
							Bukkit.getPlayer(args[1]).sendMessage(prefix + player_uncalled);
							return true;
						} else {
							plugin.getInstance().called.add(Bukkit.getPlayer(args[1]).getUniqueId());
							for(Player students : Bukkit.getOnlinePlayers()) {
								if(plugin.getInstance().getClass(students) != null && plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
									students.sendMessage(prefix + player_called.replace("%professor%", p.getName()).replace("%player%", Bukkit.getPlayer(args[1]).getName()));
								}
							}
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				if(args.length == 1 && args[0].equalsIgnoreCase("queue")) {
					if(p.hasPermission("class.queue") || p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId())) {

						if(plugin.getInstance().getClass(p) == null){
							p.sendMessage(prefix + ChatColor.RED + "You are not in a class!");
							return true;
						}

						int x;
						int i = 0;
						if(conf.getCustomConfig().contains("Classes." + plugin.getInstance().getClass(p) + ".max_students")) {
							x = Integer.parseInt(conf.getCustomConfig().getString("Classes." + plugin.getInstance().getClass(p) + ".max_students"));
						} else {
							x = 40;
						}
						for (Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							Player student  = Bukkit.getPlayer(entry.getKey());
							if(entry.getValue().equalsIgnoreCase(plugin.getInstance().getClass(p)) && !plugin.getInstance().professors.contains(entry.getKey())) {
								i++;
							}
						}

						if(i < 2) p.sendMessage(prefix + ChatColor.AQUA + "There is " + ChatColor.WHITE + ChatColor.BOLD + i +  "/" + x + ChatColor.AQUA + " players queued for your class");
						if(i > 1) p.sendMessage(prefix + ChatColor.AQUA + "There are " + ChatColor.WHITE + ChatColor.BOLD + i + "/" + x + ChatColor.AQUA + " players queued for your class");
						return true;

					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}
				
				/* CUSTOM COMMAND ENABLE FOR CAM
				if(args.length == 2 && args[0].equalsIgnoreCase("reward")) {
					if(p.hasPermission("class.reward") || p.hasPermission("class.*")) {
						
						if(plugin.getInstance().getClass(p) == null){
							p.sendMessage(prefix + ChatColor.RED + "You are not in a class!");
							return true;
						}

						if(Bukkit.getPlayer(args[1]) == null) {
							p.sendMessage(prefix + ChatColor.RED + "Player not found");
							return true;
						}
						
						Player student = Bukkit.getPlayer(args[1]);
						plugin.getInstance().economy.depositPlayer(student, 1);
						playPing(student);
						student.sendMessage(prefix + ChatColor.GREEN + "You have been awarded with 1 CC!");
						p.sendMessage(prefix + ChatColor.AQUA + "Given 1 CC to " + args[1] + "!");
						return true;

					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}
				

				
				if(args.length == 1 && args[0].equalsIgnoreCase("gcc")) {
					if(p.hasPermission("class.reward") || p.hasPermission("class.*")) {
						
						if(plugin.getInstance().getClass(p) == null){
							p.sendMessage(prefix + ChatColor.RED + "You are not in a class!");
							return true;
						}
						
						for (Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
							if(Bukkit.getPlayer(entry.getKey()) != null && !plugin.getInstance().professors.contains(entry.getKey())) {
								Player student = Bukkit.getPlayer(entry.getKey());
								if(plugin.getInstance().getClass(p).equalsIgnoreCase(plugin.getInstance().getClass(student))) {
									plugin.getInstance().economy.depositPlayer(student, 1);
									student.sendMessage(prefix + ChatColor.GREEN + "You have been awarded with 1 CC!");
									playPing(student);
								}
							
							}
						}
						
						p.sendMessage(prefix + ChatColor.AQUA + "Given 1 CC to the whole class!");
						return true;

					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}
				 */

				if(args.length == 2 && args[0].equalsIgnoreCase("addhelper")){
					if(p.hasPermission("class.addhelper") || p.hasPermission("class.*")) {

						if(Bukkit.getPlayer(args[1]) == null){
							p.sendMessage(prefix + ChatColor.RED + "Player is not online!");
							return true;
						}

						Player helper = Bukkit.getPlayer(args[1]);

						if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
							p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
							return true;
						}

						if(helper.hasPermission("class.helper")) {
							p.sendMessage(prefix + args[1] + " already has access to global helper permission (class.helper)");
							return true;
						}

						if(plugin.getInstance().getClass(Bukkit.getPlayer(args[1])) == null){
							p.sendMessage(prefix + ChatColor.RED + "Player is not in a class!");
							return true;
						}

						if(plugin.getInstance().helpers.contains(helper.getUniqueId())) {
							p.sendMessage(prefix + ChatColor.RED + args[1] + " is already a helper for this class!");
							return true;
						} else {
							plugin.getInstance().helpers.add(helper.getUniqueId());
							p.sendMessage(prefix + ChatColor.AQUA + "You have made " + args[1] + " helper for this class!");
							helper.sendMessage(prefix + ChatColor.AQUA + "You have been granted helper permissions for this class!");
							playPing(helper);
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				if(args.length == 2 && args[0].equalsIgnoreCase("removehelper")){
					if(p.hasPermission("class.addhelper") || p.hasPermission("class.*")) {

						if(Bukkit.getPlayer(args[1]) == null){
							p.sendMessage(prefix + ChatColor.RED + "Player is not online!");
							return true;
						}

						Player helper = Bukkit.getPlayer(args[1]);

						if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
							p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
							return true;
						}

						if(helper.hasPermission("class.helper")) {
							p.sendMessage(prefix + args[1] + " has the global helper permission! (class.helper)");
							return true;
						}

						if(plugin.getInstance().getClass(Bukkit.getPlayer(args[1])) == null){
							p.sendMessage(prefix + ChatColor.RED + "Player is not in a class!");
							return true;
						}

						if(plugin.getInstance().helpers.contains(helper.getUniqueId())) {
							plugin.getInstance().helpers.remove(helper.getUniqueId());
							helper.sendMessage(prefix + ChatColor.AQUA + "Thank you for helping! Your help is no longer required");
							p.sendMessage(prefix + ChatColor.GREEN + args[1] + "'s helper permissions removed");
							return true;
						} else {
							plugin.getInstance().helpers.add(helper.getUniqueId());
							p.sendMessage(prefix + ChatColor.RED + args[1] + " is not a helper!");
							return true;
						}

					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}





				if(args.length == 2 && args[0].equalsIgnoreCase("grade")){

					if(p.hasPermission("class.grade") || p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId())) {
						
						if(!plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p))) {
							p.sendMessage(prefix + ChatColor.GRAY + "You must be in a started class to use this!");
							return true;
						}


						if(p.getInventory().getItemInHand().getType().equals(Material.BOOK_AND_QUILL) 
								|| p.getInventory().getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {

							ItemStack is = p.getInventory().getItemInHand();
							ItemMeta iMeta = is.getItemMeta();
							String name = iMeta.getDisplayName().replace("'s work", "");
							ArrayList<String> itemMetaLore = new ArrayList<String>();
							
							if(plugin.getInstance().getConfig().getConfigurationSection("Rewards").getKeys(false).size() == 0) {
								plugin.getInstance().reloadConfig();
							}
							
							if(plugin.getInstance().getConfig().getConfigurationSection("Rewards").getKeys(false).contains(args[1].toUpperCase())){

								if(Bukkit.getPlayer(name) == null) {
									p.sendMessage(prefix + ChatColor.RED + "Unable to return players work!");
									return true;
								}
								
								itemMetaLore.add(ChatColor.GRAY + "Grade: " + ChatColor.YELLOW + args[1].toUpperCase());
								itemMetaLore.add(ChatColor.GRAY + "Graded by: " + ChatColor.DARK_PURPLE + p.getName());
								iMeta.setLore(itemMetaLore);
								is.setItemMeta(iMeta);

								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
								Bukkit.getPlayer(name).getInventory().addItem(is);
								Bukkit.getPlayer(name).sendMessage(prefix + work_returned);
								playPing(Bukkit.getPlayer(name));

								List<String> cmds = plugin.getInstance().getConfig().getStringList("Rewards." + args[1].toUpperCase());
								for(int i = 0; i < cmds.size(); i++) {
									String replacePlayer = cmds.get(i).replace("%player%", name);
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacePlayer);
								}

								return true;

							} else {
								p.sendMessage(prefix + ChatColor.RED + "Invalid grade!");
								return true;
							}



							/*  1.8 CODE
						if(args[1].equalsIgnoreCase("T") || args[1].equalsIgnoreCase("P") || args[1].equalsIgnoreCase("D") || args[1].equalsIgnoreCase("O+") || args[1].equalsIgnoreCase("O*")
								|| args[1].equalsIgnoreCase("A") || args[1].equalsIgnoreCase("E") || args[1].equalsIgnoreCase("O")) {

							itemMetaLore.add(ChatColor.GRAY + "Grade: " + ChatColor.YELLOW + args[1].toUpperCase());
							itemMetaLore.add(ChatColor.GRAY + "Graded by: " + ChatColor.DARK_PURPLE + p.getName());
							iMeta.setLore(itemMetaLore);
							is.setItemMeta(iMeta);

							p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
							Bukkit.getPlayer(name).getInventory().addItem(is);
							Bukkit.getPlayer(name).sendMessage(prefix + work_returned);
							playPing(Bukkit.getPlayer(name));

							if(args[1].equalsIgnoreCase("O+") || args[1].equalsIgnoreCase("O*")) {
								List<String> cmds = plugin.getInstance().getConfig().getStringList("Rewards.O*");
								for(int i = 0; i < cmds.size(); i++) {
									String replacePlayer = cmds.get(i).replace("%player%", name);
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacePlayer);
								}
								return true;
							}

							List<String> cmds = plugin.getInstance().getConfig().getStringList("Rewards." + args[1].toUpperCase());
							for(int i = 0; i < cmds.size(); i++) {
								String replacePlayer = cmds.get(i).replace("%player%", name);
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacePlayer);
							}
							return true;
						} else {
							p.sendMessage(prefix + ChatColor.RED + "Invalid grade!");
							return true;
						}

							 */

						} else {
							p.sendMessage(prefix + ChatColor.RED + "Ungradable item!");
							return true;
						}
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				if(args.length == 2 && args[0].equalsIgnoreCase("kick")){

					if(p.hasPermission("class.kick") || p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId())) {

						if(Bukkit.getPlayer(args[1]) == null) {
							p.sendMessage(prefix + ChatColor.RED + args[1] + " is not a valid player!");
							return true;
						}

						Player kick = Bukkit.getPlayer(args[1]);
						
						if(plugin.getInstance().professors.contains(kick.getUniqueId())){
							p.sendMessage(prefix + ChatColor.RED + "You cannot kick the professor!");
							return true;
						}

						if(plugin.getInstance().openclasses.contains(plugin.getInstance().getClass(p))) {
							plugin.getInstance().students.remove(kick.getUniqueId());
							kick.sendMessage(prefix + queue_kicked);
							p.sendMessage(prefix + ChatColor.AQUA + "Removed " + kick.getName() + " from the queue");
							return true;
						}

						if(plugin.getInstance().getClass(kick) == null) {
							p.sendMessage(prefix + ChatColor.RED + kick.getName() + " is not in a class");
							return true;
						}

						for(Player students : Bukkit.getOnlinePlayers()) {
							if(plugin.getInstance().getClass(students) != null 
									&& plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p)) 
									&& plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
								students.sendMessage(prefix + left_class.replace("%player%", kick.getName()));
								playPing(students);
							}
						}

						if(plugin.getInstance().getClass(kick) != null) {
							kick.sendMessage(class_kicked);
							kick.sendMessage(class_kicked_info);
							p.sendMessage(prefix + ChatColor.AQUA + kick.getName() + " has been removed from the class");
							playPing(kick);
						}


						if(plugin.getInstance().previouslocs.containsKey(kick.getUniqueId())) {
							if(isInClass(kick, plugin.getInstance().getClass(p))) {
								Location s = plugin.getInstance().previouslocs.get(kick.getUniqueId());
								kick.teleport(s);
								plugin.getInstance().previouslocs.remove(p.getUniqueId());
							}
						}


						/*
						for (Map.Entry<UUID,Location> entry : plugin.getInstance().previouslocs.entrySet()) {
							if(kick.getUniqueId() == entry.getKey()) {
								if(plugin.getInstance().getClass(kick) != null && isInClass(kick, plugin.getInstance().getClass(kick))) {
									Location s = entry.getValue();
									kick.teleport(s);
								}
							}
						}
						 */

						plugin.getInstance().helpers.remove(kick.getUniqueId());
						plugin.getInstance().students.remove(kick.getUniqueId());
						plugin.getInstance().handin.remove(kick.getUniqueId());
						plugin.getInstance().called.remove(kick.getUniqueId());

						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}


				/* CUSTOM COMMAND ENABLE
				if(args.length == 2 && args[0].equalsIgnoreCase("kickcc")){

					if(p.hasPermission("class.kick") || p.hasPermission("class.*")) {

						if(Bukkit.getPlayer(args[1]) == null) {
							p.sendMessage(prefix + ChatColor.RED + args[1] + " is not a valid player!");
							return true;
						}

						Player kick = Bukkit.getPlayer(args[1]);

						if(plugin.getInstance().readyclasses.contains(plugin.getInstance().getClass(p))) {
							plugin.getInstance().students.remove(kick.getUniqueId());
							kick.sendMessage(prefix + queue_kicked);
							p.sendMessage(prefix + ChatColor.AQUA + "Removed " + kick.getName() + " from the queue");
							return true;
						}

						if(plugin.getInstance().getClass(kick) == null) {
							p.sendMessage(prefix + ChatColor.RED + kick.getName() + " is not in a class");
							return true;
						}

						for(Player students : Bukkit.getOnlinePlayers()) {
							if(plugin.getInstance().getClass(students) != null 
									&& plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p)) 
									&& plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
								students.sendMessage(prefix + left_class.replace("%player%", kick.getName()));
								playPing(students);
							}
						}

						plugin.getInstance().economy.depositPlayer(kick, 1);
						kick.sendMessage(prefix + ChatColor.AQUA + "Thank you for attending class");
						kick.sendMessage(prefix + ChatColor.GREEN + "You have been awarded 1 CC");

						if(plugin.getInstance().getClass(kick) != null) {
							//kick.sendMessage(class_kicked);
							//kick.sendMessage(class_kicked_info);
							//p.sendMessage(prefix + ChatColor.AQUA + kick.getName() + " has been removed from the class");
							playPing(kick);
						}


						if(plugin.getInstance().previouslocs.containsKey(kick.getUniqueId())) {
							if(isInClass(kick, plugin.getInstance().getClass(p))) {
								Location s = plugin.getInstance().previouslocs.get(kick.getUniqueId());
								kick.teleport(s);
								plugin.getInstance().previouslocs.remove(p.getUniqueId());
							}
						}

						plugin.getInstance().helpers.remove(kick.getUniqueId());
						plugin.getInstance().students.remove(kick.getUniqueId());
						plugin.getInstance().handin.remove(kick.getUniqueId());
						plugin.getInstance().called.remove(kick.getUniqueId());

						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}
				}

				 */

				if(args.length == 1 && args[0].equalsIgnoreCase("leave")){

					if(p.hasPermission("class.leave") || p.hasPermission("class.student") || p.hasPermission("class.*")) {

						if(plugin.getInstance().students.containsKey(p.getUniqueId()) && studentCount(plugin.getInstance().getClass(p)) <= 1) {
							for(Player current : Bukkit.getOnlinePlayers()) {
								if(plugin.getInstance().getClass(current) != null && plugin.getInstance().getClass(current).equalsIgnoreCase(plugin.getInstance().getClass(p))) {
									current.sendMessage("");
									current.sendMessage(prefix + class_ended);
									current.sendMessage("");
									playPing(current);
								}

								for (Map.Entry<UUID,Location> entry : plugin.getInstance().previouslocs.entrySet()) {
									if(current.getUniqueId() == entry.getKey()) {
										if(isInClass(current, plugin.getInstance().getClass(current))) {
											Location s = entry.getValue();
											current.teleport(s);
										}
									}
								}
							}

							for (Entry<UUID, String> s : plugin.getInstance().students.entrySet()) {
								if(s.getValue().contains(plugin.getInstance().getClass(p))) {
									plugin.getInstance().students.remove(s.getKey());
								}
							}

							for (Entry<UUID, ItemStack> s : plugin.getInstance().handin.entrySet()) {
								if(plugin.getInstance().getClass(p).equalsIgnoreCase(plugin.getInstance().getClass(Bukkit.getPlayer(s.getKey())))){
									plugin.getInstance().handin.remove(s.getKey());
								}
							}

							for(UUID s : plugin.getInstance().called) {
								if(plugin.getInstance().getClass(p).equalsIgnoreCase(plugin.getInstance().getClass(Bukkit.getPlayer(s)))) {
									plugin.getInstance().called.remove(s);
								}
							}

							plugin.getInstance().mutedclasses.remove(plugin.getInstance().getClass(p));
							plugin.getInstance().professors.remove(p.getUniqueId());
							//plugin.getInstance().openclasses.remove(args[1]);
							//plugin.getInstance().startedclasses.remove(args[1]);
							return true;
						}

						for(Player students : Bukkit.getOnlinePlayers()) {
							if(plugin.getInstance().getClass(students) != null 
									&& plugin.getInstance().startedclasses.contains(plugin.getInstance().getClass(p)) 
									&& plugin.getInstance().getClass(students).equals(plugin.getInstance().getClass(p))) {
								students.sendMessage(prefix + left_class.replace("%player%", p.getName()));
								playPing(students);
							}
						}

						if(plugin.getInstance().openclasses.contains(plugin.getInstance().getClass(p))) {
							plugin.getInstance().students.remove(p.getUniqueId());
							p.sendMessage(prefix + left_queue);
							return true;
						}

						if(!plugin.getInstance().students.containsKey(p.getUniqueId())) {
							p.sendMessage(prefix + handin_not_in_class);
							return true;
						}


						if(plugin.getInstance().getClass(p) != null && plugin.getInstance().handin.containsKey(p.getUniqueId())) {
							p.sendMessage(class_leave);
							p.sendMessage(class_leave_info);
							playPing(p);
						}


						if(plugin.getInstance().getClass(p) != null && !plugin.getInstance().handin.containsKey(p.getUniqueId())) {
							p.sendMessage(prefix + class_leave_goodbye);
							playPing(p);
						}

						Location s = plugin.getInstance().previouslocs.get(p.getUniqueId());
						p.teleport(s);


						plugin.getInstance().handin.remove(p.getUniqueId());
						plugin.getInstance().called.remove(p.getUniqueId());
						plugin.getInstance().students.remove(p.getUniqueId());
						plugin.getInstance().handin.remove(p.getUniqueId());
						plugin.getInstance().called.remove(p.getUniqueId());
						return true;
					} else {
						p.sendMessage(prefix + no_permission);
						return true;
					}

				} 

				if(args.length == 1 && args[0].equalsIgnoreCase("gatherwork")) {	

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


				if(args.length == 2 && args[0].equalsIgnoreCase("help")) {	
					if(p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId()) || p.hasPermission("class.start")) {

						if(args[1].equals("1")) {
							p.sendMessage("");
							p.sendMessage("§3§l§m=======================§7[§e§lSC+ Help§7]§3§l§m======================");
							p.sendMessage(ChatColor.YELLOW + "/Class Join <CLASS>" + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "Players can manually join a class ");
							p.sendMessage(ChatColor.YELLOW + "/Class Leave" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Players may leave classes when they wish");
							p.sendMessage(ChatColor.YELLOW + "/RH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "While in a class you can raise your hand");
							p.sendMessage(ChatColor.YELLOW + "/HandIn"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Hand in completed work to the professor");
							p.sendMessage(ChatColor.YELLOW + "/Class Create <NAME> <MAX-STUDENTS>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Create a class with max students allowed");
							p.sendMessage(ChatColor.YELLOW + "/Class Open <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Teleports students to the classroom");
							p.sendMessage(ChatColor.YELLOW + "/Class Start <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Notifies students that the class has begun");
							p.sendMessage("§3§l§m=======================§7[§e§lPage §61§8/§63§7]§3§l§m======================");
						}

						if(args[1].equals("2")) {
							p.sendMessage("");
							p.sendMessage("§3§l§m=======================§7[§e§lSC+ Help§7]§3§l§m======================");
							p.sendMessage(ChatColor.YELLOW + "/Class Ready <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Lets server know class is ready and opens queue");
							p.sendMessage(ChatColor.YELLOW + "/Class End <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "End a class and return students");
							p.sendMessage(ChatColor.YELLOW + "/Class Grade <GRADE>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Grade students work");
							p.sendMessage(ChatColor.YELLOW + "/Class AddPlayer <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "You can add players into class after it has started");
							p.sendMessage(ChatColor.YELLOW + "/Class Mute"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Professors can silence their class");
							p.sendMessage(ChatColor.YELLOW + "/Class Call <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Professors toggle if a player can talk when silenced");
							p.sendMessage(ChatColor.YELLOW + "/Class Kick <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "You can remove students from the classroom");
							p.sendMessage("§3§l§m=======================§7[§e§lPage §62§8/§63§7]§3§l§m======================");
						}

						if(args[1].equals("3")) {
							p.sendMessage("");
							p.sendMessage("§3§l§m=======================§7[§e§lSC+ Help§7]§3§l§m======================");
							p.sendMessage(ChatColor.YELLOW + "/Class List"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "List all the saved classes");
							p.sendMessage(ChatColor.YELLOW + "/Class GatherWork" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Open GUI to view students work");
							p.sendMessage(ChatColor.YELLOW + "/Class Give" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Give students the items in your hand");
							p.sendMessage(ChatColor.YELLOW + "/Class Magic" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Toggle magic wands in class if installed");
							p.sendMessage(ChatColor.YELLOW + "/Class Queue" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "See how many students queued for your class");

							//HELP FOR CUSTOM COMMANDS
							//p.sendMessage(ChatColor.YELLOW + "/Class KickCC <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Kicks a player from a class and gives them 1 CC");
							//p.sendMessage(ChatColor.YELLOW + "/Class KickAllCC <CLASS>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Ends class and gives students 1 CC");
							
							
							
							p.sendMessage(ChatColor.YELLOW + "/Class AddHelper" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Lets you grant helper permissions to a player");
							p.sendMessage(ChatColor.YELLOW + "/Class RemoveHelper" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Remove a helpers permissions");
							p.sendMessage(ChatColor.YELLOW + "/Class ToggleRH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Toggle when students can raise hands");

							p.sendMessage("§3§l§m=======================§7[§e§lPage §63§8/§63§7]§3§l§m======================");
						}


						/*
						if(args[1].equals("4")) {
							p.sendMessage("");
							p.sendMessage("§3§l§m=======================§7[§e§lSC+ Help§7]§3§l§m======================");
							p.sendMessage(ChatColor.YELLOW + "/Class ToggleRH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Toggle when students can raise hands");
							//p.sendMessage(ChatColor.YELLOW + "/Class Reward <NAME>" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Reward a student with 1 CC");
							//p.sendMessage(ChatColor.YELLOW + "/Class GCC" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Gives whole class 1 CC");
							p.sendMessage(ChatColor.YELLOW + "/Class AddHelper" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Lets you grant helper permissions to a player");
							p.sendMessage(ChatColor.YELLOW + "/Class RemoveHelper" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Remove a helpers permissions");
							p.sendMessage("");
							p.sendMessage("");
							//HELP FOR CUSTOM COMMANDS
						//	p.sendMessage(ChatColor.YELLOW + "/Class KickCC <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Kicks a player from a class and gives them 1 CC");
						//	p.sendMessage(ChatColor.YELLOW + "/Class KickAllCC <CLASS>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Ends class and gives students 1 CC");

							p.sendMessage("§3§l§m=======================§7[§e§lPage §64§8/§64§7]§3§l§m======================");
						}
						*/
						
						return true;
					} 
				} else if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
					if(p.hasPermission("class.start") || p.hasPermission("class.*") || p.hasPermission("class.helper") || plugin.getInstance().helpers.contains(p.getUniqueId())) {
						p.sendMessage("");
						p.sendMessage("§3§l§m=======================§7[§e§lSC+ Help§7]§3§l§m======================");
						p.sendMessage(ChatColor.YELLOW + "/Class Join <CLASS>" + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "Players can manually join a class ");
						p.sendMessage(ChatColor.YELLOW + "/Class Leave" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Players may leave classes when they wish");
						p.sendMessage(ChatColor.YELLOW + "/RH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "While in a class you can raise your hand");
						p.sendMessage(ChatColor.YELLOW + "/HandIn"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Hand in completed work to the professor");
						p.sendMessage(ChatColor.YELLOW + "/Class Create <NAME> <MAX-STUDENTS>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Create a class with max students allowed");
						p.sendMessage(ChatColor.YELLOW + "/Class Open <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Teleports students to the classroom");
						p.sendMessage(ChatColor.YELLOW + "/Class Start <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Notifies students that the class has begun");
						p.sendMessage("§3§l§m=======================§7[§e§lPage §61§8/§63§7]§3§l§m======================");

						return true;
					} else {			
						p.sendMessage("§3§l§m==========================§7[§e§lSC+ Help]§3§l§m==========================");
						p.sendMessage("");
						p.sendMessage(ChatColor.YELLOW + "/Class Join <CLASS>" + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "Players can manually join a class ");
						p.sendMessage(ChatColor.YELLOW + "/Class Leave" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Players may leave classes when they wish");
						p.sendMessage(ChatColor.YELLOW + "/RH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "While in a class you can raise your hand");
						p.sendMessage(ChatColor.YELLOW + "/HandIn"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Hand in completed work to the professor");
						p.sendMessage("");
						p.sendMessage("§3§l§m=========================================================");
						return true;
					}
				}

				if(args.length > 2 || args.length < 1) {
					p.sendMessage(prefix + invalid_usage);
					//showHelp(p);
					return true;
				}
			}

			p.sendMessage(prefix + invalid_usage);
			return true;
		}
		return false;
	}

	public void showHelp(Player p) {
		if(p.hasPermission("class.start") || p.hasPermission("class.*") || p.hasPermission("class.helper")) {
			p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + "===============================[SC+ Help]===============================");
			p.sendMessage(ChatColor.YELLOW + "/Class Join <CLASS>" + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "Players can manually join a class ");
			p.sendMessage(ChatColor.YELLOW + "/Class Leave" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Players may leave classes when they wish");
			p.sendMessage(ChatColor.YELLOW + "/RH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "While in a class you can raise your hand");
			p.sendMessage(ChatColor.YELLOW + "/HandIn"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Hand in completed work to the professor");
			p.sendMessage(ChatColor.YELLOW + "/Class Create <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Create a class");
			p.sendMessage(ChatColor.YELLOW + "/Class Open <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Allow students to queue for classes");
			p.sendMessage(ChatColor.YELLOW + "/Class Start <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Allow students to enter a class ");
			p.sendMessage(ChatColor.YELLOW + "/Class End <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "End a class and return students");
			p.sendMessage(ChatColor.YELLOW + "/Class Grade <GRADE>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Grade students work");
			p.sendMessage(ChatColor.YELLOW + "/Class AddPlayer <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "You can add players into class after it has started");
			p.sendMessage(ChatColor.YELLOW + "/Class Mute"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Professors can silence their class");
			p.sendMessage(ChatColor.YELLOW + "/Class Call <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Professors toggle if a player can talk when silenced");
			p.sendMessage(ChatColor.YELLOW + "/Class Kick <NAME>"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "You can remove students from the classroom");
			p.sendMessage(ChatColor.YELLOW + "/Class List"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "List all the saved classes");
			p.sendMessage(ChatColor.YELLOW + "/Class GatherWork"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Open GUI to view students work");
			p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + "===============================[Page 1/4]==============================");
		} else {			
			p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + "==============================================================");
			p.sendMessage(ChatColor.YELLOW + "/Class Join <CLASS>" + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "Players can manually join a class ");
			p.sendMessage(ChatColor.YELLOW + "/Class Leave" + ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Players may leave classes when they wish");
			p.sendMessage(ChatColor.YELLOW + "/RH"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "While in a class you can raise your hand");
			p.sendMessage(ChatColor.YELLOW + "/HandIn"+ ChatColor.DARK_GRAY + " - "  + ChatColor.AQUA + "Hand in completed work to the professor");
			p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + "=============================================================");
		}
	}

	public int studentCount(String classname) {
		int i = 0;
		for(String s : plugin.getInstance().students.values()) {
			if(s.equalsIgnoreCase(classname)) i++;
		}
		return i;
	}

	public void playPing(Player p) {		
		if (Bukkit.getVersion().contains("1.7")) p.playSound(p.getEyeLocation(), Sound.valueOf("NOTE_PLING") , 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.8")) p.playSound(p.getEyeLocation(), Sound.valueOf("NOTE_PLING") , 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.9")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.10")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.11")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.12")) p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 0.6F, 1F);
		if (Bukkit.getVersion().contains("1.13")) {
			p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"), 0.6F, 1F);
		} 
	}

	public boolean isInClass(Player p, String classname) {
		for (Map.Entry<UUID, String> entry : plugin.getInstance().students.entrySet()) {
			if(entry.getKey().equals(p.getUniqueId()) && entry.getValue().equalsIgnoreCase(classname)) {
				plugin.getInstance().students.remove(entry.getKey());
				return true;
			}
		}
		return false;
	}


	public void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color) {
		try {
			Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
			Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

			sendPacket(player, packet);
		}

		catch (Exception ex)
		{

		}
	}

	private void sendPacket(Player player, Object packet) {
		try
		{
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		}
		catch(Exception ex)
		{
		}
	}

	private Class<?> getNMSClass(String name) {
		try
		{
			return Class.forName("net.minecraft.server" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
		}
		catch(ClassNotFoundException ex)
		{
		}
		return null;
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

	public boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
