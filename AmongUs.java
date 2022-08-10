import java.util.*;
import java.io.IOException;
import java.util.Scanner;

public class AmongUs
{
  private int numPlayers;
  private List<Entity> players;
  private List<Entity> bots;
  private int taskCount;
  private String[] colors;
  private List<String> colorList;
  private Map<String, String> colorCodeList;
  private MapPathFinder skeld;
  private List<Task> totalTaskList;
  private int turnCounter;
  private static final Scanner userInput = new Scanner(System.in);
  private static final String BLACK = "\033[0;30m";    // BLACK
  private static final String RED = "\033[0;31m";      // RED
  private static final String GREEN = "\033[0;32m";    // GREEN
  private static final String YELLOW = "\033[0;33m";   // YELLOW
  private static final String BLUE = "\033[0;34m";     // BLUE
  private static final String MAGENTA = "\033[0;35m";  // MAGENTA
  private static final String CYAN = "\033[0;36m";   // CYAN
  private static final String WHITE = "\033[0;37m";    // WHITE
  private static final String BROWN = "\0[0;33m";     //BROWN
  private static final String BLACK_BRIGHT = "\033[0;90m";    // BLACK
  private static final String RED_BRIGHT = "\033[0;91m";      // RED
  private static final String GREEN_BRIGHT = "\033[0;92m";    // GREEN
  private static final String YELLOW_BRIGHT = "\033[0;93m";   // YELLOW
  private static final String BLUE_BRIGHT = "\033[0;94m";     // BLUE
  private static final String MAGENTA_BRIGHT = "\033[0;95m";  // MAGENTA
  private static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
  private static final String WHITE_BRIGHT = "\033[0;97m";    // WHITE
  public static final String ANSI_RESET = "\u001B[0m"; //RESET

