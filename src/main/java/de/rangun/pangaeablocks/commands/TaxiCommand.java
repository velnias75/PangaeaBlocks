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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class TaxiCommand extends NonDefaultTabCompleter implements CommandExecutor { // NOPMD by heiko on
																							// 09.11.22, 05:00

	private final static String GIVE_SUBCMD = "give";

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (sender instanceof Player) {

			if (args.length > 0 && GIVE_SUBCMD.equalsIgnoreCase(args[0])) {

				if (sender.hasPermission("pangaeablocks.taxi.give")) {
					if (!Bukkit.dispatchCommand(sender, "give " + sender.getName() + " " // NOPMD by heiko on 09.11.22,
																							// 05:00
							+ Material.STICK.getKey().asString()
							+ "{display:{Name:'{\"text\":\"LauchTaxi C220\",\"color\":\"gold\",\"bold\":true,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false}'},CustomModelData:42} 1")) {
						Audience.audience(sender).sendMessage(Component.text("Taxi konnte nicht vergeben werden.",
								NamedTextColor.RED, TextDecoration.ITALIC));
					}
				}

			} else {

				final Player player = (Player) sender;
				final PlayerInventory inv = player.getInventory();

				// already driving a taxi?
				final ItemStack helmet = inv.getHelmet();

				if (helmet == null || !isTaxi(helmet)) {

					// holding a taxi in hand
					final ItemStack taxi = inv.getItemInMainHand();

					if (isTaxi(taxi)) {

						// clone helmet && taxi
						final ItemStack clonedHelmet = helmet != null ? helmet.clone() : new ItemStack(Material.AIR);
						final ItemStack clonedTaxi = taxi.clone();

						inv.setHelmet(clonedTaxi);
						inv.setItemInMainHand(clonedHelmet);

					} else {
						Audience.audience(sender)
								.sendMessage(Component.text(
										"Auf Pangäa muss man sein Taxi in der Hand halten, um es fahren zu können!",
										NamedTextColor.GOLD));
					}

				} else {
					Audience.audience(sender).sendMessage(Component.text(
							"Selbst ein P-Schein befähigt NICHT 2 Taxen gleichzeitig zu fahren!", NamedTextColor.GOLD));
				}
			}

		} else {
			Bukkit.getLogger().warning("Hast Du einen P-Schein?");
		}

		return true;
	}

	private boolean isTaxi(final ItemStack item) {
		return Material.STICK.equals(item.getType()) && item.getItemMeta().hasCustomModelData()
				&& item.getItemMeta().getCustomModelData() == 42;
	}
}
