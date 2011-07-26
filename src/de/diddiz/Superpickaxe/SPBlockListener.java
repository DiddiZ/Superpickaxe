package de.diddiz.Superpickaxe;

import java.util.HashSet;
import java.util.Set;
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
		tools = new HashSet<Integer>(sp.getConfiguration().getIntList("tools", null));
		dontBreak = new HashSet<Integer>(sp.getConfiguration().getIntList("dontBreak", null));
		disableDrops = sp.getConfiguration().getBoolean("disableDrops", false);
		disableToolWear = sp.getConfiguration().getBoolean("disableToolWear", false);
		consumer = disableToolWear ? (sp.getServer().getPluginManager().getPlugin("LogBlock") != null ? ((LogBlock)sp.getServer().getPluginManager().getPlugin("LogBlock")).getConsumer() : null) : null;
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
}
