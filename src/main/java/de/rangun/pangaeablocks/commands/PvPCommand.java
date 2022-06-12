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

import com.google.common.collect.ImmutableList;

import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

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
					Audience.audience(audience).sendMessage(enableDisableMsg(true, sender));
				}

				if ("off".equalsIgnoreCase(args[0])) {
					world.setPVP(false);
					Audience.audience(audience).sendMessage(enableDisableMsg(false, sender));
				}
			}
		}

		return true;
	}

	private Component enableDisableMsg(final boolean onOff, final CommandSender sender) {

		final TextColor textColor = onOff ? NamedTextColor.RED : NamedTextColor.GREEN;
		final TextDecoration deco = TextDecoration.BOLD;

		return Component.text("PvP ", textColor, deco)
				.append(Component.text(onOff ? "enabled" : "disabled", textColor, TextDecoration.ITALIC))
				.append(Component.text(" by ", textColor, deco)).append(Utils.getTeamFormattedPlayer((Player) sender));
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
