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

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;

/**
 * @author heiko
 *
 */
public final class VehicleExitListener implements Listener {

	private final NamespacedKey pig;

	public VehicleExitListener(final Plugin plugin) {
		this.pig = new NamespacedKey(plugin, "zordans_pig");
	}

	@EventHandler
	void onVehicleExit(final VehicleExitEvent event) {

		if (event.getVehicle().getPersistentDataContainer().has(pig)) {
			event.getVehicle().remove();
			event.setCancelled(true);
		}
	}
}
