import java.util.*;
import java.io.*;
 
/**
* 
*
*@param
*@param 
*@return 
*/

public class SpellChecker{
	
    private HashMap<String, Double> dictionary = new HashMap<String, Double>();
	
	private static String dictLocation = "ScrabbleWords.txt";
	private static String commonLocation = "20k.txt";
	private static double NUM_COMMON_WORDS = 40000;
	
	public SpellChecker() throws FileNotFoundException{
		
		initHashMap();
		
	}
	
	/**
	* initializes the hashmap and gives each word a rank in terms of how common it is
	*
	*@author Samuel Feldman
	*@param none
	*@return void
	*/
	private void initHashMap() throws FileNotFoundException{
		
		File dict;
		Scanner sc;
		
			dict = new File(dictLocation);	
			sc = new Scanner(dict);		
			
			while(sc.hasNextLine()){
			
				String temp = sc.nextLine();
				
				dictionary.put(temp.toLowerCase(), NUM_COMMON_WORDS);
			
			}
		
			
			dict = new File(commonLocation);	
			sc = new Scanner(dict);		
			int count = 1;
			
			while(sc.hasNextLine()){
			
				String temp = sc.nextLine();
				
				dictionary.put(temp.toLowerCase(), (double)(count)*((temp.length() - 4)*(temp.length() - 4) + 1));
			
				count ++;
				
			}
		
		

		
	}
	

	/**
	* This function computes the levenshtein distance between two words. 
	* We use the results to rank suggestions of words.
	*
	*@author Samuel Feldman
	*@param <word1> the first word to compare
	*@param <word2> the second word to compare
	*@return The levenshtein distance between the two words
	*/
	private static int Ldistance(String word1, String word2){
		
		if(word1.length() == 0){
			return word2.length();
		}
		else if(word2.length() == 0){
			return word1.length();
		}
		else if(word1.charAt(0) == word2.charAt(0)){
			
			StringBuffer first = new StringBuffer(word1);
			StringBuffer second = new StringBuffer(word2);
			
			first.delete(0,1);
			second.delete(0, 1);
			
			return Ldistance(first.toString(), second.toString());
			
		}
		else{
			
			StringBuffer first = new StringBuffer(word1);
			StringBuffer second = new StringBuffer(word2);
			
			first.delete(0,1);
			second.delete(0, 1);
			
			int levs[] = {Ldistance(first.toString(), word2), Ldistance(word1, second.toString()), Ldistance(first.toString(), second.toString())};
			
			int min = levs[0];
			
			for(int i = 1; i < 3; i++){
				if(levs[i] < min){
					min = levs[i];
				}
			}
			
			return 1 + min;
			
		}
		
		
	}


	/**
	* function that generates all possiblities for words to be cross referenced with dictionary words
	*
	*@author Samuel Feldman
	*@param word to generate possibilities 
	*@return An arraylist of possible words based on a missplled word
	*/
	private static ArrayList<String> generate_possibilities(String word){
		
		ArrayList<String> suggest = new ArrayList<String>();
		
		// generate deletions
		for(int i = 0; i < word.length(); i++){
			StringBuffer temp = new StringBuffer(word);
			
			temp.delete(i, i+1);
			
			suggest.add(temp.toString());
		}
		
		// generate insertions
		char letters[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		for(char c : letters){
			for(int j = 0; j <= word.length(); j ++){
				
			StringBuffer temp = new StringBuffer(word);
			
			temp.insert(j, c);
			
			suggest.add(temp.toString());

			}
			
		}
		
		// generate swaps
		for(int k = 0; k < word.length() - 1; k++){
			
			StringBuffer temp = new StringBuffer(word);
			
			String f = String.valueOf(temp.charAt(k));
			String s = String.valueOf(temp.charAt(k + 1));
			
			temp.replace(k+1, k+2, f);
			
			temp.replace(k, k+1, s);
			
			suggest.add(temp.toString());
		}
		
		// generate wrong character possibilities
		String letters2[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		for(String s : letters2){
			for(int p = 0; p < word.length(); p++){
					
				StringBuffer temp = new StringBuffer(word);
				
				temp.replace(p, p + 1, s);
				
				suggest.add(temp.toString());

			}
		}
		
		
		// generate space insertions
		for(int l = 1; l < word.length(); l++){
			
			StringBuffer temp = new StringBuffer(word);
			
			temp.insert(l, ' ');
			
			suggest.add(temp.toString());
			
		}
		
		
		return suggest;
		
	}
	
	
	/**
	* this is what the document class will pass to the gui to ask what the user wants to replace the misspelled word with
	*
	*@author Samuel Feldman
	*@param word to spellcheck
	*@return return a list of words from the dictionary that to suggest
	*/
	public ArrayList<String> spellCheck(String word){
		
		ArrayList<String> suggestions = new ArrayList<String>();
		ArrayList<Double> ranks = new ArrayList<Double>();
		ArrayList<String> possibilities = generate_possibilities(word.toLowerCase());
		
		for(int i = 0; i < possibilities.size() - word.length(); i ++){
			
			if(dictionary.containsKey(possibilities.get(i))){
				
				double rank = dictionary.get(possibilities.get(i));
				
				if(suggestions.size() == 0 || rank == NUM_COMMON_WORDS){
					suggestions.add(possibilities.get(i));
					ranks.add(rank);
				}
				else{
					int count = 0;
					while(count < ranks.size() && rank > ranks.get(count)){
						count++;
					}
					
					suggestions.add(count, possibilities.get(i));
					ranks.add(count, rank);
				}
				
			}
			
		}
		
		for(int i = possibilities.size() - word.length() + 1; i < possibilities.size(); i++){
						
			StringBuffer first = new StringBuffer();
			StringBuffer second = new StringBuffer(possibilities.get(i));
			
			for(int j = 0; j < i - possibilities.size() + word.length(); j ++){
				
				first.append(second.charAt(0));
				second.deleteCharAt(0);
				
			}
			
			second.deleteCharAt(0);
			
			if(dictionary.containsKey(first.toString()) && dictionary.containsKey(second.toString())){
								
				double rank = (dictionary.get(first.toString()) + dictionary.get(second.toString()))/2;
				
				if(suggestions.size() == 0 || rank == NUM_COMMON_WORDS){
					suggestions.add(possibilities.get(i));
					ranks.add(rank);
				}
				else{
					int count = 0;
					while(count < ranks.size() && rank > ranks.get(count)){
						count++;
					}
					
					suggestions.add(count, possibilities.get(i));
					ranks.add(count, rank);
				}
								
			}
						
		}		
		
		return suggestions;
	}
	
	
	public void addWordSession(String word){
		
		dictionary.put(word.toLowerCase(), (double)0);
		
	}	
	
	public boolean isWord(String word){
		if(dictionary.containsKey(word.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public static void main(String[] args){
		
		String test = "Iwas";
		
		try{
		
			SpellChecker sp = new SpellChecker();
			ArrayList<String> possibilities = sp.spellCheck(test);
			
			for(int i = 0; i < possibilities.size(); i ++){
					
					System.out.println(possibilities.get(i));
					
					
				}
				
				System.out.println(possibilities.size());
		}
		catch(FileNotFoundException e){
			System.out.println("oopsie daisy");
		}
		
		
	
		
	}

}