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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import de.rangun.pangaeablocks.utils.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class InvseeCommand implements CommandExecutor, InventoryHolder { // NOPMD by heiko on 12.06.22, 09:09

	private final static List<ItemStack> SEPARATOR = Collections.nCopies(9,
			new ItemStack(Material.CYAN_STAINED_GLASS_PANE));

	private Player target;

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (args.length > 0 && sender instanceof Player) {

			Bukkit.getServer().getOnlinePlayers().forEach(new Consumer<Player>() {

				@Override
				public void accept(final Player player) {

					if (args[0].equals(player.getName())) {

						target = Bukkit.getServer().getPlayerExact(args[0]);

						if (target != null) {

							if (!target.hasPermission("pangaeablocks.invsee_blocked") || ((Player) sender).isOp()) {
								((Player) sender).openInventory(getInventory());
							} else {
								Audience.audience(sender)
										.sendMessage(Utils.getTeamFormattedPlayer(target)
												.append(Component.text(" forbids you to see his/her inventory!",
														NamedTextColor.RED, TextDecoration.BOLD)));
							}
						}
					}
				}
			});

			return true; // NOPMD by heiko on 12.06.22, 09:09
		}

		return false;
	}

	@Override
	public Inventory getInventory() {

		final Inventory inv = Bukkit.getServer().createInventory(this, 45,
				Utils.getTeamFormattedPlayer(target).decorate(TextDecoration.BOLD));

		final List<ItemStack> cInv = new ArrayList<>(45);

		cInv.addAll(Arrays.asList(Arrays.copyOfRange(target.getInventory().getContents(), 9, 36)));
		cInv.addAll(SEPARATOR);
		cInv.addAll(Arrays.asList(Arrays.copyOfRange(target.getInventory().getContents(), 0, 9)));

		inv.setContents(cInv.toArray(new ItemStack[0]));

		return inv;
	}
}
