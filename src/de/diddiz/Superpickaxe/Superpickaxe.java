package de.diddiz.Superpickaxe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Superpickaxe extends JavaPlugin
{
	private PermissionHandler permissions = null;
	private final HashSet<String> playerswithsp = new HashSet<String>();

	@Override
	public void onEnable() {
		final PluginManager pm = getServer().getPluginManager();
		if (pm.getPlugin("Permissions") != null)
			permissions = ((Permissions)pm.getPlugin("Permissions")).getHandler();
		final Map<String, Object> def = new HashMap<String, Object>();
		def.put("overrideWorldEditCommands", false);
		def.put("tools", Arrays.asList(270, 274, 278, 285));
		def.put("dontBreak", Arrays.asList(7));
		def.put("disableDrops", false);
		def.put("disableToolWear", false);
		final Configuration config = getConfiguration();
		config.load();
		for (final Entry<String, Object> e : def.entrySet())
			if (config.getProperty(e.getKey()) == null)
				config.setProperty(e.getKey(), e.getValue());
		config.save();
		final SPPlayerListener playerListener = new SPPlayerListener(this);
		pm.registerEvent(Type.BLOCK_DAMAGE, new SPBlockListener(this), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Monitor, this);
		if (getConfiguration().getBoolean("overrideWorldEditCommands", false))
			pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Lowest, this);
		getServer().getLogger().info("Superpickaxe v" + getDescription().getVersion() + " by DiddiZ enabled");
	}

	@Override
	public void onDisable() {
		getServer().getLogger().info("Superpickaxe disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("spa"))
			return false;
		if (!(sender instanceof Player)) {
			sender.sendMessage("You aren't a player");
			return true;
		}
		final Player player = (Player)sender;
		if (!hasPermission(player, "superpickaxe.use")) {
			player.sendMessage(ChatColor.RED + "You aren't allowed to do this.");
			return true;
		}
		if (hasEnabled(player))
			removePlayer(player);
		else
			addPlayer(player);
		return true;
	}

	void addPlayer(Player player) {
		playerswithsp.add(player.getName());
		player.sendMessage(ChatColor.GREEN + "Super pickaxe enabled.");
	}

	void removePlayer(Player player) {
		playerswithsp.remove(player.getName());
		player.sendMessage(ChatColor.GREEN + "Super pickaxe disabled.");
	}

	boolean hasEnabled(Player player) {
		return playerswithsp.contains(player.getName());
	}

	boolean hasPermission(CommandSender sender, String permission) {
		if (permissions != null && sender instanceof Player)
			return permissions.has((Player)sender, permission);
		return sender.hasPermission(permission);
	}

	boolean hasPermission(CommandSender sender, String permission, World world) {
		if (permissions != null && sender instanceof Player)
			return permissions.has(world.getName(), ((Player)sender).getName(), permission);
		return sender.hasPermission(permission);
	}
}
