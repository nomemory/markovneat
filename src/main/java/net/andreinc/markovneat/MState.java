package net.andreinc.markovneat;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

public class MState<T> {

    private final LinkedList<T> state;

    public MState() {
        this.state = new LinkedList<>();
    }

    public MState(final T... elements) {
        this.state = stream(elements)
                        .collect(toCollection(LinkedList::new));
    }

    public MState<T> shallowCopy() {
        return new MState<>(new LinkedList(this.data()));
    }

    public MState(final LinkedList<T> elements) {
        this.state = elements;
    }

    public MState(final Iterable<T> elements) {
        this.state = StreamSupport
                        .stream(elements.spliterator(), false)
                        .collect(toCollection(LinkedList::new));
    }

    protected LinkedList<T> data() {
        return state;
    }

    public MState<T> nextState(final T element) {

        final LinkedList<T> newState = new LinkedList<>(state);

        newState.removeFirst();
        newState.addLast(element);

        return new MState(newState);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final MState<?> mState = (MState<?>) other;
        return Objects.equals(state, mState.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return "net.andreinc.markovneat.MState{" +
                "state=" + state +
                '}';
    }
}
