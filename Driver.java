/**
 * Name:	Elise Davis
 * 		Varsha Sampath
 * PID:		A16275858
 * 		A16294875
 * USER:	cs12fa21gj@ieng6.ucsd.edu
 * 		cs12fa21du@ieng6.ucsd.edu
 * File name:	Driver.java
 * Description:	Tester file for Tree.java, including tester class
 * 		UCSDStudent 
 */

import java.io.*;

/**
 * Class:		UCSDStudent
 * Description:		Tester class for Tree.java representing a student
 *
 * Fields:		name 	   - name of student
 * 			studentNum - idenitification number
 * 			tracker    - tracks memory
 * 			counter    - counts how many students
 * 			count	   - this's reference number
 *
 * Public Functions	jettison   - jettisons memory of student
 * 			copy       - returns a copy of current student
 * 			getName    - returns name of student
 * 			getTrimName- returns trimmed name of student
 * 			equals 	   - compares student to another to test equality
 * 			isLessThan - compares student to another
 * 			toString   - formats object to print
 * 			write      - writes current student to disk
 */
class UCSDStudent extends Base {

	private String name; //name of student
	private long studentNum; //identification number
	private Tracker tracker; //tracks memory
	private static long counter = 0; //counts how many students
	private long count; //this's reference number

	/*
	 * Default constructor for the UCSDStudent object.
	 * Tracks the memory associated with the UCSDStudent object.
	 */
	public UCSDStudent () {
		tracker = new Tracker ("UCSDStudent " + count + " " + name,
		Size.of (name) 
		+ Size.of (studentNum)
		+ Size.of (count)
		+ Size.of (tracker),
		"UCSDStudent Ctor");

		count = ++counter;
		name = String.format ("%1$-14s", "default");
	}

	/*
	 * Constructor for the UCSDStudent object given a name and student
	 * number. Tracks the memory associated with the UCSDStudent.
	 *
	 * @param nm the name of the UCSDStudent being created
	 * @param sn the student number of the UCSDStudent being created
	 */
	public UCSDStudent (String nm, long sn) {
		tracker = new Tracker ("UCSDStudent " + count + " " + nm,
		nm.length ()
		+ Size.of (studentNum)
		+ Size.of (count)
		+ Size.of (tracker),
		"UCSDStudent Ctor");

		count = ++counter;
		name = String.format ("%1$-14s", nm);
		studentNum = sn;
	}

	/**
	 * returns a copy of current student
	 *
	 * @returns none
	 */
	public Base copy () {

		return new UCSDStudent(name, studentNum);

	}
	
	/**
	 * jettisons memory allocated
	 *
	 * @returns None
	 */
	public void jettison () {
		tracker.jettison();
		tracker = null;
	}
	
	/**
	 * returns name of student
	 *
	 * @returns name of student 
	 */
	public String getName(){
		return name;
	}

	/**
	 * returns name of student
	 *
	 * @returns name of student 
	 */
	public String getTrimName(){
		return name.trim();
	}

	/**
	 * tests equality with another object
	 *
	 * @param object - object to be compared to this
	 *
	 * @returns true if the two students have the same name, false otherwise
	 */
	public boolean equals (Object object){
		if(this == null){
			return true;
		}

		if(!(object instanceof UCSDStudent))
			return false;

		//creating a new student out of object
		UCSDStudent otherStudent = (UCSDStudent) object;

		//testing equality
		return name.equals(otherStudent.getName());
	}

	/**
	 * tests is other Base object is less than this
	 *
	 * @param base - object to be compared
	 * 
	 * @returns true if this is less than base, false otherwise
	 */
	public boolean isLessThan(Base base){
		return (name.compareTo(base.getName()) < 0) ? true:false;
	}

	/**
	 * reads a student from an input file
	 *
	 * @param fio - input file to be read
	 * 
	 * @returns none
	 */
	public void read (RandomAccessFile fio){
		try{
			name = fio.readUTF();
			studentNum = fio.readLong();
		}

		catch(IOException ioe){
			System.err.println("IOException in UCSDStudent Read");
		}

	}

	/**
	 * writes a student onto aninput file
	 *
	 * @param fio - input file to be written on
	 * 
	 * @returns none
	 */
	public void write (RandomAccessFile fio){
		try{
			fio.writeUTF(name);
			fio.writeLong(studentNum);
		}

		catch(IOException ieo){
			System.err.println("IOException in UCSDStudent write");
		}
	}

	public String toString () {
		if (Tree.getDebug ())
			return "UCSDStudent #" + count + ":  name:  " 
				+ name.trim () + "  studentnum:  " + studentNum;

		return "name:  " + name.trim () + "  studentnum:  "
			+ studentNum;
	}
}

