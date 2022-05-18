# Exercise Session 5, Solutions

## Problem 1: Message Processing Semantics

### Problem 1.1

For the `Client1` actor, the only possible output is `0`. The reason is that messages between two actors are guaranteed to be received in the order they were sent.

You can try the code yourself by running:
```
sbt "runMain problem1_1"
```

### Problem 1.2

1. For the `Client2` actor, either `0` or `1` can be printed. There are no restrictions on the order in which messages are processed in this case. It might be the case that the `Memory` actor receives the `Write` message from the `Client2` first, or the `Read` message from the `Proxy` first.

2. The order in which the messages are sent by the `Client2` doesn't change the possible behaviours of the system.

3. In the case both messages are sent through the `Proxy`, then the only possible output is `0`, since in this case the messages between the `Client2` and `Proxy`, as well as between `Proxy` and `Memory`, are guaranteed to be handled in the same order they were sent.

You can try the code yourself by running:
```
sbt "runMain problem1_2"
```

## Problem 2

The solution is in `Problem2.scala`.

You can run it using:

```
sbt "runMain problem2 10"
```
