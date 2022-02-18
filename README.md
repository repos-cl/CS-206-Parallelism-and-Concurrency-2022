This repository will be used as the website for Parallelism and Concurrency CS-206. It will be updated weekly throughout the semester. This README contains general information about the class.

- [previous-exams](previous-exams) contains PDFs for the previous exams.
- [exercises](exercises) contains markdown documents for exercises and solutions.
- [slides](slides) contains the slides presented in class.
- [labs](labs) contains markdown documents for the labs.

We will use GitLab's issue tracker as a discussion forum. Feel free to [open an issue](https://gitlab.epfl.ch/lamp/cs206/issues) if you have any comments or questions.

# First-week tasks

1. Join [the Discord](https://discord.gg/tgbPcCFSm2)
1. Log into gitlab: https://gitlab.epfl.ch/users/sign_in
1. Please fill in [this form](https://forms.gle/N6F3Q3jZm71AASby9) with your GASPAR and SCIPER number
1. Please fill in [this doodle](https://doodle.com/poll/yi2c33zkb8nre3ug) by picking the room in which you will do the exercises.
1. Follow the [Tools Setup](labs/tools-setup.md) page.
1. Do the [example lab](labs/example-lab.md).
1. Watch all videos under *Parallelism 1: Introduction to Parallel Programming* below
1. Do the [first graded lab](labs/lab1-parallel-box-blur-filter/).

# Grading

The grading of the course is divided between labs (30%), midterm exam (30%) and final exam (40%).

# Staff

| Role        | People |
| :---        | :--- |
| Professors  | [Martin Odersky](https://people.epfl.ch/martin.odersky), [Kashyap Sanidhya](https://people.epfl.ch/sanidhya.kashyap) |
| TAs         | [Dragana Milovancevic](https://people.epfl.ch/dragana.milovancevic),  [Matthieu Bovel](https://people.epfl.ch/matthieu.bovel), [Simon Guilloud](https://people.epfl.ch/simon.guilloud), [Tao Lyu](https://people.epfl.ch/tao.lyu), [Gupta Vishal](https://people.epfl.ch/vishal.gupta)|
| Student TAs | Mohamed Boukhari, Nicolas Matekalo, Tuomas Pääkkönen, Abel Wilkinson |

# Course Schedule

Lectures are partially live and partially prerecorded (on YouTube).
Live sessions will be held on Wednesdays from 14:15 to 16:00 if it is a week with live lecture.
Exercise sessions will be held on Wednesdays from 14:15 to 16:00  if it is a week with exercises.
Lab sessions will be held on Wednesdays from 16:15 to 18:00.
You should watch the prerecorded lectures before doing the exercies.

<!-- seq 0 7 100 | xargs -i date -d "02/24/2021 {} days" +"%d.%m.%Y"  -->

| Week | Date       | Topic            | Lectures (14:15-16:00) | Exercises (14:15-16:00) | Labs (16:15-18:00) |
| :--  | :--        | :--              | :--                    | :--                     | :--                |
| 1    | 2022.02.23 | Parallelism 1    | Prerecorded            | Welcome session         | Lab 1              |


Solutions to the exercises are released after each deadline. We do not provide solutions for the labs.

Before each Exercise session, students should watch videos corresponding to that week's topic:

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


# Labs

Labs are individual assignments where you get to write Scala programs using the concepts learned during lectures.
Labs are submitted by pushing your code on GitLab, see details in the [grading and submission](labs/grading-and-submission.md) page.

| Labs  | Name                       | Start date  | Due date (23:59 [AoE](https://en.wikipedia.org/wiki/Anywhere_on_Earth)) |
| :--   | :--                        | :--         | :--         |
| Lab 1 | Parallel Box Blur Filter   | 2022.02.23  | 2022.03.06  |

# Exercises

Exercises are pen and paper style questions that will help you consolidate the knowledge learned during lectures.
Exercises should be done in groups during the lecture.

# Exams

The midterm exam will take place on 2022.??.??. The midterm exam will cover all the material seen in the class up to week 6 (included).

The final exam will take place on 2022.??.??. The final exam will cover all material seen during the semester.

Information about exams organization will be communicated by email.

[YouTubeConcurrency1]: https://www.youtube.com/watch?v=5oUpSoUoII4
[YouTubeConcurrency2]: https://www.youtube.com/watch?v=Jvo-vrxaGnk
[YouTubeConcurrency3]: https://www.youtube.com/watch?v=t4tqMzfvclk
