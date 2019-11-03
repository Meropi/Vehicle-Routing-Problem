import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class LocalSearchExtendedVRP {

	public static void main(String[] args) 
	{	//110592
		int myBirthNumber = 30041992;

		Random ran = new Random(myBirthNumber);

		//Set up Input for VRP
		int numberOfCustomers = 30;
		//int numberOfVehicles = 10;
		int vehicleCapacity = 50;

		//Create the depot
		Node depot = new Node();
		depot.x = 50;
		depot.y = 50;
		depot.ID = 0;
		
		//Create the list with the customers
		ArrayList <Node> customers = new ArrayList<Node>();
		for (int i = 1 ; i <=numberOfCustomers; i++)
		{
			Node cust = new Node();

			cust.x = ran.nextInt(100);
			cust.y = ran.nextInt(100);
			cust.demand = 4+ran.nextInt(7);
			cust.ID = i;
			customers.add(cust);
		}

		//Build the allNodes array and the corresponding distance matrix
		ArrayList <Node> allNodes = new ArrayList<Node>();

		allNodes.add(depot);
		for (int i = 0 ; i < customers.size(); i++)
		{
			Node cust = customers.get(i);
			allNodes.add(cust);
		}
		
		for (int i = 0 ; i < allNodes.size(); i++)
		{
			Node nd = allNodes.get(i);
			nd.ID = i;

		}


		// This is a 2-D array which will hold the distances between node pairs
		// The [i][j] element of this array is the distance required for moving 
		// from the i-th node of allNodes (node with id : i)
		// to the j-th node of allNodes list (node with id : j)
		double [][] distanceMatrix = new double [allNodes.size()][allNodes.size()];
		for (int i = 0 ; i < allNodes.size(); i++)
		{
			Node from = allNodes.get(i);

			for (int j = 0 ; j < allNodes.size(); j++)
			{
				Node to = allNodes.get(j);

				double Delta_x = (from.x - to.x);
				double Delta_y = (from.y - to.y);
				double distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));

				distance = Math.round(distance);

				distanceMatrix[i][j] = distance;

			}
		}
		// This is the solution object - It will store the solution as it is iteratively generated
		// The constructor of Solution class will be executed
		Solution s = new Solution();

		Route route = new Route();
		
		// indicate that all customers are non-routed
		for (int i = 0 ; i < customers.size(); i++)
		{
			customers.get(i).isRouted = false;
		}

		ArrayList <Node> nodeSequence = route.nodes;
		nodeSequence.add(depot);
		
		int CountOfNonRouted=numberOfCustomers;
		int currentRouteDemand=0;

		while (CountOfNonRouted!=0) {
			
				//this will be the position of the nearest neighbor customer -- initialization to -1
				int positionOfTheNextOne = -1;

				// This will hold the minimal cost for moving to the next customer - initialized to something very large
				double bestCostToTheNextOne = Double.MAX_VALUE;

				//This is the last customer of the route (or the depot if the route is empty)
				Node lastInTheRoute = nodeSequence.get(nodeSequence.size() - 1);


				//First Step: Identify the non-routed nearest neighbor (his position in the customers list) of the last node in the nodeSequence list
				for (int j = 0 ; j < customers.size(); j++)
				{
					// The examined node is called candidate
					Node candidate = customers.get(j);

					// if this candidate has not been pushed in the solution
					if (candidate.isRouted == false)
					{
						//This is the cost for moving from the last to the candidate one
						double trialCost = distanceMatrix[lastInTheRoute.ID][candidate.ID];

						//If this is the minimal cost found so far -> store this cost and the position of this best candidate
						if (trialCost < bestCostToTheNextOne)
						{
							positionOfTheNextOne = j;
							bestCostToTheNextOne = trialCost;
						}						
					}
									
				}
				currentRouteDemand=0;
				for (int b=0; b<nodeSequence.size(); b++) {
					currentRouteDemand=currentRouteDemand+nodeSequence.get(b).demand;
				}
				
				if (customers.get(positionOfTheNextOne).demand+currentRouteDemand<=vehicleCapacity) {
					Node insertedNode=customers.get(positionOfTheNextOne);
					nodeSequence.add(insertedNode);
					insertedNode.isRouted = true;
					CountOfNonRouted=CountOfNonRouted-1;
					route.cost=route.cost+bestCostToTheNextOne;
				}
				else {
					nodeSequence.add(depot);
					route.cost = route.cost + distanceMatrix[lastInTheRoute.ID][depot.ID];
					s.cost = s.cost + route.cost;
					s.rtlist.add(route);
					drawRoutes(route, allNodes, Integer.toString(s.rtlist.size()));
					route = new Route();
					nodeSequence=route.nodes;
					nodeSequence.add(depot);
				}
				if (CountOfNonRouted==0) {
					lastInTheRoute = nodeSequence.get(nodeSequence.size() - 1);
					nodeSequence.add(depot);
					route.cost = route.cost + distanceMatrix[lastInTheRoute.ID][depot.ID];
					s.cost = s.cost + route.cost;
					s.rtlist.add(route);
					drawRoutes(route, allNodes, Integer.toString(s.rtlist.size()));
				}
				}
		int routeDemand;
		System.out.println("INITIAL SOLUTION NN");
		System.out.println("Number of Vehicles used:"+s.rtlist.size());
		System.out.println("Total Solution Cost:"+s.cost);
		for(int l=0;l<s.rtlist.size();l++){
			System.out.println("------------------");
			System.out.println("Vehicle "+(l+1)+" ");
			System.out.println("Route cost:"+s.rtlist.get(l).cost);
			routeDemand=0;
			for(int k=0; k<s.rtlist.get(l).nodes.size(); k++) 
			{
			if (k==0)
			System.out.print("Node sequence:"+s.rtlist.get(l).nodes.get(k).ID);
			else
			System.out.print("->"+s.rtlist.get(l).nodes.get(k).ID);
			routeDemand=routeDemand+s.rtlist.get(l).nodes.get(k).demand;
			}
			System.out.println("");
			System.out.println("Route demand:"+routeDemand);
		}
