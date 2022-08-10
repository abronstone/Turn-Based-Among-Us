import java.util.*;
//import java.io.IOException;
import java.util.Scanner;


public class EmergencyMeeting
{
  private static final Scanner userInput = new Scanner(System.in);
  private List<Entity> players;
  private List<String> playerColors;
  private Map<Entity, Integer> votes;
  private Map<String, String> colorCodeList;
  private Entity subject;
  private int confirmedImposterCount;
  private MapPathFinder MPF;
  public static final String GREEN = "\u001B[32m";
  public static final String RED = "\u001B[31m";
  public static final String YELLOW = "\u001B[33m";
  public static final String WHITE = "\u001B[37m";
  public static final String BLACK = "\u001B[30m";
  public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
  public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
  public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
  public static final String ANSI_RESET = "\u001B[0m";

  public EmergencyMeeting(List<Entity> LOE, Entity caller, MapPathFinder MPF, Map<String, String> colorCodeList)
  {
    this.MPF = MPF; //each emergency meeting gets its own copy of the map in its current state (is this a REFERENCE!!! or a copy?) Maybe we need a reference???
    this.colorCodeList = colorCodeList; //REFERENCE!!! or copy?
    confirmedImposterCount = 0;
    if(caller.getMostSus().size() > 0)
    {
      this.subject = caller.getMostSus().get(0); //runs in O(N) time, could possibly be made faster
    }else
    {
      this.subject = null;
    }
    System.out.println("\n");
    printEM();
    players = LOE; //stands for 'list of entities'?
    playerColors = new ArrayList<String>(); //specific list of colors for participants in EM
    votes = new HashMap<Entity, Integer>(); //Self explanatory
    //int playerIndex = 0; //Do we avoid playerIndex=0 because the user is in the 0th index?
    for(int i=1; i<players.size(); i++)
    {
      Entity e = players.get(i);
      if(e.calledMeeting())
      {
        confirmedImposterCount++;
      }
      e.updatePlayers(players);
      playerColors.add(e.getColor().toLowerCase());
    }
    if(confirmedImposterCount < 2) //only one player knows you are the imposter: not enough damning evidence
    {
      for(int i=1; i<players.size(); i++)
      {
        Entity e = players.get(i);
        if(e.isAlive() && e.getCurrentLocation().getTasksInRoom().contains(e.getTaskList().get(0))) //Checks to see if the current task for the entity is in the same room that they were in when the EM was called
        {
          System.out.println(colorCodeList.get(e.getColor()) + e.getColor() + ANSI_RESET + ": \"I was in " + e.getCurrentLocation().getLocationName() + " doing " + e.getTaskList().get(0).getTaskName() + "\"");
        }else if(e.isAlive())
        {
          System.out.println(colorCodeList.get(e.getColor()) + e.getColor() + ANSI_RESET + ": \"I was in " + e.getCurrentLocation().getLocationName() + " heading to " + e.getTaskList().get(0).getTaskName() + " in " + e.getTaskList().get(0).getLocationName() + "\"");
        }
      }
      System.out.println(colorCodeList.get(players.get(0).getColor())+players.get(0).getColor()+": Where were you? (Enter your location)");
      String userLocation = userInput.nextLine();
      String actualInput = userLocation.substring(0,1).toUpperCase() + userLocation.substring(1, userLocation.length()).toLowerCase(); //converts user input to proper case
      boolean liar = false;
      if(MPF.getLocationObject(actualInput).getLivePlayersInRoom().size() > 1 && !MPF.getLocationObject(actualInput).getLivePlayersInRoom().contains(players.get(0))) //Checks to see if the room the player inputed as their alibi has othe players in it and if the user isn't in that room. If so, the bots in that room determine that you are lying.
      {
        System.out.println(colorCodeList.get(MPF.getLocationObject(actualInput).getLivePlayersInRoom().get(0).getColor()) + MPF.getLocationObject(actualInput).getLivePlayersInRoom().get(0).getColor() + ANSI_RESET + ": \"I was in that room, and you weren't there! Liar!\"");
        confirmedImposterCount += MPF.getLocationObject(actualInput).getLivePlayersInRoom().size(); //Adds confirms based on bots that are in the alibi room
        confirmedImposterCount += players.get(0).getCurrentLocation().getLivePlayersInRoom().size(); //Adds confirms based on bots that are in the actual room with the user
        liar = true;
      }
      if(players.get(0).getCurrentLocation().getLivePlayersInRoom().size() > 1 && !(players.get(0).getCurrentLocation().getLocationName().equals(actualInput))) // Checks to see if there are any other players in the user's current room, if the user provided a different alibi
      {
        int correctIndex = 0;
        if(players.get(0).equals(players.get(0).getCurrentLocation().getLivePlayersInRoom().get(0))) //only used if the user is in the correct room that they inputted. Corrects 
        {
          correctIndex = 1;
        }
        System.out.println(colorCodeList.get(players.get(0).getCurrentLocation().getLivePlayersInRoom().get(correctIndex).getColor()) + players.get(0).getCurrentLocation().getLivePlayersInRoom().get(correctIndex).getColor() + ANSI_RESET + ": \"No you were not, you were in " + players.get(0).getCurrentLocation().getLocationName() + " with me! Liar!\"");
        confirmedImposterCount += MPF.getLocationObject(actualInput).getLivePlayersInRoom().size();
        confirmedImposterCount += players.get(0).getCurrentLocation().getLivePlayersInRoom().size();
        liar = false; //why is liar=false? Didn't the user just lie?
      }
      if(!liar && confirmedImposterCount < 2)
      {
        if(MPF.getLocationObject(actualInput).getAdjacentLocations().contains(caller.getCurrentLocation()) || MPF.getLocationObject(actualInput).equals(caller.getCurrentLocation())) //If user is not confirmed to be a liar, then the unanimous most suspicious player is ejected
        {
          System.out.println(colorCodeList.get(caller.getColor()) + caller.getColor() + ANSI_RESET + ": that's near the body... kind of sus.");
          caller.resusPlayer(players.get(0), 3);
        }
      }
    }
    getVerdict();
  }

