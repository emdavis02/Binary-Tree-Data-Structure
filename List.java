import java.io.*;

public class List extends Base {

	static boolean debug = false;	// debug status
	static int listCounter = 0;	// used to number each List

	public static final int		// List controls
		END = 0,
		FRONT = 1,
		SORTED = 2;

	private class ListEngine {

		// catastrophic error messages
		static final String 
			ADNEXT_EMPTY = "Advance next from empty list!!!\n",
			ADPRE_EMPTY = "Advance pre from empty list!!!\n",
			REMOVE_EMPTY = "Remove from empty list!!!\n",
			VIEW_EMPTY = "Viewing an empty list!!!\n",
			WRITE_NONEXISTFILE 
				= "Writing to a non-existent file!!!\n";


		// debug messages
		static final String 
			ADNEXT = "[List %d - Advancing next]\n",
			ADPRE = "[List %d - Advancing pre]\n",
			INSERT = "[List %d - Inserting node]\n",
			LOOKUP = "[List %d - Looking up node]\n",
			REMOVE = "[List %d - Removing node]\n",
			VIEW = "[List %d - Viewing node]\n",
			LIST_ALLOCATE 
				= "[List %d has been allocated]\n",
			LIST_JETTISON
				= "[List %d has been jettisoned]\n";

		int count;	// which list is it
		Node front;	// start of the List
		long occupancy;	// how many items stored
		Base sample;	// sample object of what is stored
		Tracker tracker; // to track memory

		// TODO: ListEngine CTOR METHOD HEADER
		ListEngine (Base sample, String caller) {
			tracker = new Tracker ("ListEngine", 
				Size.of (count) 
				+ Size.of (front)
				+ Size.of (occupancy)
				+ Size.of (sample)
				+ Size.of (tracker),
				caller + " calling ListEngine Ctor");
			// ---- DO NOT CHANGE TRACKER ---- //

			// TODO: YOUR CODE GOES HERE
			front = null;
			occupancy = 0;
			listCounter += 1;
			count = listCounter;
			this.sample = sample;

			if (debug) {
				System.err.print (
					String.format (
						LIST_ALLOCATE, 
						count));
			}
		}		

		// TODO: ListEngine JETTISION METHOD HEADER
		void jettisonList () {  

			// TODO: YOUR CODE GOES HERE 

			if (debug) {
				System.err.print (
					String.format (
						LIST_JETTISON, 
						count));
			}

			for (int idx = 0; idx < occupancy - 1; idx++) {
				Node oldFront = front;
				front = front.getNext ();
				oldFront.setNext (null);
				front.setPre (null);
				oldFront.jettisonNode ();
			}
			if (front != null) {
				front.jettisonNode ();
			}
			tracker.jettison ();
			front = null;
			listCounter--;
			count = listCounter;
		}

		// TODO: ListEngine ISEMPTY METHOD HEADER
		boolean isEmpty () {

			// TODO: YOUR CODE GOES HERE 
			return (occupancy == 0);
		}

		// TODO: ListEngine ADVANCENEXT METHOD HEADER
		void advanceNext () {

			// TODO: YOUR CODE GOES HERE
			if (debug) {
				System.err.print (
					String.format (
						ADNEXT, count));
			}	

			if (isEmpty ()) {
				System.err.print (ADNEXT_EMPTY);
				return;
			}

			front = front.getNext ();
		}

		// TODO: ListEngine ADVANCEPRE METHOD HEADER
		void advancePre () {

			// TODO: YOUR CODE GOES HERE

			if (debug) {
				System.err.print (
					String.format (
						ADNEXT, count));
			}

			if (isEmpty ()) {
				System.err.print (ADNEXT_EMPTY);
				return;
			}

			front = front.getPre ();

		}

		// TODO: ListEngine INSERT METHOD HEADER
		boolean insert (Base element, long where) {

			// TODO: YOUR CODE GOES HERE

			if (debug)  {
				System.err.print (
					String.format (
						INSERT, count));
			}

			if (isEmpty ()) {
				front = new Node (element);
				return true;
			}

			Node frontPtr = front;
			// locate
			if (where == SORTED) {
				if (locate (element)) {
					where = FRONT;
				}
			}
			front = front.insert (element);
			if (where != FRONT) {	// insert not at front
				front = frontPtr;
			}

			return true;

		}

