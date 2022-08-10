import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.*;

public class TaskCreator
{
  String difficulty;
  boolean imposter;
  int numPlayers;
  Scanner scanner;
  List<Task> taskList;
  
  public TaskCreator(String difficulty, boolean imposter, int numPlayers)
  {
    this.difficulty = difficulty;
    this.imposter = imposter;
    this.numPlayers = numPlayers;

    File inputFile = new File("TasksInLocations.txt");
    scanner = null;
    try{
      scanner = new Scanner(inputFile);
    }catch(FileNotFoundException f)
    {
      System.err.println(f);
      System.exit(1);
    }

    taskList = new ArrayList<Task>();
    while(scanner.hasNextLine())
    {
      taskList.add(createTask());
    }
  }

  //Reads through the task text file and creates a task
  public Task createTask()
  {
    String locName = scanner.next();
    String taskName = scanner.next();
    boolean isLong = true;
    if(scanner.next().equals("short"))
    {
      isLong = false;
    }
    String type = scanner.next();
    boolean isVisual = true;
    if(scanner.next().equals("unVisual"))
    {
      isVisual = false;
    }
    boolean hasSubTask = false;
    Task subtask = null;
    if(scanner.next().equals("sub"))
    {
      hasSubTask = true;
      subtask = createTask();
    }
    if(hasSubTask)
    {
      return new Task(taskName, locName, isLong, type, isVisual, subtask);
    }else
    {
      return new Task(taskName, locName, isLong, type, isVisual);
    }
  }

  //Returns the list of tasks created
  public List<Task> getTaskList()
  {
    return taskList;
  }

  public static void main(String args[])
  {
    TaskCreator TC = new TaskCreator("easy", false, 10);
    List<Task> myTaskList = TC.getTaskList();
    for(Task t: myTaskList)
    {
      System.out.println("Task Name: " + t.getTaskName() + ". Location: " + t.getLocationName() + ". Long: " + t.isLong() + ". Type: " + t.getTaskType() + ". Visual: " + t.isVisual() + ".");
    }
  }
}