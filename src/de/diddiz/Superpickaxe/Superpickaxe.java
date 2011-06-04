package de.diddiz.Superpickaxe;

import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Superpickaxe extends JavaPlugin
{
	private PermissionHandler permissions = null;
	private final HashSet<Integer> playerswithsp = new HashSet<Integer>();

	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("Permissions") != null)
			permissions = ((Permissions)getServer().getPluginManager().getPlugin("Permissions")).getHandler();
		getConfiguration().load();
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGE, new SPBlockListener(), Event.Priority.Normal, this);
		if (getConfiguration().getBoolean("overrideWorldEditCommands", false))
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, new SPPlayerListener(), Event.Priority.Lowest, this);
		getServer().getLogger().info("Superpickaxe v" + getDescription().getVersion() + " by DiddiZ enabled");
	}

	@Override
	public void onDisable() {
		getServer().getLogger().info("Superpickaxe disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("spa")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You aren't a player");
				return true;
			}
			final Player player = (Player)sender;
			if (!(permissions != null && permissions.has(player, "superpickaxe.use")) && !player.isOp()) {
				player.sendMessage(ChatColor.RED + "You aren't allowed to do that.");
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
		return false;
	}

	public class SPBlockListener extends BlockListener
	{
		@Override
		public void onBlockDamage(BlockDamageEvent event) {
			if (!event.isCancelled() && playerswithsp.contains(event.getPlayer().getName().hashCode()))
				if (event.getItemInHand() != null) {
					final int item = event.getItemInHand().getTypeId();
					if (item == 270 || item == 274 || item == 278 || item == 285)
						if (event.getBlock().getTypeId() == 7 && !(permissions != null && permissions.has(event.getPlayer(), "superpickaxe.breakBedrock")) && !event.getPlayer().isOp())
							return;
					event.setInstaBreak(true);
				}
		}
	}

	public class SPPlayerListener extends PlayerListener
	{
		@Override
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
			if (!event.isCancelled()) {
				final String msg = event.getMessage().toLowerCase();
				if (msg.equals("//") || msg.equals("/,") || msg.equals("sp")) {
					event.setMessage("dummy");
					event.setCancelled(true);
					getServer().dispatchCommand(event.getPlayer(), "spa");
				}
			}
		}
	}
}
