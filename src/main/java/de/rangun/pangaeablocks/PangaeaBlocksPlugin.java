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

package de.rangun.pangaeablocks;

import org.bukkit.plugin.java.JavaPlugin;

import de.rangun.pangaeablocks.commands.FrameCommand;
import de.rangun.pangaeablocks.commands.HologramCommand;
import de.rangun.pangaeablocks.commands.InvseeCommand;
import de.rangun.pangaeablocks.commands.LockDoorCommand;
import de.rangun.pangaeablocks.commands.PvPCommand;
import de.rangun.pangaeablocks.commands.TicketCommand;
import de.rangun.pangaeablocks.commands.UnlockDoorCommand;
import de.rangun.pangaeablocks.db.Database;
import de.rangun.pangaeablocks.db.SQLite;
import de.rangun.pangaeablocks.listener.BlockBreakListener;
import de.rangun.pangaeablocks.listener.InventoryClickListener;
import de.rangun.pangaeablocks.listener.PlayerInteractListener;
import de.rangun.pangaeablocks.listener.PrepareAnvilListener;

public final class PangaeaBlocksPlugin extends JavaPlugin { // NOPMD by heiko on 05.06.22, 00:46

	private Database db; // NOPMD by heiko on 05.06.22, 00:50

	@Override
	public void onEnable() {

		this.db = new SQLite(this);
		this.db.open();

		final LockDoorCommand lockdoor = new LockDoorCommand(db);
		final UnlockDoorCommand unlockdoor = new UnlockDoorCommand(db);
		final HologramCommand holo = new HologramCommand();
		final FrameCommand frame = new FrameCommand();
		final TicketCommand ticket = new TicketCommand(this);
		final PvPCommand pvp = new PvPCommand(this);
		final InvseeCommand inv = new InvseeCommand();

		getCommand("lockdoor").setExecutor(lockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("unlockdoor").setExecutor(unlockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("hologram").setExecutor(holo); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("frame").setExecutor(frame); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("ticket").setExecutor(ticket);
		getCommand("pvp").setExecutor(pvp);
		getCommand("invsee").setExecutor(inv);

		getCommand("lockdoor").setTabCompleter(lockdoor); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("unlockdoor").setTabCompleter(unlockdoor); // NOPMD by heiko on 05.06.22, 00:52
		getCommand("hologram").setTabCompleter(holo); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("frame").setTabCompleter(frame); // NOPMD by heiko on 05.06.22, 00:51
		getCommand("ticket").setTabCompleter(ticket);
		getCommand("pvp").setTabCompleter(pvp);

		getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, db), this);
		getServer().getPluginManager().registerEvents(new BlockBreakListener(this, db), this);
		getServer().getPluginManager().registerEvents(new PrepareAnvilListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
	}

	@Override
	public void onDisable() {
		this.db.vacuum();
	}
}
