/*
 * Copyright 2023 by Heiko Schäfer <heiko@rangun.de>
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
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import de.rangun.pangaeablocks.PangaeaBlocksPlugin;
import de.rangun.pangaeablocks.utils.Constants;
import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class AFKCommand extends NonDefaultTabCompleter implements Constants, CommandExecutor {

	private final PangaeaBlocksPlugin plugin;

	public AFKCommand(final PangaeaBlocksPlugin plugin) { // NOPMD by heiko on 31.01.23, 12:03
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (sender instanceof Player) {

			final Player player = (Player) sender;

			final List<Audience> audience = new ArrayList<>(List.of((Audience) sender.getServer().getConsoleSender()));

			audience.addAll(Bukkit.getOnlinePlayers());

			final PersistentDataContainer container = player.getPersistentDataContainer();
			final boolean isAfk = !(container.has(AFK_KEY)
					&& container.get(AFK_KEY, PersistentDataType.BYTE) == (byte) 1);

			container.set(AFK_KEY, PersistentDataType.BYTE, (byte) (isAfk ? 1 : 0));

			final Component text = Component.text(isAfk ? "ist nun " : "ist nicht mehr ").color(NamedTextColor.GRAY)
					.append(AFK_TEXT);

			Audience.audience(audience)
					.sendMessage(Utils.getTeamFormattedPlayer(player).append(Component.text(' ')).append(text));

			if (isAfk) {
				Audience.audience(Bukkit.getOnlinePlayers().stream().filter(opplayer -> {
					return opplayer.isOp();
				}).toList()).sendMessage(Component.empty()
						.append(Component.text("Teleportiere zu ")
								.append(Utils.getTeamFormattedPlayer(player).append(Component.text("?"))))
						.decorations(Set.of(TextDecoration.UNDERLINED), true)
						.clickEvent(ClickEvent.suggestCommand("/execute in " + player.getWorld().getKey().asString()
								+ " run tp @p " + player.getLocation().getX() + " " + player.getLocation().getY() + " "
								+ player.getLocation().getZ())));
			}

			plugin.sendToDiscordSRV(Component.text(":a: :regional_indicator_f: :regional_indicator_k:"), player);

		} else {
			Audience.audience(sender).sendMessage(
					Component.text("Command can get executed by a player only").color(NamedTextColor.YELLOW));
		}

		return true;
	}

}
