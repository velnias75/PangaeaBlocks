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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

/**
 * @author heiko
 *
 */
abstract class AbstractRaytraceCommand extends NonDefaultTabCompleter implements CommandExecutor {

	protected AbstractRaytraceCommand() {
	}

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (sender instanceof Player) {

			final Player player = (Player) sender;
			final RayTraceResult result = player.getWorld().rayTraceEntities(player.getLocation(),
					player.getLocation().getDirection(), 8, 1.5d);

			return processRayTraceResult(result, args);

		} else {
			Bukkit.getLogger().info("You must be an online player to execute this command.");
		}

		return true;
	}

	protected abstract boolean processRayTraceResult(final RayTraceResult result, final String[] args);

}