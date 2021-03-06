package net.andreinc.markovneat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Arrays.stream;


/**
 * Markov chain class.
 *
 * @param <T>
 */
public class MChain<T> {

    protected Random random;
    protected Map<MState<T>, MProb<T>> chain = new ConcurrentHashMap<>();
    protected List<MState<T>> states = new ArrayList<>();

    private final int noStates;

    public MChain() {
        this(1);
    }

    public MChain(int noStates) {
        this(noStates, ThreadLocalRandom.current());
    }

    public MChain(final int noStates, Random random) {
        if (noStates < 1) {
            throw new IllegalArgumentException("The number of states used to create the Markov chain needs to be {@code >= 1}");
        }
        this.noStates = noStates;
        this.random = random;
    }

    /**
     * Adds a new state transition to the Markov Chain
     *
     * @param state The initial state
     * @param element The element where we transition.
     */
    public void add(final MState<T> state, final T element) {
        chain.putIfAbsent(state, new MProb<>(random));
        chain.get(state).add(1, element);
    }

    public void add(final MState<T> state, final T element, double weight) {
        chain.putIfAbsent(state, new MProb<>(random));
        chain.get(state).add(weight, element);
    }

    /**
     * Trains the Markov chain with a sequence of elements.
     *
     * @param elements
     */
    public void train(final Iterable<T> elements) {
        train(elements.iterator());
    }

    /**
     * Trains the markov chain with a sequence of elements.
     *
     * @param elements
     */
    public void train(final T... elements) {
        if (elements.length < noStates) {
            throw new IllegalArgumentException("Cannot train a chain with based on a number of elements smaller than noStates.");
        }
        train(stream(elements).iterator());
    }

    /**
     * Trains the markov chain with a sequence of elements.
     *
     * The minimum number of the iterator needs to be {@code >=} noStates.
     *
     * @param iterator
     */
    protected void train(final Iterator<T> iterator) {
        MState<T> state = new MState<>();
        while(iterator.hasNext()) {
            if (state.data().size() < noStates) {
                state.data().add(iterator.next());
            } else {
                final T next = iterator.next();
                add(state, next);
                state = state.nextState(next);
            }
        }
    }

    /**
     * Generates a number of elements and stores in a {@code List<T>} based on the current Markov Chain.
     *
     * Careful: the resulting {@code List<T>} will contain numElements + noStates elements.
     *
     * @param numElements The number of elements to be generate on top of an arbitrary initial state.
     * @return A {@code List<T>} of elements.
     */
    public List<T> generate(final int numElements) {
        return generate(randomState(), numElements);
    }

    public MState<T> randomState() {

        if (chain.isEmpty()) {
            throw new IllegalArgumentException("Markov chain is empty. Please train the chain first.");
        }

        if (states.size() != chain.keySet().size()) {
            states = new ArrayList<>(chain.keySet());
        }

        final int idx = random.nextInt(states.size());

        return states.get(idx);
    }

    /**
     * Generates a number of elements and stores them in a {@code List<T>} based on the current Markov chain.
     * @param initialState The initial state from which we start the generation of elements.
     *                     If the initial state doesn't exist in the Markov Chain an empty List will be returned.
     * @param numElements The number of elements that will be generated on top of the initial state.
     *                    The number should be a positive value.
     *                    (If the state has a 3 elements, and numElements is 2 a {@code List<T>} of 5=3+2 elements
     *                    will be returned).
     * @return A {@code List<T>} generated with the markov chain.
     */
    public List<T> generate(final MState<T> initialState, final int numElements) {

        if (chain.isEmpty()) {
            throw new IllegalArgumentException("Markov chain is empty. Please train the chain first.");
        }

        if (numElements <= 0) {
            throw new IllegalArgumentException("The initial number of elements cannot be negative or zero. (>0)");
        }

        if (!chain.containsKey(initialState)) {
            throw new IllegalArgumentException("The initial state cannot be found in the Markov Chain. Please use an existing state.");
        }

        final List<T> result = new ArrayList<>();
        MState<T> state = initialState.shallowCopy();

        result.addAll(state.data());

        int goUntil = numElements;
        T element;
        while(goUntil-->0) {
            if (!chain.containsKey(state)) {
                state = randomState();
                result.addAll(state.data());
                continue;
            }
            element = chain.get(state).next();
            result.add(element);
            state = state.nextState(element);
        }

        return result;
    }
}
