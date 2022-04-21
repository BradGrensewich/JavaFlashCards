package flashcards;

public class Main {
    public static void main(String[] args) {
        String inFile = null;
        boolean hasIn = false;
        String outFile = null;
        boolean hasOut = false;

        for (int i = 0; i< args.length; i++) {
            if (args[i].equals("-import")) {
                inFile = args[i + 1];
                hasIn = true;
                i++;
            } else if (args[i].equals("-export")) {
                outFile = args[i + 1];
                hasOut = true;
                i++;
            }
        }
        UI ui;
        if (hasIn && hasOut) {
            ui = new UI(inFile, outFile);
        } else if (hasIn) {
            ui = new UI(inFile, true);
        } else if (hasOut) {
            ui = new UI(outFile);
        } else {
            ui = new UI();
        }
        ui.menu();
    }
}
