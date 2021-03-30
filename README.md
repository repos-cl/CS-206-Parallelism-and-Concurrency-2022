This repository will be used as the website for Parallelism and Concurrency CS-206. It will be updated weekly throughout the semester. This README contains general information about the class.

- [previous-exams](previous-exams) contains PDFs for the previous exams.
- [exercises](exercises) contains markdown documents for exercises and solutions.
- [slides](slides) contains the slides presented in class.
- [labs](labs) contains markdown documents for the labs.

We will use GitLab's issue tracker as a discussion forum. Feel free to [open an issue](https://gitlab.epfl.ch/lamp/cs206/issues) if you have any comments or questions.

# First-week tasks

1. Join [the Discord](https://discord.gg/hJsreTeZjc)
1. Log into gitlab: https://gitlab.epfl.ch/users/sign_in
1. Please fill in [this table](https://docs.google.com/spreadsheets/d/1rcq_UMgR6bAH-iK1L2I6WoClZtCqUoIOLqQO3NJKdgg) with your GASPAR and SCIPER number
   * Choose the group for the exercises
   * This will initialize your GitLab repository for the course
1. Follow the [Tools Setup](labs/tools-setup.md) page.
1. Do the [example lab](labs/example-lab.md).
1. Watch all videos under *Parallelism 1: Introduction to Parallel Programming* below
1. Do the [first graded lab](labs/lab1-parallel-box-blur-filter/).

# Grading

The grading of the course is divided between exercies (5%), labs (25%), midterm exam (30%) and final exam (40%).

# Staff

| Role        | People |
| :---        | :--- |
| Professors  | [Martin Odersky](https://people.epfl.ch/martin.odersky), [Viktor Kunčak](https://people.epfl.ch/viktor.kuncak) |
| TAs         | [Aleksander Boruch-Gruszecki](https://people.epfl.ch/aleksander.boruch-gruszecki), [Dragana Milovancevic](https://people.epfl.ch/dragana.milovancevic), [Guillaume Martres](https://people.epfl.ch/guillaume.martres), [Nicolas Stucki](https://people.epfl.ch/nicolas.stucki), [Olivier Blanvillain](https://people.epfl.ch/olivier.blanvillain) |
| Student TAs | [Antoine Masanet](https://people.epfl.ch/antoine.masanet), [Lucas Giordano](https://people.epfl.ch/lucas.giordano), [Kajetan Pyszkowski](https://people.epfl.ch/kajetan.pyszkowski), [Marco Vögeli](https://people.epfl.ch/marco.vogeli), [Quentin Guignard](https://people.epfl.ch/quentin.guignard), [Sara Djambazovska](https://people.epfl.ch/sara.djambazovska) |

# Course Schedule

Lectures are partially live (on Zoom) and partially prerecorded (on YouTube).
Live sessions will be held on Wednesdays from 14:15 to 16:00.
Weekly Discord sessions will be held on Wednesdays from 14:15 to 16:00 for exercises (if it is a week with exercises) and 16:15 to 18:00 for labs.
You should watch the prerecorded lectures before doing the exercies.
In the first week of the semester, there will be a live Zoom session on Wednesday at 14:15 to welcome you to the class and answer questions you might have, followed by a Discord session.

<!-- seq 0 7 100 | xargs -i date -d "02/24/2021 {} days" +"%d.%m.%Y"  -->

| Week | Date     | Topic            | Lectures (14:15-16:00) | Exercises (14:15-16:00) | Labs (16:15-18:00) |
| :--  | :--      | :--              | :--                    | :--                     | :--                |
| 1    | 24.02.21 | Parallelism 1    | Prerecorded            | Welcome Zoom session    | Lab 1              |
| 2    | 03.03.21 | Parallelism 2    | Prerecorded            | Exercise 1              | Lab 1 & 2          |
| 3    | 10.03.21 | Parallelism 3    | Prerecorded            | Exercise 2              | Lab 2 & 3          |
| 4    | 17.03.21 | Parallelism 4    | Prerecorded            | Exercise 3              | Lab 3 & 4          |
| 5    | 24.03.21 | Concurrency 1    | Live                   |                         | Lab 4 & 5          |
| 6    | 31.03.21 | Concurrency 2    | Live                   |                         | Lab 5 & 6          |
| 7    | 07.04.21 | _Easter_         |                        |                         |                    |
| 8    | 14.04.21 | **Midterm Exam** |                        |                         |                    |
| 9    | 21.04.21 | Concurrency 3    | Live                   |                         | Lab 6              |
| 10   | 28.04.21 | Actors 1         | Prerecorded            | Exercise 4              | Lab 7              |
| 11   | 05.05.21 | Actors 2         | Prerecorded            | Exercise 5              | Lab 7              |
| 12   | 12.05.21 | Spark 1          | Prerecorded            | Exercise 6              | Lab 8              |
| 13   | 19.05.21 | Spark 2          | Prerecorded            | Exercise 7              | Lab 8 & 9          |
| 14   | 26.05.21 | Spark 3          | Prerecorded            | Exercise 8              | Lab 9              |
| 15   | 02.06.21 | **Final Exam**   |                        |                         |                    |

Solutions to the exercises are released after each deadline. We do not provide solutions for the labs.

Before each Discord session, students should watch videos corresponding to that week's topic:

### Intro
- [Welcome Zoom session][Zoom1]

### Parallelism 1: Introduction to Parallel Programming

- [Introduction to Parallel Computing](https://www.youtube.com/watch?v=94O72nyNFY0)
- [Parallelism on the JVM I](https://www.youtube.com/watch?v=I8w-q1TPtjA)
- [Parallelism on the JVM II](https://www.youtube.com/watch?v=BbVWGWTNAXw)
- [Running Computations in Parallel](https://www.youtube.com/watch?v=KkMZGJ3M2-o)
- [Monte Carlo Method to Estimate Pi](https://www.youtube.com/watch?v=VBCf-aTgpPU)
- [First-Class Tasks](https://www.youtube.com/watch?v=mrVVaXCuhBc)
- [How Fast are Parallel Programs?](https://www.youtube.com/watch?v=Lpnexp_Qxgo)
- [Benchmarking Parallel Programs](https://www.youtube.com/watch?v=LvS_kjCssfg)

### Parallelism 2: Basic Task Parallel Algorithms

- [Parallel Sorting](https://www.youtube.com/watch?v=AcuvVgQbphg)
- [Data Operations and Parallel Mapping](https://www.youtube.com/watch?v=ghYtMLrphZw)
- [Parallel Fold (Reduce) Operation](https://www.youtube.com/watch?v=hEBgyhIoWww)
- [Associativity I](https://www.youtube.com/watch?v=q-Cl3whISCY)
- [Associativity II](https://www.youtube.com/watch?v=XBjqYavDUB8)
- [Parallel Scan (Prefix Sum) Operation](https://www.youtube.com/watch?v=CYr3YaQiMwo)

### Parallelism 3: Data-Parallelism

- [Data-Parallel Programming](https://www.youtube.com/watch?v=WW7TabCiOV8)
- [Data-Parallel Operations I](https://www.youtube.com/watch?v=Vd35YQ8DEO4)
- [Data-Parallel Operations II](https://www.youtube.com/watch?v=dcMgKtuAh3s)
- [Scala Parallel Operations](https://www.youtube.com/watch?v=NjkxjAT7ohE)
- [Splitters and Combiners](https://www.youtube.com/watch?v=Redz85Nlle4)

### Parallelism 4: Data-Structures for Parallel Computing

- [Implementing Combiners](https://www.youtube.com/watch?v=dTP0ntniB2I)
- [Parallel Two-phase Construction](https://www.youtube.com/watch?v=XcMtq3OdjQ0)
- [Conc-Tree Data Structure](https://www.youtube.com/watch?v=cUXHXKL8Xvs)
- [Amortized, Constant-Time Append Operation](https://www.youtube.com/watch?v=Ic5DUZLITVI)
- [Conc-Tree Combiners](https://www.youtube.com/watch?v=aLfFlCC1vjc)

### Concurrency 1, 2 & 3

- Live lectures
  - Concurrency 1 ([Zoom Recording][ZoomConcurrency1]) ([YouTube version][YouTubeConcurrency1])
  - Concurrency 2 ([Zoom Lecture][ZoomConcurrency2]): 31.03.21 from 14:15 to 16:00

### Actors 1

- [Introduction: why actors?](https://www.youtube.com/watch?v=ZQAe9AItH8o)
- [The Actor Model](https://www.youtube.com/watch?v=c49tDZuFtPA)

### Actors 2

- [Message Processing Semantics](https://www.youtube.com/watch?v=Uxn1eg6R0Fc)
- [Designing Actor Systems](https://www.youtube.com/watch?v=uxeMJLo3h9k)
- [Testing Actor Systems](https://www.youtube.com/watch?v=T_2nwLr-H2s)

### Spark 1: Spark Basics

- [From Parallel to Distributed](https://www.youtube.com/watch?v=bfMbJ8NzTZI)
- [Latency](https://www.youtube.com/watch?v=igNIz2Ent5E)
- [RDDs, Spark's Distributed Collection](https://www.youtube.com/watch?v=EuVmW62aIXI)
- [RDDs: Transformations and Actions](https://www.youtube.com/watch?v=qJlfATheS38)
- [Evaluation in Spark: Unlike Scala Collections!](https://www.youtube.com/watch?v=0pVYuuUrN74)
- [Cluster Topology Matters](https://www.youtube.com/watch?v=lS4vRzwrmtU)

### Spark 2: Reduction Operations & Distributed Key-Value Pairs

- [Reduction Operations](https://www.youtube.com/watch?v=JhF0_Ka_iqU)
- [Pair RDDs](https://www.youtube.com/watch?v=kIUzgweDMUs)
- [Transformations and Actions on Pair RDDs](https://www.youtube.com/watch?v=ovf0GFbnp5g)
- [Joins](https://www.youtube.com/watch?v=kYpaZpj4qTM)

### Spark 3: Partitioning and Shuffling

- [Shuffling: What it is and Why it's important](https://www.youtube.com/watch?v=LrgA4PrKrks)
- [Partitioning](https://www.youtube.com/watch?v=sTcki6mxjcA)
- [Optimizing with Partitioners](https://www.youtube.com/watch?v=4Vfp5kp2jnE)
- [Wide vs Narrow Dependencies](https://www.youtube.com/watch?v=L9BnaYp10c8)


# Labs

Labs are individual assignments where you get to write Scala programs using the concepts learned during lectures.
Labs are submitted by pushing your code on GitLab, see details in the [grading and submission](labs/grading-and-submission.md) page.

| Labs  | Name                       | Start date | Due date (23:59 [AoE](https://en.wikipedia.org/wiki/Anywhere_on_Earth)) |
| :--   | :--                        | :--        | :--        |
| Lab 1 | Parallel Box Blur Filter   | 24.02.21   | 07.03.2021 |
| Lab 2 | Reductions and Prefix Sums | 01.03.21   | 14.03.2021 |
| Lab 3 | K-Means                    | 08.03.21   | 21.03.2021 |
| Lab 4 | Barnes-Hut Simulation      | 15.03.21   | 28.03.2021 |
| Lab 5 | Bounded Buffer             | 22.03.21   | 04.04.2021 |
| Lab 6 | Lock-free Sorted List      | 29.03.21   | 25.04.2021 |
| Lab 7 | Actors Binary Tree         | 26.04.21   | 09.05.2021 |
| Lab 8 | Wikipedia                  | 10.05.21   | 23.05.2021 |
| Lab 9 | StackOverflow              | 17.05.21   | 30.05.2021 |

# Exercises

Exercises are pen and paper style questions that will help you consolidate the knowledge learned during lectures.
Exercises should be done in groups and submitted on GitLab.
You should form groups of up to five students for each exercise, solve the exercise remotely with your group (using Discord, Hangouts, Zoom, ...), and write your solutions in a text file.
The first line of your solution file should list all the group members' SCIPER numbers.
After you solve the exercise with your group, **each member should submit a copy of this file** to their GitLab repository following the instructions given in the problem statement.
Exercises will be given a participation grade at the end of the semester, which accounts for 5% of the overall course grade.

| Exercises  | Start date | Due date (23:59 [AoE](https://en.wikipedia.org/wiki/Anywhere_on_Earth)) |
| :--        | :--        | :--        |
| Exercise 1 | 01.03.2021 | 07.03.2021 |
| Exercise 2 | 08.03.2021 | 14.03.2021 |
| Exercise 3 | 15.03.2021 | 21.03.2021 |
| Exercise 4 | 26.04.2021 | 02.05.2021 |
| Exercise 5 | 03.05.2021 | 09.05.2021 |
| Exercise 6 | 10.05.2021 | 16.05.2021 |
| Exercise 7 | 17.05.2021 | 23.05.2021 |
| Exercise 8 | 24.05.2021 | 30.05.2021 |

# Exams

The midterm exam will take place on 14.04.21. The midterm exam will cover all the material seen in the class up to week 6 (included).

The final exam will take place on 02.06.21. The final exam will cover all material seen during the semester.

Information about exams organization will be communicated by email.

[Zoom1]: https://epfl.zoom.us/rec/share/wKUHvD1vNyczKpb2e161QCnqi3BtURL5D9zfVie3iXqXmrsIbJ2lrl4cCO_1FgSx.ZfvYlrRfSAFFdMcq?startTime=1614170578000
[ZoomConcurrency1]: https://epfl.zoom.us/rec/play/LzV8eccIFbEgHMi-DLJqynUus7chng8NYyR7XeA5Jjn_NGqkeAhIiTNsOLmIOp0XJHfjrNeB-XS6F5Fw.47WW7-mux-mqQZN8?startTime=1616586685000
[YouTubeConcurrency1]: https://www.youtube.com/watch?v=5oUpSoUoII4
[ZoomConcurrency2]: https://epfl.zoom.us/j/88026736642
[ZoomConcurrency3]: https://epfl.zoom.us/j/87636608499
