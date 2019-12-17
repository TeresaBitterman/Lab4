import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;


public class MergeSort {



    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );



    /* define constants */

    static long MAXVALUE =  2000000000;

    static long MINVALUE = -2000000000;

    static int numberOfTrials = 50;

    static int MAXINPUTSIZE  = (int) Math.pow(2,10);

    static int MININPUTSIZE  =  1;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time



    static String ResultsFolderPath = "/home/teresa/Results/";//pathname to results folder

    static FileWriter resultsFile;

    static PrintWriter resultsWriter;





    public static void main(String[] args) {



        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized

        long[] testList = createRandomIntegerList(10);
        System.out.println("Checking correctness of MergeSort function");
        System.out.println(java.util.Arrays.toString(testList));
        mergeSort(testList);
        System.out.println(java.util.Arrays.toString(testList));
        System.out.println("-----------------------------");

        System.out.println("Experiment 1 working...");
        runFullExperiment("MergeSort-Exp1-ThrowAway.txt");
        System.out.println("Experiment 2 working...");
        runFullExperiment("MergeSort-Exp2.txt");
        System.out.println("Experiment 3 working...");
        runFullExperiment("MergeSort-Exp3.txt");

    }



    static void runFullExperiment(String resultsFileName){



        try {

            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);

            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {

            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);

            return; // not very foolproof... but we do expect to be able to create/open the file...

        }



        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials

        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial



        resultsWriter.println("#InputSize    AverageTime    DoublingRatio"); // # marks a comment in gnuplot data

        resultsWriter.flush();

        /* for each size of input we want to test: in this case starting small and doubling the size each time */

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {

            // progress message...

            System.out.println("Running test for input size "+inputSize+" ... ");



            /* repeat for desired number of trials (for a specific size of input)... */

            long batchElapsedTime = 0;

            // generate a list of randomly spaced integers in ascending sorted order to use as test input

            // In this case we're generating one list to use for the entire set of trials (of a given input size)

            // but we will randomly generate the search key for each trial

            System.out.print("    Generating test data...");

            long[] testList = createRandomIntegerList(inputSize);

            System.out.println("...done.");

            System.out.print("    Running trial batch...");



            /* force garbage collection before each batch of trials run so it is not included in the time */

            System.gc();





            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)

            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the

            // stopwatch methods themselves

            BatchStopwatch.start(); // comment this line if timing trials individually



            // run the tirals

            for (long trial = 0; trial < numberOfTrials; trial++) {

                // generate a random key to search in the range of a the min/max numbers in the list

                long testSearchKey = (long) (0 + Math.random() * (testList[testList.length-1]));

                /* force garbage collection before each trial run so it is not included in the time */

                // System.gc();



                //TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */

                long[] foundIndex = mergeSort(testList);

                // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually

            }

            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch
            //double prevTimePerTrial =0;

           // double doublingRatio = averageTimePerTrialInBatch / prevTimePerTrial;
            // prevTimePerTrial = averageTimePerTrialInBatch;

            /* print data for this size of input */

            resultsWriter.printf("%12d  %15.2f\n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice

            resultsWriter.flush();

            System.out.println(" ....done.");

        }

    }



    /* return index of the searched number if found, or -1 if not found */

    public static long[] mergeSort(long[] list)
    {
       if(list.length <=1)
       {
           return list;
       }




        long [] first = new long[list.length/2];
        long[] second = new long[list.length - first.length];
        System.arraycopy(list, 0, first, 0, first.length);
           System.arraycopy(list, first.length, second, 0, second.length);

           mergeSort(first);
           mergeSort(second);

           merge(first, second, list);
           return list;
    }
    private static void merge(long[] first, long[] second, long [] result)
    {
        int i1 = 0;
        int i2 = 0;

        int j = 0;
        while(i1 < first.length && i2 < second.length)
        {
            if(first[i1] < second[i2])
            {
                result[j] = first[i1];
                i1++;
            }
            else
            {
                result[j] = second[i2];
                i2++;
            }
            j++;
        }
        System.arraycopy(first, i1, result, j, first.length-i1);
        System.arraycopy(second, i2, result, j, second.length-i2);
    }






    public static long[] createRandomIntegerList(int size) {

        long[] newList = new long[size];
        for (int j = 0; j < size; j++) {
            newList[j] = (long) (MAXVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }


}



