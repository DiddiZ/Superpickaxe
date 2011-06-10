package de.diddiz.Superpickaxe;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import com.nijiko.permissions.PermissionHandler;

public class SPBlockListener extends BlockListener
{
	private final Set<Integer> playerswithsp;
	private final PermissionHandler permissions;

	SPBlockListener(Set<Integer> playerswithsp, PermissionHandler permissions) {
		this.playerswithsp = playerswithsp;
		this.permissions = permissions;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		final Player player = event.getPlayer();
		if (!event.isCancelled() && playerswithsp.contains(player.getName().hashCode()) && event.getItemInHand() != null) {
			final int item = event.getItemInHand().getTypeId();
			if (item == 270 || item == 274 || item == 278 || item == 285) {
				if (event.getBlock().getTypeId() == 7 && !(permissions != null && permissions.has(player, "superpickaxe.breakBedrock")) && !player.isOp())
					return;
				event.setInstaBreak(true);
			}
		}
	}
}
