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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

/**
 * @author heiko
 *
 */
public final class FrameCommand extends AbstractRaytraceCommand {

	private final List<String> cmd_args = ImmutableList.of("toggle_visibility", "toggle_fixation");

	@Override
	protected boolean processRayTraceResult(RayTraceResult result, String[] args) {

		if (result != null && result.getHitEntity() instanceof ItemFrame) {

			final ItemFrame ifr = (ItemFrame) result.getHitEntity();

			if (args.length > 0) {

				if (cmd_args.get(0).equals(args[0])) {
					ifr.setVisible(!ifr.isVisible());
				}

				if (cmd_args.get(1).equals(args[0])) {
					ifr.setFixed(!ifr.isFixed());
				}

				return true;
			}

			return false;
		}

		return true;
	}

	@Override
	public final List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (args.length > 0 && args.length < 2 && args[0].length() > 0) {

			List<String> sugg = new ArrayList<>(cmd_args.size());

			for (String string : cmd_args) {
				if (StringUtil.startsWithIgnoreCase(string, args[0])) {
					sugg.add(string);
				}
			}

			return ImmutableList.copyOf(sugg);
		}

		return args.length >= 2 ? super.onTabComplete(sender, command, label, args) : cmd_args;
	}
}
