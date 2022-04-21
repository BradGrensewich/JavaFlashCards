package flashcards;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class UI {
    private Scanner scanner;
    private Map<String, Card> deck;
    private Set<String> fronts;
    private Set<String> backs;
    private StringBuilder log;
    private String fileOut;

    //creates a UI with a file to export but no import file
    public UI(String fileOut) {
        this.scanner = new Scanner(System.in);
        this.deck = new LinkedHashMap<>();
        this.fronts = new HashSet<>();
        this.backs = new HashSet<>();
        this.log = new StringBuilder();
        this.fileOut = fileOut;
    }
    //creates a UI with an imported file and an export file
    public UI(String fileIn, String fileOut){
        this(fileOut);
        importFile(fileIn);
    }

    //creates a UI with an imported file but no exported file
    public UI(String fileIn, boolean imported) {
        this("");
        importFile(fileIn);
    }
    //creates a UI with no initial files
    public UI() {
        this("");
    }

    public void menu() {
        while (true) {
            printlnAndLog("Input the action " +
                    "(add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String option = getString();
            switch (option) {
                case "add":
                    makeCard();
                    printlnAndLog("");
                    break;
                case "remove":
                    removeCard();
                    printlnAndLog("");
                    break;
                case "import":
                    importNewFile();
                    printlnAndLog("");
                    break;
                case "export":
                    exportToNewFile();
                    printlnAndLog("");
                    break;
                case "ask":
                    printlnAndLog("How many times?");
                    practice(getInt());
                    printlnAndLog("");
                    break;
                case "log":
                    saveLog();
                    printlnAndLog("");
                    break;
                case "hardest card":
                    printHardest();
                    printlnAndLog("");
                    break;
                case "reset stats":
                    resetStats();
                    printlnAndLog("");
                    break;
                case "exit":
                    printlnAndLog("Bye bye!");
                    if(!this.fileOut.isEmpty()) {
                        exportDeck(this.fileOut);
                    }
                    return;
                default:
                    printlnAndLog("Command not recognized.\n");
                    break;
            }
        }
    }

    private void saveLog() {
        //the real implementation will be way different later
        printlnAndLog("File name:");
        try(PrintWriter writer = new PrintWriter(getString())) {
            writer.print(this.log);
            printlnAndLog("The log has been saved.");
        } catch (IOException e) {
            printlnAndLog("Error saving log.");
        }

    }

    private void resetStats() {
        for (Card c : this.deck.values()) {
            c.resetMistakes();
        }
        printlnAndLog("Card statistics have been reset.");
    }

    private void printHardest() {
        int max = 0;
        ArrayList<String> hardest = new ArrayList<>();
        for (Card c : this.deck.values()) {
            if (c.getMistakes() > max) {
                max = c.getMistakes();
                hardest.clear();
                hardest.add(c.getFront());
            } else if (c.getMistakes() == max) {
                hardest.add(c.getFront());
            }
        }
        if (max == 0) {
            printlnAndLog("There are no cards with errors");
        } else if (hardest.size() == 1) {
            printlnAndLog("The hardest card is \"" + hardest.get(0) + "\"." +
                    "You have " + max + " errors answering it");
        } else if (hardest.size() > 1) {
            printAndLog("The hardest cards are ");
            for (int i = 0; i < hardest.size(); i++) {
                printAndLog("\"" + hardest.get(i) + "\"");
                if (i == hardest.size() - 1) {
                    printAndLog(". ");
                } else {
                    printAndLog(", ");
                }
            }
            printlnAndLog("You have " +  max + " errors answering them.");
        }
    }

    private void exportDeck(String f) {
        try(PrintWriter printWriter = new PrintWriter(f)) {
            int count = 0;
            for (Card card : this.deck.values()) {
                printWriter.printf(
                        "%s:%s:%d%n", card.getFront(), card.getDefinition(), card.getMistakes());
                count++;
            }
            printlnAndLog(count + " cards have been saved.");

        } catch (IOException e) {
            printlnAndLog(e.getMessage());
        }
    }

    private void exportToNewFile() {
        printlnAndLog("File name:");
        String f = getString();
        exportDeck(f);
    }
    private void importFile(String f) {
        File file = new File(f);
        int count = 0;
        try (Scanner fileReader = new Scanner(file)) {
            while (fileReader.hasNext()) {
                String[] curr = fileReader.nextLine().split(":");
                removeDuplicate(curr);
                Card c = new Card(curr[0], curr[1], Integer.parseInt(curr[2]));
                addToDeck(c);
                count++;
            }
        } catch (IOException e) {
            printlnAndLog("File not found.");
        }
        printlnAndLog(count + " cards have been loaded.");
    }
    private void importNewFile() {
        printlnAndLog("File name:");
        String f = getString();
        importFile(f);
    }
    private void removeDuplicate(String[] card) {
        if (this.fronts.contains(card[0])) {
            for (Card c : this.deck.values()) {
                if (c.getFront().equals(card[0])) {
                    this.backs.remove(c.getDefinition());
                    this.deck.remove(c.getDefinition());
                }
            }
        }
    }

    private void removeCard() {
        printlnAndLog("Which card?");
        String toRemove = getString();

        if (!this.fronts.contains(toRemove)) {
            printlnAndLog("Can't remove \"" + toRemove + "\": there is no such card.");
        } else {
            for (Card curr : this.deck.values()) {
                if (curr.getFront().equals(toRemove)) {
                    this.deck.remove(curr.getDefinition());
                    this.fronts.remove(curr.getFront());
                    this.backs.remove(curr.getDefinition());
                    printlnAndLog("The card has been removed");
                    return;
                }
            }
        }
    }

    private void practice(int n) {
        int count = 0;
        if (this.deck.isEmpty()) {
            printlnAndLog("There are no cards yet.");
            return;
        }
        while (count < n) {
            for (Card c : this.deck.values()) {
                printlnAndLog("Print the definition of \"" + c.getFront() + "\":");
                String answer = getString();
                System.out.println(checkAnswer(c, answer));
                count ++;
                if (count >= n) {
                    break;
                }
            }
        }
    }

    private String checkAnswer(Card card, String answer) {
        if (card.getDefinition().equals(answer)) {
            return "Correct!";
        }
        card.addMistake();
        if (this.deck.containsKey(answer)) {
            return "Wrong. The right answer is \"" + card.getDefinition() + "\"," +
                    " but your definition is correct for \"" + this.deck.get(answer).getFront() + "\".";
        } else {
            return "Wrong. The right answer is \"" + card.getDefinition() + "\".";
        }
    }

    private void makeCard() {
        Card curr = fillCard();
        addToDeck(curr);
        printlnAndLog("The card " + curr + " has been added.");
    }

    private void addToDeck(Card card) {
        this.deck.put(card.getDefinition(), card);
        this.fronts.add(card.getFront());
        this.backs.add(card.getDefinition());
    }

    private Card fillCard() {
        String front = makeNewFront();
        String back = makeNewBack();
        return new Card(front, back);
    }

    private String makeNewFront() {
        printlnAndLog("The card:");
        while (true) {
            String curr = getString();
            if(!this.fronts.contains(curr)) {
                return curr;
            }
            printlnAndLog("The card \"" + curr + "\" already exists. Try again:");
        }
    }

    private String makeNewBack() {
        printlnAndLog("The definition of the card:");
        while (true) {
            String curr = getString();
            if(!this.backs.contains(curr)) {
                return curr;
            }
            printlnAndLog("The definition \"" + curr + "\" already exists. Try again:");
        }
    }

    private String getString() {
        String line = scanner.nextLine();
        this.log.append(line + "\n");
        return line;
    }

    private int getInt() {
        while(true) {
            try{
                return Integer.parseInt(getString());
            } catch (NumberFormatException e) {
                printlnAndLog("You must enter a single positive integer");
            }
        }
    }

    private void printlnAndLog(String str) {
        System.out.println(str);
        this.log.append(str + "\n");
    }

    private void printAndLog(String str) {
        System.out.print(str);
        this.log.append(str);
    }

}
