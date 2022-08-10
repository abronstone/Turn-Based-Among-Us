import java.util.*;
import java.io.*;

public class MapPathFinder {
  UnweightedGraph locationMap = new MysteryUnweightedGraphImplementation();
  //UnweightedGraph ventMap = new MysteryUnweightedGraphImplementation();
  Map<String, Integer> articleVertex = new HashMap<String, Integer>();
  Map<Integer, String> vertexArticle = new HashMap<Integer, String>();
  Map<String, Location> stringLocation = new HashMap<String, Location>();
  Map<Location, Integer> locationVertex = new HashMap<Location, Integer>();
  List<Task> taskList;
  List<Location> locationList;
  /**
  * Constructs a MapPathFinder that represents the graph with nodes (vertices) specified as in
  * nodeFile and edges specified as in edgeFile.
  * @param nodeFile name of the file with the node names
  * @param edgeFile name of the file with the edge names
  */
  public MapPathFinder(){
    readNodes("Locations.txt");
    readEdges("Connections.txt");
    readVentEdges("VentConnections.txt");
  }

  private void readNodes(String nodeFile) {
    TaskCreator t = new TaskCreator("easy", true, 10);
    taskList = t.getTaskList();
    locationList = new ArrayList<Location>();
    File inputFile = new File(nodeFile);
    Scanner scanner = null;
    try {
        scanner = new Scanner(inputFile);
    } catch (FileNotFoundException e) {
        System.err.println(e);
        System.exit(1);
    }

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.length() > 0 && !line.substring(0,1).equals("#")) {
        Integer nodeNum = locationMap.addVertex();
        articleVertex.put(line, nodeNum);
        vertexArticle.put(nodeNum, line);
        Location l = new Location(line);
        stringLocation.put(line, l);
        locationVertex.put(l, nodeNum);
        locationList.add(l);
      }
    }
    for(Task a: taskList)
    {
      connectTaskAndLocation(a);
    }
  }

  public void connectTaskAndLocation(Task t)
  {
    t.setLocation(stringLocation.get(t.getLocationName()));
    if(t.hasSubTask())
    {
      connectTaskAndLocation(t.getSubTask());
    }
  }

  /**
  * Returns the total task list
  **/
  public List<Task> getTasksOfTheMap()
  {
    return taskList;
  }

  /**
  * Returns a list of all of the locations on the map
  **/
  public List<Location> getLocationsOfTheMap()
  {
    return locationList;
  }

  /**
  * Puts the player in the location specified in the parameter
  */
  public void putPlayerInLocation(Entity p, String loc)
  {
    stringLocation.get(loc).addPlayerToLocation(p);
  }

  //Removes the player in the location specified in the parameter
  public void removePlayerFromLocation(Entity p, String loc)
  {
    stringLocation.get(loc).removePlayerFromLocation(p);
  }

  //Returns the location associated with the parameter string name
  public Location getLocationObject(String loc)
  {
    return stringLocation.get(loc);
  }

  //USED FOR TESTING PURPOSES
  public void printLocationsAndPlayers()
  {
    for(Location l: locationList)
    {
      System.out.println("Players in " + l.getLocationName() + ": \n");
      for(Entity e: l.getPlayersInRoom())
      {
        System.out.print(e.getColor() + ", ");
      }
      System.out.println("");
    }
  }

  private void readEdges(String edgeFile) {
    File inputFile = new File(edgeFile);
    Scanner scanner = null;
    try {
        scanner = new Scanner(inputFile);
    } catch (FileNotFoundException e) {
        System.err.println(e);
        System.exit(1);
    }

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.length() > 0 && !line.substring(0,1).equals("#")) {
        String[] splitline = line.split("\\s+");
        //System.out.println(line);
        String begin = splitline[0];
        String end = splitline[1];

        //System.out.println("Begin: " + begin);
        //System.out.println("End: " + end);
        
        
        int beginNum = locationVertex.get(stringLocation.get(begin));
        int endNum = locationVertex.get(stringLocation.get(end));
        locationMap.addEdge(beginNum, endNum);
        stringLocation.get(begin).addAdjacentLocation(stringLocation.get(end));
        //System.out.println("Connect: " + stringLocation.get(begin).getLocationName() + " and " + stringLocation.get(end).getLocationName());
        //stringLocation.get(end).addAdjacentLocation(stringLocation.get(begin));
      }
    }
  }

  private void readVentEdges(String edgeFile) {
    File inputFile = new File(edgeFile);
    Scanner scanner = null;
    try {
        scanner = new Scanner(inputFile);
    } catch (FileNotFoundException e) {
        System.err.println(e);
        System.exit(1);
    }

    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.length() > 0 && !line.substring(0,1).equals("#")) {
        String[] splitline = line.split("\\s+");
        String begin = splitline[0];
        String end = splitline[1];
        stringLocation.get(begin).addVentLocation(stringLocation.get(end));
       
      }
    }
  }


  /**
  * Performs a breadth first search on the graph starting at the parameter string.
  * Each node is put into the resulting map as a key with its value being the previous node that is directed into it on the shortest path. 
  * @param: the beginning node of the search
  * @return: the map of links from the beginning node to the end node
  */
  public Map<Integer, Integer> breadthFirstSearch(String start) {
    int nodeNumber = articleVertex.get(start);
    Map<Integer, Integer> BFSmap = new HashMap<Integer, Integer>();//1
    BFSmap.put(nodeNumber, 0);//1
    Queue<Integer> BFSqueue = new LinkedList<Integer>();//1
    BFSqueue.add(nodeNumber);//1
    while(!BFSqueue.isEmpty()){
      int currVertex = BFSqueue.element();//2
      Iterator<Integer> vertexNeighbors = locationMap.getNeighbors(currVertex).iterator();//3
      while(vertexNeighbors.hasNext())//2
      {
        int x = vertexNeighbors.next();//2
        if(!BFSmap.containsKey(x))//2
        {
          BFSqueue.add(x);//1
          BFSmap.put(x, currVertex);//2
        }
      }
      BFSqueue.remove();//1
    }

    return BFSmap;
  }

  public int getNumVerts()
  {
    return locationMap.numVerts();
  }
  
  /**
  * Returns a shortest path from node1 to node2, represented as list that has node1 at
  * index 0, node2 at the final index, and the names of each node on the path
  * (in order) in between. If node1 and node2 are the same, then the list only contains that node.
  * If no path exists, returns an empty list.
  * @param node1 name of the starting article node
  * @param node2 name of the ending article node
  * @return list of the names of nodes on the shortest path
  */
  public List<Location> getShortestPath(String node1, String node2){
    ArrayList<Location> myList = new ArrayList<Location>();

    Map<Integer, Integer> newMap = breadthFirstSearch(node1);

    int searchInt = articleVertex.get(node2);
    myList.add(stringLocation.get(vertexArticle.get(searchInt)));

    while(true)
    {
      if(!newMap.containsKey(searchInt))
      {
        System.out.println("MapPathFinder breadthFirstSearch() error: The sink node isn't connected to the source.");
        return new ArrayList<Location>();
      }
      int x = newMap.get(searchInt);
      myList.add(stringLocation.get(vertexArticle.get(x)));
      if(x == articleVertex.get(node1))
      {
        break;
      }else
      {
        searchInt = newMap.get(searchInt);
      }
    }
    Collections.reverse(myList);
    
    return myList;
  }
  

  /**
  * @param: nothing
  * @return: a string with the number of vertices and edges in the   * graph
  *
  */
  public String toString() {
    return "Num edges: " + locationMap.numEdges() + ", num nodes: " + locationMap.numVerts(); 
  }

  public String getArticleAtVertex(int x)
  {
    return vertexArticle.get(x);
  }

  public int getVertexAtArticle(String s)
  {
    return articleVertex.get(s);
  }

  public static void main(String[] args) {
    MapPathFinder p = new MapPathFinder();
  }
    
  
}
                  