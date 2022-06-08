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

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableList;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class TicketCommand extends NonDefaultTabCompleter implements CommandExecutor {

	private final NamespacedKey key;
	private final List<Component> lore = ImmutableList.of(Component.text("Benenne dieses Ticket am Amboss"),
			Component.text("zu dem Spielernamen oder der Nummer"),
			Component.text("von ")
					.append(Component.text("https://minecraft-heads.com").decoration(TextDecoration.ITALIC, true))
					.append(Component.text(" um,")),
			Component.text("dessen Kopf Du haben möchtest."), Component.text(""),
			Component.text("Zusammen mit Diamanten"), Component.text("bekommst Du dann die Köpfe."));

	public TicketCommand(final Plugin plugin) {
		super();
		this.key = new NamespacedKey(plugin, "ticket");
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (sender instanceof Player) {

			final ItemStack ticket = new ItemStack(Material.PAPER);
			final ItemMeta meta = ticket.getItemMeta();

			meta.displayName(Component.text("Kopfticket", NamedTextColor.YELLOW));
			meta.lore(lore);

			meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, Byte.valueOf((byte) 0));

			if (ticket.setItemMeta(meta)) {
				((Player) sender).getInventory().addItem(ticket);
			}

		} else {
			Bukkit.getLogger().info("You must be an online player to execute this command.");
		}

		return true;
	}

}