		// TODO: ListEngine LOCATE METHOD HEADER
		boolean locate (Base element) {

			// TODO: YOUR CODE GOES HERE

			int idx = 0;
			for (idx = 0; idx < occupancy; idx ++) {
				if (element.isLessThan (
					front.nodeEngine.data)) {
					break;
				}
				else {
					advanceNext ();
				}
			}
			return idx == 0;

		}

		// TODO: ListEngine REMOVE METHOD HEADER
		Base remove (long where) {

			// TODO: YOUR CODE GOES HERE

			if (debug) {
				System.err.print (
					String.format (
						REMOVE, count));
			}

			if (isEmpty ()) {
				System.err.print (REMOVE_EMPTY);
				return null;
			}
			Node frontPtr = front;

			if (where == FRONT) {
				frontPtr = front.getNext ();
			} 
			else {
				advancePre ();
			}

			Base ret = front.remove ();
			occupancy --;
			front = frontPtr;
			if (occupancy == 0) {
				front = null;
			}
			return ret;

		}

		// TODO: ListEngine VIEW METHOD HEADER
		Base view (long where) {

			// TODO: YOUR CODE GOES HERE

			Node nodeToView = front;

			if (debug) {
			System.err.print (
				String.format (
					VIEW, count));
			}

			if (listEngine.isEmpty ()) {
				System.err.print (VIEW_EMPTY);
				return null;
			}

			if (where == END) {
				nodeToView = nodeToView.getPre ();
			}
			return nodeToView.view ();

		}

		// ListEngine WRITELIST
		void writeList (PrintStream stream) {

			if (stream == null) {
				System.err.print (WRITE_NONEXISTFILE);
				return;
			}

			// extra output if we are debugging
			if (stream == System.err) {
				stream.print ("List " 
					+ count + " has "
					+ occupancy + " items in it\n");
			}

			// display each Node in the List
			Node priorFront = front;  // to save prior front
			for (long idx = 1; idx <= occupancy; idx++) {
				stream.print (" element " + idx + ": ");
				front.writeNode (stream);
				advanceNext ();
			}

			// memory tracking output if we are debugging
			if (debug) {
				System.err.print (tracker);
			}

			// restore front to prior value
			front = priorFront;
		}

		// TODO: ListEngine WRITEREVERSELIST METHOD HEADER
		void writeReverseList (PrintStream stream) {

			// TODO: YOUR CODE GOES HERE

			if (stream == null) {
				System.err.print (WRITE_NONEXISTFILE);
				return;
			}

			Node frontTemp = front;
			if (stream == System.err) {
				stream.print ("List " + count 
					+ " has "+ occupancy 
					+ " items in it\n");
			}
			for (long idx = 1; idx <= occupancy; idx++) {
				advancePre ();
				stream.print (" element " + idx + ": ");
				front.writeNode (stream);
			}
			if (debug) {
				System.err.print (tracker);
			}
			front = frontTemp;

		}

		// List TOSTRING METHOD
		public String toString () {

			long count;             /* to know how many elements to print */
			Node current;           /* working node */
			String string = "";     /* string to be returned */

			if(isEmpty ())
					return "empty";

			current = front;
			for (count = 1; count <= occupancy; count++) {
					string += "" + current;
					current = current.getNext ();
			}

			return string;
		}

		private class Node {

			private class NodeEngine {

				static final String WRITE_NONEXISTFILE 
					= "Writing to a " 
					+ "non-existent file!!!\n";

				Base data;	// the item stored
				Node next;	// to get to following item
				Node pre;	// to get to previous item
				Tracker tracker; // to track memory

				// TODO: NodeEngine CTOR METHOD HEADER HERE. 
				NodeEngine (Node newNode, 
					Base element, String caller) {

					tracker = new Tracker ("NodeEngine", 
						Size.of (data) 
						+ Size.of (next) 
						+ Size.of (pre)
						+ Size.of (tracker),
						caller 
						+= " calling NodeEngine Ctor");
					// ---- DO NOT CHANGE TRACKER ---- //

					// TODO: YOUR CODE GOES HERE

					data = (sample == null) ? element
						: sample.copy ();

					// In a circular linked list, never null
					pre = newNode;
					next = newNode;

					occupancy++;

				}

				// TODO: NodeEngine JETTISON METHOD HEADER HERE. 
				void jettisonNode () {
					// TODO: YOUR CODE GOES HERE

					tracker.jettison ();
					data.jettison();
					data = null;

				} 

