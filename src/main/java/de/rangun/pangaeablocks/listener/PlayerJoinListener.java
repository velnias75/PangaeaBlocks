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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class PlayerJoinListener implements Listener { // NOPMD by heiko on 12.06.22, 11:13

	@EventHandler
	public void onPlayerJoinEvent(final PlayerJoinEvent event) {

		final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		if (scoreboard.getEntityTeam(event.getPlayer()) == null) {

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

				team.addPlayer(event.getPlayer());

				audi.sendMessage(Component.text("Added ", NamedTextColor.RED)
						.append(Utils.getTeamFormattedPlayer(event.getPlayer())).append(Component
								.text(" to team ", NamedTextColor.RED).append(Component.text(teamName, team.color()))));

			} else {

				audi.sendMessage(Component.text("Couldn't add ", NamedTextColor.RED)
						.append(Utils.getTeamFormattedPlayer(event.getPlayer()))
						.append(Component.text(" to team ", NamedTextColor.RED)
								.append(Component.text(teamName, NamedTextColor.YELLOW, TextDecoration.BOLD))));
			}
		}
	}
}
