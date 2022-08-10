import java.util.*;

public class Task
{
  private String name;
  private String locationName;
  private Location location;
  private boolean completed;
  private boolean isLong;
  private List<Task> subTasks;
  private String type;
  private boolean isVisual;
  private Task subtask;

  public Task(String name, String location, boolean isLong, String type, boolean isVisual)
  {
    this.name = name;
    this.locationName = location;
    this.isLong = isLong;
    this.completed = false;
    this.type = type;
    this.isVisual = isVisual;
  }

  public Task(String name, String location, boolean shortLong, String type, boolean isVisual, Task subtask)
  {
    this.name = name;
    this.locationName = location;
    this.isLong = shortLong;
    this.completed = false;
    this.subtask = subtask;
    this.type = type;
    this.isVisual = isVisual;
  }

  //Returns the name of the task
  public String getTaskName()
  {
    return name;
  }

  //Sets the location of the task to the parameter location
  public void setLocation(Location location)
  {
    this.location = location;
  }

  //Returns the location of the task
  public Location getLocation()
  {
    return location;
  }

  //Returns the name of the location that the task is in
  public String getLocationName()
  {
    return locationName;
  }

  //Completes the current task and returns it
  public Task completeTask()
  {
    if(subTasks.size() > 0)
    {
      Task t = subTasks.get(0);
      subTasks.remove(0);
      return t;
    }
    completed = true;
    return this;
  }

  //Returns the type of task
  public String getTaskType()
  {
    return type;
  }

  //Returns true if the task is a visual task
  public boolean isVisual()
  {
    return isVisual;
  }

  //Returns the subtask of the current task
  public Task getSubTask()
  {
    return subtask;
  }

  //Returns true if the current task has a subtask
  public boolean hasSubTask()
  {
    return !(subtask==null);
  }

  //Returns true if the task is a long task
  public boolean isLong()
  {
    return isLong;
  }
}