/*
 * Copyright 2009, Niklas Kyster Rasmussen
 *
 * This file is part of NiKR Log.
 *
 * NiKR Log is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * NiKR Log is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NiKR Log; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.log;


public class NikrUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {


	public void uncaughtException(Thread t, Throwable e) {
		Log.error(
			"Uncaught Exception (Thread): " + Log.getsUncaughtErrorMessage()
			, e);
	}
	
	public void handle(Throwable e){
		//Workaround:
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		if (stackTraceElements.length > 0){
			if (stackTraceElements[0].getClassName().equals("sun.font.FontDesignMetrics")
						&& stackTraceElements[0].getLineNumber() == 492
						&& stackTraceElements[0].getMethodName().equals("charsWidth")
						){
				Log.warning("sun.font.FontDesignMetrics bug detected");
				return;
			}
		}
		Log.error("Uncaught Exception (sun.awt.exception.handler):"
				+ Log.getsUncaughtErrorMessage()
				, e);
	}
}
