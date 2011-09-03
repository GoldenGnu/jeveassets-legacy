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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.i18n.GuiShared;


public class JCustomFileChooser extends JFileChooser {
	
	private static final long serialVersionUID = 1L;

	private String customExtension;
	private String customDescription;

	private Program program;

	public JCustomFileChooser(Program program, String customExtension) {
		this(program, customExtension, GuiShared.get().files(customExtension.toUpperCase()));
	}

	public JCustomFileChooser(Program program, String customExtension, String customDescription) {
		this.program = program;
		this.customExtension = customExtension;
		this.customDescription = customDescription;
		this.addChoosableFileFilter(new CustomFileFilter());
		this.setAcceptAllFileFilterUsed(false);
	}

	@Override
	public void approveSelection(){
		File selectedFile = this.getSelectedFile();
		if (Utils.getExtension(selectedFile) == null){
			selectedFile = new File(selectedFile.getAbsolutePath()+"."+customExtension);
			this.setSelectedFile(selectedFile);
		}
		if (selectedFile != null && selectedFile.exists()){
			int nReturn = JOptionPane.showConfirmDialog(
					program.getMainWindow().getFrame(),
					GuiShared.get().overwrite(),
					GuiShared.get().overwriteFile(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE
					);
			if (nReturn == JOptionPane.NO_OPTION){
				return;
			}
		}
		super.approveSelection();
	}

	public class CustomFileFilter extends FileFilter {


		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = Utils.getExtension(f);
			if (extension != null) {
				if (extension.equals(customExtension)) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		//The description of this filter
		@Override
		public String getDescription() {
			return customDescription;
		}
	}

	static public class Utils {
		//public final static String XML = "xml";
		public static String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

	}
}
