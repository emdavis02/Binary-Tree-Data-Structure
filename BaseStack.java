public class BaseStack extends List {

	public BaseStack (String caller) {
		super (null, caller + " calling BaseStack Ctor");
	}

	public Base pop () {
		return remove (END);
	}

	public boolean push (Base element) {
		return insert (element, END);
	}

	public Base top () {
		return view (END);
	}
}
