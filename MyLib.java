/**
 * @(#)MyLib.java    2.0, 01/17/21
 * 
 */

/**
 * The MyLib class is a direct port of mylib.c to java.
 * As you already know in java when you pass literal strings like
 * <P>
 *   writeline("a literal string\n", stream);
 * <P>
 * in C is considered a char[], but in java it's automatically
 * converted and treated as a String object.  Therefore 
 * the function writeline accepts literal strings and 
 * String types.  The getline function returns a String type.
 *
 * @author Gary Gillespie 
 * @author Bryner Pancho (C ported to java)
 * @version 1.0, 04/24/98
 * 
 * @author Gary Gillespie
 * @author Bora Yuksel (updated Java code)
 * @version 2.0 01/17/21
 */

import java.io.*;      // System.in and System.out
import java.util.*;      // Stack

class MyLibCharacter {
	private Character character;

	public MyLibCharacter (char ch) {
		character = Character.valueOf (ch);
	}

	public char charValue () {
		return character.charValue ();
	}

	public String toString () {
		return "" + character;
	}
}

public class MyLib extends Object {
	/**
	 * A constant for passing in the baseout function.to display digits in hex.
	 */
	static final int HEX = 16;

	/**
	 * A constant for passing in the baseout function.to display digits 
	 * in decimal.
	 */
	static final int DECIMAL = 10;

	/**
	 * A constant for converting ASCII characters to a numeric value.
	 */
	static final long ASCII_ZERO = 48;

	private static final int CR = 13;          // Carriage Return (ASCII)
	private static final int MAX_LINE_LENGTH = 80;
	private static final int EOF = -1;
	// Max string length for input.
	private static final char digits[] = 
		new String("0123456789abcdefghijklmnopqrstuvwxyz").toCharArray();

	private static Stack<MyLibCharacter> InStream = new Stack<MyLibCharacter> ();


	/**
	 * Reads in a decimal number from the input stream.
	 *
	 * @return  <code>long</code> 
	 */
	public static long decin (Reader is) {

		int character;         // Character from user.
		long digit;             // Each digit as it is found.
		long sum = 0;

		// Input terminates with a non-digit character.
		if ((character = getchar (is)) == EOF) 
			return EOF;

		while (Character.isDigit (character)) {

			// Change from ASCII to numeric value.
			digit = character & ~ASCII_ZERO;

			// Accumulate number.
			sum *= DECIMAL;
			sum += digit;
			character = getchar (is);
		}

		// Return non-digit back to input stream.
		ungetc ((char) character);

		return sum;   
	}

	public static void decout (long number) {
		baseout( number, DECIMAL );
	}


	/**
	 * Takes in a positive or negative number and displays in a given
	 * base to the screen.
	 *
	 * @param    Numeric positive or negative value to be displayed.
	 * @param    Base to display number in.
	 */
	public static void baseout (long number, long base) {

		if (number < 0) {
			fputc('-', System.out);
			posbaseout (-number, base);               
		}

		else {
			posbaseout (number, base);
		}
	}


	/**
	 * Takes in a positive number and base to be displayed to the screen.
	 *
	 * @param    A positive numeric value to be displayed.
	 * @param    Base to display number in.
	 */
	private static void posbaseout (long number, long base) {

		long quotient;                  // Quotient of div by 10.
		long remainder;                 //  Remainder of div by 10.

		// Remove one digit from number.
		//      digit (remainder) is for output,
		//      the rest of the number (quotient) 
		//      still needs to be printed.

		remainder = number % base;
		quotient = number / base;

		// Call function to output remaining digits, if any.
		if (quotient != 0)
			posbaseout (quotient, base);

		// Output digit.
		fputc (digits [(int) remainder], System.out);
	}


	/**
	 * Takes in a positive number and displays it in hex.
	 *
	 * @param    A positive numeric value to be displayed in hex.
	 */
	public static void hexout (long number) {

		// Output "0x" for hexidecimal.
		writeline ("0x", System.out);
		posbaseout (number, HEX);
	}


	/**
	 * Clears the input stream buffer until the '\n' is
	 *   encountered or has already been read indicated by
	 *   the LastChar parameter..
	 *
	 * @param    Last character taken from the input stream.
	 */
	public static void clrbuf (char LastChar, Reader is) {
		// Check for buffer already being empty.
		//   then remove all characters from the buffer
		//   until empty.
		while (LastChar != '\n') {
			LastChar = (char) getchar (is);

			if (LastChar == EOF)
				return;
		}
	}

	/**
	 * Returns a character from the input stream.
	 *
	 * @return  <code>char</code> 
	 */
	public static int getchar (Reader is) {

		char ToRet = '\0';

		// Check our local input stream first.
		//   If it's empty read from System.in
		if (InStream.isEmpty ()) {

			try {
				// Java likes giving the user the
				// CR character too. Dumb, so just 
				// ignore it and read the next character
				// which should be the '\n'.                  
				ToRet = (char) is.read ();
				if (ToRet == CR)
					ToRet = (char) is.read ();

				// check for EOF
				if ((long) ToRet == 0xFFFF)
					return EOF;
			}

			// Catch any errors in IO.
			catch (EOFException eof) {

				// Throw EOF back to caller to handle
				return EOF;
			}

			catch (IOException ioe) {

				writeline ("Unexpected IO Exception caught!\n",
						System.out);
				writeline (ioe.toString (), System.out);
			}

		}

		// Else just pop it from the InStream.
		else
			ToRet = ((MyLibCharacter) InStream.pop ()).charValue ();

		return ToRet;
	}


	/**
	 * Displays a single character.
	 *
	 * @param    Character to display.
	 */
	public static void fputc(char CharToDisp, PrintStream stream) {

		// Print a single character.
		stream.print (CharToDisp);   

		// Flush the system.out buffer, now. 
		stream.flush ();
	}


	/**
	 * Prints out a newline character.
	 *
	 */
	public static void newline () {
		fputc ('\n', System.out);
	}


	/**
	 * Prints out a string.
	 *
	 * @param    A string to print out.
	 * @return     <code>long</code> The length of the string.
	 */
	public static long writeline (String string, PrintStream stream) {
		for (int index = 0; index < (string.length ()); index++)
			fputc (string.charAt (index), stream);

		return string.length ();
	}

	/**
	 * Returns a string from the user.
	 *
	 * @return     <code>String</code> 
	*/
	public static String getline(Reader is) {

			char buffer[] = new char[MAX_LINE_LENGTH];          
			int input;           
			int index = 0; 


			do {
					input = getchar (is);
					buffer[index] = (char) input;
					index++;

			} while (input != '\n' && (index < MAX_LINE_LENGTH-1));

			return new String (buffer, 0, index - 1);              
	}


	public static String namein (Reader is) {

			char buffer[] = new char[MAX_LINE_LENGTH];          

			int character;         // input from user
			int index = 0;          // index into buffer

			while (Character.isLetterOrDigit (character = getchar (is))
					|| character == '_')
							buffer[index++] = (char) character;

			/* put non-digit back into stdin */
			ungetc ((char) character);

			return new String (buffer, 0, index);              
	}

	/**
	 * Places back a character into the input stream buffer.
	 *
	 * @param    A character to putback into the input buffer stream.
	 */
	public static void ungetc (char ToPutBack) {

		// Push the char back on our local input stream buffer.
		InStream.push (new MyLibCharacter (ToPutBack));
	}
}