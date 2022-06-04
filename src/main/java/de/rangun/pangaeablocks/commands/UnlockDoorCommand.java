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

package de.rangun.pangaeablocks.commands;

import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.rangun.pangaeablocks.db.DatabaseClient;

/**
 * @author heiko
 *
 */
public final class UnlockDoorCommand extends AbstractDoorCommand {

	public UnlockDoorCommand(final DatabaseClient db) {
		super(db);
	}

	@Override
	protected void doorAction(final Block block, final Player player, final String[] args) {
		db.deleteBlock(block);
	}

	@Override
	protected Particle particle() {
		return Particle.VILLAGER_ANGRY;
	}

	@Override
	protected String status() {
		return "unlocked from";
	}
}
