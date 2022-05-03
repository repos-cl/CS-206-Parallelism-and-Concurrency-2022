# How to run

This folder contains the code of most of the exercises of the midterm, along with tests and comments.

- Questions 1-3: [Part1.scala](src/main/scala/midterm/Part1.scala) and [Part1Test.scala](src/test/scala/midterm/Part1Test.scala)
- Questions 4-7: [Part2.scala](src/main/scala/midterm/Part2.scala) and [Part2Test.scala](src/test/scala/midterm/Part2Test.scala)
- Question 8: [Part3.scala](src/main/scala/midterm/Part3.scala)
- Questions 9-15: [Part4.scala](src/main/scala/midterm/Part4.scala) and [Part4Test.scala](src/test/scala/midterm/Part4Test.scala)
- Question 21: [Part6.scala](src/main/scala/midterm/Part6.scala) and [Part6Test.scala](src/test/scala/midterm/Part6Test.scala)
- Questions 22-24: [Part7.scala](src/main/scala/midterm/Part7.scala) and [Part7Test.scala](src/test/scala/midterm/Part7Test.scala)
- Question 25: [Part8.scala](src/main/scala/midterm/Part8.scala) and [Part8Test.scala](src/test/scala/midterm/Part8Test.scala)

## Test

```
sbt test
```

or to run a specific suite:

```
sbt "testOnly midterm.Part1Test"
```

## Run a main function

```
sbt "runMain midterm.part3"
```

## Format

```
sbt scalafmt
```

## Benchmark

```
sbt "jmh:run -bm ss -i 40 -wi 5 -rf JSON -rff CollectionsBenchmarkResults.json bench.CollectionBenchmark"
```
