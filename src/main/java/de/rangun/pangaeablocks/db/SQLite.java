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
		this.dbname = plugin.getConfig().getString("SQLite.Filename", "block_registry"); // Set the table name here e.g
																							// player_kills
	}

	private final static String createBlockTable = """
			CREATE TABLE IF NOT EXISTS "blocks" (
				"id"	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,
				"x"	INTEGER NOT NULL,
				"y"	INTEGER NOT NULL,
				"z"	INTEGER NOT NULL,
				"world"	VARCHAR NOT NULL,
				"uuid"	VARCHAR DEFAULT NULL
			);
			""" + """
			CREATE INDEX IF NOT EXISTS "location" ON blocks (
				"x",
				"y",
				"z",
				"world"
			);
			""" + """
			CREATE UNIQUE INDEX IF NOT EXISTS "unique_block" ON "blocks" (
				"x",
				"y",
				"z",
				"world");
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
	public void load() {

		connection = getSQLConnection();

		try {

			final Statement s = connection.createStatement();

			s.executeUpdate(createBlockTable);
			s.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		initialize();
	}

}
