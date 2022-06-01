# Problem 2: Wait and Notify

## Setup

Use the following commands to make a fresh clone of your repository:

```
git clone -b concpar22final02 git@gitlab.epfl.ch:lamp/student-repositories-s22/cs206-GASPAR.git concpar22final02
```

If you have issues with the IDE, try [reimporting the
build](https://gitlab.epfl.ch/lamp/cs206/-/blob/master/labs/example-lab.md#troubleshooting),
if you still have problems, use `compile` in sbt instead.

## Problem 2.1: Implement Barrier methods

Your first task is to implement a _barrier_. A barrier acts as a synchronization point between multiple threads. It is used when you want all threads to finish a task before starting the next one. 

You have to complete the following two methods in the `Barrier` class:
1. `awaitZero` function waits for the count value to be zero.
2. `countDown` function decrements the count by one. It notifies other threads if count is less than or equal to zero. 

The barrier will be implemented using these functions. When the thread finish a task, it will decrement the count by using the `countDown` function. Then, it will call the `awaitZero` function to wait for other threads.

## Problem 2.2: Use the Barrier to apply filters to an image

In this part, each thread will apply an array of filters to each row of the image. Your task is to use the `Barrier` to act as a synchronization point while applying the filters. Each thread should wait for the other threads to complete the current filter before applying the next filter.

`ImageLib.scala` provides an implementation of an image processing library with different filters. Each filter has a kernel which is applied on the image. `ImageLib.scala` implements four different filters. It provides an `applyFilter` method which applies a particular filter's kernel on a particular row on the input `Array` and generates the output in the output `Array`.

The `ImageLib` class takes the size of an image as input. The image is a square matrix. The class has two buffers `buffer1` and `buffer2`. The initial image will be in `buffer1`. For the filtering task, you will switch between these buffers as input and output. For example, for the first filter `buffer1` will the input buffer and `buffer2` will be the output buffer. For the second filter `buffer2` will the input and `buffer1` will be the output and so on for subsequent filters.

In `Problem2.scala` file where you will complete the `imagePipeline` function:

- The `imagePipeline` function gets a array of filters and an array of row numbers. This filter needs to be applied to all the rows present in `row` array. After applying each filter, the thread has to wait for other threads to complete before applying the next filter. You will use barrier in this case. 
- The `imagePipeline` function will return the output buffer. Note the output buffer can change between `buffer1` and `buffer2` depending on the number of filters applied.

You can get partial points for solving parts of this exercise.
