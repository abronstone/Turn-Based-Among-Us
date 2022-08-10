import java.util.*;

public class Location
{
  private String name;
  private List<Task> tasksInRoom;
  private List<Entity> playersInRoom;
  private List<String> playerNamesInRoom;
  private List<Location> adjacencies;
  private List<Location> vents;

  public Location(String name)
  {
    this.name = name;
    tasksInRoom = new ArrayList<Task>();
    playersInRoom = new ArrayList<Entity>();
    playerNamesInRoom = new ArrayList<String>();
    adjacencies = new ArrayList<Location>();
    vents = new ArrayList<Location>();
  }

  //Adds the parameter task to the location
  public void addTaskToRoom(Task t)
  {
    tasksInRoom.add(t);
  }

  //Removes the parameter task from the location
  public void removeTaskFromRoom(Task t)
  {
    tasksInRoom.remove(t);
  }

  //Returns the list of tasks associated with this location
  public List<Task> getTasksInRoom()
  {
    return tasksInRoom;
  }

  //Returns the players currently in this location, either dead or alive
  public List<Entity> getPlayersInRoom()
  {
    return playersInRoom;
  }

  //Returns the number of alive players in this location
  public List<Entity> getLivePlayersInRoom()
  {
    List<Entity> newList = new ArrayList<Entity>();
    for(Entity e: playersInRoom)
    {
      if(e.isAlive())
      {
        newList.add(e);
      }
    }
    return newList;
  }

  //Returns a list of colors of alive players in this location
  public List<String> getLivePlayerNamesInRoom()
  {
    List<String> newList = new ArrayList<String>();
    for(Entity e: playersInRoom)
    {
      if(e.isAlive())
      {
        newList.add(e.getColor());
      }
    }
    return newList;
  }

  //Returns a list of colors of players in this location, either dead or alive
  public List<String> getPlayerNamesInRoom()
  {
    return playerNamesInRoom;
  }

  //Adds the parameter entity to this location
  public void addPlayerToLocation(Entity p)
  {
    playersInRoom.add(p);
    playerNamesInRoom.add(p.getColor());
  }

  //Removes the parameter entity from this location
  public void removePlayerFromLocation(Entity p)
  {
    playersInRoom.remove(p);
    playerNamesInRoom.remove(p.getColor());
  }

  //Adds an adjacent location to this location
  public void addAdjacentLocation(Location l)
  {
    adjacencies.add(l);
  }

  //Returns a list of adjacent locations to this location
  public List<Location> getAdjacentLocations()
  {
    return adjacencies;
  }

  //Adds an adjacent vent location to this location
  public void addVentLocation(Location l)
  {
    vents.add(l);
  }

  //Returns a list of adjacent vent locations to this location
  public List<Location> getVentLocations()
  {
    return vents;
  }

  //Returns the name of this location
  public String getLocationName()
  {
    return name;
  }
}