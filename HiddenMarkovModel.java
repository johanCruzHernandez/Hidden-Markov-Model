import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This program includes the process of creating the observation
 * and transition maps from a given pair of text files -- the first
 * has the sentences and the other includes the corresponding
 * part-of-speech tags. Furthermore, this program includes
 * the Viterbi algorithm that uses the Hidden Markov Model created by the
 * observation and transition maps to properly tag sentence(s).
 *
 * @author Johan Cruz Hernandez, Samuel Crombie
 * Dartmouth College, CS 10, Spring 2021
 */
public class HiddenMarkovModel {
    private Map<String, Map<String, Double>> observationMap;  // keeps track of POS-word frequencies
    private Map<String, Map<String, Double>> transitionMap;  // keeps track of POS-POS transition frequencies

    /**
     * Constructor that instantiates Hidden Markov Model.
     *
     * @param fileTags      training text file with POS tags.
     * @param fileSentences training text file with sentences.
     */
    public HiddenMarkovModel(String fileTags, String fileSentences) throws IOException {
        observationMap = loadFileToMapObservations(fileTags, fileSentences);
        transitionMap = loadFileToMapTransitions(fileTags);
    }

    /**
     * Reads training text file with sentences and part-of-speech (POS) tags
     * and keeps track POS-word frequencies.
     *
     * @param fileTags training text file with POS tags.
     * @param fileSentences training text file with sentences.
     * @return frequency map.
     * @throws IOException checks if file exists and can be read.
     */
    public static Map<String, Map<String, Double>> loadFileToMapObservations(String fileTags, String fileSentences) throws IOException {
        Map<String, Map<String, Double>> observationMap = new HashMap<>();

        BufferedReader inputPartOfSpeech = new BufferedReader(new FileReader(fileTags));  // reads POS sequences
        BufferedReader inputSentence = new BufferedReader(new FileReader(fileSentences));  // reads sentences

        String strPOS, strWordObservations;
        while ((strPOS = inputPartOfSpeech.readLine()) != null && (strWordObservations = inputSentence.readLine()) != null) {

            // split line of tag sequences by a space.
            String[] listOfPOS = strPOS.split(" ");

            // split line of sentences by a space.
            String[] listOfWordObservations = strWordObservations.split(" ");

            // iterates through list of tags
            for(int i = 0; i < listOfPOS.length; i++) {
                // checks if tag is encountered for the first time.
                if(!observationMap.containsKey(listOfPOS[i])) {
                    Map<String, Double> frequencyMap = new HashMap<>(); // instantiate a new map to store word-tag pair.
                    frequencyMap.put(listOfWordObservations[i].toLowerCase(), 1.0);
                    observationMap.put(listOfPOS[i], frequencyMap);
                }

                // checks if tag has already been encountered.
                else if(observationMap.containsKey(listOfPOS[i])) {
                    // checks if word-tag pair already exists and increments frequency.
                    if(observationMap.get(listOfPOS[i]).get(listOfWordObservations[i]) != null) {
                        observationMap.get(listOfPOS[i]).put(listOfWordObservations[i],
                                observationMap.get(listOfPOS[i]).get(listOfWordObservations[i]) + 1);
                    }
                    // the tag exists but has not been paired with word.
                    else {
                        observationMap.get(listOfPOS[i]).put(listOfWordObservations[i].toLowerCase(), 1.0);
                    }
                }
            }
        }
        // converts word-tag pair frequencies into percentages
        return getMapProbabilities(observationMap);
    }

    /**
     * Read training tag file and keeps track of the
     * transitions from one part-of-speech to the next.
     *
     * @param fileTags training text file with POS tags.
     * @throws IOException checks if file exists and can be read.
     */
    public static Map<String, Map<String, Double>> loadFileToMapTransitions(String fileTags) throws IOException {
        Map<String, Map<String, Double>> transitionMap = new HashMap<>();
        BufferedReader inputPartOfSpeech = new BufferedReader(new FileReader(fileTags));

        String strPOS;
        while((strPOS = inputPartOfSpeech.readLine()) != null) {
            String[] listOfPOS = strPOS.split(" ");

            // iterates through line of tags
            for(int i = -1; i <= listOfPOS.length - 2; i++) {
                if(i == -1) {
                    // checks that transitionMap has the # (start) tag.
                    if (!transitionMap.containsKey("#")) {
                    Map<String, Double> frequencyMap = new HashMap<>();
                    frequencyMap.put(listOfPOS[i + 1], 1.0);
                    transitionMap.put("#", frequencyMap);
                    }
                    // checks that transitionMap already has # (start) tag.
                    else if(transitionMap.containsKey("#")) {
                        // has the # (start) tag and encounters am already-seen POS tag.
                        if(transitionMap.get("#").containsKey(listOfPOS[i+1])) {
                            transitionMap.get("#").put(listOfPOS[i+1], transitionMap.get("#").get(listOfPOS[i+1]) + 1);
                        }
                        // has the # (start) but encounters a new POS.
                        else {
                            transitionMap.get("#").put(listOfPOS[i+1], 1.0);
                        }
                    }
                }
                // starts to keep track of transitions after # (start) tag.
                if(i != -1) {
                    int j = i + 1;
                    // checks that a newly-encountered POS tag has been found.
                    if(!transitionMap.containsKey(listOfPOS[i])) {
                        Map<String, Double> frequencyMap = new HashMap<>();
                        frequencyMap.put(listOfPOS[j], 1.0);
                        transitionMap.put(listOfPOS[i], frequencyMap);
                    }
                    // checks that an already existing tag exists in the transitionMap.
                    else if(transitionMap.containsKey(listOfPOS[i])) {
                        // checks that a transition pair already exits and increments its frequency.
                        if(transitionMap.get(listOfPOS[i]).get(listOfPOS[j]) != null) {
                            transitionMap.get(listOfPOS[i]).put(listOfPOS[j],
                                    transitionMap.get(listOfPOS[i]).get(listOfPOS[j]) + 1);
                        }
                        // increments count to 1 for a newly-discovered transition pair.
                        else {
                            transitionMap.get(listOfPOS[i]).put(listOfPOS[j], 1.0);
                        }
                    }
                }
            }
        }
        // convert to probabilities
        return getMapProbabilities(transitionMap);
    }

