package de.diddiz.superpickaxe;

import static org.bukkit.Bukkit.getPluginCommand;
import static org.bukkit.Bukkit.getPluginManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

public class Superpickaxe extends JavaPlugin implements Listener
{
	private PermissionHandler permissions = null;
	private final Set<String> playerswithsp = new HashSet<String>();
	private Set<Integer> tools, dontBreak;
	private boolean disableDrops, disableToolWear, overrideWorldEditCommands;
	private Consumer consumer;

	@Override
	public void onEnable() {
		final PluginManager pm = getPluginManager();
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
		final ConfigurationSection cfg = getConfig();
		tools = new HashSet<Integer>(cfg.getIntegerList("tools"));
		dontBreak = new HashSet<Integer>(cfg.getIntegerList("dontBreak"));
		disableDrops = cfg.getBoolean("disableDrops", false);
		disableToolWear = cfg.getBoolean("disableToolWear", false);
		overrideWorldEditCommands = cfg.getBoolean("overrideWorldEditCommands");
		if (disableDrops) {
			if (getPluginManager().isPluginEnabled("LogBlock"))
				consumer = ((LogBlock)getPluginManager().getPlugin("LogBlock")).getConsumer();
			else {
				getLogger().severe("[Superpickaxe] LogBlock not found");
				consumer = null;
			}
		} else
			consumer = null;
		pm.registerEvents(this, this);
		if (overrideWorldEditCommands) {
			getLogger().info("[Superpickaxe] Overriding WorldEdit commands");
			for (final String cmd : new String[]{"/", "superpickaxe"})
				if (getPluginCommand(cmd) != null)
					getPluginCommand(cmd).setExecutor(this);
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

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getItemInHand();
		if (!event.isCancelled() && hasEnabled(player) && tool != null && tools.contains(tool.getTypeId()) && !(dontBreak.contains(event.getBlock().getTypeId()) && !hasPermission(player, "superpickaxe.breakAll")))
			if (disableDrops && consumer != null) {
				consumer.queueBlockBreak(player.getName(), event.getBlock().getState());
				event.getBlock().setTypeId(0);
				event.setCancelled(true);
			} else {
				event.setInstaBreak(true);
				if (disableToolWear)
					tool.setDurability((short)(tool.getDurability() - 1));
			}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled() && overrideWorldEditCommands) {
			final String msg = event.getMessage().toLowerCase();
			if (msg.equals("/") || msg.equals("//") || msg.equals("/,") || msg.equals("/sp")) {
				event.setMessage("dummy");
				event.setCancelled(true);
				getServer().dispatchCommand(event.getPlayer(), "spa");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if (hasEnabled(player) && !hasPermission(player, "superpickaxe.use"))
			removePlayer(player);
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