  //Prints the emergency meeting symbol
  public void printEM()
  {
    System.out.println(WHITE+ "---------------------------------" + ANSI_RESET);
    System.out.println(ANSI_RED_BACKGROUND+"                                           "+ANSI_RESET);
    System.out.println(BLACK+ANSI_YELLOW_BACKGROUND+" ||   |-------     /\\        /\\       ||  "+ANSI_RESET);
    System.out.println(ANSI_RESET+BLACK+ANSI_YELLOW_BACKGROUND+" ||   |           /  \\      /  \\      ||  "+ANSI_RESET);
    System.out.println(ANSI_RESET+BLACK+ANSI_YELLOW_BACKGROUND+" ||   |-------   /    \\    /    \\     ||  "+ANSI_RESET);
    System.out.println(ANSI_RESET+BLACK+ANSI_YELLOW_BACKGROUND+" ||   |         /      \\  /      \\    ||  "+ANSI_RESET);
    System.out.println(ANSI_RESET+BLACK+ANSI_YELLOW_BACKGROUND+" @@   |------- /        \\/        \\   @@  " +ANSI_RESET);
    System.out.println(ANSI_RED_BACKGROUND+"                                           "+ANSI_RESET);
  }

  //Gets the most suspicious players for each bot, and puts those votes into a list
  public void getBotVotes()
  {
    //int index = 0;
    for(int i=1; i<players.size(); i++)
    {
      Entity e = players.get(i);
        Entity botVerdict = null;
        List<Entity> CVSL = e.getMostSus(); //lmao why is this called CVSL xD
        if(subject != null && CVSL.contains(subject)) //If the most suspicious person from the CALLER is in the current entity's most suspicious list, then that most suspicious person is this entity's verdict
        {
          botVerdict = subject;
        }else if(CVSL.size() > 0)
        {
          botVerdict = CVSL.get(0); //Why does this just grab the first index entity? Isn't that the user?
        }
        //This segment adjusts the votes HashMap
        if(votes.containsKey(botVerdict))
        {
          votes.put(botVerdict, votes.get(botVerdict)+1); //Can this be replaced with 'votes.replace()?'
        }else
        {
          votes.put(botVerdict, 0);
        }
      }
  }

