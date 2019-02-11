package net.andreinc.markovneat;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class MProb<T> {

    private final NavigableMap<Double, T> map = new TreeMap<>();
    private final Random random = ThreadLocalRandom.current();
    private double total;

    public MProb() {}

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

    @Override
    public String toString() {
        return "net.andreinc.markovneat.MProb{" +
                "map=" + map +
                '}';
    }
}

