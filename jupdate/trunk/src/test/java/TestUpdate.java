
import net.nikr.update.Program;
import org.junit.Ignore;
import org.junit.Test;

public class TestUpdate {
	
	@Test @Ignore
	public void EveAssetsProgram() {
		run("http://eve.nikr.net/jeveassets/update/program/");
	}
	@Test @Ignore
	public void EveAssetsData() {
		run("http://eve.nikr.net/jeveassets/update/data/");
	}
	@Test @Ignore
	public void jWarframe() {
		run("http://warframe.nikr.net/jwarframe/download/update/");
	}

	private void run(String link) {
		Program program = new Program(link, "", true);
	}
}
