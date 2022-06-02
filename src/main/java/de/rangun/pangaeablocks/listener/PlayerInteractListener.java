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

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
public final class PlayerInteractListener implements Listener {

	private final NamespacedKey pig;
	private final DatabaseClient db;

	public PlayerInteractListener(final Plugin plugin, final DatabaseClient db) {
		this.db = db;
		this.pig = new NamespacedKey(plugin, "zordans_pig");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void onPlayerInteractEvent(final PlayerInteractEvent event) {

		if (!event.hasBlock())
			return;

		final Block block = event.getClickedBlock();
		final Action action = event.getAction();

		if (block.getBlockData() instanceof Openable && action.isRightClick()) {

			final Set<UUID> uuids = db.getBlockOwners(Utils.doorBottom(block));

			if (!uuids.isEmpty() && !uuids.contains(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}

		} else if (block.getBlockData() instanceof Stairs && action.isRightClick()
				&& (isValidForChair((Stairs) block.getBlockData()) && (Material.REDSTONE_BLOCK.equals(
						block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType())))) {

			block.getWorld().spawn(block.getLocation().add(0.5d, -0.5d, 0.5d), Pig.class, new Consumer<Pig>() {

				@Override
				public void accept(Pig d) {

					d.setInvisible(true);
					d.setSilent(true);
					d.setInvulnerable(true);
					d.setGravity(false);
					d.addPassenger(event.getPlayer());
					d.setAware(false);
					d.setAI(false);
					// d.setMaxHealth(0.0000000001d);
					d.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.0000000001d);
					d.getPersistentDataContainer().set(pig, PersistentDataType.BYTE, (byte) 1);

					switch (((Stairs) block.getBlockData()).getFacing()) {
					case SOUTH:
						d.setRotation(180.0f, 0.0f);
						break;
					case NORTH:
						d.setRotation(0.0f, 0.0f);
						break;
					case WEST:
						d.setRotation(-90.0f, 0.0f);
						break;
					default:
						d.setRotation(90.0f, 0.0f);
					}
				}
			});

			event.setCancelled(true);
		}
	}

	private boolean isValidForChair(Stairs block) {
		return Shape.STRAIGHT.equals(block.getShape()) && Half.BOTTOM.equals(block.getHalf());
	}
}
