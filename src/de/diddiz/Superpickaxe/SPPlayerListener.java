package de.diddiz.Superpickaxe;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import com.nijiko.permissions.PermissionHandler;

public class SPPlayerListener extends PlayerListener
{
	private final PermissionHandler permissions;
	private final Superpickaxe sp;

	SPPlayerListener(Superpickaxe sp, PermissionHandler permissions) {
		this.sp = sp;
		this.permissions = permissions;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			final String msg = event.getMessage().toLowerCase();
			if (msg.equals("/") || msg.equals("//") || msg.equals("/,") || msg.equals("sp")) {
				event.setMessage("dummy");
				event.setCancelled(true);
				sp.getServer().dispatchCommand(event.getPlayer(), "spa");
			}
		}
	}

	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (!event.isCancelled()) {
			final Player player = event.getPlayer();
			if (sp.hasEnabled(player) && !permissions.has(event.getTo().getWorld().getName(), player.getName(), "superpickaxe.use"))
				sp.removePlayer(player);
		}
	}
}
