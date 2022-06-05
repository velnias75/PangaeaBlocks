/*
 * Copyright 2022 by Heiko Schäfer <heiko@rangun.de>
 *
 * This file is part of PangaeaBlocks.
 *
 * PangaeaBlocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * PangaeaBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PangaeaBlocks.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.rangun.pangaeablocks.listener;

import java.util.Set;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import de.rangun.pangaeablocks.db.DatabaseClient;
import de.rangun.pangaeablocks.utils.Utils;

/**
 * @author heiko
 *
 */
public final class PlayerInteractListener extends AbstractListener {

	public PlayerInteractListener(final Plugin plugin, final DatabaseClient db) { // NOPMD by heiko on 05.06.22, 01:14
		super(plugin, db);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteractEvent(final PlayerInteractEvent event) {

		if (!event.hasBlock()) {
			return;
		}

		final Block block = event.getClickedBlock();
		final Action action = event.getAction();

		if (block.getBlockData() instanceof Openable && action.isRightClick()) {

			final Set<UUID> uuids = db.getBlockOwners(Utils.doorBottom(block));

			if (!uuids.isEmpty() && !uuids.contains(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
}
