package de.diddiz.Superpickaxe;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

public class SPBlockListener extends BlockListener
{
	private final Superpickaxe sp;

	SPBlockListener(Superpickaxe sp) {
		this.sp = sp;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		final Player player = event.getPlayer();
		if (!event.isCancelled() && sp.hasEnabled(player) && event.getItemInHand() != null) {
			final int item = event.getItemInHand().getTypeId();
			if (item == 270 || item == 274 || item == 278 || item == 285) {
				if (event.getBlock().getTypeId() == 7 && !sp.hasPermission(player, "superpickaxe.breakBedrock"))
					return;
				event.setInstaBreak(true);
			}
		}
	}
}
