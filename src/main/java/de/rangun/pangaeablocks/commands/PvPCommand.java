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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableList;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * @author heiko
 *
 */
public final class PvPCommand extends NonDefaultTabCompleter implements CommandExecutor { // NOPMD by heiko on 09.06.22,
																							// 11:34

	private final Plugin plugin;

	public PvPCommand(final Plugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, // NOPMD by heiko on
																									// 09.06.22, 11:33
			final String[] args) {

		final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		final Team team = sender instanceof Player ? scoreboard.getEntityTeam((Player) sender) : null;
		final TextColor color = team != null ? team.color() : null;
		final Component prefix = team != null ? team.prefix().color(color) : Component.text("");
		final Component suffix = team != null ? team.suffix().color(color) : Component.text("");

		final List<World> worlds = plugin.getServer().getWorlds().stream().filter(world -> {
			return Environment.NORMAL.equals(world.getEnvironment());
		}).toList();

		for (final World world : worlds) {

			if (args.length == 0) {

				@SuppressWarnings("serial")
				final Set<Audience> audience = new HashSet<>() { // NOPMD by heiko on 09.06.22, 11:32
					{ // NOPMD by heiko on 09.06.22, 11:32
						{
							add(sender);
							addAll(Bukkit.getOnlinePlayers().stream().filter(player -> {
								return player.hasPermission("pangaeablocks.pvp");
							}).toList());
						}
					}
				};

				Audience.audience(audience).sendMessage(
						Component.text("PvP ").append(Component.text(world.getPVP() ? "enabled" : "disabled"))
								.append(Component.text(" on ")).append(Component.text(world.getName())),
						MessageType.CHAT);

			} else {

				@SuppressWarnings("serial")
				final List<Audience> audience = new ArrayList<>() { // NOPMD by heiko on 09.06.22, 11:32
					{ // NOPMD by heiko on 09.06.22, 11:32
						{
							add(Bukkit.getConsoleSender());
							addAll(Bukkit.getOnlinePlayers());
						}
					}
				};

				if ("on".equalsIgnoreCase(args[0])) {
					world.setPVP(true);
					Audience.audience(audience).sendMessage(Component.text("PvP enabled by ").append(prefix)
							.append(Component.text(sender.getName(), color)).append(suffix));
				}

				if ("off".equalsIgnoreCase(args[0])) {
					world.setPVP(false);
					Audience.audience(audience).sendMessage(Component.text("PvP disabled by ").append(prefix)
							.append(Component.text(sender.getName(), color)).append(suffix));
				}
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (args.length > 0 && args.length < 2) {
			return ImmutableList.of("on", "off"); // NOPMD by heiko on 09.06.22, 11:31
		}

		return super.onTabComplete(sender, command, label, args);
	}
}
