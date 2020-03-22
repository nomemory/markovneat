package net.andreinc.markovneat;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class TestMChain {

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeNumberOfStates() {
        new MChain<String>(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroNumberOfStates() {
        new MChain<Integer>(0);
    }

    // With Add

    @Test
    public void testChainWithAddMethodAndInitState() {
        MChain<Boolean> mChain = trueFalseChainWithAdd();
        List<Boolean> transitions = mChain.generate(new MState<>(true), 10);
        assertTrue(isAlternating(true, transitions));
    }

    @Test
    public void testChainWithAddMethodAndRandomState() {
        MChain<Boolean> mChain = trueFalseChainWithAdd();
        List<Boolean> transitions = mChain.generate(10);
        assertTrue(isAlternating(transitions.get(0), transitions));
    }

    // With train - Array

    @Test
    public void testChainWithTrainArrayAndInitState() {
        MChain<Boolean> mChain = trueFalseChainWithTrainArray();
        List<Boolean> transitions = mChain.generate(new MState<>(true), 10);
        assertTrue(isAlternating(true, transitions));
    }

    @Test
    public void testChainWithTrainArrayAndRandomState() {
        MChain<Boolean> mChain = trueFalseChainWithTrainArray();
        List<Boolean> transitions = mChain.generate(10);
        assertTrue(isAlternating(transitions.get(0), transitions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCHainWithTrainArrayAndInsufStates() {
        MChain mChain = new MChain(3);
        mChain.train("A", 2);
    }

    // With train - Iterable

    @Test
    public void testChainWithTrainIterableAndInitState() {
        MChain<Boolean> mChain = trueFalseChainWithTrainIterable();
        List<Boolean> transitions = mChain.generate(new MState<>(true), 10);
        assertTrue(isAlternating(true, transitions));
    }

    @Test
    public void testChainWithTrainIterableAndRandomState() {
        MChain<Boolean> mChain = trueFalseChainWithTrainIterable();
        List<Boolean> transitions = mChain.generate(10);
        assertTrue(isAlternating(transitions.get(0), transitions));
    }

    // Utils

    private static boolean isAlternating(final boolean iState, List<Boolean> transitions) {
        Boolean b = iState;

        for (Boolean transition : transitions) {
            if (transition != b) {
                return false;
            }
            b = !b;
        }

        return true;
    }

    private static MChain<Boolean> trueFalseChainWithAdd() {
        MChain<Boolean> mChain = new MChain<>();

        mChain.add(new MState<>(true), false);
        mChain.add(new MState<>(false), true);

        return mChain;
    }

    private static MChain<Boolean> trueFalseChainWithTrainArray() {
        MChain<Boolean> mChain = new MChain<>();

        mChain.train(new Boolean[]{true, false, true});

        return mChain;
    }

    private static MChain<Boolean> trueFalseChainWithTrainIterable() {
        MChain<Boolean> mChain = new MChain<>();

        mChain.train(asList(true, false, true));

        return mChain;
    }
}
