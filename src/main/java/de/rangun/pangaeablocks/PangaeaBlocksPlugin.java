/*
 * Copyright 2022-2023 by Heiko Schäfer <heiko@rangun.de>
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import de.rangun.pangaeablocks.commands.AFKCommand;
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
import de.rangun.pangaeablocks.utils.Constants;
import de.rangun.pangaeablocks.utils.Utils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.TablistFormatManager;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class PangaeaBlocksPlugin extends JavaPlugin implements Constants { // NOPMD by heiko on 05.06.22, 00:46

	private Database db; // NOPMD by heiko on 05.06.22, 00:50
	private boolean discordSRVavail;

	public final NamespacedKey SOMNIA_KEY = new NamespacedKey(this, "somnia_recipe");

	private final static LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().extractUrls()
			.hexColors().useUnusualXRepeatedCharacterHexFormat().build();

	private final static String AFK_MARKER = SERIALIZER
			.serialize(Component.empty()
					.append(Component.text(" [").color(NamedTextColor.GRAY).append(AFK_TEXT)
							.append(Component.text(']').color(NamedTextColor.GRAY)))
					.decoration(TextDecoration.ITALIC, true));

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
		final AFKCommand afk = new AFKCommand(this);

		discordSRVavail = getServer().getPluginManager().getPlugin("DiscordSRV") != null;

		if (getServer().getPluginManager().getPlugin("TAB") != null) {

			final TabAPI tabAPI = TabAPI.getInstance();
			final TablistFormatManager manager = tabAPI.getTablistFormatManager();

			if (manager != null) {

				tabAPI.getPlaceholderManager().registerPlayerPlaceholder("%pangaea_afk%", 50, player -> {

					final Player serverPlayer = (Player) player.getPlayer();
					final PersistentDataContainer container = serverPlayer.getPersistentDataContainer();
					final boolean isAfk = container.has(AFK_KEY)
							&& container.get(AFK_KEY, PersistentDataType.BYTE) == (byte) 1;

					return !isAfk ? "" // NOPMD by heiko on 31.01.23, 12:03
							: AFK_MARKER;
				});

				tabAPI.getEventBus().register(PlayerLoadEvent.class, event -> {

					final TabPlayer tabPlayer = event.getPlayer();

					final String playerName = SERIALIZER.serialize(
							Utils.getTeamFormattedPlayer((HumanEntity) tabPlayer.getPlayer())) + "%pangaea_afk%";

					manager.setName(tabPlayer, playerName);

				});
			}
		}

		getCommand("lockdoor").setExecutor(lockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("unlockdoor").setExecutor(unlockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("hologram").setExecutor(holo); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("frame").setExecutor(frame); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("ticket").setExecutor(ticket);
		getCommand("pvp").setExecutor(pvp);
		getCommand("invsee").setExecutor(inv);
		getCommand("taxi").setExecutor(taxi);
		getCommand("afk").setExecutor(afk);

		getCommand("lockdoor").setTabCompleter(lockdoor); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("unlockdoor").setTabCompleter(unlockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("hologram").setTabCompleter(holo); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("frame").setTabCompleter(frame); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("ticket").setTabCompleter(ticket);
		getCommand("pvp").setTabCompleter(pvp);
		getCommand("taxi").setTabCompleter(taxi);
		getCommand("afk").setTabCompleter(afk);

		getServer().getPluginManager()
				.registerEvents(new AsyncPlayerPreLoginListener(getConfig().getString("discord-url")), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, db), this);
		getServer().getPluginManager().registerEvents(new BlockBreakListener(this, db), this);
		getServer().getPluginManager().registerEvents(new PrepareAnvilListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(getConfig().getStringList("vote-sites")),
				this);
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

				DiscordSRV.getPlugin().processChatMessage(player, SERIALIZER.serialize(message),
						DiscordSRV.getPlugin().getMainChatChannel(), false, null);

			} else {
				DiscordUtil.queueMessage(DiscordSRV.getPlugin().getOptionalTextChannel("broadcasts"),
						MessageUtil.reserializeToDiscord(
								MessageUtil.toComponent(MessageUtil.translateLegacy(SERIALIZER.serialize(message)))),
						true);
			}
		}
	}
}
