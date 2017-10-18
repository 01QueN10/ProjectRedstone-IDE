package projectredstone.ide;

import java.util.ArrayList;
import java.util.Random;

public abstract class Function {

	public Class[] argument;
	public ArrayList<Function> codes = new ArrayList<>();
	public String name;
	public boolean isPrivate = false;

}
