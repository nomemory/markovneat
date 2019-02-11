package net.andreinc.markovneat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Character.toUpperCase;
import static java.nio.file.Files.lines;
import static java.util.stream.Collectors.toList;

public class MChainText extends MChain<String> {

    private static final Pattern PT_PATTERN = Pattern.compile("[\\p{Punct}\\s]+");

    public MChainText(final int noStates) {
        super(noStates);
    }

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

    public String generateText(final int approximateLength) {
        final StringBuilder result = new StringBuilder();

        MState<String> cState = randomState();

        // Avoid starting the text with punctuation
        while(isPunctuation(cState.data().getFirst())) {
            cState = randomState();
        }

        appendInitialState(result, cState);

        // Finish abruptly if the first state gets bigger than the approximate size.
        if (result.length() > approximateLength) {
            return result.append(".").toString();
        }

        // Adds every subsequent element
        String cElement;

        // Works until the approximate length is bigger than the actual length
        while(result.length() <= approximateLength) {

            // If chain is not cyclic start from a random state again.
            if (!chain.containsKey(cState)) {
                cState = randomState();
            }

            cElement = chain.get(cState).next();

            if (!isPunctuation(cElement)) {
                result.append(" ");
            }

            // If last element in the result buffer is "." capitalise next String
            if (result.charAt(result.length()-2) == '.') {
                result.append(capital(cElement));
            } else {
                result.append(cElement);
            }

            cState = cState.nextState(cElement);
        }

        return result.append(".").toString();
    }

    private static void appendInitialState(final StringBuilder buff, final MState<String> firstState) {
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
