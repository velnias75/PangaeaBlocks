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

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
public final class PlayerJoinListener implements Constants, Listener { // NOPMD by heiko on 12.06.22, 11:13

	private final static Component AFK_WARNING = Component.empty()
			.append(Component.text("Du bist noch als ").append(AFK_TEXT).append(Component.text(" markiert! ")))
			.color(NamedTextColor.DARK_RED).decorations(Set.of(TextDecoration.BOLD, TextDecoration.ITALIC), true)
			.append(Component.text("Umschalten?").color(NamedTextColor.DARK_GREEN)
					.decorations(Set.of(TextDecoration.BOLD, TextDecoration.UNDERLINED), true)
					.clickEvent(ClickEvent.runCommand("/afk")));

	@EventHandler
	public void onPlayerJoinEvent(final PlayerJoinEvent event) {

		final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		final Player player = event.getPlayer();

		if (scoreboard.getEntityTeam(player) == null) {

			final String teamName = "gast"; // NOPMD by heiko on 12.06.22, 11:13
			final Team team = scoreboard.getTeam(teamName);

			@SuppressWarnings("serial")
			final Audience audi = Audience.audience(new ArrayList<>() {
				{ // NOPMD by heiko on 12.06.22, 11:13
					add(Bukkit.getConsoleSender());
					addAll(Bukkit.getOnlinePlayers().stream().filter(player -> {
						return player.isOp();
					}).toList());
				}
			});

			if (team != null) {

				team.addPlayer(player);

				audi.sendMessage(Component.text("Added ", NamedTextColor.RED)
						.append(Utils.getTeamFormattedPlayer(player)).append(Component
								.text(" to team ", NamedTextColor.RED).append(Component.text(teamName, team.color()))));

			} else {

				audi.sendMessage(
						Component.text("Couldn't add ", NamedTextColor.RED).append(Utils.getTeamFormattedPlayer(player))
								.append(Component.text(" to team ", NamedTextColor.RED)
										.append(Component.text(teamName, NamedTextColor.YELLOW, TextDecoration.BOLD))));
			}
		}

		final PersistentDataContainer container = player.getPersistentDataContainer();

		if (container.has(AFK_KEY) && container.get(AFK_KEY, PersistentDataType.BYTE) == (byte) 1) {
			Audience.audience(player).sendMessage(AFK_WARNING);
		}
	}
}
