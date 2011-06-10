package de.diddiz.Superpickaxe;

import java.util.HashSet;
import org.bukkit.ChatColor;
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
	private PermissionHandler permissions = null;
	private final HashSet<Integer> playerswithsp = new HashSet<Integer>();

	@Override
	public void onEnable() {
		final PluginManager pm = getServer().getPluginManager();
		if (pm.getPlugin("Permissions") != null)
			permissions = ((Permissions)pm.getPlugin("Permissions")).getHandler();
		getConfiguration().load();
		pm.registerEvent(Type.BLOCK_DAMAGE, new SPBlockListener(playerswithsp, permissions), Priority.Normal, this);
		if (getConfiguration().getBoolean("overrideWorldEditCommands", false))
			pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, new SPPlayerListener(getServer()), Event.Priority.Lowest, this);
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
		if (!(permissions != null && permissions.has(player, "superpickaxe.use")) && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You aren't allowed to do this.");
			return true;
		}
		final int hash = player.getName().hashCode();
		if (playerswithsp.contains(hash)) {
			playerswithsp.remove(hash);
			player.sendMessage(ChatColor.GREEN + "Super pickaxe disabled.");
		} else {
			playerswithsp.add(hash);
			player.sendMessage(ChatColor.GREEN + "Super pickaxe enabled.");
		}
		return true;
	}

}
