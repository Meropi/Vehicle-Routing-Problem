
/*
 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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



/**
 *
 * @author AUEB-QUAD
 */
public class GreedyVRP {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) 
	{	
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
				//System.out.print(distanceMatrix[i][j]+"|");

			}
			//System.out.println("");
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
			Logger.getLogger(GreedyVRP.class.getName()).log(Level.SEVERE, null, ex);
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
