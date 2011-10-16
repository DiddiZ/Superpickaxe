package de.diddiz.Superpickaxe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

public class SPBlockListener extends BlockListener
{
	private final Superpickaxe sp;
	private final Set<Integer> tools, dontBreak;
	private final boolean disableDrops, disableToolWear;
	private final Consumer consumer;

	SPBlockListener(Superpickaxe sp) {
		this.sp = sp;
		final ConfigurationSection cfg = sp.getConfig();
		tools = new HashSet<Integer>(toIntList(cfg.getList("tools")));
		dontBreak = new HashSet<Integer>(toIntList(cfg.getList("dontBreak")));
		disableDrops = cfg.getBoolean("disableDrops", false);
		disableToolWear = cfg.getBoolean("disableToolWear", false);
		consumer = disableToolWear ? sp.getServer().getPluginManager().isPluginEnabled("LogBlock") ? ((LogBlock)sp.getServer().getPluginManager().getPlugin("LogBlock")).getConsumer() : null : null;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		final Player player = event.getPlayer();
		final ItemStack tool = event.getItemInHand();
		if (!event.isCancelled() && sp.hasEnabled(player) && tool != null && tools.contains(tool.getTypeId()) && !(dontBreak.contains(event.getBlock().getTypeId()) && !sp.hasPermission(player, "superpickaxe.breakAll"))) {
			if (disableDrops) {
				if (consumer != null)
					consumer.queueBlockBreak(player.getName(), event.getBlock().getState());
				event.getBlock().setTypeId(0);
				event.setCancelled(true);
				return;
			}
			event.setInstaBreak(true);
			if (disableToolWear)
				tool.setDurability((short)(tool.getDurability() - 1));
		}
	}

	static List<Integer> toIntList(List<?> list) {
		if (list == null)
			return new ArrayList<Integer>();
		final List<Integer> ints = new ArrayList<Integer>(list.size());
		for (final Object obj : list)
			if (obj instanceof Integer)
				ints.add((Integer)obj);
			else
				try {
					ints.add(Integer.valueOf(String.valueOf(obj)));
				} catch (final NumberFormatException ex) {
					Bukkit.getLogger().warning("[Superpickaxe] Config error: '" + obj + "' is not a number");
				}
		return ints;
	}
}
