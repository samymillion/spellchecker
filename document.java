import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

/**
 * This is a sample class that demonstrates Javadoc comments.
 * 
 * @author Kevin Li
 * @version 1.0
 */

interface dInterface {
    String readDoc();
    void addToDict(String word, boolean permanent);
    void save() throws FileNotFoundException, IOException;
    int getNumChars();
    void setNumChars(int num);
    int getNumWords();
    void setNumWords(int num);
    int getNumMisspellings();
    boolean getIsSaved();
}

/**
    This Class is used to hold the word data of the User's chosen document file. 
    It also keeps track of the number of words, characters and misspellings
 */

public class document implements dInterface {

    private int numWords = 0;
    private int numChars = 0;
    private int numMisspellings = 0;
    private boolean isSaved = false;
    private String fileName = "";

    private String dContent = "";
    private ArrayList<String> wordList = new ArrayList<>();

    private SpellChecker check = new SpellChecker();
    static Main mainCheck = new Main();

    /**
    This is an Default Empty Constructor Method that the GUI initially creates an object for
    */
    public document() throws FileNotFoundException{
    }

    /**
    This is a Constructor Method that accepts a string file name, reads from the file, adds
    the text onto both String variable "dcontent" and arrayList "wordList", and increments
    integer variables "numWords", "numChars" and "numMisspellings"

    @param nameOfFile - String name of the User's selected text file
     */
    public document(String nameOfFile) throws FileNotFoundException, IOException{

        fileName = nameOfFile;


            BufferedReader reader1 = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = reader1.readLine()) != null){
                String[] words = line.split("\\s+");
                for(String word : words){
                    if(word.equals("")){
                        wordList.add("\n");
                    }
                    else{
                        word = word.replace("’", "'");
                        wordList.add(word + " ");
                        numWords++;
                        for(int i = 0; i < word.length(); i++){
                            numChars++;
                        }
                    }
                }
                wordList.add("\n");
            }
            reader1.close();

        for(String word: wordList){
            dContent = dContent + word;
        }
    }

    /**
     * Setter Method for the object's file name 
     * @param inputFileName
     */

    public void setFileName(String inputFileName){
        fileName = inputFileName;
    }

    /**
     * This method returns the document's text content in the form of a string
     * 
     * @return dContent - The string form of the document's text
     */
    @Override
    public String readDoc() {
        return dContent;
    }

    /**
     * This method adds a string input "word" to either a temporary dictionary or
     * permanent dictionary depending on the boolean input variable "permanent"
     * @param word
     * @param permanent
     */
    @Override
    public void addToDict(String word, boolean permanent) {
        if(permanent == true){
            check.addWordSession(word);
            mainCheck.addWord(word);
        }
        else{ // non permanent
            check.addWordSession(word);
        }    
    }

    /**
     * This method rewrites a text file with the name "fileName" with the contents of 
     * "wordList"
     */
    @Override
    public void save() throws FileNotFoundException, IOException{

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"));
    
        writer.write(dContent);
 
        writer.flush();
        writer.close();
    }
    
    /**
     * This method returns the number of characters in the text file
     * @return numChars
     */
    @Override
    public int getNumChars() {
        return numChars;
    }

    /**
     * This method returns the number of words in the text file
     * @return numWords
     */
    @Override
    public int getNumWords() {
        return numWords;
    }
    /**
     * This method sets the number of words in the text file using the input variable "num"
     * @param num - the updated number of words
     */
    public void setNumWords(int num){
        numWords = num;
    }
    /**
     * This method sets the number of characters in the text file using the input variable "num"
     * @param num - the updated number of characters
     */
    public void setNumChars(int num){
        numChars = num;
    }

    /**
     * This method acts like a construtor method but updates the variables and arrayList using the
     * input string variable. 
     * @param replacementString
     */
    public void setDContent(String replacementString){
        dContent = "";
        numChars = 0;
        numWords = 0;
        numMisspellings = 0;
        wordList.clear();
        dContent = replacementString;

        try (BufferedReader reader3 = new BufferedReader(new StringReader(dContent))) {
            String line;
            while((line = reader3.readLine()) != null){
                String[] words = line.split("\\s+");
                for(String word : words){
                    if(word.equals("")){
                        wordList.add("\n");
                    }
                    else{
                        word = word.replace("’", "'");
                        wordList.add(word + " ");
                        numWords++;
                        for(int i = 0; i < word.length(); i++){
                            numChars++;
                        }
                    }
                }
                wordList.add("\n");
            }
            reader3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method scans through "wordList" and counts the total number of misspelled
     * words in the text document using a SpellChecker method
     * @return numMispellings
     */
    @Override
    public int getNumMisspellings() {
        for(String i: wordList){
            if(check.isWord(i) == false){
                numMisspellings++;
            }
        }
        return numMisspellings;
    }   

    /**
     * This method returns the boolean value isSaved
     * @return isSaved
     */
    @Override
    public boolean getIsSaved() {
        return isSaved;
    }

    /**
     * This method sets the boolean value isSaved
     * @param saved
     */
    public void setIsSaved(boolean saved){
        isSaved = saved;
    }

    
}
