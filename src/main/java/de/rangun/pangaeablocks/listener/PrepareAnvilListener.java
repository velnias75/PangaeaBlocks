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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jodd.jerry.Jerry;

/**
 * @author heiko
 *
 */
public final class PrepareAnvilListener implements Listener {

	private final NamespacedKey key;
	private final Plugin plugin;

	public PrepareAnvilListener(final Plugin plugin) {
		this.plugin = plugin;
		this.key = new NamespacedKey(this.plugin, "ticket");
	}

	private static class Texture {

		public final UUID uuid;
		public final String tex;

		public Texture(final UUID uuid, final String texture) {
			this.uuid = uuid;
			this.tex = texture;
		}
	}

	@EventHandler
	public void onPrepareAnvilEvent(final PrepareAnvilEvent event) {

		final ItemStack first = event.getInventory().getFirstItem();
		final ItemStack second = event.getInventory().getSecondItem();

		if (first != null && second != null && Material.PAPER.equals(first.getType()) && first.getAmount() == 1
				&& Material.DIAMOND.equals(second.getType()) && event.getInventory().getRenameText() != null) {

			final ItemStack head = new ItemStack(Material.PLAYER_HEAD, second.getAmount());
			final SkullMeta meta = (SkullMeta) head.getItemMeta();

			if (first.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {

				try {

					final Texture tex = getTexture(event.getInventory().getRenameText());

					if (tex != null) {

						final PlayerProfile profile = plugin.getServer().createProfile(tex.uuid,
								event.getInventory().getRenameText());

						profile.setProperty(new ProfileProperty("textures", tex.tex));
						profile.complete(true, !plugin.getServer().getOnlineMode());

						meta.setPlayerProfile(profile);
						head.setItemMeta(meta);

						if (profile.hasTextures()) {
							event.setResult(head);
						}
					}

				} catch (IOException e) {
				}

				plugin.getServer().getScheduler().runTask(plugin,
						() -> event.getInventory().setRepairCost(second.getAmount()));
			}
		}
	}

	private Texture getTexture(final String name) throws IOException {

		try {

			final String val = Jerry
					.of(readStringFromURL("https://minecraft-heads.com/custom-heads/" + Long.parseLong(name)))
					.find("#UUID-Value").first().text();

			if (!val.isEmpty()) {
				return new Texture(UUID.fromString("3f8ae234-25a4-4de6-acd8-e24fa9ca0845"), val); // NOPMD by heiko on
																									// 08.06.22, 17:45
			}

		} catch (NumberFormatException e4) {

			final URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);

			try (InputStreamReader reader_0 = new InputStreamReader(url_0.openStream())) {

				try {

					final UUID uuid = UUID.fromString(
							JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString().replaceFirst(
									"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
									"$1-$2-$3-$4-$5"));

					final URL url_1 = new URL(
							"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

					try (InputStreamReader reader_1 = new InputStreamReader(url_1.openStream())) {

						final JsonObject textureProperty = JsonParser.parseReader(reader_1).getAsJsonObject()
								.get("properties").getAsJsonArray().get(0).getAsJsonObject();

						return new Texture(uuid, textureProperty.get("value").getAsString()); // NOPMD by heiko on
																								// 08.06.22,
																								// 07:15

					} catch (IllegalStateException e2) {
					}

				} catch (IllegalStateException | MalformedURLException e6) {
				}

			} catch (FileNotFoundException e3) {
			}
		}

		return null;
	}

	private String readStringFromURL(final String requestURL) throws IOException {
		try (Scanner scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}
}
