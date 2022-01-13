import java.io.*;
/**
 * Name:	Elise Davis
 * 		Varsha Sampath
 * PID:		A16275858
 * 		A16294875
 * USER:	cs12fa21gj@ieng6.ucsd.edu
 * 		cs12fa21du@ieng6.ucsd.edu 
 * File Name:	Tree.java
 * Description: implements a binary tree data structure, holds TNodes that
 * 		contain Whatever type objects. Contains methods to
 * 		manipulate stored TNodes, which are stored at the disk
 */

/**
 * Class:		Tree
 * Description:		binary tree data structure, holds TNodes that contain
 * 			Whatever type objects.
 *
 * Fields:		RandomAccessFile fio - write to and read from disk
 * 			occupancy - number of TNodes in the tree
 * 			root - position of the root of the Tree
 * 			representation - String representation of Tree
 * 			sample - copy Base object in TNode's read Ctor
 * 			sampleNode - to call TNode searchTree
 * 			tracker - track Tree's memory
 * 			treeCount - which Tree it is
 *			tree Counter -  how many Tree;s are allocated
 *
 * Public Functions:	debugOn 	 - Turns on debugging for this Tree
 * 			debugOff	 - Turns off debugging for this Tree
 * 			Tree 		 - allocates and initiliazes the memory
 * 					   associated with the Tree object.
 * 			getDebug	 - checks if debug is true or false
 * 			getCost		 - returns number of disk reads
 * 			getOperation	 - returns number of insert, 
 * 					   lookup, remove operations
 * 			jettison	 - jettisons trees memory, calls
 * 					   jettisonAllNodes
 * 			insert		 - inserts the element into tree
 * 			isEmpty		 - checks if tree is empty or not
 * 			lookup		 - looks up the matching data in tree
 * 			remove		 - removes the matching data from tree
 * 			toString	 - Creates string representation of
 * 					   this tree
 */
public class Tree<Whatever extends Base> {

	private static final long BEGIN = 0;

	// data fields
	private RandomAccessFile fio;	// to write to and read from disk
	private long occupancy;		// number of TNode's in the Tree
	private long root;		// position of the root of the Tree
	private String representation;	// String representation of Tree
	private Base sample;		// copy Base object in TNode's read CTor
	private TNode sampleNode;	// to call TNode searchTree
	private Tracker tracker;	// track Tree's memory
	private long treeCount;		// which Tree it is
	private static long treeCounter;// how many Tree's are allocated

	// debug flag
	private static boolean debug;

	// number of disk reads and writes
	public static long cost = 0;

	// number of insert, remove, locate operations
	public static long operation = 0;

	// debug messages
	private static final String 
		TREE = "[Tree ",
		ALLOCATE = " - Allocating]\n",
		JETTISON = " - Jettisoning]\n",
		CLOSE = "]\n",
		COST_READ = "[Cost Increment (Disk Access): Reading ",
		COST_WRITE = "[Cost Increment (Disk Access): Writing ",
		AND = " and ",
		COMPARE = " - Comparing ",
		INSERT = " - Inserting ",
		CHECK = " - Checking ",
		UPDATE = " - Updating ",
		REPLACE = " - Replacing ";

	/*
	 * PositionBox class creates a PositionBox object to wrap a long type
	 * to be passed by reference in TNode methods.
	 */
	private class PositionBox {
		public long position;	// position value to be wrapped

		/*
		 * Constructor for PositionBox object, wraps position parameter.
		 *
		 * @param position the value to be wrapped by PositionBox
		 */
		public PositionBox (long position) {
			this.position = position;
		}
	}

