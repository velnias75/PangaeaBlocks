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

	protected final static String table = "blocks";

	private final static String insertBlock = """
			REPLACE INTO "blocks" (x, y, z, world, uuid) VALUES (?, ?, ?, ?, ?);
			""";

	private final static String deleteBlock = """
			DELETE FROM "blocks" WHERE "x" = ? AND "y" = ? AND "z" = ? AND "world" = ?;
			""";

	private final static String getBlock = """
			SELECT "uuid" FROM "blocks" WHERE "x" = ? AND "y" = ? AND "z" = ? AND "world" = ?;
			""";

	protected AbstractDatabase(Plugin instance) {
		this.plugin = instance;
	}

	protected abstract Connection getSQLConnection();

	@Override
	public abstract void load();

	protected final void initialize() {

		connection = getSQLConnection();

		try {

			final PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE id = ?");
			final ResultSet rs = ps.executeQuery();

			close(ps, rs);

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
		}
	}

	@Override
	public UUID getBlock(final Block block) {

		String uuid = null;

		try {

			final PreparedStatement ps = connection.prepareStatement(getBlock);

			ps.setInt(1, block.getX());
			ps.setInt(2, block.getY());
			ps.setInt(3, block.getZ());
			ps.setString(4, block.getWorld().getName());

			final ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				uuid = new String(rs.getString(1));
			}

			close(ps, rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return uuid == null ? null : UUID.fromString(uuid);
	}

	@Override
	public void registerBlock(final Block block, final UUID uuid) {

		try {

			final PreparedStatement ps = connection.prepareStatement(insertBlock);

			ps.setInt(1, block.getX());
			ps.setInt(2, block.getY());
			ps.setInt(3, block.getZ());

			ps.setString(4, block.getWorld().getName());
			ps.setString(5, uuid.toString());

			ps.executeUpdate();

			close(ps, null);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteBlock(Block block) {

		try {

			final PreparedStatement ps = connection.prepareStatement(deleteBlock);

			ps.setInt(1, block.getX());
			ps.setInt(2, block.getY());
			ps.setInt(3, block.getZ());

			ps.setString(4, block.getWorld().getName());

			ps.executeUpdate();

			close(ps, null);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(PreparedStatement ps, ResultSet rs) {

		try {

			if (ps != null)
				ps.close();

			if (rs != null)
				rs.close();

		} catch (SQLException ex) {
			Error.close(plugin, ex);
		}
	}
}
