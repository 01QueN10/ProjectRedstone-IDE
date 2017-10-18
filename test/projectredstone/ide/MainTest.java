package projectredstone.ide;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MainTest {
	@Test
	public void loadScriptTest() throws Exception {
		ScriptSerializer script = Main.loadScript(new File("Sources\\TestScript.prs"));
	}

}