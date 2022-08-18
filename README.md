## Table of Content
1. [Two-Dimensional Bin Packing Problem](#two-dimensional-bin-packing-problem)
2. [Data Structure](#data-structure)
3. [Packing Heuristics](#packing-heuristics)
4. [Dataset](#dataset)
5. [Usage](#usage)
6. [Python](#python)


# Two-Dimensional Bin Packing Problem
The two-dimensional bin packing problem (2BPP) is an <i>NP-Hard</i> problem which has been 
subject of research in <i>combinatorial optimization</i> for quite some time now.

2BPP is concerned with packing two-dimensional items in two-dimensional bins. The version of 
the 2BPP that we considered here is referred to as <i>rectangular bin packing problem</i>
since the bins and the items are rectangular.

The one-dimensional bin packing problem, which is a special case of 2BPP, was one of the 
problem chosen in the <a href="https://link.springer.com/chapter/10.1007/978-3-642-25566-3_49" target="_blank">CHeSC-2011</a> 
challenge for cross-domain search techniques.


# Data Structure
There are several data structures proposed to track the free spaces in the bins after packing 
items such as the shelf data structure. In this implementation we used the maximal space data 
structure which is the most efficient for such a problem. Please read 
<a href="https://www.sciencedirect.com/science/article/pii/S0925527313001837" target="_blank">this paper</a> for
further information.


# Packing Heuristics
The placement (the location) of the items inside the bin is determined by a packing heuristic that 
selects an appropriate free maximal space to place the item in. 
Three packing heuristics are implemented which are 
<ul> 
    <li>Best area fit.</li>
    <li>Touching perimeter.</li>
    <li> Distance to the front-top-right corner.</li>
</ul>
Please read <a href="https://www.sciencedirect.com/science/article/pii/S095741741930257X" target="_blank">this paper</a>
to understand these packing heuristics.


# Dataset
The benchmark dataset is grouped into 10 classes. Each class is subdivided into 5 categories. 
Each category contains 10 instances of sizes 20, 40, 60, 80 or 100. In total, there are 500 instances.
Most of the instances from categories (60 and above) are not solved optimally. Therefore, there is a 
room for improvement.

You can download the dataset from <a href="http://or.dei.unibo.it/library/two-dimensional-bin-packing-problem" target="_blank">here</a>.


# Usage
You can easily use my implementation as explained below
```java
    //Create an instance of the two-dimensional bin packing problem seeded with 12345
    RectPacking problem = new RectPacking(12345);
    //Read the problem instance file
    problem.read(filename);
    //The problem file consists of 50 instances. We need to define which instance we are solving
    problem.setInstance(0);
    //Create an initial solution
    RBPSolution solution = problem.initializeSolution();
    //Check if the solution is feasible (valid)
    if(!solution.isFeasible()){
        throw new RuntimeException("Infeasible solution");
    }
    System.out.println("Number of bins (using best area fit heuristic) = " + solution.getNumberOfBin());
```

The above example create a solution in which the items are packed using the default packing heuristic which is 
best area fit. To pack the items using other packing heuristics do the following:
```java
    //Create an instance of the two-dimensional bin packing problem seeded with 12345
    RectPacking problem = new RectPacking(12345);
    //Read the problem instance file
    problem.read(filename);
    //The problem file consists of 50 instances. We need to define which instance we are solving
    problem.setInstance(0);
    // Create an empty solution (does not have bins)
    RectPacking solution = problem.getEmptySolution();
    //Get the items to be packed
    List<Rect> queue = problem.getPackingQueue();
    //You can shuffle or sort it
    Random rng = new Random(123456);
    Collections.shuffle(queue, rng);
    solution.pack(queue, RectPacking.PackingHeuristic.TouchingPerimeter);
    //Check if the solution is feasible (valid)
    if(!solution.isFeasible()){
        throw new RuntimeException("Infeasible solution");
    }
    System.out.println("Number of bins (using touching perimeter heuristic) = " + solution.getNumberOfBin());
```

# Python
If you prefer Python, I have a Python implementation over <a href="https://github.com/Al-Madina/pyRectPacking" target="_blank">here</a>.
However, it is slower than the Java implementation.