    /**
     * Converts frequencies to probabilities.
     * @param map either the observationMap or transitionMap.
     * @return map of maps with probabilities
     */
    private static Map<String, Map<String, Double>> getMapProbabilities(Map<String, Map<String, Double>> map) {
        // iterate through every key in the outer map.
        for(Map.Entry<String, Map<String, Double>> entryOuter : map.entrySet()) {
            Double total = 0.0;
            // iterate through every key in the inner map.
            for(Map.Entry<String, Double> entryInner : entryOuter.getValue().entrySet()) {
                total += entryInner.getValue();
            }
            // override the frequency with probability.
            for(Map.Entry<String, Double> tagToWordFrequency : entryOuter.getValue().entrySet()) {
                map.get(entryOuter.getKey()).put(tagToWordFrequency.getKey(),
                        Math.log(tagToWordFrequency.getValue()/total));
            }
        }
        return map;
    }

    /**
     * Performs Viterbi decoding to find the best sequence of tags for a line (sequence of words)
     * @param words array of words
     * @return best possible path
     * NOTE: DOES NOT WORK WITH PUNCTUATION!!!! FIND A WAT TO HANDLE THIS SPECIAL CASE
     */
    public ArrayList<String> Viterbi(String[] words) {

        // keeps track of a state and its the previus state it came from.
        ArrayList<Map<String, String>> backtrack = new ArrayList<>();

        // initiate back-pointer to that of # which is null.
        String prev = null;

        // stores the tag sequences of a sentence.
        ArrayList<String> tagSequence = new ArrayList<>();

        // keeps track of the current states.
        Set<String> currentStates = new HashSet<>();
        currentStates.add("#");

        // keeps track of scores.
        Map<String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);

        // score given to a word not seen by a state.
        int penaltyScore = -1000;

        // iterates through sentence word by word.
        for (int i = 0; i < words.length; i++) {
            Set<String> nextStates = new HashSet<>();  // will reset after moving to next word.
            Map<String, Double> nextScores = new HashMap<>();  // will reset after moving to next word.
            Map<String, String> bestScores = new HashMap<>();  // creates new map that holds best scores.
            backtrack.add(i, bestScores);  // adds bestsores map to backtrack at index i.
            // iterates through evert current state
            for (String currState : currentStates) {

                // does not allow punctuation to be considered (punctuation is exists in the observations but not in the transitions map)
                if (!transitionMap.containsKey(currState)) {
                    continue;
                }

                // goes into the transitions map of the current state we are on
                for (String nextState : transitionMap.get(currState).keySet()) {
                    nextStates.add(nextState);  // add the states that currState can reach

                    // calculates score at observation i.
                    Double nextScore = currScores.get(currState) + transitionMap.get(currState).get(nextState);

                    // the word is not in observation map paired with currState.
                    if (observationMap.get(nextState).get(words[i]) == null) {
                        nextScore += penaltyScore;
                    }
                    // the word is not in observation map paired with currState.
                    else {
                        nextScore += observationMap.get(nextState).get(words[i]);
                    }

                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                        nextScores.put(nextState, nextScore);
                        prev = nextState;  // update back-pointer
                        backtrack.get(i).put(prev, currState);  // add
                    }
                }
            }
            currentStates = nextStates;
            currScores = nextScores;
        }

        String currentBestState = currentStates.iterator().next();
        Double bestScore = currScores.get(currentBestState);

        // finds the state with the highest score at the last observation.
        for (String tag : currentStates) {
            if (!tag.equals(currentBestState) && currScores.get(tag) > bestScore) {
                currentBestState = tag;
                bestScore = currScores.get(tag);
            }
        }
        // adds the tag associated with the last highest score to tag sequences list.
        tagSequence.add(currentBestState);

        // uses bactrack map to backtrace and from end to start.
        for (int i = backtrack.size() - 1; i > 0; i--) {
            String currentTag = backtrack.get(i).get(currentBestState);
            tagSequence.add(0, currentTag);
            currentBestState = currentTag;
        }
        return tagSequence;
    }
}
