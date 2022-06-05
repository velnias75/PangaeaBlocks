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
public final class HologramCommand extends AbstractRaytraceCommand { // NOPMD by heiko on 05.06.22, 01:32

	@Override
	protected boolean processRayTraceResult(final RayTraceResult result, final String[] args) {

		if (result != null && EntityType.ARMOR_STAND.equals(result.getHitEntity().getType())) {

			final ArmorStand armorStand = (ArmorStand) result.getHitEntity();

			if (!(args.length == 0 && armorStand.isInvisible())) { // NOPMD by heiko on 05.06.22, 01:32

				armorStand.customName(LegacyComponentSerializer.legacyAmpersand().deserialize(String.join(" ", args)));
				armorStand.setCustomNameVisible(true);
				armorStand.setInvulnerable(true);
				armorStand.setInvisible(true);
				armorStand.setGravity(false);

				for (final EquipmentSlot es : EquipmentSlot.values()) {
					armorStand.setDisabledSlots(es);
				}

			} else {
				armorStand.setInvisible(false);
				armorStand.setInvulnerable(false);
			}
		}

		return true;

	}
}