  public AmongUs(int numPlayers)
  {
    skeld = new MapPathFinder(); //Create general map path finder (used from previous homework)
    players = new ArrayList<Entity>(); //Creates empty list of total entities
    bots = new ArrayList<Entity>(); //Creates empty list of entities used for bots only
    colorList = new ArrayList<String>(); //Creates empty list for colors
    colorCodeList = new HashMap<String, String>(); //Creates empty list for colors and their respective color codes
    createColorList(); //Adds color strings to colorList, and adds color strings as keys to colorCodeList with respective color codes
    Collections.shuffle(colorList);//Shuffles the colorList to assign random colors to players
    this.numPlayers = numPlayers; //Initializes global variable for number of players
    
    //SHOULD COME INTO USE WHEN USER-CREWMATE INTERFACE IS IMPLEMENTED
    int randomImposter = (int)(Math.random()*numPlayers); //Selects random imposter from the list of players


    totalTaskList = skeld.getTasksOfTheMap(); //Initializes global list of total tasks to distribute to players
    turnCounter = 1; //Initialize the turn counter, starts at turn 1
    taskCount = 0; // Initializes task count, total tasks completed at the start of the game is 0

    //AUTOMATICALLY SETS USER AS IMPOSTER
    //Can we use that colorList counter mentioned below??
    Entity user = new UserImposter(colorList.get(0), colorCodeList.get(colorList.remove(0)), colorCodeList, skeld); //Create user player

    user.assignTasks(totalTaskList, 4); //Assigns user tasks
    players.add(user); //Add user to player list (USER IS ALWAYS INDEX 0)


    for(int i=0; i<numPlayers-1; i++) //Assigns bot tasks
    {
      //Instead of using stack method with removes, why not just use an index counter? Still runs in O(N), and less complicated
      Entity testCMB = new CrewmateBot(colorList.get(0), colorCodeList.get(colorList.remove(0)), colorCodeList, skeld);
      testCMB.assignTasks(totalTaskList, 4);
      players.add(testCMB);//Adds bot to list of total entities
      bots.add(testCMB);//Adds bot to list of bots
    }
    for(Entity e: bots)
    {
      e.updatePlayers(players);//Initial entity update for all bots (lets all bots know that everyone is alive)
    }
    System.out.println(GREEN_BRIGHT + "Welcome to AmongUs! You are an imposter among " + RED + (numPlayers-1) + GREEN_BRIGHT + " other unsuspecting crewmates. Your objective is to kill all of them before they become suspicious of you and vote you off the ship! Play the game multiple times to get a feel for the map (it is hard to understand because it is in written version, if you want a view of the map, google \"Skeld map\"), use the vents, and kill the crewmates before they finish their tasks! \n\n " + YELLOW + "Instructions: " + ANSI_RESET + "the options for each of your turns will be displayed. You can move to adjacent locations, use a vent if one is available, or kill if there are other bots in the same room. You can kill bots either at the beginning or the end of your turn. While lots of debugging code has been implements, try to enter your inputs carefully as to avoid any errors (inputs not case sensitive). \n\n" + RED_BRIGHT + "NOTE: " + ANSI_RESET + "If you enter certain inputs incorrectly, the game will skip that turn automatically, so pay attention to your inputs. In the actual game, if you aren't paying attention to details as imposter, you are sure to lose!  \n\n Press enter to play!" + ANSI_RESET);
    userInput.nextLine();
    System.out.println("Your color is " + colorCodeList.get(user.getColor()) + user.getColor() + ANSI_RESET);
    while(true)//Is infinite loop ok? Can we bring continuing conditions up here?
    {
      if(getNumAlive() > 2 && taskCount < (players.size()-1)*4 && user.isAlive())//Conditions to continue the game after each turn: 1.) there are more than 3 entities alive, including the user. 2.) The total task count is not maxed
      {
        System.out.println(WHITE_BRIGHT + "---------------------------------------------------\n" + ANSI_RESET);
        System.out.println("\n \nTurn #" + turnCounter + ": " + YELLOW + user.getCurrentLocation().getLocationName() + ANSI_RESET);
        boolean called = false; //An emergency meeting is not called at the beginning of a turn
        int turncounter = 0;
        for(Entity e: players)
        {
          if(e.isAlive())
          {
            if(e.turn() && turncounter!=0) //e.turn() is in 'Entity' interface and has different definitions in 'CrewmateBot' and 'UserImposter', returns true for bots if they complete a task, returns true for user if killed someone. 
            {
              taskCount++;//why does this increase when user kills someone in e.turn() for index 0?
            }
          }
        }
        if(getNumAlive() <= 2) //Imposter win condition if they kill the second to last crewmate in their turn. Checks to see if there is only the user and one crewmate alive. Unreachable if user is ejected in emergencyMeeting
        {
          System.out.println("Imposters win!");
          return;
        }
        Entity caller = null;
        for(Entity e: bots)
        {
          if(e.isAlive())
          {
            boolean EM = e.updateSuspicions();//updates the current player's suspicions of the other players and determines if the current player walked into a room with an unnoticed (non-ghost) dead body, and returns true if an emergency meeting was called due to this.
            if(EM)
            {
              called = true; //why don't we just call an emergency meeting here? 
              caller = e;
            }
          }
        }
        if(called)
        {
          EmergencyMeeting newEM = new EmergencyMeeting(players, caller, skeld, colorCodeList); //When new EM object is created, EM automatically begins. Why does emergencyMeeting() constructor contain the main code of the emergency meeting? It should only be a constructor for the global variables, and should call upon a different main method for the main code
        }

      }else if(user.isAlive())//Imposter win condition if second to last crewmate is dead after the end of a turn, meaning they got ejected during the emergency meeting. Should also check if the task count has not been reached.
      {
        System.out.println("Imposters win!");
        break;
      }else
      {
        System.out.println("Crewmates win!");
        break;
      }
      turnCounter++;
    }
  }

  //Returns the number of players alive
  public int getNumAlive()
  {
    int result = 0;
    for(Entity e: players)
    {
      if(e.isAlive())
      {
        result++;
      }
    }
    return result;
  }

  //Creates a map of the color names and their respective ANSI code (except for brown)
  public void createColorList()
  {
    colorCodeList.put("Blue", BLUE);
    colorCodeList.put("Lime", GREEN_BRIGHT);
    colorCodeList.put("Yellow", YELLOW_BRIGHT);
    colorCodeList.put("Orange", YELLOW);
    colorCodeList.put("Red", RED_BRIGHT);
    colorCodeList.put("Black", BLACK_BRIGHT);
    colorCodeList.put("White", WHITE_BRIGHT);
    colorCodeList.put("Cyan", CYAN_BRIGHT);
    colorCodeList.put("Pink", MAGENTA_BRIGHT);
    colorCodeList.put("Purple", MAGENTA);
    colorCodeList.put("Brown", ANSI_RESET);
    for(String s: colorCodeList.keySet())//
    {
      colorList.add(s);
    }
  }

  

  public static void main(String args[])
  {
    int numPlayers = 5;//Default number of players is 5
    if(args.length >= 1)
    {
      if(Integer.valueOf(args[0]) >=2 && Integer.valueOf(args[0]) <= 6)
      {
        numPlayers = Integer.valueOf(args[0]);
      }else
      {
        System.out.println("There can't be less than 2 or more than 6 players. Default set to 5!"); //We should figure out why there cannot be more than 6 players
      }
    }
    AmongUs newGame = new AmongUs(numPlayers);
  }
}