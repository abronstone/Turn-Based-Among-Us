import java.util.*;

public interface Entity
{
  public String getColor();

  /**
  * Returns the location of this player
  **/
  public Location getCurrentLocation();

  public List<Task> getTaskList();

  public boolean calledMeeting();

  /**
  * Updates the current location of the player
  **/
  public void updateLocation(Location l);

  /**
  * Assigns tasks to the player
  **/
  public void assignTasks(List<Task> ttL, int numTasks);

  public void confirmImposter(Entity e);

  /**
  * Returns true if the player has not been killed
  **/
  public boolean isAlive();

  public boolean justKilled();

  public boolean updateSuspicions();

  //Updates the bots on who is dead and alive
  public void updatePlayers(List<Entity> LOE);

  //Alters the suspicion scores for the parameter player
  public void resusPlayer(Entity e, int seeKill);

  //Returns the two most suspicious entities for the current entity instance. Returns an empty list if more than a three way tie
  public List<Entity> getMostSus();
  
  //Kills the current entity instance
  public void kill();

  //Does a turn for this entity
  public boolean turn();

  //Only for if the current entity instance is dead. Sets noticed boolean to true
  public void notice();

  //Returns true if the current entity instance's dead body has already been found
  public boolean isNoticed();
}