	/**
	 * allocates and initiliazes the memory associated with the Tree object.
	 * allocates for writing to the disk
	 *
	 * @param 	name	name of tree
	 * 		caller	to reference tree
	 */
	public Tree (String datafile, Whatever sample, String caller) {
		tracker = new Tracker ("Tree", Size.of (root)
			+ Size.of (occupancy)
			+ Size.of (representation)
			+ Size.of (treeCount)
			+ Size.of (tracker)
			+ Size.of (fio)
			+ Size.of (this.sample),
			caller + " calling Tree CTor");

		// DO NOT CHANGE TRACKER CODE ABOVE

		// allocate variables
		occupancy = 0;
		root = 0;
		this.sample = sample;
		sampleNode = new TNode(caller);
		treeCounter++;
		treeCount = treeCounter;

		//debug message
		if(debug){
			System.err.print(TREE + treeCount + ALLOCATE);
		}

		try{
			//allocate the fio object
			fio = new RandomAccessFile(datafile, "rw");

			fio.seek(BEGIN);
			long begin = fio.getFilePointer(); //to store begin offset

			fio.seek(fio.length());
			long end = fio.getFilePointer(); //to store end offset

			//check for empty file
			if(begin == end){
				//reserve space for root and occupancy
				fio.seek(BEGIN);
				fio.writeLong(root);
				fio.writeLong(occupancy);

				//value of root is unknown
				root = fio.getFilePointer();
				occupancy = 0;
			}

			else{
				fio.seek(BEGIN);
				root = fio.readLong();
				occupancy = fio.readLong();
			}
		}

		catch(IOException ieo){
			System.err.println("IOException in Tree Constructor");
		}
	}

	/**
	 * Disable debug messager
	 */
	public static void debugOff () {
		debug = false;
	}

	/**
	 * Enable debug messages
	 */
	public static void debugOn () {
		debug = true;
	}

	/**
	 * Debug accessor
	 *
	 * @return true if debug is one, false otherwise
	 */
	public static boolean getDebug () {
		return debug;
	}

	/**
	 * Getter method for cost
	 *
	 * @return number of disk reads and writes of TNode
	 */
	public long getCost () {
		return cost;
	}

	/**
	 * Getter method for operation
	 *
	 * @return number of insert, lookup, remove operations
	 */
	public long getOperation () {
		return operation;
	}

	/**
	 * Count a TNode disk read or write
	 */
	public void incrementCost () {
		cost++;
	}

	/**
	 * Count an insert, lookup, or remove
	 */
	public void incrementOperation () {
		operation++;
	}

	/**
	 * Inserts the element into the binary tree. Inserts at the root TNode 
	 * if Tree is empty, otherwise delegates to TNode's insert by calling 
	 * the searchTree method on the sampleTNode. Returns the data inserted.
	 * @param element - element to be inserted
	 * @return the element inserted
	 */
	public Whatever insert (Whatever element) {
		//debug statements
		if(debug){
			System.err.print(TREE + treeCount + INSERT
				+ element.getName() + CLOSE);
		}

		incrementOperation();
		// check if the tree is empty
		if (isEmpty()) {
			// create new node for root
			TNode tempNode = new TNode(element, "Tree.insert");
			// set root position
			root = tempNode.position;
			// get rid of tnode
			tempNode.jettisonTNode();
			//debug messages
		}
		else {
			// insert the element using boxing
			PositionBox rootBox = new PositionBox(root);
			sampleNode.searchTree(element, rootBox, "insert");
			root = rootBox.position;
		}
		// return the element inserted
		return element;	
	}

	/**
	 * Checks if tree is empty or not
	 *
	 * @return None
	 */
	public boolean isEmpty () {
		//if occupancy is zero, tree is empty
		if(occupancy == 0)
			return true;

		else
			return false;
	}

	/*
	 * jettison method for the Tree object. Untracks all memory associated
	 * with the Tree.
	 */
	public void jettison () {
		// Debug messages
		if (debug) {
			System.err.print (TREE);
			System.err.print (treeCount);
			System.err.print (JETTISON);
		}

		write (); // write the final root and occupancy to disk

		try {
			fio.close (); // close the file accessor
		} catch (IOException ioe) {
			System.err.println ("IOException in Tree's jettison");
		}

		// Jettison TNodes and then tree itself
		sampleNode.jettisonTNode ();
		sampleNode = null;
		tracker.jettison ();
		tracker = null;
		treeCounter--;
	}