//END OF NN CODE
//
//The NN Solution has been generated
//
////////////////////////////////////////////////////////////////////////////////////////////////////
//START OF LOCAL SEARCH CODE/////////////////////////////////////////////////////////////////////////
//
//The NN Solution has been generated
//
//Local Search
//this is a boolean flag (true/false) for terminating the local search procedure
boolean terminationCondition = false;
				

System.out.println("------------------");
System.out.println("------------------");
System.out.println("------------------");
System.out.println("START OF LOCAL SEACRH");
 
	
//This is an object for holding the best relocation move that can be applied to the candidate solution
RelocationMove rm = new RelocationMove();
terminationCondition = false;

//this is a counter for holding the local search iterators
int localSearchIterator = 0;
int intraIterator = 0;
int interIterator= 0;



		// Until the termination condition is set to true repeat the following block of code
		while (terminationCondition == false)
		{
			//Initialize the relocation move rm
			rm.positionOfRelocated = -1;
			rm.positionToBeInserted = -1;
			rm.moveCost = Double.MAX_VALUE;

			//With this function we look for the best relocation move
			//the characteristics of this move will be stored in the object rm
			findBestRelocationMove(rm, s, distanceMatrix, vehicleCapacity);
			
			if (rm.routeOfRelocated!= rm.routeToBeInserted) {
				interIterator=interIterator+1;
			}
			else {
				intraIterator=intraIterator+1;
			}
			
			// If rm (the identified best relocation move) is a cost improving move, or in other words
			// if the current solution is not a local optimum
			//if (l==0 || l==1 || l==2 || l==3) {
			if (rm.moveCost < 0)
			{
			
				//This is a function applying the relocation move rm to the candidate solution
			applyRelocationMove(rm, s,  distanceMatrix);

				//my function just to visualize things
			//	drawRoutes(s, allNodes, Integer.toString(localSearchIterator));
			}
			else
			{
				//if no cost improving relocation move was found,
				//or in other words if the current solution is a local optimum
				//terminate the local search algorithm
				terminationCondition = true;
			}
			//}
			//else {
			//	applyRelocationMove(rm, s, s.rtlist.get(l), distanceMatrix);
			//}
			
			localSearchIterator = localSearchIterator + 1;
		}
	

System.out.println("Total No of iterations to improve route of Vehicles is:"+localSearchIterator);
System.out.println("No of intra-route relocations is:"+intraIterator);
System.out.println("No of inter-route relocations among routes is:"+interIterator);


//Check total cost for debugging purposes
double totalco=0;
for(int m=0; m<s.rtlist.size(); m++) {
	totalco=totalco+s.rtlist.get(m).cost;
}

if (totalco != s.cost) {
		System.out.println("Something Went wrong with the total cost calculations !!!!");	
	}

