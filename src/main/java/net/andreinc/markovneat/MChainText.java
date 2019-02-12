package net.andreinc.markovneat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.toUpperCase;
import static java.nio.file.Files.lines;
import static java.util.stream.Collectors.toList;

/**
 * An extension of the {@code MChain} class specialised in the generation of "English" text.
 */
public class MChainText extends MChain<String> {

    private static final Pattern PT_PATTERN = Pattern.compile("[\\p{Punct}\\s]+");

    public MChainText() {
        super();
    }

    public MChainText(final int noStates) {
        super(noStates);
    }

    /**
     * Trains the chain using a txt file as the source.
     *
     * Splits the text into words, removes quotes and trains the chain.
     *
     * @param path The path to the source text file.
     */
    public void train(final Path path) {
        try {

            final Iterator<String> wordsIt = lines(path)
                                 .map(line -> line.replaceAll("\"", ""))
                                 .map(MChainText::split)
                                 .map(words ->
                                         words
                                            .stream()
                                            .map(word -> word.trim().toLowerCase())
                                            .filter(word -> !"".equals(word))
                                            .collect(toList())
                                 )
                                 .flatMap(List::stream)
                                 .collect(toList())
                                 .iterator();

            train(wordsIt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates random text starting with a supplied initial {@code MState<String>}.
     *
     * If the state doesn't exist in the chain an {@IllegalArgumentException} is thrown.
     *
     * If the state starts with a "." a new random state will be arbitrary picked form the chain.
     *
     * @param approximateLength The approximate length of the text.
     *              The generated output can be smaller or bigger than the desired size with a few characters.
     * @param state The initial state.
     * @return The arbitrary generated text.
     */
    public String generateText(final MState<String> state, final int approximateLength) {

        if (chain.isEmpty()) {
            throw new IllegalArgumentException("Markov chain is empty. Please train the chain first.");
        }

        if (approximateLength < 0) {
            throw new IllegalArgumentException("The approximate text length cannot be a negative number.");
        }

        if (!chain.containsKey(state)) {
            throw new IllegalArgumentException("The initial state cannot be found in the Markov Chain. Please use an existing state.");
        }

        final StringBuilder result = new StringBuilder();

        MState<String> currentState = state;

        // Avoid starting the text with punctuation
        while(isPunctuation(currentState.data().getFirst())) {
            currentState = randomState();
        }

        appendState(result, currentState);

        // Finish abruptly if the first state gets bigger than the approximate size.
        if (result.length() > approximateLength) {
            return result.append(".").toString();
        }


        // Works until the approximate length is bigger than the actual length.
        String cElement;
        while(result.length() <= approximateLength) {

            // If chain is not cyclic start from a random state again.
            if (!chain.containsKey(currentState)) {
                currentState = randomState();
            }

            cElement = chain.get(currentState).next();

            if (!isPunctuation(cElement)) {
                result.append(" ");
            }

            // If last element (before the space)
            // in the result buffer is "." capitalise next String
            if (result.charAt(result.length()-2) == '.') {
                result.append(capital(cElement));
            } else {
                result.append(cElement);
            }

            currentState = currentState.nextState(cElement);
        }

        // Even if the "dot" is not the last element in the chain
        // Add it to the result.
        return result.append(".").toString();
    }

    /**
     * Generates text starting with an arbitrary state from the chain.
     *
     * @param approximateLength The approximate length of the text.
     *                          The generated output can be smaller or bigger than the desired size with a few characters.
     * @return Arbitrary text from the chain
     */
    public String generateText(final int approximateLength) {
        return generateText(randomState(), approximateLength);
    }

    /**
     * Appends the initial state to the result buffer (StringBuilder)
     *
     * @param buff The buffer where the state is appended
     * @param firstState The initial Markov Chain state
     */
    private static void appendState(final StringBuilder buff, final MState<String> firstState) {

        // Append first state to the buffer
        for(final String element : firstState.data()) {
            if (!isPunctuation(element)) {
                buff.append(' ');
            }
            buff.append(element);
        }

        // Delete initial " " <space>"
        buff.deleteCharAt(0);

        // Replace the first character with the upper letter
        final char newChar = toUpperCase(buff.charAt(0));
        buff.deleteCharAt(0).insert(0, newChar);
    }

    /**
     * Capitalise the first letter of a string. Locale is not taken into consideration.
     * @param element
     * @return
     */
    private static String capital(final String element) {
        return element.substring(0, 1).toUpperCase()
                + element.substring(1);
    }

    private static boolean isPunctuation(final String element) {
        return PT_PATTERN.matcher(element).matches();
    }

    /**
     * Splits a string by punctuation, keeping also the punctuation signs.
     *
     * @param source The strings to be split.
     * @return
     */
    private static final List<String> split(final String source) {
        final List<String> result = new ArrayList<>();
        final Matcher matcher = PT_PATTERN.matcher(source);

        int last = 0;
        int start = 0;

        while(matcher.find()) {
            start = matcher.start();

            if (last != start) {
                result.add(source.substring(last, start));
            }

            final String delim = matcher.group();
            result.add(delim);

            final int end = matcher.end();
            last = end;
        }

        if (last != source.length()) {
            result.add(source.substring(last));
        }

        return result;
    }
}
