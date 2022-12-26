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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ShapedRecipe;

import de.rangun.pangaeablocks.PangaeaBlocksPlugin;
import de.rangun.pangaeablocks.somnia.SomniaRecipe.Somnia;
import de.rangun.pinkbull.IPinkBullPlugin;

/**
 * @author heiko
 *
 */
public final class SomniaListener implements Listener {

	private final PangaeaBlocksPlugin plugin;

	public SomniaListener(final PangaeaBlocksPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPrepareItemCraft(final PrepareItemCraftEvent event) {

		if (event.getRecipe() instanceof ShapedRecipe
				&& plugin.SOMNIA_KEY.equals(((ShapedRecipe) event.getRecipe()).getKey())) {

			final IPinkBullPlugin pbPlugin = (IPinkBullPlugin) Bukkit.getServer().getPluginManager()
					.getPlugin("PinkBull");

			event.getInventory().setResult(new Somnia(event.getViewers().get(0)));
		}
	}
}
