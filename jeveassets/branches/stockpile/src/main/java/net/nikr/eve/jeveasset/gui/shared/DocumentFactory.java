/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.gui.shared;

import java.awt.Toolkit;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


public class DocumentFactory {

	public static IntegerPlainDocument getIntegerPlainDocument(){
		return new IntegerPlainDocument();
	}
	public static WordPlainDocument getWordPlainDocument(){
		return new WordPlainDocument();
	}

	public static DoublePlainDocument getDoublePlainDocument(){
		return new DoublePlainDocument();
	}

	public static class IntegerPlainDocument extends PlainDocument {

		@Override
		public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			try {
				//FIXME Integer might be to small to hold the KeyID?
				Integer.parseInt(newValue);
				super.insertString(offset, string, attributes);
			} catch (NumberFormatException exception) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}


	public static class DoublePlainDocument extends PlainDocument {

		@Override
		public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			try {
				Double.parseDouble(newValue);
				super.insertString(offset, string, attributes);
			} catch (NumberFormatException exception) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	public static class WordPlainDocument extends PlainDocument {

		@Override
		public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
			int length = getLength();
			if (string == null) {
				return;
			}
			String newValue;
			if (length == 0) {
				newValue = string;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, string);
				newValue = currentBuffer.toString();
			}
			boolean b = Pattern.matches("[\\w\\s]*", newValue);
			if (b && !newValue.isEmpty()){
				super.insertString(offset, string, attributes);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

}
