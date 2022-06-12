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

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class EntityDeathListener implements Listener { // NOPMD by heiko on 12.06.22, 16:23

	@EventHandler
	public void onEntityDeathEvent(final EntityDeathEvent event) {

		final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

		if (EntityType.VILLAGER.equals(event.getEntityType()) && players.contains(event.getEntity().getKiller())) {

			final Villager villager = (Villager) event.getEntity();

			final Component prof = Component.translatable(villager.getProfession());
			final Component name = villager.customName() != null
					? villager.customName().colorIfAbsent(NamedTextColor.AQUA)
							.append(Component.text(" (", NamedTextColor.DARK_AQUA)
									.append(prof.colorIfAbsent(NamedTextColor.DARK_AQUA))
									.append(Component.text(")", NamedTextColor.DARK_AQUA)))
					: prof.colorIfAbsent(NamedTextColor.DARK_AQUA);

			final Location location = villager.getLocation();

			Audience.audience(players)
					.sendMessage(Component.text("FATAL: ", NamedTextColor.DARK_RED, TextDecoration.BOLD)
							.append(Utils.getTeamFormattedPlayer(event.getEntity().getKiller()))
							.append(Component.text(" cowardly killed ", NamedTextColor.RED).append(name)
									.hoverEvent(HoverEvent.showText(Component.text("Click to teleport")))
									.clickEvent(ClickEvent.runCommand("/tp " + location.getBlockX() + " "
											+ location.getBlockY() + " " + location.getBlockZ()))));
		}
	}
}