/**
 * Class:		Driver
 * Description:		tests Tree data structure
 */
public class Driver {
	private static final int
		NULL = 0,
		FILE = 0,
		KEYBOARD = 1,
		EOF = -1;

	public static void main (String [] args) {

		/* initialize debug states */
		Tree.debugOff ();

		/* check command line options */
		for (int index = 0; index < args.length; ++index) {
			if (args[index].equals ("-x"))
				Tree.debugOn ();
		}

		UCSDStudent sample = new UCSDStudent ();
		/* The real start of the code */
		SymTab<UCSDStudent> symtab = 
			new SymTab<UCSDStudent> ("Driver.datafile",
						sample, "Driver");

		String buffer = null;
		int command;
		long number = 0;
		UCSDStudent stu = null;

		Writer os = new FlushingPrintWriter (System.out, true);
		Reader is = new InputStreamReader (System.in);
		int readingFrom = KEYBOARD;

		System.out.println ("Initial Symbol Table:\n" + symtab);

		// SUGGESTED TEST STUDENT NUMBERS FOR VIEWING IN OCTAL DUMPS
		// 255, 32767, 65535, 8388607, 16777215
		// FF	7FFF	FFFF	7FFFFF	FFFFFF
		while (true) {
		try {
			command = NULL; // reset command each time in loop
			os.write ("Please enter a command ((c)heck memory, "
				+ "(f)ile, (i)nsert, (l)ookup, (r)emove, "
				+ "(w)rite):  ");
			command = MyLib.getchar (is);

			//end of file
			if (command == EOF) {
				//checking if reading from keyboard
				if(readingFrom == KEYBOARD){
					break;
				}

				//else if reading from file,
				//changing input and output stream
				else{
					os = new FlushingPrintWriter(System.out, true);
					is = new InputStreamReader(System.in);
					readingFrom = KEYBOARD;
				}
			}

			if (command != EOF)
				MyLib.clrbuf ((char) command, is);

			switch (command) {
			case 'c':
				Tracker.checkMemoryLeaks ();
				System.out.println();

				break;

			case 'f':
				//prompt user
				os.write ("PLease enter file name for commands: ");
				buffer = MyLib.getline(is); //the name of the file

				//changin input and output streams
				is = new FileReader(buffer);
				os = new FlushingFileWriter("/dev/null");
				readingFrom = FILE;

				break;

			case 'i':
				os.write
				("Please enter UCSD student name to insert:  ");

				buffer = MyLib.getline (is);

				os.write
					("Please enter UCSD student number:  ");

				number = MyLib.decin (is);
				MyLib.clrbuf ((char) command, is);

				// create student and place in symtab
				stu = new UCSDStudent (buffer, number);
				symtab.insert (stu);

				break;

			case 'l': 
				UCSDStudent found;

				os.write
					("Please enter UCSD student name to "
					+ "lookup:  ");
				buffer = MyLib.getline (is);

				stu = new UCSDStudent (buffer, 0);
				found = symtab.lookup (stu);
				stu.jettison ();
				stu = null;
					
				if (found != null) {
					System.out.println 
						("Student found!!!\n");
					System.out.println (found);

					found.jettison ();
					found = null;
				}
				else
					System.out.println 
						("student " + buffer 
						+ " not there!");
					
					break;

			case 'r':
				// data to be removed 	
				UCSDStudent removed;

				os.write 
				("Please enter UCSD student name to remove:  ");

				buffer = MyLib.getline (is);

				stu = new UCSDStudent (buffer, 0);
				removed = symtab.remove (stu);

				stu.jettison ();
				stu = null;

				if (removed != null) {
					System.out.println ("Student "
								+ "removed!!!"); 
					System.out.println (removed);

					removed.jettison ();
					removed = null;
				}
				else
					System.out.println 
					("student " + buffer + " not there!");

				break;

			case 'w':
				System.out.print ("The Symbol Table " +
				"contains:\n" + symtab);
				break;
			}
		}
		catch (IOException ioe) {
			System.err.println ("IOException in Driver main");
		}
		}


		System.out.print ("\nFinal Symbol Table:\n" + symtab);

		if (symtab.getOperation () != 0){
			System.out.print ("\nCost of operations:    ");
			System.out.print (symtab.getCost());
			System.out.print (" tree accesses");

			System.out.print ("\nNumber of operations:  ");
			System.out.print (symtab.getOperation());

			System.out.print ("\nAverage cost:          ");
			System.out.print (((float) (symtab.getCost ())) 
					  / (symtab.getOperation ()));
			System.out.print (" tree accesses/operation\n");
		}
		else{
			System.out.print ("\nNo cost information available.\n");
		}
		symtab.jettison ();
		sample.jettison ();
		Tracker.checkMemoryLeaks ();
	}
}