				// TODO: NodeEngine JETTISON METHOD HEADER HERE. 
				void jettisonNodeOnly () {
					// TODO: YOUR CODE GOES HERE

					tracker.jettison ();
					data = null;				
				} 

				// TODO: NodeEngine INSERT METHOD HEADER HERE. 
				Node insert (Node frontNode, 
					Base element) {

					// TODO: YOUR CODE GOES HERE

					Node newNode = new Node (element);

					// attach
					newNode.setPre (pre);
					newNode.setNext (frontNode);

					// integrate
					pre.setNext (newNode);
					pre = newNode;
					return newNode;

				}

				// TODO: NodeEngine REMOVE METHOD HEADER HERE. 
				Base remove () {
					// TODO: YOUR CODE GOES HERE

					Base ret = data;
					boolean nodeOnly = true;

					// restucture the list
					pre.setNext (next);
					next.setPre (pre);
					jettisonNodeOnly ();

					return ret;

				}

				// TODO: NodeEngine VIEW METHOD HEADER HERE. 
				Base view () {
					// TODO: YOUR CODE GOES HERE
					return data;
				}

				// NodeEngineWRITENODE METHOD
				void writeNode (PrintStream stream) {
					if (stream == null) {
						System.err.print (
							WRITE_NONEXISTFILE);
						return;
					}
					stream.print (data + "\n");
				}
			}

			// -------- YOUR CODE SHOULD GO ABOVE --------
			// NOTE: 
			// READ THE CODE BELOW TO SEE WHAT METHOD YOU CAN USE

			static final String
				GETPRE_NONEXISTNODE
				= "Getting pre of a non-existent node!!!\n",
				GETNEXT_NONEXISTNODE
				= "Getting next of a non-existent node!!!\n",
				SETPRE_NONEXISTNODE
				= "Setting pre of a non-existent node!!!\n",
				SETNEXT_NONEXISTNODE
				= "Setting next of a non-existent node!!!\n",
				JETTISON_NONEXISTNODE 
				= "Jettisoning a non-existent node!!!\n",
				LOOKUP_NONEXISTNODE 
				= "Looking up a non-existent node!!!\n",
				INSERT_NONEXISTNODE
				= "Inserting a non-existent node!!!\n",
				REMOVE_NONEXISTNODE
				= "Removing a non-existent node!!!\n",
				VIEW_NONEXISTNODE 
				= "Viewing a non-existent node!!!\n",
				WRITE_NONEXISTNODE 
				= "Writing from a non-existent node!!!\n";

			NodeEngine nodeEngine;

			// Node CTOR METHOD 
			Node (Base element) {
				nodeEngine = new NodeEngine (
					this, element, "Node Ctor");
			}

			Base getData () {
				return nodeEngine.data;
			}

			// Node GETPRE METHOD
			Node getPre () {
				if (!exist ()) {
					System.err.print (
						GETPRE_NONEXISTNODE);
					return null;
				}
				return nodeEngine.pre;
			}

			// Node GETNEXT METHOD
			Node getNext () {
				if (!exist ()) {
					System.err.print (
						GETNEXT_NONEXISTNODE);
					return null;
				}
				return nodeEngine.next;
			}

			// Node SETNEXT METHOD
			void setNext (Node next) {
				if (!exist ()) {
					System.err.print (
						SETNEXT_NONEXISTNODE);
					return;
				}
				nodeEngine.next = next;
			}

			void setPre (Node pre) {
				if (!exist ()) {
					System.err.print (
						SETPRE_NONEXISTNODE);
					return;
				}
				nodeEngine.pre = pre;
			}

			// Node JETTISON METHOD
			boolean jettisonNode () {
				if (!exist ()) {
					System.err.print (
						JETTISON_NONEXISTNODE);
					return false;
				}
				nodeEngine.jettisonNode ();
				nodeEngine = null;
				return true;
			} 

			// Node EXIST METHOD 
			boolean exist () {
				return nodeEngine != null;
			}

			// Node INSERT METHOD 
			Node insert (Base element) {
				if (!exist ()) {
					System.err.print (INSERT_NONEXISTNODE);
					return null;
				}
				return nodeEngine.insert (this, element);
			} 

