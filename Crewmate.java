public interface Crewmate
{
  /**
  * Returns the tasks assigned to this crewmate
  **/
  //public List<Task> getCrewmateTasks();

  /**
  * Adds the parameter task to the list of assigned task for the crewmate
  **/
  //public void assignTasks(List<Task> ttL);

  /**
  * Completes the parameter task and removes it from the crewmate's task list
  **/
  public void completeTask(Task t);

  /**
  * Reports a dead player
  **/
  public void report(Player p);

  //public void turn();
}