System.out.println("END OF EXTENDED LOCAL SEARCH");
System.out.println("RESULTS OF EXTENDED LOCAL SEACRH");
System.out.println("------------------");
		int routeDemand2;
		System.out.println("Number of Vehicles used:"+s.rtlist.size());
		System.out.println("Total Solution Cost:"+s.cost);
		for(int l=0;l<s.rtlist.size();l++){
			System.out.println("------------------");
			System.out.println("Vehicle "+(l+1)+" ");
			System.out.println("Route cost:"+s.rtlist.get(l).cost);
			routeDemand2=0;
			for(int k=0; k<s.rtlist.get(l).nodes.size(); k++) 
			{
			if (k==0)
			System.out.print("Node sequence:"+s.rtlist.get(l).nodes.get(k).ID);
			else {
			System.out.print("->"+s.rtlist.get(l).nodes.get(k).ID);
			routeDemand2=routeDemand2+s.rtlist.get(l).nodes.get(k).demand;
			}}
			System.out.println("");
			System.out.println("Route demand:"+routeDemand2);}
		
	}

	
	
	private static void findBestRelocationMove(RelocationMove rm, Solution s, double [][] distanceMatrix, int vehicleCapacity) 
	{
		
		//This is a variable that will hold the cost of the best relocation move
		double bestMoveCost = Double.MAX_VALUE;
		/*double costRemoved1=0;
		double costRemoved2=0;
		double costRemoved=0;
		double costAdded1=0;
		double costAdded2=0;
		double costAdded=0;
		double moveCost=0;
		double moveCostRouteOfRelocated=0;
		double moveCostRouteToBeInserted=0;*/

for (int l=0; l<s.rtlist.size(); l++) {
		
		//We will iterate through all available routes to be relocated
		for (int relIndex = 1; relIndex < s.rtlist.get(l).nodes.size() - 1; relIndex++)
		{
			//Node A is the predecessor of B
			Node A = s.rtlist.get(l).nodes.get(relIndex - 1);
			//Node B is the relocated node
			Node B = s.rtlist.get(l).nodes.get(relIndex);
			//Node C is the successor of B
			Node C = s.rtlist.get(l).nodes.get(relIndex + 1);

			for (int m=0; m<s.rtlist.size(); m++) {
			//We will iterate through all possible re-insertion positions for B
				
				int routeDemand=0;
				
				for(int k=0; k<s.rtlist.get(m).nodes.size(); k++) 
				{routeDemand=routeDemand+s.rtlist.get(m).nodes.get(k).demand;}
				
				
				for (int afterInd = 0; afterInd < s.rtlist.get(m).nodes.size() -1; afterInd ++)
				{
				// Why do we have to write this line?
				// This line has to do with the nature of the 1-0 relocation
				// If afterInd == relIndex -> this would mean the solution remains unaffected
				// If afterInd == relIndex - 1 -> this would mean the solution remains unaffected
					
				if (l==m) {
					if (afterInd != relIndex && afterInd != relIndex - 1){
						//Node F the node after which B is going to be reinserted
						Node F = s.rtlist.get(m).nodes.get(afterInd);
						//Node G the successor of F
						Node G = s.rtlist.get(m).nodes.get(afterInd + 1);
	
						//The arcs A-B, B-C, and F-G break
						double costRemoved1 = distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID];
						double costRemoved2 = distanceMatrix[F.ID][G.ID];
						double costRemoved = costRemoved1 + costRemoved2;
	
						//The arcs A-C, F-B and B-G are created
						double costAdded1 = distanceMatrix[A.ID][C.ID];
						double costAdded2 = distanceMatrix[F.ID][B.ID] + distanceMatrix[B.ID][G.ID];
						double costAdded = costAdded1 + costAdded2;
	
						//This is the cost of the move, or in other words
						//the change that this move will cause if applied to the current solution
						double moveCost = costAdded - costRemoved;
						double moveCostRouteOfRelocated=costAdded1-costRemoved1;
						double moveCostRouteToBeInserted=costAdded2-costRemoved2;
						
						if (moveCost < bestMoveCost) 
						{
							//set the best cost equal to the cost of this solution
							bestMoveCost = moveCost;
	
							//store its characteristics
							rm.positionOfRelocated = relIndex;
							rm.positionToBeInserted = afterInd;
							rm.moveCost = moveCost;
							rm.routeOfRelocated= l;
							rm.routeToBeInserted= m;
							rm.moveCostRouteOfRelocated=moveCostRouteOfRelocated;
							rm.moveCostRouteToBeInserted=moveCostRouteToBeInserted;			
						}
						}	
				}
				else {
					
					//Node F the node after which B is going to be reinserted
					Node F = s.rtlist.get(m).nodes.get(afterInd);
					//Node G the successor of F
					Node G = s.rtlist.get(m).nodes.get(afterInd + 1);

					//The arcs A-B, B-C, and F-G break
					double costRemoved1 = distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID];
					double costRemoved2 = distanceMatrix[F.ID][G.ID];
					double costRemoved = costRemoved1 + costRemoved2;

					//The arcs A-C, F-B and B-G are created
					double costAdded1 = distanceMatrix[A.ID][C.ID];
					double costAdded2 = distanceMatrix[F.ID][B.ID] + distanceMatrix[B.ID][G.ID];
					double costAdded = costAdded1 + costAdded2;

					//This is the cost of the move, or in other words
					//the change that this move will cause if applied to the current solution
					double moveCost = costAdded - costRemoved;
					double moveCostRouteOfRelocated=costAdded1-costRemoved1;
					double moveCostRouteToBeInserted=costAdded2-costRemoved2;

					 if ((moveCost < bestMoveCost) && ((routeDemand + B.demand) <=vehicleCapacity) )
						{
							//set the best cost equal to the cost of this solution
							bestMoveCost = moveCost;
	
							//store its characteristics
							rm.positionOfRelocated = relIndex;
							rm.positionToBeInserted = afterInd;
							rm.moveCost = moveCost;
							rm.routeOfRelocated= l;
							rm.routeToBeInserted= m;
							rm.moveCostRouteOfRelocated=moveCostRouteOfRelocated;
							rm.moveCostRouteToBeInserted=moveCostRouteToBeInserted;
					
						}
					 
					 
					}
					
				}
			}
		}
	}

	}
	private static void applyRelocationMove(RelocationMove rm, Solution s, double[][] distanceMatrix) 
	{
		
		Node relocatedNode = s.rtlist.get(rm.routeOfRelocated).nodes.get(rm.positionOfRelocated);
		/*
		System.out.println("RouteOfRelocated:"+rm.routeOfRelocated);
		System.out.println("IDOfRelocated:"+s.rtlist.get(rm.routeOfRelocated).nodes.get(rm.positionOfRelocated).ID);
		System.out.println("RouteToBeInserted:"+rm.routeToBeInserted);
		System.out.println("IDToBeInserted:"+s.rtlist.get(rm.routeToBeInserted).nodes.get(rm.positionToBeInserted).ID);
		*/
		
		if (rm.routeOfRelocated!= rm.routeToBeInserted) {
		

		s.rtlist.get(rm.routeOfRelocated).nodes.remove(rm.positionOfRelocated);
		
		s.rtlist.get(rm.routeOfRelocated).cost=s.rtlist.get(rm.routeOfRelocated).cost+rm.moveCostRouteOfRelocated;

		s.rtlist.get(rm.routeToBeInserted).nodes.add(rm.positionToBeInserted +1, relocatedNode);

		s.rtlist.get(rm.routeToBeInserted).cost=s.rtlist.get(rm.routeToBeInserted).cost+rm.moveCostRouteToBeInserted;

		
		}
		
		else {
		
		//Take out the relocated node
		s.rtlist.get(rm.routeOfRelocated).nodes.remove(rm.positionOfRelocated);

		//Reinsert the relocated node into the appropriate position
		//Where??? -> after the node that WAS (!!!!) located in the rm.positionToBeInserted of the route

		//Watch out!!! 
		//If the relocated customer is reinserted backwards we have to re-insert it in (rm.positionToBeInserted + 1)
		if (rm.positionToBeInserted < rm.positionOfRelocated)
		{
			s.rtlist.get(rm.routeOfRelocated).nodes.add(rm.positionToBeInserted + 1, relocatedNode);
		}
		////else (if it is reinserted forward) we have to re-insert it in (rm.positionToBeInserted)
		else
		{
			s.rtlist.get(rm.routeOfRelocated).nodes.add(rm.positionToBeInserted, relocatedNode);
		}


		//update the cost of the solution and the corresponding cost of the route object in the solution
		s.rtlist.get(rm.routeOfRelocated).cost = s.rtlist.get(rm.routeOfRelocated).cost + rm.moveCost;
		
		}
		
		s.cost = s.cost + rm.moveCost;
		//drawRoutes(r, allnodes, Integer.toString(s.rtlist.size()));
		//System.out.println("Cost:"+s.cost);
	}
	
	private static void drawRoutes(Route s, ArrayList<Node> allnodes, String fileName) 
	{

		int VRP_Y = 800;
		int VRP_INFO = 200;
		int X_GAP = 600;
		int margin = 30;
		int marginNode = 1;
		int XXX =  VRP_INFO + X_GAP;
		int YYY =  VRP_Y;


		BufferedImage output = new BufferedImage(XXX, YYY, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = output.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, XXX, YYY);
		g.setColor(Color.BLACK);


		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		for (int i = 0; i < allnodes.size(); i++)
		{
			Node n = allnodes.get(i);
			if (n.x > maxX) maxX = n.x;
			if (n.x < minX) minX = n.x;
			if (n.y > maxY) maxY = n.y;
			if (n.y < minY) minY = n.y;
		}

		int mX = XXX - 2 * margin;
		int mY = VRP_Y - 2 * margin;

		int A, B;
		if ((maxX - minX) > (maxY - minY))
		{
			A = mX;
			B = (int)((double)(A) * (maxY - minY) / (maxX - minX));
			if (B > mY)
			{
				B = mY;
				A = (int)((double)(B) * (maxX - minX) / (maxY - minY));
			}
		}
		else
		{
			B = mY;
			A = (int)((double)(B) * (maxX - minX) / (maxY - minY));
			if (A > mX)
			{
				A = mX;
				B = (int)((double)(A) * (maxY - minY) / (maxX - minX));
			}
		}

		// Draw Route
		for (int i = 1; i < s.nodes.size(); i++)
		{
			Node n;
			n = s.nodes.get(i - 1);
			int ii1 = (int)((double)(A) * ((n.x - minX) / (maxX - minX) - 0.5) + (double)mX / 2) + margin;
			int jj1 = (int)((double)(B) * (0.5 - (n.y - minY) / (maxY - minY)) + (double)mY / 2) + margin;
			n = s.nodes.get(i);
			int ii2 = (int)((double)(A) * ((n.x - minX) / (maxX - minX) - 0.5) + (double)mX / 2) + margin;
			int jj2 = (int)((double)(B) * (0.5 - (n.y - minY) / (maxY - minY)) + (double)mY / 2) + margin;


			g.drawLine(ii1, jj1, ii2, jj2);
		}

		for (int i = 0; i < allnodes.size(); i++)
		{
			Node n = allnodes.get(i);

			int ii = (int)((double)(A) * ((n.x - minX) / (maxX - minX) - 0.5) + (double)mX / 2) + margin;
			int jj = (int)((double)(B) * (0.5 - (n.y - minY) / (maxY - minY)) + (double)mY / 2) + margin;
			if (i != 0)
			{
				g.fillOval(ii - 2 * marginNode, jj - 2 * marginNode, 4 * marginNode, 4 * marginNode);
				String id = Integer.toString(n.ID);
				g.drawString(id, ii + 8 * marginNode, jj+ 8 * marginNode);
			}
			else
			{
				g.fillRect(ii - 4 * marginNode, jj - 4 * marginNode, 8 * marginNode, 8 * marginNode);
				String id = Integer.toString(n.ID);
				g.drawString(id, ii + 8 * marginNode, jj + 8 * marginNode);
			}
		}

		String cst = "Cost: " + s.cost;
		g.drawString(cst, 10, 10);

		fileName = fileName + ".png";
		File f = new File(fileName);
		try 
		{
			ImageIO.write(output, "PNG", f);
		} catch (IOException ex) {
			Logger.getLogger(LocalSearchExtendedVRP.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
	
}

class Node 
{
	int x;
	int y;
	int demand;
	int ID;

	// true/false flag indicating if a customer has been inserted in the solution
	boolean isRouted; 

	Node() 
	{
	}
}

class Solution 
{
	double cost;
	ArrayList<Route> rtlist;

	//This is the Solution constructor. It is executed every time a new Solution object is created (new Solution)
	Solution ()
	{
		// A new route object is created addressed by rt
		// The constructor of route is called
		rtlist = new ArrayList<Route>();
		cost = 0;
	}
}

class Route 
{
	ArrayList <Node> nodes;
	double cost;

	//This is the Route constructor. It is executed every time a new Route object is created (new Route)
	Route() 
	{
		cost = 0;
		// A new arraylist of nodes is created
		nodes = new ArrayList<Node>();
	}
}

class RelocationMove 
{
	int positionOfRelocated;
	int positionToBeInserted;
	int routeOfRelocated;
	int routeToBeInserted;
	double moveCostRouteOfRelocated;
	double moveCostRouteToBeInserted;
	double moveCost;

	RelocationMove() 
	{
	}
}
