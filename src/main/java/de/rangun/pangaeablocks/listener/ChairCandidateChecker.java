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
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;

/**
 * @author heiko
 *
 */
class ChairCandidateChecker {

	protected ChairCandidateChecker() {
	}

	protected final boolean isValidForChair(Block block) {

		if (!(block.getBlockData() instanceof Stairs)) {
			return false;
		}

		final Stairs stair = (Stairs) block.getBlockData();

		return Shape.STRAIGHT.equals(stair.getShape()) && Half.BOTTOM.equals(stair.getHalf())
				&& (Material.REDSTONE_BLOCK
						.equals(block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType()));
	}
}