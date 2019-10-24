
package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/

		Scanner scan = new Scanner(new File(docFile));
		HashMap<String, Occurrence> hash = new HashMap<String, Occurrence>();

		while (scan.hasNext()) {
			String word = scan.next();
			word = this.getKeyword(word);

			if (word != null && !noiseWords.contains(word)) {

				if (hash.containsKey(word)) {
					Occurrence tmp = hash.get(word);
					tmp.frequency++;
					hash.put(word, tmp);

				} else {
					Occurrence occur = new Occurrence(docFile, 1);
					hash.put(word, occur);
				}
			}
		}

		return hash;

	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		/** COMPLETE THIS METHOD **/

		Set<String> set = kws.keySet();
		Iterator<String> iter = set.iterator();

		while (iter.hasNext()) {

			String word = iter.next();
			Occurrence occ = kws.get(word);
			ArrayList<Occurrence> master = keywordsIndex.get(word);
			
			if(master !=null) {
				master.add(occ);
				this.insertLastOccurrence(master);
			}
			else{
				master = new ArrayList<Occurrence>();
				keywordsIndex.put(word, master);
				master.add(occ);
				this.insertLastOccurrence(master);
			} 
		}
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!' NO
	 * OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be
	 * stripped So "word!!" will become "word", and "word?!?!" will also become
	 * "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/

		// String punc = ".,?:;!";
		word = word.toLowerCase();
		for (int i = 0; i < word.length(); i++) {
			if (!Character.isLetter(word.charAt(i))) {
				if ((i + 1 < word.length())) {
					if (Character.isLetter(word.charAt(i + 1))) { // maybe this should be i +1
						return null;
					} else {
						String sub = word.substring(i, word.length());
						for (int j = 0; j < sub.length(); j++) {
							if (Character.isLetter(sub.charAt(j))) {
								return null; // or just break instead
							} else if (j == sub.length() - 1) {
								word = word.substring(0, i); // mayve this should be i -1
								break;
							}
						}
					}
				} else {
					String sub = word.substring(i, word.length());
					for (int j = 0; j < sub.length(); j++) {
						if (Character.isLetter(sub.charAt(j))) {
							return null; // or just break instead
						} else if (j == sub.length() - 1) {
							word = word.substring(0, i); // mayve this should be i -1
							break;
						}
					}
				}
			}
		}

		if (word.length() <= 0) {
			return null;
		}
		if (noiseWords.contains(word)) {
			return null;
		}
		return word;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/

		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		if (occs.size() < 2)
			return null;

		int low = 0;
		int high = occs.size() - 2;
		int curLast = occs.get(occs.size() - 1).frequency;
		int mid = 0;
		ArrayList<Integer> intList = new ArrayList<Integer>();

		while (high >= low) {
			mid = ((low + high) / 2);
			int data = occs.get(mid).frequency;
			intList.add(mid);
			if (data < curLast) {
				high = mid - 1;
			} else if (data > curLast) {
				low = mid + 1;
				if (high <= mid)
					mid = mid + 1;
			} else if (data == curLast) {
				break;
			}
		}

		intList.add(mid);
		Occurrence temp = occs.remove(occs.size() - 1);
		occs.add(intList.get(intList.size() - 1), temp);

		return intList;
		
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies.
	 * 
	 * Note that a matching document will only appear once in the result.
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. That is,
	 * if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all,
	 * result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Occurrence> docs = new ArrayList<Occurrence>();
		ArrayList<String> top5 = new ArrayList<String>();
		int timesAdded = 0;
		int occured = 0;

		if (keywordsIndex.containsKey(kw1)) {
			if (keywordsIndex.containsKey(kw2)) {
				for (int i = 0; i < keywordsIndex.get(kw1).size() && i < 5; i++) {
					docs.add(keywordsIndex.get(kw1).get(i));
				}
				for (int i = 0; i < keywordsIndex.get(kw2).size() && i < 5; i++) {
					for (int j = 0; j < docs.size() && timesAdded < 1; j++) {
						if (docs.get(j).document == keywordsIndex.get(kw2).get(i).document) {
							occured++;
							if (keywordsIndex.get(kw2).get(i).frequency > docs.get(j).frequency) {
								docs.remove(j);
								for (int k = 0; k < docs.size(); k++) {
									if (keywordsIndex.get(kw2).get(i).frequency >= docs.get(k).frequency) {
										docs.add(k, keywordsIndex.get(kw2).get(i));
										timesAdded++;
										break;
									}
								}
								if (timesAdded < 1 && docs.size() < 5) {
									docs.add(keywordsIndex.get(kw2).get(i));
								}
							}
						}
					}
					for (int j = 0; j < docs.size() && timesAdded < 1 && occured < 1; j++) {
						if (keywordsIndex.get(kw2).get(i).frequency >= docs.get(j).frequency) {
							docs.add(j, keywordsIndex.get(kw2).get(i));
							timesAdded++;
							break;
						}
					}
					if (timesAdded < 1 && occured < 1 && docs.size() < 5) {
						docs.add(keywordsIndex.get(kw2).get(i));
					}
				}
			} else if (keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)) {
				for (int i = 0; i < keywordsIndex.get(kw1).size() && i < 5; i++) {
					docs.add(keywordsIndex.get(kw1).get(i));
				}
			}
		} else {
			if (keywordsIndex.containsKey(kw2)) {
				for (int i = 0; i < keywordsIndex.get(kw2).size() && i < 5; i++) {
					docs.add(keywordsIndex.get(kw2).get(i));
				}
			} else {
				return null;
			}
		}
		
		for(Occurrence occur : docs) {
			top5.add(occur.document);
		}
		
		return top5;
	}

}
