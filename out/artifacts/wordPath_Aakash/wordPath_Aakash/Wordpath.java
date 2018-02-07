//************************************************************
//  Aakash Basnet
//
// This class has a main class which calls  wordPath constructor
// This class take command line argument, reads the dictionary and
// make  a graph and finally search the graph. It also checks for
// error on command line arguments. Finally, finds and print the
// shortest path, if there is any.
//***********************************************************


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Wordpath
{
  private int keepTrack;
  private HashMap<String, Node> wordsMap = new HashMap<>();
  private static BufferedReader reader = null;


  /**
   * @param commandWords Type: array of string, it point to
   *                     command line arguments.
   *                     It is a constructor for Wordpath and checks for error for
   *                     command line arguments
   *                     It shows error message and terminates if the argument is less
   *                     than 3 or even.
   *                     If there is no error, it calls MakeList method to create a map and
   *                     search path
   */

  public Wordpath(String[] commandWords)
  {

    if (commandWords.length < 3)
    {
      System.out.print("ERROR:Command must be greater than 3");
    } else if (commandWords.length % 2 == 0)
    {
      System.out.println("Error. the number of argument must be odd ");
    } else
    {
      MakeList(commandWords);
      //System.out.println("the command argument is fine");
    }

  }

  /**
   * @param command Type: array of string, it point to command line argument.
   *                It contains the path to the dictionary file and start and
   *                end/goal words for A* search.
   *                This method creates buffer reader and reads the word from the dictionary and
   *                creates a maps for all the possible words that can be travelled from one to
   *                another. After, creating a map, it reads the pair of words and calls
   *                passes it to th A* search method to find the shortest path. Before, caliing
   *                A* search, it also checks whether the pair of word are in dictionary. If not,
   *                it will show error message and terminates.
   */
  private void MakeList(String[] command)
  {
    String word = null;
    try
    {
      reader = new BufferedReader(new FileReader(command[0]));
    } catch (IOException e)
    {
      System.out.println("IO Exception: " + e.getMessage());
    }

    try
    {
      while ((word = reader.readLine()) != null)
      {
        Node tempNode = new Node(word);
        DeleteChar(tempNode);
        ReplaceChar(tempNode);
        AddChar(tempNode);
        wordsMap.put(word, tempNode);

      }
    } catch (IOException ex)
    {
      System.out.println("IO Exception: " + ex.getMessage());
    }

        /*Iterator iterator = wordsMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            Node node = wordsMap.get(key);
            System.out.print(key + "   ---->   ");
            for (int i = 0; i < node.possibleWords.size(); i++) {
                System.out.print(node.possibleWords.get(i).name + "  ,  ");
            }

            System.out.println("   ");
        }*/

    for (int i = 1; i < command.length; i += 2)
    {
      String startWord = command[i];
      String goalWord = command[i + 1];
      if (!wordsMap.containsKey(startWord) || !wordsMap.containsKey(goalWord))
      {
        System.out.println("Error: " + startWord + " and " + goalWord + " Word not in dictionary.");
      } else
      {
        AstarSearch(wordsMap.get(startWord), wordsMap.get(goalWord));
        keepTrack++;
      }
    }
  }

  /**
   * @param start Type:Node, contains name, costs and parent field
   *              It is the start point for A*search.
   * @param goal  Type:Node, contains name, costs and parent field
   *              It is the end/goal point for A*Search
   *              This method creates a Piority Queue, whhich also defines compare methode that
   *              keeps the queue of Nodes in ascending order according to the heuristic cost.
   *              It keeps the node with lowest cost at the top so, that we can retrive and
   *              remove it using .poll() method. This method starts searching our graph from the
   *              start node adds it to the piority queue. Then, it searches until the queue is
   *              empty. It searches all the neighbouring nodes with lowest cost and also keeps track
   *              og parent node while searching. The method stops when the end node is found, or if
   *              it searches all the neighbours and finds no path. It keeps updating the new costs
   *              and  parent node whenever it goes to the new node.
   */
  private void AstarSearch(Node start, Node goal)
  {
    PriorityQueue<Node> openList = new PriorityQueue<Node>(11, new Comparator<Node>()
    {
      @Override
      public int compare(Node o1, Node o2)
      {
        return Integer.compare(o1.fValue, o2.fValue);
      }
    });

    openList.add(start);
    start.parent = null;
    start.gValue = 0;
    Node current;
    start.visited++;

    while (!openList.isEmpty())
    {
      current = openList.poll();
      if (current.name.equals(goal.name))
      {
        PrintPath(current);
        return;
      }
      for (Node neighNode : current.possibleWords)
      {
        int newCost = current.gValue + 1;
        if (hasNode(current, neighNode) || newCost < neighNode.gValue)
        {
          if (neighNode.visited <= keepTrack)
          {
            neighNode.parent = current;
            if (hasNode(current, neighNode)) neighNode.visited = keepTrack + 1;
            neighNode.gValue = newCost;
            neighNode.calculateHValue(goal);
            neighNode.fValue = neighNode.gValue + neighNode.hValue;
            openList.offer(neighNode);
          }
        }
      }
    }
    System.out.println("NO POSSIBLE PATH:" + start.name + " to " + goal.name);
  }


  /**
   * @param node, Type: Node
   *              DeleteChar methods takes node as an input which contains
   *              a Sting name for a word in a dictionary.
   *              This method deletes character at each index and makes all the
   *              possible combination of the word that can be made by deleting
   *              letters. Then, it checks whether the new word is in our dictionary
   *              or not. If it is in dictionary, it will add it to the possibleWord
   *              array list and also add the original word to the possible arraylist
   *              of the new word. Hence, the map is created between the all new words
   *              and original word.
   */
  public void DeleteChar(Node node)
  {
    String word = node.name;
    for (int i = 0; i < word.length(); i++)
    {
      StringBuilder sb = new StringBuilder(word);
      sb.deleteCharAt(i);
      String deleted = sb.toString();
      if (wordsMap.containsKey(deleted))
      {
        Node tempNode = wordsMap.get(deleted);

        node.possibleWords.add(tempNode);
        tempNode.possibleWords.add(node);

      }
    }

  }


  /**
   * @param node, Type: Node
   *              AddChar method takes node as an input which contains
   *              a String name for a word in a dictionary.
   *              This method add character at each index and makes all the
   *              possible combination of the word that can be made by adding
   *              letters. Then, it checks whether the new word is in our dictionary
   *              or not. If it is in dictionary, it will add it to the possibleWord
   *              array list and also add the original word to the possibleWord arraylist
   *              of the new word. Hence, the map is created between the all new words
   *              and original word.
   */
  public void AddChar(Node node)
  {
    String word = node.name;
    for (int i = 0; i <= word.length(); i++)
    {
      String front = word.substring(0, i);
      String back = word.substring(i, word.length());
      for (char c = 'a'; c <= 'z'; c++)
      {
        String middle = Character.toString(c);
        String added = front + middle + back;
        if (wordsMap.containsKey(added))
        {
          wordsMap.get(added).possibleWords.add(node);
          node.possibleWords.add(wordsMap.get(added));
        }
      }
    }

  }


  /**
   * @param node, Type: Node
   *              ReplaceChar method takes node as an input which contains
   *              a String name for a word in a dictionary.
   *              This method add character at each index and makes all the
   *              possible combination of the word that can be made by replacing a
   *              letter. Then, it checks whether the new word is in our dictionary
   *              or not. If it is in dictionary, it will add it to the possibleWord
   *              array list and also add the original word to the possibleWord arraylist
   *              of the new word. Hence, the map is created between the all new words
   *              and original word.
   */
  public void ReplaceChar(Node node)
  {
    String word = node.name;
    String possible = "abcdefghijklmnopqrstuvwxyz";
    for (int i = 0; i < word.length(); i++)
    {
      for (int j = 0; j < possible.length(); j++)
      {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(i, possible.charAt(j));
        String name = sb.toString();

        if (name.equals(word))
        {

          int a = 0;
        }
        if (wordsMap.containsKey(name) && !sb.toString().equals(word))
        {

          node.possibleWords.add(wordsMap.get(name));
          wordsMap.get(name).possibleWords.add(node);

        }
      }
    }

  }


  /**
   * @param node, type Node
   *              It takes the goal Node from A* search once the path is found
   *              This methodes backtracs the parent field which is inside the node
   *              and keeps backtracking until our start node/word is found.Then it
   *              prints the possible shortest path from start node to goal node after
   *              concatenating the name field from the parent nodes.
   */
  private void PrintPath(Node node)
  {
    Node temp = node;
    String str = "";
    while (temp != null)
    {
      str = temp.name + " " + str;
      temp = temp.parent;
    }
    System.out.println(str);
  }


  /**
   * @param node   Type: Node , current node
   * @param toFind Type:Node, neighbour node
   * @return Boolean. r
   * Returns false if the node is found.
   */
  private boolean hasNode(Node node, Node toFind)
  {
    Node temp = node;
    while (temp != null)
    {
      if (temp.name.equals(toFind.name)) return false;
      temp = temp.parent;
    }

    return true;
  }


  /**
   * It is a main file that take command line arguments.
   *
   * @param args calls constructor for wordPath class.
   */
  public static void main(String[] args)
  {
    new Wordpath(args);
  }
}