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

/**
 * @author heiko
 *
 */
final class Errors {

	public static String sqlConnectionExecute() {
		return "Couldn't execute MySQL statement: ";
	}

	public static String sqlConnectionClose() {
		return "Failed to close MySQL connection: ";
	}

	public static String noSQLConnection() {
		return "Unable to retreive MYSQL connection: ";
	}

	public static String noTableFound() {
		return "Database Error: No Table Found";
	}
}
