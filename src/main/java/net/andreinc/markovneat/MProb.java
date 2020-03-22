package net.andreinc.markovneat;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class MProb<T> {

    private final NavigableMap<Double, T> map = new TreeMap<>();
    private final Random random;
    private double total;

    public MProb() {
        this(ThreadLocalRandom.current());
    }

    public MProb(Random random) {
        this.random = random;
    }

    public MProb<T> add(final double weight, final T result) {

        if (weight <= 0) {
            return this;
        }

        total += weight;
        map.put(total, result);

        return this;
    }

    public T next() {
        final double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}

