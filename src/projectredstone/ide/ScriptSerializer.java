package projectredstone.ide;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ScriptSerializer {

	public ArrayList<Integer> api_version;
	public int id;
	public String name;
	public String display_name;
	public int version;
	public String display_version;
	public ArrayList<FunctionSerializer> functions;

}

class FunctionSerializer {

	public String name;
	public int id;
	@SerializedName("private")
	public boolean isPrivate;
	public ArrayList<TypeSerializer> arguments;
	public ArrayList<FunctionReferenceSerializer> codes;

}

class TypeSerializer {

	public String name;
	public int id;

}

class FunctionReferenceSerializer {

	public String name;
	public int id;
	public ArrayList<FunctionReferenceSerializer> arguments;

}