package edu.coursera.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SumRecursiveTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 5000;
    //private static ForkJoinPool POOL = new ForkJoinPool(2);
    private int low, high;
    private int[] array;

    public SumRecursiveTask(int low, int high, int[] array) {
        this.low = low;
        this.high = high;
        this.array = array;
    }

    @Override
    protected Long compute() {
        if (high - low <= THRESHOLD) {
            long sum = 0;
            for (int i = low; i < high; ++i) {
                sum += array[i];
            }

            return sum;
        } else {
            int mid = low + (high - low) / 2;
            SumRecursiveTask left = new SumRecursiveTask(low, mid, array);
            SumRecursiveTask right = new SumRecursiveTask(mid, high, array);
            left.fork();
            long rightResult = right.compute();
            long leftResult = left.join();

            return leftResult + rightResult;
        }
    }

    static long parallelSum(int[] array) {
        return ForkJoinPool.commonPool().invoke(new SumRecursiveTask(0, array.length, array));
    }

    static long sequentialSum(int[] array) {
        long sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }

    public static void main(String[] args) {
        int n = 200_000_000;
        int[] arr = new int[n];
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < n; i++) {
                arr[i] = arr[i] + i;
            }

            long before = System.nanoTime();
            long result = sequentialSum(arr);
            long after = System.nanoTime();
            System.out.format("Time of sequential execution: %d in %s \n", result, (after - before));

            before = System.nanoTime();
            result = parallelSum(arr);
            after = System.nanoTime();
            System.out.format("Time of   parallel execution: %d in %s \n\n", result, (after - before));
        }
    }
}
