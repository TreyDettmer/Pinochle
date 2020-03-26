package edu.up.cs301.card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Steven R. Vegdahl
 * @author Trey Dettmer
 * @author Justin Lee
 * @author Alexander Mak
 * @author Kai Vickers
 * @version March 2020
 */
public enum Rank implements Serializable {
	
	// nine
	NINE {
		// override the getShortName behavior: the corresponding digit
		@Override
		public char getShortName() {
			return '9';
		}
	},
	
	// ten
	TEN,

	// jack
	JACK,

	// queen
	QUEEN,

	// king
	KING,

	// ace
	ACE,

	;

	// to satisfy the Serializable interface
	private static final long serialVersionUID = 893542931190030342L;

	public int getOrder() {
		return ordinal();
	}

	/**
	 * the "long name" of the rank--fully spelled out
	 *
	 * @return
	 * 		the rank's long name
	 */
	public String getName() {
		// use the print-string, with the first character upper case, and the
		// rest lower case
		String s = this.toString();
		return s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();
	}

	/**
	 * the short (one-character) name of a rank
	 * 
	 * @return
	 * 		the rank's short name
	 */
	public char getShortName() {
		// the default is the first character of the print-string;
		// TWO through NINE override this
		return this.toString().charAt(0);
	}
	
	// an array to help us convert from character to rank; it will be initialized
	// the first time the fromChar method is called
	private static ArrayList<Character> rankChars = null;
	
	/**
	 * convert a character into a rank
	 * 
	 * @param c
	 * 		the character
	 * @return
	 * 		the rank that corresponds to that character, or null if the character
	 * 		does not denote a rank
	 */
	public static Rank fromChar(char c) {
		// if the helper-array is null, create it
		if (rankChars == null) {
			initRankChars();
		}
		// find the numeric rank of the card denoted by the character
		int idx = rankChars.indexOf(Character.toLowerCase(c));
		// return null if the character did not denote a rank
		if (idx < 0) return null;
		// return the corresponding rank
		return Rank.values()[idx];
	}
	
	/**
	 * method that initializes the helper-array for use by the 'fromChar' method
	 */
	private static void initRankChars() {
		// the list of ranks, in numeric order
		Rank[] vals = Rank.values();

		// create the list of characters
		rankChars = new ArrayList<Character>();

		// initialize the list with the characters, in rank-order
		for (Rank r : vals) {
			rankChars.add(Character.toLowerCase(r.getShortName()));
		}
	}

}
