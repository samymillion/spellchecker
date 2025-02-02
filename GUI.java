import javax.swing.*;
import java.util.*;
import java.util.Timer;

import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main GUI class for the Spell Checker application. 
 * Contains the logic for interacting with the user and saving/loading files
 * Acts as entry point for entire application
 * 
 * @author Modin Wang
 */

class GUI extends JFrame implements ActionListener {

    private SpellChecker spellChecker;
    private JTextArea checkArea;
    private JTextPane textArea;
    private JFrame frame;
    private document doc;
    private ArrayList<Integer> mispelledWordsIndex;
    private Timer highlightTimer;
    private JPanel textAreaWrapper;
    private JPanel checkAreaWrapper;

     /**
     * Constructor for the GUI class.
     * Initializes instance variables and handles loading in graphics
     * 
     */
    GUI() {
        // initialize arraylist to contain all the indices of misspelled words
        mispelledWordsIndex = new ArrayList<Integer>();

        // Initialize highlight timer 
        highlightTimer = new Timer();

        // Initialize document object
        try {
            doc = new document();
        }
        catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "File not found" + e.getMessage());
        }
        catch(Exception ex) {
            JOptionPane.showMessageDialog(frame, "IO error detected" + ex.getMessage());
        }
        
        frame = new JFrame("Spell Checker");
        frame.setLayout(new BorderLayout());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!doc.getIsSaved() && doc.readDoc().length() != 0) {
                    int choice = JOptionPane.showConfirmDialog(
                            frame,
                            "You have unsaved changes. Do you want to save before closing?",
                            "Unsaved Changes Warning",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );

                    // if user selects save then save here
                    if (choice == JOptionPane.YES_OPTION) {
                        saveFile();
                    } else if (choice == JOptionPane.CANCEL_OPTION) {
                        return; // Cancel closing the window
                    }
                }

                // Close the window if no unsaved changes or user chose to close without saving
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.dispose();
            }
        });

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error setting theme: " + ex.getMessage());
        }

        setupUI();
        
    }
    
    /**
     * Sets up the user interface components.
     * Creates spellcheck object and logic for handling button presses and default behaviour of UI elements
     */
    private void setupUI() {

        //create spellchecker object 
        try {
            spellChecker = new SpellChecker();
        }
        catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Error loading dictionary" + e.getMessage());
        }
        
        // create JPanels to wrap our text area and suggestion area
        textAreaWrapper = new JPanel(new BorderLayout());
        checkAreaWrapper = new JPanel(new BorderLayout());

        // create our text area and make it editable 
        textArea = new JTextPane();
        textArea.setEditable(true);

        // add a mouselistener to listen for clicks in text area
        WordClickListener wordClickListener = new WordClickListener();
        textArea.addMouseListener(wordClickListener);

        // Add a keyboard listener to listen for any manual corrections
        textArea.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                // Cancel the current timer and start a new one
                highlightTimer.cancel();
                highlightTimer = new Timer(); // Reset the timer for subsequent use

                highlightTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        // Clear highlights first
                        clearHighlighting();
                        highlightMisspelledWords();
                        
                        // Update the document's text value
                        doc.setDContent(textArea.getText());
                    }
                }, 1000); // Delay for one second
            }
        });



        // Create spell check suggestion preview
        checkArea = new JTextArea("No Spelling Suggestions");
        checkArea.setEditable(false);
        checkAreaWrapper.add(checkArea);

        // Add the 
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Spell Check Suggestions");
        checkAreaWrapper.setBorder(titledBorder);


        JScrollPane scrollPane = new JScrollPane(textArea);

        textAreaWrapper.add(scrollPane, BorderLayout.CENTER);
        textAreaWrapper.setPreferredSize(new Dimension(700, 700));


        
        checkAreaWrapper.setPreferredSize(new Dimension(300, 700));


        frame.add(textAreaWrapper, BorderLayout.CENTER);
        frame.add(checkAreaWrapper, BorderLayout.EAST);

        JMenuBar menuBar = createMenuBar();

        frame.setJMenuBar(menuBar);
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * Creates the menu bar for the GUI.
     *
     * @return The constructed JMenuBar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem printMenuItem = new JMenuItem("Print");

        newMenuItem.addActionListener(this);
        openMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        printMenuItem.addActionListener(this);

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(printMenuItem);

        menuBar.add(fileMenu);

                // Help Menu
                JMenu helpMenu = new JMenu("Help");

                // About Spell Checker Option
                JMenuItem aboutMenuItem = new JMenuItem("About Spell Checker");
                aboutMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(frame, "Spell Checker v1.4\nCS2212A\nDeveloped by Group 19", "About Spell Checker", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
        
                // Documentation Option
                JMenuItem documentationMenuItem = new JMenuItem("Documentation");
                documentationMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        JOptionPane.showMessageDialog(frame, "Visit our website for documentation.", "Documentation", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
        
                // Credits Option
                JMenuItem creditsMenuItem = new JMenuItem("Credits");
                creditsMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(frame, "Credits:\n- Modin Wang\n- Kevin Li\n- Samuel Feldman\n- Samuel Million Gebretsion\n- Mohammed Elabed", "Credits", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                
                // how to use menu item
                JMenuItem howToUseMenuItem = new JMenuItem("How to Use");
                howToUseMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(frame, "Welcome to the spellchecker help page. Below is a table of contents.\r\n" + //
                                "\r\n" + 
                                "1. Workflow and Functionailty.\r\n" + 
                                "2. Adding New Words to the Dictionary.\r\n" + 
                                "\r\n" + 

                 
                                "1. Workflow and Functionailty.\r\n" + 
                                "\r\n" + 
                                "\tWhen you launch the spellchecker you will be met with a few simple options in the top left of the window. \nTo open a new document select open, and select your .txt document using the file explorer. \nOnce you open a document it will be automatically spellchecked with misspelled words highlighted in red.\nTo correct a spelling mistake click the word and it will automatically. \nOn the side of the scren you will see several options including words from the dictionary that are close to your misspelled word,\nyou can click any of these words to replace your misspelled word. \nUnder the word suggestions there will also be options to ignore the word once, ignore for the rest of the session, or add the word to the dictionary. \nClicking any of these options will also resolve the misspelled word. \nOnce you're done editing your document you can use the options at the top left of the window to save your document.\r\n" + 
                                "\r\n" +
                                "\r\n" + 
  
                                "2. Adding Words to the Dictionary.\r\n" +
                                "\r\n" + 
                                "\tThe spellchecker works by relying on a dictionary to look up words from. \nWhen you start up the spellchecker it loads the dictionary so words can be checked. \nWhen you ignore a word for just this session it adds that word into the list of correcty spelled words stored by \nthe program but does not store it in the dictionary file so when you close and start the dictionary again that word will be spelled incorrecty again.\nIn order to avoid this you also have the option to add the word to the dictionary, \nthis has the same effect as adding the word for the session with the additional effect being \nSthat it will add this word into the dictionary that the spellchecker stores, \nso when you close and open the application again this word will be remebered by the program. \nIgnoreing once simply marks that specific word as spelled correctly and makes no other changes. \r\n"
                               ,"How to use" , JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                helpMenu.add(aboutMenuItem);
                helpMenu.add(documentationMenuItem);
                helpMenu.add(creditsMenuItem);
                helpMenu.add(howToUseMenuItem);

                menuBar.add(helpMenu);
        
        return menuBar;
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Save":
                saveFile();
                break;
            case "Print":
                printFile();
                break;
            case "Open":
                openFile();
                break;
            case "New":
                textArea.setText("");
                break;
        }


    }

    /**
     * Saves the current content of the document to a user-selected file.
     */
    private void saveFile() {
        doc.setDContent(textArea.getText());
        String currentDirectory = System.getProperty("user.dir");
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            doc.setFileName(fileChooser.getSelectedFile().getAbsolutePath());

            try {
                doc.save();
            } 

            catch (FileNotFoundException e) {
                 JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage());
            }

            catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
            }
            
            doc.setIsSaved(true);
        }
    }

    /**
     * Prints the content of the text area.
     */
    private void printFile() {
        try {
            textArea.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error printing: " + ex.getMessage());
        }
    }

    /**
     * Opens a user-selected text file and displays its content in the text area.
     * Also creates document object and invokes the highlighting of misspelled words
     */
    private void openFile() {

        // NEEDS TO HANDLE CASE WHERE INVALID FILE CHOSEN
        String currentDirectory = System.getProperty("user.dir");
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));

        int result = fileChooser.showOpenDialog(null);

        // clear any highlights just in case
        clearHighlighting();
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                doc = new document(fileChooser.getSelectedFile().getAbsolutePath());
            }
            catch(FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "File not found" + e.getMessage());
            }
            catch(IOException ex) {
                JOptionPane.showMessageDialog(frame, "IO error detected" + ex.getMessage());
            }
        
            
            // set text and highlight misspellings
            textArea.setText(doc.readDoc());

            highlightMisspelledWords();
           
        }
    }

    /**
     * Retrieves the word at the clicked offset in the text area.
     *
     * @param offset The offset in the text area.
     * @return The word at the clicked offset.
     */
    private String getClickedWord(int offset) {
        String text = textArea.getText();
        int start = offset;

        // Exclude leading punctuation
        while (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) {
            start--;
        }

        int end = offset;

        // Exclude trailing punctuation
        while (end < text.length() && Character.isLetterOrDigit(text.charAt(end))) {
            end++;
        }

        return text.substring(start, end);
    }

    /**
     * Gets the index of the first occurrence of a clicked word in the text area's content.
     *
     * @param clickedWord The word that was clicked.
     * @return The index of the first occurrence of the clicked word, or -1 if the word is not found.
     */
    private int getClickedWordIndex(String clickedWord) {
        String text = textArea.getText();
        int index = text.indexOf(clickedWord);

        if (index != -1) {
            return index;
        } else {
            return -1; // Word not found
        }
    }

    
    /**
     * ActionListener for mouse clicks on words in the text area.
     */
    private class WordClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point2D point = e.getPoint();
            int offset = textArea.viewToModel2D(point);
            String clickedWord = getClickedWord(offset);
            
            if (clickedWord != null) {
                System.out.println("Clicked word: " + clickedWord);
                int startIndex = getClickedWordIndex(clickedWord);
                if (mispelledWordsIndex.contains((Integer)startIndex)) {
                    System.out.println("Start index of clicked word and is a misspelled word: " + startIndex);

                    // Generate suggestions
                    ArrayList<String> suggestions = spellChecker.spellCheck(clickedWord);
                    System.out.println(suggestions.toString());

                    // Invoke the fillSideBar function to show user suggestions
                    fillSideBar(clickedWord, suggestions, startIndex);



                }
                else {
                    refreshDisplaySidebar();
                    checkArea = new JTextArea("Word is Correctly Spelled!");
                    checkAreaWrapper.add(checkArea);
                    System.out.println("Start index of clicked word: " + startIndex);
                }
               
               
            }
            else {

                // If no word is clicked simply display empty sidebar
                refreshDisplaySidebar();
            }
        }
    }

    /**
     * Highlights misspelled words in the text area.
     * Also populates an arraylist with the indices of the mispelled words
     * 
     */
    private void highlightMisspelledWords() {

        // reset the misspelled words index array
        mispelledWordsIndex = new ArrayList<Integer>();

        // reset save to unsaved
        doc.setIsSaved(false);
        StyledDocument doc = (StyledDocument) textArea.getDocument();
        Style style = doc.addStyle("misspelled", null);
    
        // Clear existing highlighting
        StyleConstants.setBackground(style, Color.WHITE);
        doc.setCharacterAttributes(0, doc.getLength(), style, true);
        
        // Highlight misspelled words
        String text = textArea.getText();
        for (String word : text.split("\\W+")) {
            if (!spellChecker.isWord(word) && !word.matches("\\d+")) {
                Matcher matcher = Pattern.compile("\\b" + Pattern.quote(word) + "\\b").matcher(text);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();

                    // Add start index of mispelled words to list
                    mispelledWordsIndex.add(start);

                    // Apply the red highlight style to each occurrence of the misspelled word
                    StyleConstants.setBackground(style, Color.RED);
                    doc.setCharacterAttributes(start, end - start, style, true);
                }
            }
        }
        //System.out.println(mispelledWordsIndex.toString());
    }

    /**
     * Clears any existing highlighting in the text area.
     */
    private void clearHighlighting() {
        StyledDocument doc = (StyledDocument) textArea.getDocument();
        Style style = doc.addStyle("misspelled", null);
        StyleConstants.setBackground(style, Color.WHITE);
    }

    /**
     * Fills the sidebar with suggestions and options related to a misspelled word. 
     * Build buttons and define logic for buttons
     *
     * @param misspelledWord The misspelled word.
     * @param suggestions    The suggestions for the misspelled word.
     * @param start          The starting index of the misspelled word.
     */
    private void fillSideBar(String misspelledWord, ArrayList<String> suggestions, int start) {
        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new BoxLayout(wordPanel, BoxLayout.Y_AXIS));
    
        // Label to display the misspelled word
        JLabel wordLabel = new JLabel(misspelledWord, SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        wordPanel.add(wordLabel);
    
        // Panel for suggestion buttons with FlowLayout
        JPanel suggestionPanel = new JPanel();
        suggestionPanel.setLayout(new FlowLayout());

        if (!suggestions.isEmpty()) {

            
            // Add "Suggestions" label
            JLabel suggestionsLabel = new JLabel("Suggestions:", SwingConstants.CENTER);
            suggestionPanel.add(suggestionsLabel);
    
            // Add suggestion buttons based on the provided ArrayList
            int index = 0;
            int size = suggestions.size();
            while (index < size && index < 4) {
                JButton suggestionButton = new JButton((index + 1) + ". " + suggestions.get(index));
                suggestionPanel.add(suggestionButton);

                suggestionButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Access the button's text to identify which button was pressed
                        JButton clickedButton = (JButton) e.getSource();

                        // Turn the text to a string buffer so replacement can be done
                        StringBuffer tempStringToReplace = new StringBuffer(textArea.getText());
                        int end = start + misspelledWord.length() + 1;

                        // Store current caret position
                        int caretPosition = textArea.getCaretPosition();

                        // Replace the word in the string
                        tempStringToReplace.replace(start, end, clickedButton.getText().substring(3));


                        // Update the text area and the document object to reflect the user defined change
                        textArea.setText(tempStringToReplace.toString());
                        doc.setDContent(textArea.getText());

                        // Restore caret position
                        if (caretPosition <= textArea.getDocument().getLength()) {
                            textArea.setCaretPosition(caretPosition);
                        }
                        // clear highlights and rerun highlight misspellings
                        clearHighlighting();
                        highlightMisspelledWords();

                        // also clear side panel 
                        refreshDisplaySidebar();
                    }
                });

                index++;
            }
    
            // Add suggestionPanel to wordPanel
            wordPanel.add(suggestionPanel);
    
            
    
            
        }
        else {
            JLabel suggestionsLabel = new JLabel("NO SUGGESTIONS FOUND", SwingConstants.CENTER);
            suggestionsLabel.setFont(new Font("Arial", Font.BOLD, 12));
            suggestionPanel.add(suggestionsLabel);

            // Add suggestionPanel to wordPanel
            wordPanel.add(suggestionPanel);

        }

        // Ignore Once & Always Buttons
        JButton ignoreOnceButton = new JButton("Ignore In This Document");
        JButton ignoreAlwaysButton = new JButton("Ignore Forever");

        ActionListener ignoreButtonActions = new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();

                    if (command == "Ignore In This Document") {
                            spellChecker.addWordSession(misspelledWord);
                        }
                    
                    else if (command == "Ignore Forever"){
                            addWord(misspelledWord);
                        }

                    // clear highlights and rerun highlight misspellings
                    clearHighlighting();
                    highlightMisspelledWords();

                    // also clear side panel 
                    refreshDisplaySidebar();
                }
            };
        ignoreAlwaysButton.addActionListener(ignoreButtonActions);
        ignoreOnceButton.addActionListener(ignoreButtonActions);

           
    
        // Create a panel for ignore buttons with FlowLayout
        JPanel ignorePanel = new JPanel();
        ignorePanel.setLayout(new FlowLayout());
    
        ignorePanel.add(ignoreOnceButton);
        ignorePanel.add(ignoreAlwaysButton);
    
        // Add ignorePanel to wordPanel
        wordPanel.add(ignorePanel, BorderLayout.PAGE_END);

        refreshDisplaySidebar();
    
        // Add the wordPanel to the right panel
        checkAreaWrapper.add(wordPanel);
    

    }

    /**
     * Adds a word to the user dictionary file directly, this ensures that the word will forever be recognized as spelled correctly
     *
     * @param word The word to be added to the dictionary.
     */
    public void addWord(String word) {
    	try {
            PrintWriter writer = new PrintWriter(new FileWriter("ScrabbleWords.txt", true));
            writer.print(word + "\n");
    		writer.close();
    	}
    	catch(Exception e){	
            JOptionPane.showMessageDialog(frame, "Error with add word to dictionary" + e.getMessage());
		}
    	spellChecker.addWordSession(word);
    }

    /**
     * Clears the right panel to display new misspelled words.
     */
    private void refreshDisplaySidebar() {
        // Clear the rightPanel before adding new misspelled words
        checkAreaWrapper.removeAll();
        // Revalidate and repaint the rightPanel
        checkAreaWrapper.revalidate();
        checkAreaWrapper.repaint();
    }

    /**
     * The main method that launches the GUI application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }


}
