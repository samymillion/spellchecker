import java.io.*;
import java.util.HashMap;
import java.util.Scanner;


interface Application {
	void initialize();
    void run();
    void setFileName(String fileName);
    String getFileName();
    void initHashMap();	
}

/**
 * Main application class that implements the Application interface.
 * This class is responsible for initializing and running the application,
 * managing user input, and handling the main application loop.
 */
public class Main implements Application{

	public static HashMap<String, String> dictionary = new HashMap<String, String>();
    document doc;
    SpellChecker spellchecker;
    GUI gui;
    String fileName;
    private static String csvFile = "C:/Users/Samuel Gebretsion/eclipse-workspace/CS2212/src/sc/wordlist.csv";
    
    /**
     * Main method to start the application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args){
        Main application = new Main();
        application.initialize();
       // application.run();
    }
    
    /**
     * Initializes the application by setting up necessary components.
     * This includes creating a new dictionary, document, spellchecker, and GUI,
     * as well as acquiring the file name from the user.
     */
    public void initialize() {
  
      /*  Scanner input = new Scanner(System.in);
        System.out.println("Enter file name: ");
        setFileName(input.nextLine());
        input.close(); */
        
    	setFileName("C:/Users/Samuel Gebretsion/eclipse-workspace/CS2212/src/sc/test.txt");
        initHashMap();
        initDoc(doc);
        addWord("sgebrets");
        
        //spellchecker = new SpellChecker();
        gui = new GUI();
    }
    
    /**
     * Runs the main function loop of the application.
     * Continuously checks for certain conditions to maintain the application's state.
     */
    public void run() {
        // Main function loop
        while(true) {
            // Application code
            
            // Break loop when prompted by GUI
            if (true) {
                break;
            }
        }
    }
    
    /**
     * Sets the file name for the document.
     *
     * @param fileName The name of the file to be used.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Gets the current file name of the document.
     *
     * @return The current file name.
     */
    public String getFileName() {
        return this.fileName;
    }
    
	/**
	* initializes the hashmap
	*
	*@return void
	*/
	public void initHashMap(){
		
		File dict;
		Scanner sc;
		
		try{
			
			dict = new File(csvFile);	
			sc = new Scanner(dict);		
			
			while(sc.hasNextLine()){
				String temp = sc.nextLine();
				
				dictionary.put(temp, temp);
			}
		}
		catch(FileNotFoundException e){	
		}
		
		
		//debugging hashmap
		for (String dictWord: dictionary.keySet()) {
		    String value = dictionary.get(dictWord).toString();
		    System.out.println(value);
		}
	}

	
    /**
     * Initializes a new document with the specified file name.
     *
     * @param fileName The name of the file for the new document.
     */
    public void initDoc(document doc) {
    	//new document(fileName);
    }

    /**
     * Adds a word to the spellchecker's dictionary.
     *
     * @param word The word to be added.
     */
    public static void addWord(String word) {
    	try {
            PrintWriter writer = new PrintWriter(new FileWriter("C:/Users/Samuel Gebretsion/eclipse-workspace/CS2212/src/sc/wordlist.csv", true));
            writer.print(word);
            writer.print(',');
    		writer.close();
    	}
    	catch(IOException e){	
		}
    	
    	dictionary.put(word, word);
    }

}
