import java.util.*;

public class Player implements Entity
{
  protected MapPathFinder skeld;
  protected String color;
  protected String colorCode;
  protected Location currentLocation;
  protected Location previousLocation;
  protected boolean alive;
  protected List<Task> taskList;
  protected Map<Entity, Integer> suspicions;
  protected Map<String, String> colorCodeList;
  protected boolean justKilled;
  protected boolean calledMeeting;
  protected boolean noticed;
  public static final String GREEN = "\u001B[32m";
  public static final String RED = "\u001B[31m";
  public static final String YELLOW = "\u001B[33m";
  public static final String WHITE = "\u001B[37m";
  public static final String BLACK = "\u001B[30m";
  public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
  public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
  public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
  public static final String ANSI_RESET = "\u001B[0m";


  public Player(String color, String colorCode, Map<String, String> colorCodeList, MapPathFinder p)
  {
    skeld = p;
    this.color = color;
    this.colorCode = colorCode;
    this.colorCodeList = colorCodeList;
    currentLocation = skeld.getLocationObject("Cafeteria");
    previousLocation = null;
    currentLocation.addPlayerToLocation(this);
    alive = true;
    taskList = new ArrayList<Task>();
    suspicions = new HashMap<Entity, Integer>();
    justKilled = false;
    calledMeeting = false;
    noticed = false;
  }

  /**
  * Returns the color of this player
  **/
  public String getColor()
  {
    return color;
  }

  /**
  * Returns the location of this player
  **/
  public Location getCurrentLocation()
  {
    return currentLocation;
  }

  //Returns the previous location of this player
  public Location getPreviousLocation()
  {
    return previousLocation;
  }

  //Returns the list of tasks associated with this player
  public List<Task> getTaskList()
  {
    return taskList;
  }

  //Returns true if this player just killed another player
  public boolean justKilled()
  {
    return justKilled;
  }

  /**
  * This player's suspicion level for the parameter entity is raised to obscene levels as to make them the most suspicious person in the list
  *@param: the entity that is confirmed to be the imposter
  */
  public void confirmImposter(Entity e)
  {
    suspicions.put(e, 1000);
  }

  /**
  * Updates the list of players for each player so that bots don't keep reporting dead bodies or get suspicious of dead players
  * @param: the total list of players in the game
  **/
  public void updatePlayers(List<Entity> LOE)
  {
    for(Entity e: LOE)
    {
      if(e.isAlive() && !(suspicions.containsKey(e)))
      {
        suspicions.put(e, 50);
      }else if(!e.isAlive())
      {
        suspicions.remove(e);
      }
    }
  }

  /**
  * Updates the current location of the current player object (is it just for user?)
  **/
  public void updateLocation(Location l)
  {
    skeld.putPlayerInLocation(this, l.getLocationName());
    skeld.removePlayerFromLocation(this, currentLocation.getLocationName());
    previousLocation = skeld.getLocationObject(currentLocation.getLocationName());
    currentLocation = l;
  }

  /**
  * Assigns tasks to the player
  **/
  public void assignTasks(List<Task> ttL, int numTasks)
  {
    int numLongTasks = 0;
    for(int i=0; i<numTasks; i++)
    {
      int index = (int)(Math.random()*ttL.size());
      if((ttL.get(index).getTaskName().equals("Download")) || (ttL.get(index).getTaskName().substring(0, 4).equals("Dive")) || (ttL.get(index).getTaskName().equals("Wires")))
      {
        if(numLongTasks >= 2)
        {
          i--;
        }else
        {
          numLongTasks++;
          taskList.add(ttL.remove(index));
        }
      }else
      {
        taskList.add(ttL.remove(index));
      }
    }
    Collections.shuffle(taskList);
  }

  /**
  * Returns true if the player has not been killed
  **/
  public boolean isAlive()
  {
    return alive;
  }

