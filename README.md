[Markov chain](https://en.wikipedia.org/wiki/Markov_chain)s in Java.

# Installing 

To install with Maven and Gradle please check the official jcenter() repository url:

[https://bintray.com/nomemory/maven/markovneat](https://bintray.com/nomemory/maven/markovneat)

You can also create a "fat" jar using the `shadowJar` gradle tasks:

```groovy
gradle shadowJar
```

The jar will be generated in `/build/libs/markovneat*.jar`.

# Example 1 - Modelling a simple discrete-time markov chain

A directed graph is used bellow to picture the state transitions for a Markov Chain. 

The states represent whether a hypothetical stock market is exhibiting a bull market, bear market, or stagnant market trend during a given week.

(See [Market trends](https://en.wikipedia.org/wiki/Market_trend)).

![alt text](https://github.com/nomemory/markovneat/blob/master/media/example01.png)

With the **markovneat** library this can be modelled using the following code:

```java
 MChain<String> marketMChain = new MChain<>();

// Transitioning from "BULL" to "BULL" has a 90% chance
marketMChain.add(new MState<>("BULL"), "BULL", 0.9);
// Transitioning from "BULL" to "BEAR" has a 7,5% chance
marketMChain.add(new MState<>("BULL"), "BEAR", 0.075);
// Transitioning from "BULL" to "STAGNANT" has a 2,5% chance
marketMChain.add(new MState<>("BULL"), "STAGNANT", 0.025);

marketMChain.add(new MState<>("BEAR"), "BEAR", 0.8);
marketMChain.add(new MState<>("BEAR"), "BULL", 0.15);
marketMChain.add(new MState<>("BEAR"), "STAGNANT", 0.05);

marketMChain.add(new MState<>("STAGNANT"), "STAGNANT", 0.5);
marketMChain.add(new MState<>("STAGNANT"), "BULL", 0.25);
marketMChain.add(new MState<>("STAGNANT"), "BEAR", 0.25);

marketMChain.generate(10000).forEach(System.out::println);
```

Output:

```
STAGNANT
BULL
BULL
BULL
BULL
... and so on
```

# Example 2 - Creating higer-order markov chains

We define 3 states **A**, **B**, and **C** . 

