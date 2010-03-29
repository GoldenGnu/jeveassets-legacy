/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import junit.framework.TestCase;

/**
 *
 * @author Niklas
 */
public class DualPrintStreamTest extends TestCase {
    
    public DualPrintStreamTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
		Log.init(this.getClass(), "hmm");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

	public void testCheckForMaxLines(){
		for (int a = 1; a <= DualPrintStream.MAX_LINES + 50; a++) {
			Log.info("Line: " + a);
		}
		try {
			BufferedReader in = new BufferedReader(new FileReader(getLogFilename(this.getClass(), "log.txt")));
			int count = 0;
			while (in.readLine() != null) {
				count++;
			}
			if (count != DualPrintStream.MAX_LINES) {
				fail("over max lines");
			}
			in.close();
		} catch (FileNotFoundException ex) {
			fail("FileNotFoundException");
		}  catch (IOException ex) {
			fail("IOException");
		}  catch (URISyntaxException ex) {
			fail("URISyntaxException");
		}
	}

	private static String getLogFilename(Class inputClazz, String filename) throws URISyntaxException{
		File file = new File(inputClazz.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
		return file.getAbsolutePath()+File.separator+filename;
	}

}
