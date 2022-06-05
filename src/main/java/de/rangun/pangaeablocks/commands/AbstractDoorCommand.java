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
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.ParticleBuilder;

import de.rangun.pangaeablocks.db.DatabaseClient;
import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

abstract class AbstractDoorCommand extends NonDefaultTabCompleter implements CommandExecutor {

	protected final static String EVERYBODY = "everybody";

	protected final DatabaseClient db; // NOPMD by heiko on 05.06.22, 00:53

	protected AbstractDoorCommand(final DatabaseClient db) { // NOPMD by heiko on 05.06.22, 00:54
		super();
		this.db = db;
	}

	protected abstract void doorAction(Block block, Player player, String[] args); // NOPMD by heiko on 05.06.22, 00:40

	protected abstract String status();

	protected abstract Particle particle();

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (sender instanceof Player) {

			final Player player = (Player) sender;
			final Block block = player.getTargetBlockExact(16);

			if (block != null && block.getBlockData() instanceof Openable) {

				doorAction(block, player, args);

				(new ParticleBuilder(particle())).allPlayers().count(1).offset(0.0d, 0.0d, 0.0d)
						.location(Utils.doorTop(block).getLocation().add(0.5d, 1.25d, 0.5d)).spawn();

				player.sendMessage(Component.text().color(NamedTextColor.DARK_GREEN) // NOPMD by heiko on 05.06.22,
																						// 00:54
						.append(Component.translatable(block.getType())).append(Component.text(" at "))
						.append(Component.text(block.getX())).append(Component.text(", ")) // NOPMD by heiko on
																							// 05.06.22, 00:54
						.append(Component.text(block.getY())).append(Component.text(", "))
						.append(Component.text(block.getZ())).append(Component.text(" ("))
						.append(Component.text(block.getWorld().getKey().asString()))
						.append(Component.text(") " + status() + " ")).append(Component.text(
								EVERYBODY.equals(args.length > 0 ? args[0] : null) ? EVERYBODY : player.getName()))); // NOPMD
																														// by
																														// heiko
																														// on
																														// 05.06.22,
																														// 00:57
			}

		} else {
			Bukkit.getLogger().info("You must be an online player to execute this command.");
		}

		return true;
	}
}
