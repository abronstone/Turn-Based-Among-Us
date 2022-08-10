import java.util.*;

public class CrewmateBot extends Player implements Crewmate
{
  //private String color;
  //private Location currentLocation;
  //private Location previousLocation;
  //private List<Task> taskList;
  //private MapPathFinder CMB_MPF;
  private List<Location> currentPath;
  //private boolean alive;

  public CrewmateBot(String color, String colorCode, Map<String, String> colorCodeList, MapPathFinder p)
  {
    //this.color = color;
    //this.CMB_MPF = p;
    //this.currentLocation = CMB_MPF.getLocationObject("Cafeteria");
    //currentLocation.addPlayerToLocation(this);
    //previousLocation = null;
    //CMB_MPF.putPlayerInLocation(this, currentLocation.getLocationName());
    super(color, colorCode, colorCodeList, p);
    this.currentPath = new ArrayList<Location>();
    //this.taskList = new ArrayList<Task>();
    //this.alive = true;
  }
  

  public List<Location> getCurrentPath()
  {
    return currentPath;
  }

  public void updateCurrentPath()
  {
    currentPath.remove(0);
  }

  public void setCurrentPath(List<Location> newCP)
  {
    currentPath = newCP;
  }

  //Kills the current Entity
  public void kill()
  {
    super.alive = false;
  }

  /**
  * Returns the location of this player
  **/
  /*public Location getCurrentLocation()
  {
    return currentLocation;
  }
  */

  /**
  * Returns the previous location of this player
  **/
  /*public Location getPreviousLocation()
  {
    return previousLocation;
  }
  */

  /**
  * Updates the current location of the player
  **/
  /*public void updateLocation(Location l)
  {
    CMB_MPF.putPlayerInLocation(this, l.getLocationName());
    CMB_MPF.removePlayerFromLocation(this, currentLocation.getLocationName());
    currentLocation = l;
  }
  */

  /**
  * Returns the tasks assigned to this crewmate
  **/
  /*public List<Task> getCrewmateTasks()
  {
    return taskList;
  }
  */

  /**
  * Adds the parameter task to the list of assigned task for the crewmate
  **/
  /*public void assignTasks(List<Task> ttL)
  {
    int taskListSize = 5;
    int numLongTasks = 0;
    for(int i=0; i<taskListSize; i++)
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
  }
  */

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
  * Completes the parameter task and removes it from the crewmate's task list
  * UPDATE: Is this even used?
  **/
  public void completeTask(Task t)
  {
    taskList.remove(0).completeTask();
  }

  /**
  * Reports a dead player 
  * UPDATE: Is this even used?
  **/
  public void report(Player p)
  {
    return;
  }

  /**
  * Has bots move around the map based on which tasks they have next
  * @return: true if the bot completed a task during that turn
  */
  public boolean turn()
  {
    if(calledMeeting)
    {
      calledMeeting = false;//Crewmate cannot have called an emergency meeting at the beginning of the turn
    }
    if(taskList.size()==0)//If the crewmate has completed all of their tasks, there is no reason for the turn to commence (maybe have crewmates wander in hard mode?)
    {
      return false;
    }
    for(Entity e: currentLocation.getPlayersInRoom())
    {
      if(e.justKilled())
      {
        confirmImposter(e);//Confirms Entity 'e' to be the imposter from this entity's POV
        calledMeeting = true;
        return false;
      }
    }
    if(currentPath.size() > 1)
    {
      previousLocation = currentPath.remove(0);//Updates previously known location from bots
      updateLocation(currentPath.get(0));//Updates current location for current Entity based on the next room they must go to to get to their next task
    }else if(currentPath.size() == 1)
    {
      if(taskList.get(0).hasSubTask())
      {
        taskList.add(0, taskList.remove(0).getSubTask());//Why do we remove the master task? Why not just 'get' it? UPDATE: Because the master task is not a true task, the subtask is the true task. However, aren't there multiple subtasks for a master task?
      }else
      {
        taskList.remove(0);//Completes and removes current task from list
      }
      currentPath.remove(0);
      return true;//The bot has completed a task during this turn.
      
    }else
    {
      Location nextLocation = taskList.get(0).getLocation();//Sets next location to travel to
      currentPath = skeld.getShortestPath(currentLocation.getLocationName(), nextLocation.getLocationName());//Gets the shortest path to get from currentLocation to nextLocation using breadth first search
      turn();//Repeats the turn if the entity had to update their current path
    }
    return false;//The bot has not completed a task during this turn.
  }
}