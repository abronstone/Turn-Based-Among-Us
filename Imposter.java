import java.util.*;

public interface Imposter
{
  /**
  * Gets a list of task names for a fake task list. Note that the list is not comprised of actual Task objects, because the imposter should not be able to actually interact with any of them
  **/
  //public List<Task> getFakeTaskList();

  /**
  * Takes in a Task as a parameter, but only adds the task name to the fake task list
  **/
  //public void assignTasks(List<Task> ttL);

  /**
  * Returns a location that is sabotaged, as long as it is a location that actually can be sabotaged
  **/
  //public Location sabotage();

  //Lets the entity vent
  public void vent();

  public boolean justKilled();
}