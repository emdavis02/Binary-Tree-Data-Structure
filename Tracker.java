import java.util.*;

public class Tracker {

	public static final long BYTES_FOR_LONG = 8;
	public static final long BYTES_FOR_INT = 4;

	private String className;
	private long size;
	private String caller;
	private static ArrayList<Tracker> memory = new ArrayList<Tracker>();

	public Tracker (String className, long size, String caller) {
		this.className = className;
		this.size = size;
		this.caller = caller;
		trackObject ();
	}

	private void trackObject () {
		memory.add(this);
	}

	private void untrackObject () {
		memory.remove (this);
	}

	public void jettison () {
		untrackObject ();
	}

	public static boolean checkMemoryLeaks () {
		if(memory.size () == 0) {
			System.err.println (
				"\nNo memory leaks!  All memory " 
				+ "has been correctly jettisoned.");
			return true;
		}

		System.err.print (memory.get (0));
		return false; 
	}

	// print out what is currently tracked, the array list, to debug. 
	public String toString () {
		String string = "";
		// TODO: addition of the size, 
		// and print out the total at the end (similar to valgrind)
		// number of allocations, size is, etc. 
		if(memory.size () > 0) {
			string += "\n------------ " 
				+ "TRACKED MEMORY ------------\n";
			for(Tracker tracked : memory) {
				string += tracked.size 
					+ " bytes of heap memory, created in " 
					+ tracked.caller + " for the " 
					+ tracked.className + " object.\n";
			}
		}
		return string;
	}
}