			// Node REMOVE METHOD
			Base remove () {
				if (!exist ()) {
					System.err.print (REMOVE_NONEXISTNODE);
					return null;
				}
				return nodeEngine.remove ();
			}

			// Node VIEW METHOD
			Base view () {
				if (!exist ()) {
					System.err.print (
						VIEW_NONEXISTNODE);
					return null;
				}
				return nodeEngine.view ();
			}

			// Node WRITENODE METHOD
			void writeNode (PrintStream stream) {
				nodeEngine.writeNode (stream);
			}

			public String toString () {
				return "" + getData ();
			}
		}
	}

	// catastrophic error messages
	static final String 
		ADNEXT_NONEXIST = "Advance next from non-existent list!!!\n",
		ADPRE_NONEXIST = "Advance pre from non-existent list!!!\n",
		JETTISON_NONEXIST = "Jettisoning from non-existent list!!!\n",
		EMPTY_NONEXIST = "Empyting from non-existent list!!!\n",
		ISEMPTY_NONEXIST = "Is empty check from non-existent list!!!\n",
		INSERT_NONEXIST = "Inserting to a non-existent list!!!\n",
		REMOVE_NONEXIST = "Removing from non-existent list!!!\n",
		VIEW_NONEXIST = "Viewing a non-existent list!!!\n",
		WRITE_NONEXISTLIST = "Writing from a non-existent list!!!\n",
		WRITE_MISSINGFUNC = "Don't know how to write out elements!!!\n";

	private ListEngine listEngine;	// The ListEngine instance

	public static void debugOn () {
		debug = true;
	}

	public static void debugOff () {
		debug = false;
	}

	// List CTOR METHOD
	public List (Base sample, String caller) {
		caller += " calling List Ctor";	
		listEngine = new ListEngine (sample, caller);
	}

	// list JETTISON
	public void jettison () {
		jettisonList ();
	}

	// list JETTISON
	public boolean jettisonList () {

		if (!exist ()) {
			System.err.print (JETTISON_NONEXIST);
			return false;
		}

		listEngine.jettisonList ();
		listEngine = null;
		return true;
	}


	// List ADVANCENPRE METHOD
	public void advancePre () {

		if (!exist ()) {
			System.err.print (ADPRE_NONEXIST);
			return;
		}

		listEngine.advancePre ();
	}

	// List ADVANCENEXT METHOD
	public void advanceNext () {

		if (!exist ()) {
			System.err.print (ADNEXT_NONEXIST);
			return;
		}

		listEngine.advanceNext ();
	}

	// List EMPTY METHOD
	public void empty () {

		if (!exist ()) {
			System.err.print (EMPTY_NONEXIST);
			return;
		}
		while (!isEmpty ()) {
			listEngine.remove (0);
		}
	}

	// List EXIST METHOD
	public boolean exist () {

		return listEngine != null;
	}

	// List GETOCCUPANCY METHOD
	public long getOccupancy () {

		return listEngine.occupancy;
	}

	// List ISEMPTY METHOD
	public boolean isEmpty () {

		if (!exist ()) {
			System.err.print (ISEMPTY_NONEXIST);
			return false;
		}

		return listEngine.isEmpty ();
	}

	// List INSERT METHOD
	public boolean insert (Base element, long where) {

		if (!exist ()) {
			System.err.print (INSERT_NONEXIST);
			return false;
		}

		return listEngine.insert (element, where); 	
	}

	// List REMOVE METHOD
	public Base remove (long where) {

		if (!exist ()) {
			System.err.print (REMOVE_NONEXIST);
			return null;
		}

		return listEngine.remove (where);
	}

	// List TOSTRING METHOD
	public String toString () {

		return listEngine.toString ();
	}

	// List VIEW METHOD
	public Base view (long where) {

		if (!exist ()) {
			System.err.print (VIEW_NONEXIST);
			return null;
		}

		return listEngine.view (where);
	}

	// List WRITELIST METHOD
	public void writeList (PrintStream stream) {

		if (!exist ()) {
			System.err.print (WRITE_NONEXISTLIST);
			return;
		}

		listEngine.writeList (stream);
	}

	// List WRITEREVERSELIST METHOD
	public void writeReverseList (PrintStream stream) {

		if (!exist ()) {
			System.err.print (WRITE_NONEXISTLIST);
			return;
		}

		listEngine.writeReverseList (stream);
	}
}
