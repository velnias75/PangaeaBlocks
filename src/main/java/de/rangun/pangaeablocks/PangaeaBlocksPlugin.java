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

package de.rangun.pangaeablocks; // NOPMD by heiko on 29.12.22, 06:40

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.rangun.pangaeablocks.commands.FrameCommand;
import de.rangun.pangaeablocks.commands.HologramCommand;
import de.rangun.pangaeablocks.commands.InvseeCommand;
import de.rangun.pangaeablocks.commands.LockDoorCommand;
import de.rangun.pangaeablocks.commands.PvPCommand;
import de.rangun.pangaeablocks.commands.TaxiCommand;
import de.rangun.pangaeablocks.commands.TicketCommand;
import de.rangun.pangaeablocks.commands.UnlockDoorCommand;
import de.rangun.pangaeablocks.db.Database;
import de.rangun.pangaeablocks.db.SQLite;
import de.rangun.pangaeablocks.listener.AsyncPlayerPreLoginListener;
import de.rangun.pangaeablocks.listener.BlockBreakListener;
import de.rangun.pangaeablocks.listener.EntityDeathListener;
import de.rangun.pangaeablocks.listener.InventoryClickListener;
import de.rangun.pangaeablocks.listener.PlayerInteractListener;
import de.rangun.pangaeablocks.listener.PlayerJoinListener;
import de.rangun.pangaeablocks.listener.PrepareAnvilListener;
import de.rangun.pangaeablocks.somnia.SomniaListener;
import de.rangun.pangaeablocks.somnia.SomniaRecipe;
import de.rangun.pangaeablocks.somnia.SomniaRunnable;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class PangaeaBlocksPlugin extends JavaPlugin { // NOPMD by heiko on 05.06.22, 00:46

	private Database db; // NOPMD by heiko on 05.06.22, 00:50
	private boolean discordSRVavail;

	public final NamespacedKey SOMNIA_KEY = new NamespacedKey(this, "somnia_recipe");

	private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().extractUrls().hexColors()
			.useUnusualXRepeatedCharacterHexFormat().build();

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {

		saveDefaultConfig();

		// final FileConfiguration config = getConfig();

		this.db = new SQLite(this);
		this.db.open();

		final LockDoorCommand lockdoor = new LockDoorCommand(db);
		final UnlockDoorCommand unlockdoor = new UnlockDoorCommand(db);
		final HologramCommand holo = new HologramCommand();
		final FrameCommand frame = new FrameCommand();
		final TicketCommand ticket = new TicketCommand(this);
		final PvPCommand pvp = new PvPCommand(this);
		final InvseeCommand inv = new InvseeCommand();
		final TaxiCommand taxi = new TaxiCommand();

		discordSRVavail = getServer().getPluginManager().getPlugin("DiscordSRV") != null;

		getCommand("lockdoor").setExecutor(lockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("unlockdoor").setExecutor(unlockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("hologram").setExecutor(holo); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("frame").setExecutor(frame); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("ticket").setExecutor(ticket);
		getCommand("pvp").setExecutor(pvp);
		getCommand("invsee").setExecutor(inv);
		getCommand("taxi").setExecutor(taxi);

		getCommand("lockdoor").setTabCompleter(lockdoor); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("unlockdoor").setTabCompleter(unlockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("hologram").setTabCompleter(holo); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("frame").setTabCompleter(frame); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("ticket").setTabCompleter(ticket);
		getCommand("pvp").setTabCompleter(pvp);
		getCommand("taxi").setTabCompleter(taxi);

		getServer().getPluginManager()
				.registerEvents(new AsyncPlayerPreLoginListener(getConfig().getString("discord-url")), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, db), this);
		getServer().getPluginManager().registerEvents(new BlockBreakListener(this, db), this);
		getServer().getPluginManager().registerEvents(new PrepareAnvilListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);

		(new SomniaRunnable(this, getConfig().getBoolean("somnia_require_permission", true))).runTaskTimer(this, 0L,
				10L);
		Bukkit.addRecipe(new SomniaRecipe(this));

		getServer().getPluginManager().registerEvents(
				new SomniaListener(this,
						getConfig().getBoolean("somnia_require_permission", true)
								? (List<String>) getConfig().getList("moderators", Collections.EMPTY_LIST)
								: null), // NOPMD by heiko on 29.12.22, 06:40
				this);
	}

	@Override
	public void onDisable() {
		this.db.vacuum();
	}

	public void sendToDiscordSRV(final Component message, final Player player) {

		if (discordSRVavail) {

			if (player != null) {

				DiscordSRV.getPlugin().processChatMessage(player, serializer.serialize(message),
						DiscordSRV.getPlugin().getMainChatChannel(), false, null);

			} else {
				DiscordUtil.queueMessage(DiscordSRV.getPlugin().getOptionalTextChannel("broadcasts"),
						MessageUtil.reserializeToDiscord(
								MessageUtil.toComponent(MessageUtil.translateLegacy(serializer.serialize(message)))),
						true);
			}
		}
	}
}
