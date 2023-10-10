import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This program includes the tests.
 * @author Johan Cruz Hernandez
 */
public class Testing {

    /**
     * Tests the accuracy of Viterbi algorithm using a Hidden Marko Model.
     * @param fileTrainTags text file with training tags.
     * @param fileTrainSentences text file with training sentences.
     * @param fileTestSentences text file with test sentences.
     * @param fileTestTags text file with test tags.
     * @throws IOException checks that file exists.
     */
    public static void performance(String fileTrainTags, String fileTrainSentences, String fileTestSentences, String fileTestTags) throws IOException {
        // instantiate Hidden Markov Model using training files.
        HiddenMarkovModel HMM = new HiddenMarkovModel(fileTrainTags, fileTrainSentences);

        // initialize correct and incorrect score to zero.
        int correct = 0;
        int incorrect = 0;

        // reads test sentences and corresponding tag sequences
        BufferedReader inputTestSentences = new BufferedReader(new FileReader(fileTestSentences));
        BufferedReader inputTestTags = new BufferedReader(new FileReader(fileTestTags));

        String lineTestSentences;
        String lineTestTags;

        while((lineTestSentences = inputTestSentences.readLine()) != null && (lineTestTags = inputTestTags.readLine()) != null) {
            String[] listOfWords = lineTestSentences.split(" ");
            String[] listOfTags = lineTestTags.split(" ");
            ArrayList<String> ViterbiTagSequences = HMM.Viterbi(listOfWords);
            // iterate through every tag returned by Viterbo and every corresponding correct tag
            // from tags test file.
            for(int i = 0; i < listOfWords.length; i++) {
                // Viterbi tag and tag in the tags file match.
                if(listOfTags[i].equals(ViterbiTagSequences.get(i))) {
                    correct += 1;
                }
                // Viterbi tag and tag in the tags file do not match.
                else {
                    incorrect += 1;
                }
            }
        }
        System.out.println("Correct " + correct);
        System.out.println("Incorrect " + incorrect);
    }

    /**
     * Takes in a line from the console window and returns the POS tags for each word.
     * @param fileTrainTags training text file with POS tags.
     * @param fileTrainSentences training text file with sentences.
     * @throws IOException checks that files exist.
     */
    public static void readFromConsole(String fileTrainTags, String fileTrainSentences) throws IOException{
        // instantiate Hidden Markov Model using training files.
        HiddenMarkovModel HMM = new HiddenMarkovModel(fileTrainTags, fileTrainSentences);

        // instantiate scanner.
        Scanner scanner = new Scanner(System.in);

        // stores input from scanner.
        String line = scanner.nextLine();

        // splits sentence from scanner into a list of words.
        String[] listOfWords = line.split(" ");

        // pass in list of words into Viterbi
        System.out.println(HMM.Viterbi(listOfWords));
    }

    public static void main(String[] args) throws IOException {

        // testing performance using hard-coded emission and transition maps.
        System.out.println("This is a test using hard-coded emission and transition maps to train HMM. ");
        System.out.println("The result of this performance is based on brown test files.");;
        performance("/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/" +
                "Object Oriented Programming/PS5/texts/PD-train-tags", "/Users/johancruzhernandez/One" +
                "Drive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/PD-" +
                "train-sentences", "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/" +
                "Classes/Spring 2021/Object Oriented Programming/PS5/texts/brown-test-sentences.txt",
                "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/" +
                        "Object Oriented Programming/PS5/texts/brown-test-tags.txt");
        System.out.println("\n");

        // reading from console using brown-train-tags and brown-train-sentences.
        System.out.println("This is a console based test using brown training files");
        System.out.println("Write sentence into the console.");
        readFromConsole("/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring " +
                        "2021/Object Oriented Programming/PS5/texts/brown-train-tags.txt",
                "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring " +
                        "2021/Object Oriented Programming/PS5/texts/brown-train-sentences.txt");
        System.out.println("\n");

        // testing performance using brown training files and brown testing files.
        // the sample solution got 35,109 right vs. 1285 wrong for brown.
        System.out.println("Testing performance using brown training files and brown testing files.");
        performance("/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring " +
                        "2021/Object Oriented Programming/PS5/texts/brown-train-tags.txt",
                "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring " +
                        "2021/Object Oriented Programming/PS5/texts/brown-train-sentences.txt", "/Users/" +
                        "johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented " +
                        "Programming/PS5/texts/brown-test-sentences.txt", "/Users/johancruzhernandez/OneDrive" +
                        " - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/brown-" +
                        "test-tags.txt");
        System.out.println("\n");

        // testing performance using simple training files and sample test files.
        // the sample solution got 32 tags right and 5 wrong for simple.
        System.out.println("Testing performance using simple training files and simple test files.");
        performance("/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/" +
                "Object Oriented Programming/PS5/texts/simple-train-tags.txt", "/Users/johancruzhernand" +
                "ez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/si" +
                "mple-train-sentences.txt", "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dar" +
                "tmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/simple-test-sentences.txt",
                "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object" +
                        " Oriented Programming/PS5/texts/simple-test-tags.txt");
        System.out.println("\n");

        // testing performance using simple training files and example test files.
        System.out.println("Testing performance using simple training files and example test files.");
        performance("/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/" +
                "Object Oriented Programming/PS5/texts/simple-train-tags.txt", "/Users/johancruzhernande" +
                "z/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/simp" +
                "le-train-sentences.txt", "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmou" +
                "th/Classes/Spring 2021/Object Oriented Programming/PS5/texts/example-sentences.txt", "/Users" +
                "/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented Program" +
                "ming/PS5/texts/example-tags.txt");
        System.out.println("\n");

        // testing performance using brown training files and example test files.
        System.out.println("Testing performance using brown training files and example test files.");
        performance("/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/" +
                "Object Oriented Programming/PS5/texts/brown-train-tags.txt", "/Users/johancruzhernandez" +
                "/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/" +
                "brown-train-sentences.txt", "/Users/johancruzhernandez/OneDrive - Dartmouth College" +
                "/Dartmouth/Classes/Spring 2021/Object Oriented Programming/PS5/texts/example-sentences.txt",
                "/Users/johancruzhernandez/OneDrive - Dartmouth College/Dartmouth/Classes/Spring 2021/Object " +
                        "Oriented Programming/PS5/texts/example-tags.txt");
        System.out.println("\n");
    }
}
