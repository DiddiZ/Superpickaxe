package de.diddiz.Superpickaxe;

import org.bukkit.Server;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class SPPlayerListener extends PlayerListener
{
	private final Server server;

	SPPlayerListener(Server server) {
		this.server = server;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			final String msg = event.getMessage().toLowerCase();
			if (msg.equals("/") || msg.equals("//") || msg.equals("/,") || msg.equals("sp")) {
				event.setMessage("dummy");
				event.setCancelled(true);
				server.dispatchCommand(event.getPlayer(), "spa");
			}
		}
	}
}
