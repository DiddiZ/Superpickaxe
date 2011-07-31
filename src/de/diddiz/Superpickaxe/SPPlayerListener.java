package de.diddiz.Superpickaxe;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SPPlayerListener extends PlayerListener
{
	private final Superpickaxe sp;

	SPPlayerListener(Superpickaxe sp) {
		this.sp = sp;
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
			if (sp.hasEnabled(player) && !sp.hasPermission(player, "superpickaxe.use", event.getTo().getWorld()))
				sp.removePlayer(player);
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!event.isCancelled() && event.getFrom().getWorld() != event.getTo().getWorld()) {
			final Player player = event.getPlayer();
			if (sp.hasEnabled(player) && !sp.hasPermission(player, "superpickaxe.use", event.getTo().getWorld()))
				sp.removePlayer(player);
		}
	}
}
