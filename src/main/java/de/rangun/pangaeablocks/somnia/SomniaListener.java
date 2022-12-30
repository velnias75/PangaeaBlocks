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

package de.rangun.pangaeablocks.somnia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ShapedRecipe;

import de.rangun.pangaeablocks.PangaeaBlocksPlugin;
import de.rangun.pangaeablocks.somnia.SomniaRecipe.Somnia;
import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class SomniaListener implements Listener {

	private final PangaeaBlocksPlugin plugin;
	private final List<String> moderators;

	public SomniaListener(final PangaeaBlocksPlugin plugin, final List<String> moderators) {
		this.plugin = plugin;
		this.moderators = moderators;
	}

	@EventHandler
	public void onPrepareItemCraft(final PrepareItemCraftEvent event) {

		if (event.getRecipe() instanceof ShapedRecipe
				&& plugin.SOMNIA_KEY.equals(((ShapedRecipe) event.getRecipe()).getKey())) {

			event.getInventory().setResult(new Somnia(event.getViewers().get(0)));

			if (moderators != null && !event.getViewers().get(0).hasPermission("pangaeablocks.somnia_cookie")) {

				final List<Component> modNameList = moderators.isEmpty() ? Collections.emptyList()
						: new ArrayList<>(moderators.size());

				for (final String mod : moderators) {

					final OfflinePlayer modPlayer = Bukkit.getOfflinePlayer(UUID.fromString(mod));

					if (modPlayer.getPlayer() != null) {

						modNameList.add(Utils.getTeamFormattedPlayer(modPlayer.getPlayer())
								.hoverEvent(HoverEvent.showText(Component.empty()
										.append(Component.text("Klicke um höflich bei ")
												.append(Utils.getTeamFormattedPlayer(modPlayer.getPlayer()))
												.append(Component.text(" zu fragen.")))))
								.clickEvent(ClickEvent.suggestCommand("/msg " + modPlayer.getPlayer().getName()
										+ " Hey Du, ich WILL SOFORT wirkende Somnia-Kekse!"))
								.decorations(Set.of(TextDecoration.BOLD, TextDecoration.UNDERLINED), true));

					} else if (modPlayer.getName() != null) {

						modNameList.add(Component.text(modPlayer.getName()).color(NamedTextColor.DARK_GRAY)
								.decoration(TextDecoration.BOLD, true));
					}
				}

				if (!modNameList.isEmpty()) {

					Audience.audience(event.getViewers().get(0)).sendMessage(Component.empty()
							.append(Component.text("Somnia-Kekse").color(TextColor.color(184, 115, 51)))
							.append(Component.text(" sind zur Zeit ")
									.append(Component.text("wirkungslos").decoration(TextDecoration.BOLD, true))
									.append(Component.text(" für Dich.")).color(NamedTextColor.RED)
									.decoration(TextDecoration.ITALIC, true))
							.append(Component.newline())
							.append(Component.text("Wende Dich vertrauensvoll an einen dieser Spieler: ")
									.append(Component.join(JoinConfiguration.separator(Component.text(", ")),
											modNameList))
									.append(Component.newline()
											.append(Component.text(
													" und frage höflich nach, ob dies für Dich geändert werden kann.")))
									.color(NamedTextColor.GRAY)));
				}
			}
		}
	}
}
