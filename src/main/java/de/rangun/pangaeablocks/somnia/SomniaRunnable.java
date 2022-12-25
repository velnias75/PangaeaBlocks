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
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.scheduler.BukkitRunnable;

import de.rangun.pangaeablocks.PangaeaBlocksPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

/**
 * @author heiko
 *
 */
public final class SomniaRunnable extends BukkitRunnable {

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
			.append(Component.text("Vogelsberger Lohen").color(NamedTextColor.GOLD)).append(Component.text(" wegen "))
			.append(Component.text("Nicht-Schlafens").decoration(TextDecoration.ITALIC, true))
			.append(Component.text(" bestraft!"));

	private boolean doSomniaAdvent = true;
	private boolean doSomniaSoundTitle = true; // NOPMD by heiko on 25.12.22, 03:28
	private boolean doSomniaKick = true;

	private final PangaeaBlocksPlugin plugin;

	public SomniaRunnable(final PangaeaBlocksPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public void run() {

		final World world = Bukkit.getWorlds().stream().filter(w -> Environment.NORMAL.equals(w.getEnvironment()))
				.findFirst().orElse(null);

		if (world != null) {

			final long dayTime = world.getTime();

			if (dayTime >= 11_615L && dayTime < 12_540L && doSomniaAdvent) {

				Audience.audience(getOverworldPlayers()).sendMessage(NIGHTADVENT);
				doSomniaAdvent = false;

			} else if (dayTime >= 12_540L && dayTime < 12_942L && doSomniaSoundTitle) {

				final Audience audience = Audience.audience(getOverworldPlayers());

				audience.playSound(BELL);
				audience.showTitle(TITLE);

				doSomniaSoundTitle = false;

			} else if (dayTime >= 12_942L && doSomniaKick) {

				for (final Player p : getOverworldPlayers()) {

					final Component punishText = Component.empty()
							.append(p.displayName().color(NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true))
							.append(PUNISH);

					p.kick(KICK, Cause.PLUGIN);

					Audience.audience(Bukkit.getOnlinePlayers()).sendMessage(punishText);
					plugin.sendToDiscordSRV(Component.text("... ").append(PUNISH), p);
				}

				doSomniaKick = false;

			} else if (dayTime >= 0L && dayTime < 11_615L) {

				doSomniaAdvent = true;
				doSomniaSoundTitle = true;
				doSomniaKick = true;
			}
		}
	}

	private static List<? extends Player> getOverworldPlayers() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(p -> Environment.NORMAL.equals(p.getWorld().getEnvironment())
						&& !GameMode.SPECTATOR.equals(p.getGameMode()) && (!p.isSleeping() || p.isSleepingIgnored()))
				.collect(Collectors.toList());
	}
}
