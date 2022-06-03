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

package de.rangun.pangaeablocks.commands;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * @author heiko
 *
 */
public final class HologramCommand extends AbstractRaytraceCommand {

	@Override
	protected boolean processRayTraceResult(final RayTraceResult result, final String[] args) {

		if (result != null && EntityType.ARMOR_STAND.equals(result.getHitEntity().getType())) {

			final ArmorStand as = (ArmorStand) result.getHitEntity();

			if (!(args.length == 0 && as.isInvisible())) {

				as.customName(LegacyComponentSerializer.legacyAmpersand().deserialize(String.join(" ", args)));
				as.setCustomNameVisible(true);
				as.setInvulnerable(true);
				as.setInvisible(true);
				as.setGravity(false);

				for (EquipmentSlot es : EquipmentSlot.values()) {
					as.setDisabledSlots(es);
				}

			} else {
				as.setInvisible(false);
				as.setInvulnerable(false);
			}
		}

		return true;

	}
}
