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

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import de.rangun.pangaeablocks.PangaeaBlocksPlugin;
import de.rangun.pangaeablocks.utils.Utils;
import de.rangun.pangaeablocks.utils.Utils.UUIDTagType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

/**
 * @author heiko
 *
 */
public final class SomniaRunnable extends BukkitRunnable { // NOPMD by heiko on 28.12.22, 01:38

	private final static TextComponent NIGHTADVENT = Component.text("[Server] ")
			.append(Component.text("Es wird allmählich "))
			.append(Component.text("Nacht").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true))
			.append(Component.text(". Bitte suche das "))
			.append(Component.text("nächste Bett").color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true))
			.append(Component.text(" auf."));

	private final Sound BELL = Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.MASTER, 1.0f, 0.0f);

	private final Title TITLE = Title.title(Component.text("Schlafen!").color(NamedTextColor.DARK_RED),
			Component.text("... sonst kommt der Kick"),
			Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1)));

	private final TextComponent KICK = Component.empty()
			.append(Component.text("STÄHLERNE REGEL: ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD,
					true))
			.append(Component.text("Wer nicht schläft, der fliegt!").color(NamedTextColor.RED)
					.decoration(TextDecoration.ITALIC, true));

	private final TextComponent PUNISH = Component.text(" wurde von den ")
			.append(Component.text("Vogelsberger Lohen").color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD,
					true))
			.append(Component.text(" wegen "))
			.append(Component.text("Nicht-Schlafens").decoration(TextDecoration.ITALIC, true))
			.append(Component.text(" bestraft!"));

	private final TextComponent COOKIE = Component
			.text(" hat sich mit einem ").append(Component.text("Somnia-Keks").color(TextColor.color(184, 115, 51))
					.decoration(TextDecoration.ITALIC, true))
			.append(Component.text(" von der Schlafpflicht frei gekauft.")).color(NamedTextColor.GRAY);

	private final TextComponent BEFORE_DAY = Component.newline()
			.append(Component.text("... während es weiter vor sich hin nachtet.").color(NamedTextColor.GRAY));

	private final TextComponent AFTER_DAY = Component.newline()
			.append(Component.text("... trotz, dass alle anderen Pangäa-Bewohner sich brav schlafen gelegt haben.")
					.color(NamedTextColor.GRAY));

	private boolean doSomniaAdvent = true;
	private boolean doSomniaSoundTitle = true; // NOPMD by heiko on 25.12.22, 03:28
	private boolean doSomniaKick = true;
	private boolean doSomniaCookie = true;
	private boolean unsetSleepIgnore;

	private final boolean requirePermission;

	private final PangaeaBlocksPlugin plugin;

	public final static NamespacedKey SOMNIA_KEY = new NamespacedKey("pangaea_blocks", "somnia");
	private final static UUIDTagType UUID_TYPE = new Utils.UUIDTagType();

	public SomniaRunnable(final PangaeaBlocksPlugin plugin, final boolean requirePermission) {
		super();
		this.plugin = plugin;
		this.requirePermission = requirePermission;
	}

	@Override
	public void run() { // NOPMD by heiko on 28.12.22, 01:37

		final World world = Bukkit.getWorlds().stream().filter(w -> Environment.NORMAL.equals(w.getEnvironment()))
				.findFirst().orElse(null);

		if (world != null) {

			final long dayTime = world.getTime();

			if (dayTime >= 11_615L && dayTime < 12_540L && doSomniaAdvent) {

				Audience.audience(getOverworldPlayers(this.requirePermission)).sendMessage(NIGHTADVENT);
				doSomniaAdvent = false;
				unsetSleepIgnore = true;

			} else if (dayTime >= 12_540L && dayTime < 12_942L && doSomniaSoundTitle) {

				final Audience audience = Audience.audience(getOverworldPlayers(this.requirePermission));

				audience.playSound(BELL);
				audience.showTitle(TITLE);

				Bukkit.getOnlinePlayers().stream()
						.filter(p -> isPlayerHoldingSomniaCookie(p, this.requirePermission)
								&& Environment.NORMAL.equals(p.getWorld().getEnvironment())
								&& !GameMode.SPECTATOR.equals(p.getGameMode()))
						.forEach(p -> {
							p.setSleepingIgnored(true);
						});

				doSomniaSoundTitle = false;
				unsetSleepIgnore = true;

			} else if (dayTime >= 12_942L && doSomniaKick) {

				for (final Player p : getOverworldPlayers(this.requirePermission)) {

					final Component punishText = Component.empty()
							.append(Utils.getTeamFormattedPlayer(p).decoration(TextDecoration.BOLD, true))
							.append(PUNISH);

					p.kick(KICK, Cause.PLUGIN);

					Audience.audience(Bukkit.getOnlinePlayers()).sendMessage(punishText);
					plugin.sendToDiscordSRV(Component.text("... ").append(PUNISH), p);
				}

				doSomniaKick = false;
				unsetSleepIgnore = true;

			} else if (dayTime >= 12_952L && doSomniaCookie
					&& Bukkit.getOnlinePlayers().stream()
							.filter(p -> Environment.NORMAL.equals(p.getWorld().getEnvironment())
									&& !GameMode.SPECTATOR.equals(p.getGameMode()))
							.count() < 2L) {

				Bukkit.getOnlinePlayers().forEach(subtractSomniaCookie(true));

				doSomniaCookie = false;
				unsetSleepIgnore = true;

			} else if (dayTime >= 0L && dayTime < 11_615L) {

				if (unsetSleepIgnore) {

					if (doSomniaCookie) {
						Bukkit.getOnlinePlayers().forEach(subtractSomniaCookie(false));
					}

					Bukkit.getOnlinePlayers().stream()
							.filter(p -> Environment.NORMAL.equals(p.getWorld().getEnvironment())
									&& !GameMode.SPECTATOR.equals(p.getGameMode()))
							.forEach(p -> {
								p.setSleepingIgnored(false);
							});

					unsetSleepIgnore = false;
				}

				doSomniaAdvent = true;
				doSomniaSoundTitle = true;
				doSomniaKick = true;
				doSomniaCookie = true;
			}
		}

	}

	private Consumer<? super Player> subtractSomniaCookie(final boolean beforeDay) {

		return p -> {

			if (isPlayerHoldingSomniaCookie(p, this.requirePermission)
					&& Environment.NORMAL.equals(p.getWorld().getEnvironment())
					&& !GameMode.SPECTATOR.equals(p.getGameMode())) {

				final TextComponent cookieText = Component.empty()
						.append(Utils.getTeamFormattedPlayer(p).decoration(TextDecoration.BOLD, true)).append(COOKIE);

				final TextComponent addendum = beforeDay ? BEFORE_DAY : AFTER_DAY;

				p.getInventory().getItemInOffHand().subtract();

				Audience.audience(Bukkit.getOnlinePlayers()).sendMessage(cookieText.append(addendum));
				plugin.sendToDiscordSRV(Component.text("... ").append(COOKIE.append(addendum)), p);
			}
		};
	}

	private static List<? extends Player> getOverworldPlayers(final boolean requirePermission) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(p -> !isPlayerHoldingSomniaCookie(p, requirePermission)
						&& Environment.NORMAL.equals(p.getWorld().getEnvironment())
						&& !GameMode.SPECTATOR.equals(p.getGameMode()) && (!p.isSleeping() || p.isSleepingIgnored()))
				.collect(Collectors.toList());
	}

	private static boolean isPlayerHoldingSomniaCookie(final Player player, final boolean requirePermission) {

		final ItemStack offHandItem = player.getInventory().getItemInOffHand();
		final PersistentDataContainer container = offHandItem.hasItemMeta()
				? offHandItem.getItemMeta().getPersistentDataContainer()
				: null;

		return Material.COOKIE.equals(offHandItem.getType()) && container != null
				&& container.has(SOMNIA_KEY, UUID_TYPE)
				&& player.getUniqueId().equals(container.get(SOMNIA_KEY, UUID_TYPE))
				&& (requirePermission ? player.hasPermission("pangaeablocks.somnia_cookie") : true); // NOPMD by heiko
																										// on 30.12.22,
																										// 01:29
	}
}
