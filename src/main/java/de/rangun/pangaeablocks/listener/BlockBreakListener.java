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

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import de.rangun.pangaeablocks.db.Database;
import de.rangun.pangaeablocks.utils.LocationUtils;

/**
 * @author heiko
 *
 */
public final class BlockBreakListener implements Listener {

	private final Database db;

	public BlockBreakListener(final Database db) {
		this.db = db;
	}

	@EventHandler
	void onBlockBreakEvent(final BlockBreakEvent event) {

		final Block block = event.getBlock();

		if (block.getBlockData() instanceof Openable) {

			final Block locBlock = LocationUtils.doorLocation(block);
			final UUID uuid = db.getBlock(locBlock);

			if (uuid != null) {

				if (!uuid.equals(event.getPlayer().getUniqueId())) {
					event.setCancelled(true);
				} else {
					db.deleteBlock(locBlock);
				}
			}
		}
	}
}