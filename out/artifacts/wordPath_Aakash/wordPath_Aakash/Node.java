//**************************************************************
// Aakash Basnet
// Node class is the class which links word with its neighbours and also contains
//fields for search
//*************************************************************

import java.util.ArrayList;

public class Node
{
  String name;
  public ArrayList<Node> possibleWords = new ArrayList<>();
  int momentCost = 1;
  int gValue; // distance from start
  int hValue; // distance to goal(Levienstien Distance)
  int visited;// how many times the node is visite
  int fValue;// the heuristic cost; gcalue + h value


  Node parent;

  /**
   * It takes string and add to the name field
   *
   * @param word This is a constructor for the Node class. It also
   *             initiliaze the neighbour arraylost of node.
   */
  public Node(String word)
  {
    this.name = word;
    this.possibleWords = new ArrayList<>();
    this.visited = 0;
  }

  public void calculateHValue(Node goal)
  {
    this.hValue = levenshteinDistance(this.name, goal.name);
  }

  /**
   * @param a
   * @param b
   * @return the lavenshteinDistance
   * It calculates the estimate distance from current word to
   * goal word.
   * source:https://rosettacode.org/wiki/Levenshtein_distance
   */
  private int levenshteinDistance(String a, String b)
  {
    a = a.toLowerCase();
    b = b.toLowerCase();
    int[] costs = new int[b.length() + 1];
    for (int j = 0; j < costs.length; j++)
      costs[j] = j;
    for (int i = 1; i <= a.length(); i++)
    {
      costs[0] = i;
      int nw = i - 1;
      for (int j = 1; j <= b.length(); j++)
      {
        int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
        nw = costs[j];
        costs[j] = cj;
      }
    }
    return costs[b.length()];
  }

}