  //Asks the player who they want to vote off the ship
  public void getUserInput()
  {

    System.out.println(WHITE+"Who would you like to vote off? (Press enter to skip) ");//The method should list available player colors here, in lower case
    String name = userInput.nextLine().toLowerCase();
    if(playerColors.contains(name))//Should add incorrect input error
    {
      Entity userVerdict = players.get(playerColors.indexOf(name));
      //
      if(votes.containsKey(userVerdict))
      {
        votes.put(userVerdict, votes.get(userVerdict)+1);
      }else
      {
        votes.put(userVerdict, 0);
      }
    }else{
      //ADDED AUGUST 5, 2022: If incorrect color is inputted, recursively repeats the method until a correct color is inputted.
      System.out.println("Please input a correct player color.");
      getUserInput();
    }
  }

  //Added August 6,2022: CURRENTLY IMPLEMENTING
  public void printVotes(){
    System.out.println("Total votes: ");
    for(Entity e: players){
      if(e.isAlive()){
        int x = 0;
        if(votes.get(e)!=null){
          x=votes.get(e);
        }
        System.out.println(colorCodeList.get(e.getColor())+e.getColor()+": "+x);
      }
    }
    return;
  }

  //Counts the votes and kills the player with the most votes. Skips if it was a tie
  public void getVerdict()
  {
    List<Entity> oneWinner = new ArrayList<Entity>(); //why do we use an ArrayList to store the entity that we want to vote off? Can't we just store it as a new entity object?
    getBotVotes(); //Updates 'votes' HashMap with most suspicious bot entities
    getUserInput();//Updates 'votes' HashMap with user inputted color
    printVotes();
    if(confirmedImposterCount >= 2) //If two or more bots confirm that the user is the imposter in emergencyMeeting(), then the user is ejected
    {
      players.get(0).kill();
      System.out.println(colorCodeList.get(players.get(0).getColor()) + players.get(0).getColor() + ANSI_RESET + " was ejected.");
      System.out.println(GREEN + "Hint: too many people saw you kill. Try to pick them off when they are alone!" + ANSI_RESET); //Could this area be reached from the user lying as well?
      return;
    }
    int mostVotes = 0;
    for(Entity e: votes.keySet())
    {
      if(votes.get(e) > mostVotes)
      {
        mostVotes = votes.get(e);
        oneWinner.add(e);
      }else if(votes.get(e) == mostVotes)
      {
        oneWinner.clear();
      }
    }
    if(oneWinner.size() > 0 && oneWinner.get(0) != null) //If we just have 'oneWinner' as an object instead of an ArrayList, this should still work?
    {
      oneWinner.get(0).kill();
      System.out.println(colorCodeList.get(oneWinner.get(0).getColor()) + oneWinner.get(0).getColor() + ANSI_RESET + " was ejected.");
      if(oneWinner.get(0).equals(players.get(0)))//Only prints if the user is the one that was killed
      {
        System.out.println("Hint: the bots become more suspicious of players that they haven't seen for a while. Try to stick with the bots as much as you can by figuring out which tasks are most common!");
      }
    }else
    {
      System.out.println("No one was ejected (skipped).");
    }
    for(Entity e: players)//resets alive players' locations to the cafeteria
    {
      if(e.isAlive())
      {
        e.updateLocation(MPF.getLocationObject("Cafeteria"));
      }else
      {
        MPF.removePlayerFromLocation(e, e.getCurrentLocation().getLocationName());//If we remove a dead player from a location, do we need the 'noticed' ghost status?
      }
    }
  }
}