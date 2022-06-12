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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class AsyncPlayerPreLoginListener implements Listener { // NOPMD by heiko on 12.06.22, 11:14

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {

		final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(event.getUniqueId());

		if (Bukkit.hasWhitelist() && !oPlayer.isWhitelisted()) {

			final Component discord = Component.text("https://discord.gg/kQP9QSG6F4")
					.clickEvent(ClickEvent.openUrl("https://discord.gg/kQP9QSG6F4")).color(NamedTextColor.DARK_GREEN)
					.decorate(TextDecoration.UNDERLINED);

			event.kickMessage(Component
					.text("Du bist nicht auf der weissen Liste!", NamedTextColor.RED, TextDecoration.BOLD)
					.append(Component.newline().append(Component.text("Komme auf unseren Discord: ").append(discord))));

			event.setLoginResult(Result.KICK_WHITELIST);

		} else if (!oPlayer.hasPlayedBefore() && Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.hasPermission("pangaeablocks.welcome_new_players")).toList().isEmpty()) {

			event.kickMessage(Component.text("Im Moment ist kein Spieler Online, der Dich auf ")
					.append(Component.text("Pangäa", NamedTextColor.DARK_GREEN, TextDecoration.ITALIC))
					.append(Component.text(" einführen könnte.").append(Component.newline())
							.append(Component.text("Frage im Discord nach oder komme später wieder.")))
					.colorIfAbsent(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));

			event.setLoginResult(Result.KICK_OTHER);
		}
	}
}
