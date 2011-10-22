package de.diddiz.superpickaxe;

import static org.bukkit.Bukkit.getLogger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Superpickaxe extends JavaPlugin
{
	private PermissionHandler permissions = null;
	private final Set<String> playerswithsp = new HashSet<String>();

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
		final FileConfiguration config = getConfig();
		for (final Entry<String, Object> e : def.entrySet())
			if (!config.contains(e.getKey()))
				config.set(e.getKey(), e.getValue());
		saveConfig();
		final SPPlayerListener playerListener = new SPPlayerListener(this);
		pm.registerEvent(Type.BLOCK_DAMAGE, new SPBlockListener(this), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_CHANGED_WORLD, playerListener, Priority.Monitor, this);
		if (getConfig().getBoolean("overrideWorldEditCommands", false) && pm.isPluginEnabled("WorldEdit")) {
			getLogger().info("[Superpickaxe] Overriding WorldEdit commands");
			getServer().getPluginCommand("/").setExecutor(this);
			getServer().getPluginCommand("superpickaxe").setExecutor(this);
			pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Lowest, this);
		}
		getLogger().info("Superpickaxe v" + getDescription().getVersion() + " by DiddiZ enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info("Superpickaxe disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player)sender;
			if (hasPermission(player, "superpickaxe.use")) {
				if (hasEnabled(player))
					removePlayer(player);
				else
					addPlayer(player);
			} else
				player.sendMessage(ChatColor.RED + "You aren't allowed to do this.");
		} else
			sender.sendMessage("You aren't a player");
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
}
