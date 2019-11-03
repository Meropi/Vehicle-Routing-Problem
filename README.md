# Vehicle-Routing-Problem
To deal with the Vehicle Routing problem a set of assumptions were made:
1) the overall distance to be minimized.
2) each customer to have a pre-defined demand which should be covered by one vehicle-visit. 
3) each vehicle has to depart from the depot and after serving a number of customers to return to the same depot.
4) each vehicle has specific capacity which should be respected.
So, the logic to solve this problem, under the aforementioned assumptions, was:

-->	Solution structure: A set of routes that start from the depot and end to it.

-->	Solution component: A customer. 

-->	Selection criterion of the “best” feasible candidate “solution component”: At each iteration select the customer that adds the minimum cost (distance) to the partial solution and simultaneously respects the demand. If the demand requirement is not respected end the route and start a new one.

-->	Evaluation criterion: Minimization of the total cost(distance) of the routes.

Initial solution GreedyVRP.java.

Variations to initial solution:

A) LocalSearchVRP.java
This local search method will consider intra-route relocations. This means that at each iteration, the method should explore all potential relocations of the customers within their routes. The relocation yielding the best solution cost improvement should be selected to be applied to the candidate solution. The method should terminate if no improving intra-route relocation can be identified.

1. Relocation Move (LOCAL_SEARCH_MODE = 0)

2. SWAP Move(LOCAL_SEARCH_MODE = 1)
 
B) LocalSearchExtendedVRP.java
This local search method will consider all possible customer relocations (both intra- and inter-route). This means that at each iteration, the method should explore all potential relocations of customers to any point of the existing solution. 
 
C) TabuSearchVRPFull.java 
This tabu search method will consider all possible customer relocations (both intra- and inter-route). Also the code of the LocalSearchExtendedVRP.java should be used extended by a tabu policy of your selection. The method should terminate after 200 iterations. 