	public void write () {
		try{
			fio.seek(BEGIN);
			fio.writeLong(root);
			fio.writeLong(occupancy);
		}

		catch(IOException ieo){
			System.err.println("IOException in Tree write");
		}
	}

	/**
	 * Looks up matching data in the tree
	 *
	 * @param element - element to be looked up
	 * @returns data stored in element found
	 */
	public Whatever lookup (Whatever element) {
		// incremement the cost
		incrementOperation();
		// fail lookup if empty tree
		if(isEmpty()){
			return null;
		}
		// find the element
		else{
			// use boxing
			PositionBox rootBox = new PositionBox(root);
			// delegate to TNode lookup
			Whatever output = sampleNode.searchTree(element,
				       	rootBox, "lookup");
			root = rootBox.position;
			// return the element found
			return(output);
		}
	}

	/**
	 * Removes matching data from the binary tree
	 *
	 * @param element - element to be removed
	 * @return data stored in removed element
	 */
	public Whatever remove (Whatever element) {
		// fail remove if empty tree
		if(isEmpty()){
			return null;
		}
		// find element to remove
		else{
			//Boxing root before removing element
			PositionBox rootBox = new PositionBox(root);
			// delegate to TNode remove
			Whatever output = sampleNode.searchTree(element, 
					rootBox, "remove");
			
			//reset root if empty
			if (isEmpty()) {
				resetRoot();
			}
			//unboxing root
			root = rootBox.position;
			return(output);
		}
	}

	/**
	 * Resets the root datafield of this tree to be at the end of the 
	 * datafile
	 * 	 
	 * @return None
	 */
	private void resetRoot () {
		try {
			// seek to end of file and set root
			fio.seek(fio.length());
			root = fio.getFilePointer();
		}
		catch (IOException ioe) {
			System.err.println("IOException in <Method Name>");
		}
	}

	/**
	 * Creates a string representation of this tree. This method first
	 * adds the general information of this tree, then calls the
	 * recursive TNode function to add all nodes to the return string
	 *
	 * @return  String representation of this tree
	 */
	public String toString () {

		representation = "Tree " + treeCount + ":\n"
			+ "occupancy is " + occupancy + " elements.\n";

		try {
			fio.seek (fio.length ());	
			long end = fio.getFilePointer ();

			long oldCost = getCost ();

			if (root != end) {
				TNode readRootNode = new TNode (root,
							"Tree's toString");
				readRootNode.writeAllTNodes ();
				readRootNode.jettisonTNode ();
				readRootNode = null;
			}

			cost = oldCost;
		} catch (IOException ioe) {
			System.err.println ("IOException in Tree's toString");
		}

		return representation;
	}

	/**
	 * Class:		TNode
	 * Description:		to hold data, stored in binary tree written
	 * 			to disk.
	 *
	 * Fields:		data 		- holds the data stored in
	 * 					 current node
	 * 			left 		- the left child
	 * 			right 		- the right child
	 * 			height		- height of the node
	 * 			balance		- balance of the node
	 * 			position	- position on the disk
	 * 			tracker		- tracks memory usage
	 *
	 * Public Functions:	TNode (write ctor) - called when creating a
	 * 			TNode for the first time to write to the disk
	 * 			TNode (read ctor) - for reading a TNode present
	 * 			on a disk into memeory
	 * 			insert - inserts an element into the tree
	 * 			read - reads a TNode on the datafile into memory
	 * 			write - writes this TNode object to the disk
	 * 			remove - removes the matching data from the 
	 * 			binary tree
	 * 			searchTree - calls certain functions on a node
	 * 			replaceAndRemoveMin - called to maintain tree
	 * 			structure during removal
	 * 			setHeightAndBalance - sets the height and 
	 * 			balance of the nodes
	 * 			toString - creates a string representation
	 * 				 of this node
	 * 			writeAllNodes - writes all nodes to string
	 * 					representation field
	 */

