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

package de.rangun.pangaeablocks.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.HumanEntity;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * @author heiko
 *
 */
public final class Utils {

	private Utils() {
	}

	public static class UUIDTagType implements PersistentDataType<byte[], UUID> {

		@Override
		public Class<byte[]> getPrimitiveType() {
			return byte[].class;
		}

		@Override
		public Class<UUID> getComplexType() {
			return UUID.class;
		}

		@Override
		public byte[] toPrimitive(final UUID complex, final PersistentDataAdapterContext context) {

			final ByteBuffer bb = ByteBuffer.wrap(new byte[16]); // NOPMD by heiko on 26.12.22, 01:10

			bb.putLong(complex.getMostSignificantBits());
			bb.putLong(complex.getLeastSignificantBits());

			return bb.array();
		}

		@Override
		public UUID fromPrimitive(final byte[] primitive, final PersistentDataAdapterContext context) {

			final ByteBuffer bb = ByteBuffer.wrap(primitive); // NOPMD by heiko on 26.12.22, 01:10
			final long firstLong = bb.getLong();
			final long secondLong = bb.getLong();

			return new UUID(firstLong, secondLong);
		}
	}

	public static Block doorBottom(final Block block) {

		return block.getBlockData() instanceof Door && Half.TOP.equals(((Door) block.getBlockData()).getHalf())
				? block.getRelative(BlockFace.DOWN)
				: block;
	}

	public static Block doorTop(final Block block) {

		return block.getBlockData() instanceof Door && Half.BOTTOM.equals(((Door) block.getBlockData()).getHalf())
				? block.getRelative(BlockFace.UP)
				: block;
	}

	public static Component getTeamFormattedPlayer(final HumanEntity player) {

		final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		final Team team = scoreboard.getEntityTeam(player);

		final TextColor color = team != null ? team.color() : null;
		final Component prefix = team != null ? team.prefix().color(color) : Component.text("");
		final Component suffix = team != null ? team.suffix().color(color) : Component.text("");

		return team != null
				? Component.empty().append(prefix).append(Component.text(player.getName(), color)).append(suffix)
				: Component.text(player.getName());
	}
}
