package de.diddiz.Superpickaxe;

import java.util.HashSet;
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
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Superpickaxe extends JavaPlugin
{
	private PermissionHandler permissions;
	private final HashSet<String> playerswithsp = new HashSet<String>();

	@Override
	public void onEnable() {
		final PluginManager pm = getServer().getPluginManager();
		permissions = ((Permissions)pm.getPlugin("Permissions")).getHandler();
		getConfiguration().load();
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
		if (!permissions.has(player, "superpickaxe.use")) {
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