	private class TNode {
		private Whatever data;	// data to be stored in the TNode
		// 1 + height of tallest child, or 0 for leaf
		private long height;
		// left child's height - right child's height
		private long balance;
		// positions of the TNode and its left and right children
		private long left, right, position;
		private Tracker tracker;// to track memory of the tree


		// threshold to maintain in the Tree
		private static final long THRESHOLD = 2;

		/*
		 * TNode constructor to create an empty TNode
		 *
		 * @param caller method object was created in
		 */
		public TNode (String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
				+ Size.of (left)
				+ Size.of (right)
				+ Size.of (position)
				+ Size.of (height)
				+ Size.of (balance)
				+ Size.of (tracker),
				caller + " calling TNode CTor");
		}

		/*
		 * TNode constructor to create an empty TNode write TNodes
		 * to the disk and initialize fields
		 *
		 * @param caller method object was created in
		 * @param element data to be used in TNode
		 */
		public TNode (Whatever element, String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
				+ Size.of (left)
				+ Size.of (right)
				+ Size.of (position)
				+ Size.of (height)
				+ Size.of (balance)
				+ Size.of (tracker),
				caller + " calling TNode CTor");

			// DO NOT CHANGE TRACKER CODE ABOVE

			// Completed: YOUR CODE GOES HERE
			data = element;
			left = 0;
			right = 0;
			height = 0;
			balance = 0;
			occupancy++;

			//set position to end of file
			try{
				fio.seek(fio.length());
				position = fio.getFilePointer();
			}

