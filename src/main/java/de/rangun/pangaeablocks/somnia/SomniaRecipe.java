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

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import de.rangun.pangaeablocks.PangaeaBlocksPlugin;
import de.rangun.pangaeablocks.utils.Utils;
import de.rangun.pinkbull.IPinkBullPlugin;
import io.papermc.paper.inventory.ItemRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * @author heiko
 *
 */
public final class SomniaRecipe extends ShapedRecipe {

	final static class Somnia extends ItemStack { // NOPMD by heiko on 26.12.22, 01:11

		Somnia() { // NOPMD by heiko on 26.12.22, 01:11
			this(null);
		}

		Somnia(final HumanEntity player) { // NOPMD by heiko on 26.12.22, 01:11

			super(Material.COOKIE);

			final ItemMeta meta = getItemMeta();

			meta.displayName(Component.text("Somnia"));
			meta.addEnchant(new EnchantmentWrapper("somnia"), 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

			if (player != null) {
				final PersistentDataContainer container = meta.getPersistentDataContainer();
				container.set(SomniaRunnable.SOMNIA_KEY, new Utils.UUIDTagType(), player.getUniqueId());
				meta.lore(List.of(Component.text("Persönlicher ")
						.append(Component.text("Somnia-Keks").decoration(TextDecoration.BOLD, true))
						.append(Component.text(" von ")).append(Utils.getTeamFormattedPlayer(player))));
			}

			setItemMeta(meta);
		}

		@Override
		public Somnia clone() {
			return new Somnia();
		}

		@Override
		public ItemRarity getRarity() {
			return ItemRarity.UNCOMMON;
		}
	}

	final static Somnia SOMNIA = new Somnia(); // NOPMD by heiko on 26.12.22, 01:11

	public SomniaRecipe(final PangaeaBlocksPlugin plugin) {
		super(plugin.SOMNIA_KEY, SOMNIA);

		shape("CPG");
		setIngredient('C', new ItemStack(Material.COOKIE));
		setIngredient('P',
				new ExactChoice(((IPinkBullPlugin) Bukkit.getServer().getPluginManager().getPlugin("PinkBull"))
						.createPinkBullPotion()));
		setIngredient('G', new ItemStack(Material.GLOWSTONE_DUST, 2));
	}
}
