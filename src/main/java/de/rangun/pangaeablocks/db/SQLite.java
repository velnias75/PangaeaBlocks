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

package de.rangun.pangaeablocks.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

/**
 * @author heiko
 *
 */
public final class SQLite extends AbstractDatabase {

	private final String dbname;

	public SQLite(Plugin instance) {

		super(instance);
		this.dbname = plugin.getConfig().getString("SQLite.Filename", "block_registry");
	}

	private final static String createBlockTable = """
			PRAGMA foreign_keys = ON;
			CREATE TABLE IF NOT EXISTS "blocks" (
				"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
				"x"		INTEGER NOT NULL,
				"y"		INTEGER NOT NULL,
				"z"		INTEGER NOT NULL,
				"world" VARCHAR NOT NULL,
				UNIQUE("x", "y", "z", "world")
			);
			CREATE TABLE IF NOT EXISTS "players" (
				"id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
				"uuid" VARCHAR NOT NULL,
				UNIQUE("uuid")
			);
			CREATE TABLE IF NOT EXISTS "blocks_players" (
				"player_id" INTEGER NOT NULL,
				"block_id" INTEGER NOT NULL,
				UNIQUE("player_id", "block_id"),
				FOREIGN KEY ("block_id") REFERENCES "blocks" ("id") ON DELETE CASCADE
			);
			CREATE INDEX IF NOT EXISTS "bp" ON "blocks_players" (
				"player_id",
				"block_id"
			);
			CREATE INDEX IF NOT EXISTS "location" ON "blocks" (
				"world",
				"x",
				"y",
				"z"
			);
			CREATE INDEX IF NOT EXISTS "uuid" ON "players" (
				"uuid"
			);
			""";

	private final static String cleanupPlayers = """
			DELETE FROM players AS d WHERE EXISTS (SELECT * FROM players AS S LEFT JOIN blocks_players ON blocks_players.player_id = S.id WHERE player_id IS NULL AND D.id = S.id);
			""";

	@Override
	protected Connection getSQLConnection() {

		final File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");

		if (!dataFolder.exists()) {

			try {

				dataFolder.getParentFile().mkdirs();
				dataFolder.createNewFile();

			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
			}
		}

		try {

			if (connection != null && !connection.isClosed()) {
				return connection;
			}

			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);

			return connection;

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
		} catch (ClassNotFoundException ex) {
			plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
		}

		return null;
	}

	@Override
	public void open() {

		connection = getSQLConnection();

		try {

			final Statement s = connection.createStatement();

			s.executeUpdate(createBlockTable);
			s.close();

		} catch (SQLException e) {
			Error.LogError(e);
		}

		initialize();
	}

	@Override
	public void close() {

		try {
			connection.close();
		} catch (SQLException e) {
			Error.LogError(e);
		}
	}

	@Override
	public void vacuum() {

		try {

			final Statement cleanUp = connection.createStatement();

			cleanUp.executeUpdate(cleanupPlayers);
			cleanUp.close();

			final Statement vacuum = connection.createStatement();

			vacuum.executeUpdate("VACUUM;");
			vacuum.close();

		} catch (SQLException e) {
			Error.LogError(e);
		}
	}
}
