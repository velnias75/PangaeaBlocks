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

import java.util.List;
import java.util.UUID;

import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import de.rangun.pangaeablocks.db.DatabaseClient;
import de.rangun.pangaeablocks.utils.Utils;

/**
 * @author heiko
 *
 */
public final class LockDoorCommand extends AbstractDoorCommand {

	public LockDoorCommand(DatabaseClient db) {
		super(db);
	}

	@Override
	protected void doorAction(final Block block, final Player player, final String[] args) {
		db.registerBlock(Utils.doorBottom(block),
				args.length == 0 || !EVERYBODY.equals(args.length > 0 ? args[0] : null) ? player.getUniqueId()
						: UUID.fromString("00000000-0000-0000-0000-000000000000"));
	}

	@Override
	protected Particle particle() {
		return Particle.HEART;
	}

	@Override
	protected String status() {
		return "locked to";
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		if (args.length > 0 && args.length < 2) {
			return ImmutableList.of(EVERYBODY);
		}

		return super.onTabComplete(sender, command, label, args);
	}
}
