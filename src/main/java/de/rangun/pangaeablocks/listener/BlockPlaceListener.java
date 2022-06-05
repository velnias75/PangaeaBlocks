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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author heiko
 *
 */
public final class BlockPlaceListener extends ChairCandidateChecker implements Listener { // NOPMD by heiko on 05.06.22,
																							// 01:07

	@EventHandler
	public void onBlockPlaceEvent(final BlockPlaceEvent event) {

		final Block block = event.getBlockAgainst();
		final ItemStack handItem = event.getItemInHand();

		if (event.canBuild() && !event.getPlayer().isSneaking() && !Material.AIR.equals(handItem.getType())
				&& isValidForChair(block)) {
			event.setCancelled(true);
		}
	}
}