			catch(IOException ioe){
				System.err.println("IOException in TNode Constructor");
			}
			//call write
			write();

		}
		/*
		 * TNode read constructor to read in a TNode on the disk into
		 * memory
		 *
		 * @param caller method object was created in
		 * @param position of the TNode on the disk
		 */

		@SuppressWarnings ("unchecked")
		public TNode (long position, String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
				+ Size.of (left)
				+ Size.of (right)
				+ Size.of (position)
				+ Size.of (height)
				+ Size.of (balance)
				+ Size.of (tracker),
				caller + " calling TNode CTor");

			// DO NOT CHANGE TRACKER CODE ABOVE

			//make a copy of the data of the sample
			data = (Whatever) sample.copy();

			//call read
			read(position);			
		}

		/*
		 * reads a TNode with is present on the datafile into memory.
		 *
		 * @param position of the TNode corresponding to the datafile
		 */
		public void read (long position) {

			//call increment cost
			incrementCost();

			try{
				//seek to the position
				fio.seek(position);
				//read in the element
				data.read(fio);
				//read in each field of the Tnode using readLong
				height = fio.readLong();
				balance = fio.readLong();
				left = fio.readLong();
				right = fio.readLong();
				this.position = fio.readLong();
			}

			catch(IOException ioe){
				System.err.println
					("IOException in TNode's read");
			}
			
			//debug statement
			if(debug)
				System.err.print(COST_READ + data.getTrimName() + CLOSE);
		}

		/*
		 * Writes this TNode object to disk at position in the datafile.
		 *
		 * @param position of the TNode corresponding to the datafile
		 * @return None
		 */
		public void write () {
			//call increment cost
			incrementCost();

			//debug statement
			if(debug)
				System.err.print(COST_WRITE + data.getName() + CLOSE);

			try{
				//seek to the position
				fio.seek(this.position);
				//invoke the write method of the element 
				//to write it to the disk
				data.write(fio);
				//write each field to the disk using writeLong
				fio.writeLong(height);
				fio.writeLong(balance);
				fio.writeLong(left);
				fio.writeLong(right);
				fio.writeLong(position);
			}

			catch(IOException ioe){
				System.err.println
					("IOException in TNode's read");
			}
		}

		/**
		 * Inserts the element into the binary tree.
		 *
		 * @param element - element to be inserted
		 * @return data inserted
		 */
		private Whatever insert (Whatever element,
				PositionBox positionInParentBox) {

			//debug message
			if(debug){
				System.err.print(TREE + treeCount + COMPARE
					+ element.getName() + AND
					+ data.getName() + CLOSE);
			}
			// duplicate insertion
			if (data.equals(element)) {
				// replace data
				data.jettison();
				data = element;
				// write to the disk
				write();

				return element;
			}

			// go left
			else if (element.isLessThan(data)) {
				// insert if nothing exists at left
				if (left == 0) {

					//debug message
					if(debug){
						System.err.print(TREE + treeCount
							+ INSERT + element.getName()
							+ CLOSE);
					}
					// create new node and jettison node
					// after insert
					TNode leftNode = new TNode(element, 
							"TNode's insert");
					left = leftNode.position;
					leftNode.jettisonTNode();

				}

				else {
					// traverse tree using boxing
					PositionBox leftBox = new 
						PositionBox (left);
					// recursively call insert
					Whatever result = searchTree(element, 
							leftBox, "insert");
					// update left
					left = leftBox.position;
				}
			}
			// go right
			else{
				// insert if nothing exists at left
				if(right == 0){

					//debug message
					if(debug){
						System.err.print(TREE + treeCount
							+ INSERT + element.getName()
							+ CLOSE);
					}
					// create new node and jettison node
					// after insert
					TNode rightNode = new TNode(element, 
							"TNode's insert");
					right = rightNode.position;
					rightNode.jettisonTNode();

				}

				else{
					// traverse tree using boxing
					PositionBox rightBox = new 
						PositionBox(right);
					// recursively call insert
					Whatever result = searchTree(element, 
							rightBox, "insert");
					// update right
					right = rightBox.position;
				}
			}
			// set height and balance and return element inserted
			setHeightAndBalance (positionInParentBox);
			return element;

		}

		/*
		 * Jettison method for TNode object, untracks memory associated
		 * with the calling TNode.
		 */
		private void jettisonTNode () {
			left = right = 0; // reset left and right positions

			// jettison the data stored
			if (data != null) {
				data.jettison ();
				data = null;
			}

			// jettison tracker
			tracker.jettison ();
			tracker = null;
		}

		/**
		 * Looks up matching data in the tree
		 *
		 * @param element - element to be looked up
		 * @returns data stored in element found
		 */
		@SuppressWarnings ("unchecked")
		private Whatever lookup (Whatever element) {

			//debug message
			if(debug){
				System.err.print(TREE + treeCount + COMPARE
					+ element.getName() + AND
					+ data.getName() + CLOSE);
			}

			// store result of lookup
			Whatever result = null;
			
			// return data if found
			if (data.equals(element)) {
				result = (Whatever) data.copy();
				return result;
			}

			// go left
			else if (element.isLessThan(data)) {
				// could not find data
				if (left == 0) {
					return null;
				}
				// call lookup
				else {
					PositionBox leftBox = new 
						PositionBox(left);
					result = searchTree 
						(element, leftBox, "lookup");
					// return data found
					return result;
				}
			}
			// go right
			else {
				// could not find data
				if (right == 0) {
					return null;
				}
				// call lookup
				else {
					PositionBox rightBox = new 
						PositionBox(right);
					result = searchTree 
						(element, rightBox, "lookup");
					// return data found
					return result;
				}
			}
		}
		/**
		 * Removes matching data from the binary tree
		 *
		 * @param element - element to be removed
		 * @param positionInParentBox - reference to a wrapper object 
		 * that holds the TNode position in the parent TNode that was 
		 * used to get to the current TNode's offset in the datafile
		 * @param fromSHB boolean for whether or not remove was called
		 * from setHeightAndBalance
		 * @return data stored in removed element
		 */
		@SuppressWarnings("unchecked")
		private Whatever remove (Whatever element,
			PositionBox positionInParentBox, boolean fromSHB) {

			//debug message
			if(debug){
				System.err.print(TREE + treeCount + COMPARE
					+ element.getName() + AND
					+ data.getName() + CLOSE);
			}

			// store result of removal
			Whatever result = null;

			// if we found the element
			if (data.equals(element)) {
				// store a copy of the data of the element
				// being removed
				result = (Whatever)data.copy();
				occupancy--;
				// leaf case
				if (left == 0 && right == 0) {
					// reset position of pointer
					// and return result
					positionInParentBox.position = 0;
					return result;
				}
				// one child case
				else if (left == 0) {
					// reset position of pointer
					// and return result
					positionInParentBox.position = right;
					return result;
				}
				// one child case
				else if (right == 0) {
					// reset position of pointer and return
					// result
					positionInParentBox.position = left;
					return result;
				}
				// two child case
				else {
					// get rid of data in the TNode
					data.jettison();
					// go right once
					PositionBox rightBox =  
						new PositionBox(right);
					// set data to result of 
					// replaceAndRemoveMin
					data = searchTree
						(element, rightBox, "RARM");
					right = rightBox.position;
					// set height and balance if called
					// from shab, write to the disk 
					// otherwise
					if (!fromSHB) {
						setHeightAndBalance 
							(positionInParentBox);
					}

					else {
						write();
					}
					// return data of node removed
					return result;
				}
			}
			// haven't found node yet
			// go left
			else if (element.isLessThan(data)) {
				// failed to find element
				if (left == 0) {
					return null;
				}
				// call remove recursively and traverse tree
				else {
					PositionBox leftBox = new 
						PositionBox (left);
					result = searchTree 
						(element, leftBox, "remove");
					left = leftBox.position;
				
				}
			}
			else {
				// go right
				if (right == 0) {
					return null;
				}
				// call remove recursively and traverse tree
				else {
					PositionBox rightBox = new 
						PositionBox (right);
					result = searchTree 
						(element, rightBox, "remove");
					right = rightBox.position;
				
				}

			}
			// set height and balance
			if (!fromSHB) {
				setHeightAndBalance (positionInParentBox);
			}
			else {
				write();
			}
			// return data of removed node
			return result;	
		}

		/**
		 * Called when removing a TNode with 2 children, replaces that T
		 * Node with the minimum TNode in its right subtree to maintain 
		 * the Tree structure.
		 *
		 * @param targetTNode	TNode with 2 children to be removed
		 * @param pointerInParentBox	that holds the TNode pointer in
		 * the parent TNode that was used to get to the current TNode
		 *
		 * @return none
		*/
		@SuppressWarnings ("unchecked")
		private Whatever replaceAndRemoveMin
				(PositionBox positionInParentBox) {

			//debug message
			if(debug){
				System.err.print(TREE + treeCount + CHECK
					+ data.getName() + CLOSE);
			}

			// store the data
			Whatever result = null;
			// check if there are no more left nodes
			if (left == 0) {
				//debug message
				if(debug){
					System.err.print(TREE + treeCount + REPLACE
						+ data.getName() + CLOSE);
				}
				// store a copy of the data
				result = (Whatever) data.copy();
				positionInParentBox.position = right;
			}
			// continue going left
			else {
				PositionBox leftBox = new PositionBox(left);
				// result of recusively calling RARM
				result = searchTree(data, leftBox, "RARM");
				left = leftBox.position;
				// set height and balance
				setHeightAndBalance(positionInParentBox);
			}
			// return data from removed node
			return result;

		}

		/*
		 * Reads in TNode from the disk at positionInParentBox.position
		 * so an action may be performed on that TNode. Centralizes the
		 * the operations needed when reading a TNode from the disk.
		 *
		 * @param element the data the action is to be performed on
		 * @param positionInParentBox the PositionBox holding the
		 *        position of the TNode to be read from the disk
		 * @param action the action to be performed
		 * @return returns the result of the action
		 */
		private Whatever searchTree (Whatever element,
			PositionBox positionInParentBox, String action) {

			Whatever result = null;
			TNode readNode = new TNode 
			 (positionInParentBox.position, "searchTree " + action);

			if (action.equals ("insert")) {
				result = readNode.insert (element,
							positionInParentBox);
			}
			else if (action.equals ("lookup"))
				result = readNode.lookup (element);
			else if (action.equals ("RARM")) {
				result = readNode.replaceAndRemoveMin
							(positionInParentBox);
			}
			else if (action.equals ("remove")) {
				result = readNode.remove (element,
						positionInParentBox, false);
			}

			readNode.jettisonTNode (); // rename to jettisonTNode
			readNode = null;

			return result;
		}

		/* The PointerInParent parameter is used to pass to Remove
		 * and to Insert as the way to restructure the Tree if
		 * the balance goes beyond the threshold.  You'll need to
		 * store the removed data in a working RTS TNode<Whatever>
		 * because the memory for the current TNode<Whatever> will be
		 * deallocated as a result of your call to Remove.
		 * Remember that this working TNode<Whatever> should not be
		 * part of the Tree, as it will automatically be dealloacted
		 * when the function ends. When calling Remove, remember to
		 * tell Remove that its being called from SetHeightAndBalance.
		 *
		 * @param positionInParentBox holds the position of the calling
		 *        TNode on the disk
		 */


		/**
		 * Updates the height and balance of the current TNode,
		 * and checks if the balance of the TNode exceeds the threshold.
		 * If it does, balances the tree using remove and insert.
		 *
		 * @param pointerInParentBox	that holds the TNode pointer in
		 * the parent TNode that was used to get to the current TNode
		 *
		 * @return none
		*/
		@SuppressWarnings ("unchecked")
		private void setHeightAndBalance 
					(PositionBox positionInParentBox) {
			//debug message
			if(debug){
				System.err.print(TREE + treeCount + UPDATE
					+data.getName() + CLOSE);
			}
			// calculate height and balance
			// store left and right height
			long leftHeight = -1;
			long rightHeight = -1;
			// check if left exists
			if (left != 0) {
				TNode leftNode = new 
					TNode(left, "TNode's SHAB");
				leftHeight = leftNode.height;
				leftNode.jettisonTNode();
			}
			// check if right exists
			if (right != 0) {
				TNode rightNode = new 
					TNode(right, "TNode's SHAB");
				rightHeight = rightNode.height;
				rightNode.jettisonTNode();
			}
			// perform calculations
			height = Math.max((int) leftHeight, 
					(int) rightHeight) + 1;
			balance =  (leftHeight + 1) - (rightHeight + 1);

			// checking if balance is exceeding threshold
			if (Math.abs(balance) > THRESHOLD) {
				// remove node that is out of balance
				Whatever result = remove 
					(this.data, positionInParentBox, true);
				// reinsert out of balance node
				searchTree(result, 
						positionInParentBox, "insert");
				return;
			}
			// write to disk
			write();
		}

		/**
		 * Creates a string representation of this node. Information
		 * to be printed includes this node's height, its balance,
		 * and the data its storing.
		 *
		 * @return  String representation of this node
		 */

		public String toString () {
			return "at height:  " + height + " with balance:  "
				+ balance + "  " + data + "\n";
		}

		/**
		 * Writes all TNodes to the String representation field.
		 * This recursive method performs an in-order
		 * traversal of the entire tree to print all nodes in
		 * sorted order, as determined by the keys stored in each
		 * node. To print itself, the current node will append to
		 * tree's String field.
		 */
		private void writeAllTNodes () {
			if (left != 0) {
				TNode readLeftNode = new TNode (left,
							"writeAllTNodes");
				readLeftNode.writeAllTNodes ();
				readLeftNode.jettisonTNode();
				readLeftNode = null;
			}

			representation += this;

			if (right != 0) {
				TNode readRightNode = new TNode (right,
							"writeAllTNodes");
				readRightNode.writeAllTNodes ();
				readRightNode.jettisonTNode();
				readRightNode = null;
			}
		}
	}
}
