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

The following image describes a discrete-time markov chain:

![alt text](https://github.com/nomemory/markovneat/blob/master/media/example01.png)

With **markovneat** this can be modelled with the following code:

```java
 MChain<String> marketMChain = new MChain<>();

marketMChain.add(new MState<>("BULL"), "BULL", 0.9);
marketMChain.add(new MState<>("BULL"), "BEAR", 0.075);
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
