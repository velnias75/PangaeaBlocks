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
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;

/**
 * @author heiko
 *
 */
class ChairCandidateChecker {

	protected ChairCandidateChecker() { // NOPMD by heiko on 05.06.22, 06:59
	}

	protected final boolean isValidForChair(final Block block) {

		if (!(block.getBlockData() instanceof Stairs)) {
			return false; // NOPMD by heiko on 05.06.22, 06:58
		}

		final Stairs stair = (Stairs) block.getBlockData();

		return Shape.STRAIGHT.equals(stair.getShape()) && Half.BOTTOM.equals(stair.getHalf())
				&& ((block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()) // NOPMD by heiko on
																								// 05.06.22, 06:58
						.getBlockPower(BlockFace.UP) > 0
						|| isActiveTorch(block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ())))
						&& isEmptyOrLiquid(block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ()))
						&& isEmptyOrLiquid(block.getWorld().getBlockAt(block.getX(), block.getY() + 2, block.getZ())));
	}

	private boolean isEmptyOrLiquid(final Block block) {
		return block.isEmpty() || block.isLiquid();
	}

	private boolean isActiveTorch(final Block block) {
		return Material.REDSTONE_TORCH.equals(block.getType()) && ((Lightable) block.getBlockData()).isLit();
	}
}
