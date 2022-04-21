package flashcards;

public class Card {

    private String front;
    private String definition;
    private int mistakes;

    public Card (String name, String definition, int mistakes){
        this.front = name;
        this.definition = definition;
        this.mistakes = mistakes;
    }

    public Card(String name, String definition) {
        this(name, definition, 0);
    }


    public String getDefinition() {
        return definition;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void addMistake() {
        this.mistakes++;
    }

    public void resetMistakes() {
        this.mistakes = 0;
    }

    public String getFront() {
        return front;
    }

    public boolean checkAnswer(String userAnswer) {
        return (userAnswer.equals(this.getDefinition()));
    }

    public String toString() {
        return "(\"" + this.front + "\":\"" + this.definition + "\")";
    }
}
