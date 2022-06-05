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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

/**
 * @author heiko
 *
 */
abstract class AbstractDatabase implements Database {

	protected final Plugin plugin;
	protected Connection connection;

	protected final static String TABLE = "blocks";

	private final static String SQL_INSERTBLOCK = """
			INSERT OR IGNORE INTO "blocks" (x, y, z, world) VALUES (?, ?, ?, ?);
			""";

	private final static String SQL_INSERTPLAYER = """
			INSERT OR IGNORE INTO "players" (uuid) VALUES (?);
			""";

	private final static String SQL_BLOCKPLAYER = """
			INSERT OR IGNORE INTO blocks_players (block_id, player_id) SELECT blocks.id, players.id FROM blocks, players WHERE world = ? AND x = ? AND y = ? AND z = ? AND uuid = ?;
			""";

	private final static String SQL_DELETEBLOCK = """
			DELETE FROM "blocks" WHERE "x" = ? AND "y" = ? AND "z" = ? AND "world" = ?;
			""";

	private final static String SQL_GETBLOCK = """
			SELECT players.uuid, blocks.id FROM blocks LEFT JOIN blocks_players ON blocks_players.block_id = blocks.id LEFT JOIN players ON blocks_players.player_id = players.id WHERE world = ? AND x = ? AND y = ? AND z = ?;
			""";

	protected AbstractDatabase(final Plugin instance) {
		this.plugin = instance;
	}

	protected abstract Connection getSQLConnection();

	@Override
	public abstract void open();

	protected final void initialize() {

		connection = getSQLConnection();

		try {

			final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE id = ?"); // NOPMD
																													// by
																													// heiko
																													// on
																													// 05.06.22,
																													// 01:19
			final ResultSet rs = ps.executeQuery(); // NOPMD by heiko on 05.06.22, 01:19

			close(ps, rs);

		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", e);
		}
	}

	@Override
	public Set<UUID> getBlockOwners(final Block block) {

		final Set<UUID> uuids = new HashSet<>();

		try {

			final PreparedStatement ps = connection.prepareStatement(SQL_GETBLOCK); // NOPMD by heiko on 05.06.22, 01:19

			ps.setString(1, block.getWorld().getName());
			ps.setInt(2, block.getX());
			ps.setInt(3, block.getY());
			ps.setInt(4, block.getZ());

			final ResultSet rs = ps.executeQuery(); // NOPMD by heiko on 05.06.22, 01:19

			while (rs.next()) {
				uuids.add(UUID.fromString(rs.getString(1)));
			}

			close(ps, rs);

		} catch (SQLException e) {
			Error.logError(e);
		}

		return Collections.unmodifiableSet(uuids);
	}

	@Override
	public void registerBlock(final Block block, final UUID uuid) {

		try (PreparedStatement psBlock = connection.prepareStatement(SQL_INSERTBLOCK);
				PreparedStatement psPlayer = connection.prepareStatement(SQL_INSERTPLAYER);
				PreparedStatement psConnect = connection.prepareStatement(SQL_BLOCKPLAYER);) {

			connection.setAutoCommit(false);

			psBlock.setInt(1, block.getX());
			psBlock.setInt(2, block.getY());
			psBlock.setInt(3, block.getZ());
			psBlock.setString(4, block.getWorld().getName());
			psBlock.executeUpdate();

			psPlayer.setString(1, uuid.toString());
			psPlayer.executeUpdate();

			psConnect.setString(1, block.getWorld().getName());
			psConnect.setInt(2, block.getX());
			psConnect.setInt(3, block.getY());
			psConnect.setInt(4, block.getZ());
			psConnect.setString(5, uuid.toString());
			psConnect.executeUpdate();

			connection.commit();

			close(psBlock, null);
			close(psPlayer, null);
			close(psConnect, null);

		} catch (SQLException e) {

			Error.logError(e);

			if (connection != null) {

				try {
					connection.rollback();
				} catch (SQLException e2) {
					Error.logError(e2);
				}
			}

		} finally {

			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				Error.logError(e);
			}
		}
	}

	@Override
	public void deleteBlock(final Block block) {

		try {

			final PreparedStatement ps = connection.prepareStatement(SQL_DELETEBLOCK); // NOPMD by heiko on 05.06.22,
																						// 01:19

			ps.setInt(1, block.getX());
			ps.setInt(2, block.getY());
			ps.setInt(3, block.getZ());

			ps.setString(4, block.getWorld().getName());

			ps.executeUpdate();

			close(ps, null);

		} catch (SQLException e) {
			Error.logError(e);
		}
	}

	private void close(final PreparedStatement ps, final ResultSet rs) { // NOPMD by heiko on 05.06.22, 01:22

		try {

			if (ps != null) {
				ps.close();
			}

			if (rs != null) {
				rs.close();
			}

		} catch (SQLException e) {
			Error.logError(e);
		}
	}
}
