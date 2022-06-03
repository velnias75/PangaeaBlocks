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

package de.rangun.pangaeablocks.listener;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

import de.rangun.pangaeablocks.db.DatabaseClient;
import de.rangun.pangaeablocks.utils.Utils;

/**
 * @author heiko
 *
 */
public final class PlayerInteractListener extends AbstractListener {

	public PlayerInteractListener(final Plugin plugin, final DatabaseClient db) {
		super(plugin, db);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void onPlayerInteractEvent(final PlayerInteractEvent event) {

		if (!event.hasBlock())
			return;

		final Block block = event.getClickedBlock();
		final Action action = event.getAction();
		final Player player = event.getPlayer();

		if (block.getBlockData() instanceof Openable && action.isRightClick()) {

			final Set<UUID> uuids = db.getBlockOwners(Utils.doorBottom(block));

			if (!uuids.isEmpty() && !uuids.contains(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}

		} else if (action.isRightClick() && !player.isSneaking() && isValidForChair(block)) {

			final Location l = player.getLocation();

			l.setYaw(getChairYaw(block));
			player.teleport(l);

			block.getWorld().spawn(block.getLocation().add(0.5d, -0.5d, 0.5d), Pig.class, new Consumer<Pig>() {

				@Override
				public void accept(Pig p) {

					p.setInvisible(true);
					p.setSilent(true);
					p.setInvulnerable(true);
					p.setGravity(false);
					p.addPassenger(player);
					p.setAware(false);
					p.setAI(false);
					p.setRotation(getChairYaw(block), 0.0f);
					p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.0000000001d);
					p.getPersistentDataContainer().set(pig, PersistentDataType.BYTE, (byte) 1);
				}
			});

			event.setCancelled(true);
		}
	}

	private float getChairYaw(final Block block) {
		switch (((Stairs) block.getBlockData()).getFacing()) {
		case SOUTH:
			return 180.0f;
		case NORTH:
			return 0.0f;
		case WEST:
			return -90.0f;
		default:
			return 90.0f;
		}
	}
}
