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

package de.rangun.pangaeablocks.utils;

import org.bukkit.NamespacedKey;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public interface Constants {

	NamespacedKey AFK_KEY = new NamespacedKey("pangaea_blocks", "afk"); // NOPMD by heiko on 31.01.23, 12:02
	Component AFK_TEXT = Component.empty().append( // NOPMD by heiko on 31.01.23, 12:02
			Component.text('A').color(NamedTextColor.RED).append(Component.text("FK").color(NamedTextColor.BLUE)));

	NamespacedKey VOTE_KEY = new NamespacedKey("pangaea_blocks", "voted_display_last"); // NOPMD by heiko on 16.02.23,
																						// 09:22
	Component VOTE_TEXT = Component.newline().append( // NOPMD by heiko on 16.02.23, 09:22
			(Component.text("Heute schon gevoted?").color(NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true)));
}
