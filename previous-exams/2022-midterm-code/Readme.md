# How to run

This folder contains the code of most of the exercises of the midterm, along with tests and comments.

- Questions 1-3: [Part1.scala](src/main/scala/midterm/Part1.scala)
- Questions 4-7: [Part2.scala](src/main/scala/midterm/Part2.scala)
- Question 8: [Part3.scala](src/main/scala/midterm/Part3.scala)
- Questions 9-15: [Part4.scala](src/main/scala/midterm/Part4.scala)
- Question 21: [Part6.scala](src/main/scala/midterm/Part6.scala)
- Questions 22-24: [Part7.scala](src/main/scala/midterm/Part7.scala)
- Question 25: [Part8.scala](src/main/scala/midterm/Part8.scala)

## Test

```
sbt test
```

or to run a specific suite:

```
sbt testOnly midterm.Part1Test
```

## Run a main function

```
sbt runMain midterm.part1
```

## Format

```
sbt scalafmt
```

## Benchmark

```
sbt jmh:run -bm ss -i 40 -wi 5 -rf JSON -rff CollectionsBenchmarkResults.json bench.CollectionBenchmark
```