  /**
  * Used for bots. If the bot is in the same room as a player, that bot's suspicion level of that player is lowered by 3. If the bot is not in the same room as a player, then that bot's suspicion level of that player is raised by 1. If the bot is in the same location as another player that just killed, then the bot confirms that player to be an imposter. 
  *
  * @return: returns true if an emergency meeting is to be called
  **/
  public boolean updateSuspicions()
  {
    for(Entity e: currentLocation.getPlayersInRoom()) //only updates bots currently in the room
        {
          if(e.isAlive())
          {
            resusPlayer(e, 1);//why do we increase each bot's suspicioun levels of themselves that are in the room?
          }else if(!e.isNoticed())//Called if player is DEAD. isNoticed() (further down) only used for testing purposes? What is this? UPDATE: 'noticed' used as "ghost" factor so alive crewmates don't call emergency meeting for bots killed in previous rounds?
          {
            for(Entity p: currentLocation.getLivePlayersInRoom())
            {
              resusPlayer(p, 3); //Increases suspicion levels of all players in the room where a dead crewmate has been spotted
            }
            e.notice(); //Activates "ghost" status on dead crewmate, ensuring an emergency meeting will not be called on them again in the future
            if(calledMeeting) //What does calledMeeting mean? Both messages have to do with calling a meeting; why is one used when a meeting is already called and one is not?
            {
              System.out.println("\n\n\n " + colorCodeList.get(color) + color + RED + " saw you kill " + colorCodeList.get(e.getColor()) + e.getColor() + RED +" in " + currentLocation.getLocationName() + "!");
            }else
            {
              System.out.println(RED+"\n\n\n " + color + " walked in on " + e.getColor() + "'s dead body in " + currentLocation.getLocationName() + "!");
            }
            return true; //Returns true because an emergency meeting was activated. This code is only reached if the current entity walked in on an unnoticed dead player
          }
        }
    return false; //Returns false because an emergency meeting was not called. All the players in this entity's room are alive.
  }

  /**
  * Alters the suspicion levels of the parameter entity based on whether that entity and the current player instance are in the same room. If the parameter killSus is equal to three, that means that the current player instance walked in on a dead body, and the parameter entity was in the same room. Kind of suspicious...
  *
  * @param: the entity to be "resused", and the number to alter the sus score by
  **/
  public void resusPlayer(Entity e, int killSus)
  {
    if(killSus == 3)
    {
      suspicions.put(e, suspicions.get(e)+killSus);
    }else if(currentLocation.getPlayersInRoom().contains(e))
    {
      //System.out.println(color + " unsussed " + e.getColor());
      if(suspicions.get(e) <= 2)
      {
        suspicions.put(e, 0);
      }else
      {
        suspicions.put(e, suspicions.get(e)-1);
      }
    }else
    {
      //System.out.println(color + "sussed " + e.getColor());
      suspicions.put(e, suspicions.get(e)+1);
    }
  }

  //Returns true if the current player called a meeting
  public boolean calledMeeting()
  {
    return calledMeeting;
  }

  //Kills the current player
  public void kill()
  {
    alive = false;
  }

  //USED FOR  TESTING PURPOSES. UPDATE AUGUST 5, 2022: is this only here so this class can implement 'Entity' interface?
  public boolean turn()
  {
    return false;
  }

  //USED FOR  TESTING PURPOSES
  public void notice()
  {
    noticed = true;
  }

  //USED FOR  TESTING PURPOSES
  public boolean isNoticed()
  {
    return noticed;
  }

  //Returns a list of the top two players with the highest suspicion score for the current player instance. If three or more players have the same suspicion score, then an empty list is returned.

 //Is there a better way to find the top 2 most suspicious players? Faster than O(N)?
  public List<Entity> getMostSus()
  {
    List<Entity> mostSus = new ArrayList<Entity>();
    Entity numberOne = null;
    int numberOneVotes = -1;
    Entity numberTwo = null;
    int numberTwoVotes = -1;
    for(Entity e: suspicions.keySet())
    {
      if(suspicions.get(e) > numberOneVotes)
      {
        numberOne = e;
        numberOneVotes = suspicions.get(e);
      }else if(suspicions.get(e) > numberTwoVotes || suspicions.get(e) == numberOneVotes)
      {
        numberTwo = e;
        numberTwoVotes = suspicions.get(e);
      }
    }
    if(suspicions.get(numberOne) != suspicions.get(numberTwo))
    {
      mostSus.add(numberOne);
      mostSus.add(numberTwo);
    }
    return mostSus;
  }
}