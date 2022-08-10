import java.util.*;
import java.io.IOException;
import java.util.Scanner;

public class UserImposter extends Player implements Imposter
{
  private List<Task> fakeTaskList;
  private int killCoolDown;
  private static final Scanner OBJ = new Scanner(System.in);
  private boolean alive;
  private int standardKCD;
  private boolean redoTurn;


  public UserImposter(String color, String colorCode, Map<String, String> colorCodeList, MapPathFinder p)
  {
    super(color, colorCode, colorCodeList, p);
    fakeTaskList = new ArrayList<Task>();
    standardKCD = 3;
    killCoolDown = standardKCD;
    redoTurn = true;
  }

  /**
  * Prints the options that the player has for their turn. The move option is always displayed. If there are other bots in the same room, then the kill option is displayed. If there is a vent in the room, the vent option is displayed.
  *@return: the option that the user chooses
  **/
  public String printOptions()
  {
    System.out.println(YELLOW+"Your options: \n");
    System.out.print("Move");
    if(currentLocation.getVentLocations().size() > 0)
    {
      System.out.print(", Vent");
    }
    if(currentLocation.getLivePlayersInRoom().size() > 1)
    {
      System.out.print(", Kill");
    }
    System.out.print(". Press [enter] to skip a turn.");
    System.out.println(ANSI_RESET);
    return OBJ.nextLine().toLowerCase();
  }

  /**
  * Asks the player where they would like to move, and if there is an adjacent location equal to the player's input, then the player is moved to that location.
  */
  public void move()
  {
    System.out.println(YELLOW+"Where would you like to move to? (Type 'return' to go back)\n"+ANSI_RESET);
    for(Location l: currentLocation.getAdjacentLocations())
    {
      System.out.print(l.getLocationName() + ", ");
    }
    String input = OBJ.nextLine().toLowerCase();
    if(input.contains("return"))
    {
      redoTurn = true;//Goes back to while loop in turn()
      return;
    }
    for(Location l: currentLocation.getAdjacentLocations())
    {
      if(l.getLocationName().toLowerCase().equals(input))//Is there a way to handle small spelling mistakes?
      {
        updateLocation(l);//Located in Player class. Is this method only used for the user?
        System.out.println(GREEN+"You have moved to " + currentLocation.getLocationName()+ANSI_RESET);
        return;
      }
    }
    System.out.println("Not a valid location. Please try again.");
    move();
  }

  /**
  * Allows the user to move to a location that is adjacent to their current location via vent.
  */
  public void vent()
  {
    System.out.println(YELLOW+"You hoppped into a vent! Where would you like to go? (Type 'return' to go back)\n");
    for(Location l: currentLocation.getVentLocations())
    {
      System.out.print(l.getLocationName() + "  ||  ");
    }
    System.out.println(ANSI_RESET);
    String input = OBJ.nextLine().toLowerCase();
    if(input.equals("return"))
      {
        redoTurn = true;
        return;
      }
    for(Location l: currentLocation.getVentLocations())
    {
      if(l.getLocationName().toLowerCase().equals(input))
      {
        updateLocation(l);
        System.out.println(GREEN+"You have vented to " + currentLocation.getLocationName()+ANSI_RESET);
        return;
      }
    }
    System.out.println("Not a valid location. Please try again.");
    vent();
  }

  /**
  * Asks the user which player they would like to kill, and if that player is present in the room, that player is then killed.
  *@return: true if the kill was successful
  */
  public boolean killTime()
  {
      if(killCoolDown == 0)//Determines if kill cooldown is at 0 (three turns have passed since user killed last)
      {
        System.out.println(GREEN+"Your kill cooldown is ready!" + YELLOW +" Who would you like to kill? (Type 'return' to go back)\n"+ANSI_RESET);
        String input2 = OBJ.nextLine().toLowerCase();
        while(input2.equals(color.toLowerCase())){
          System.out.println("You can't kill yourself! Please try another name, or return.");
          input2 = OBJ.nextLine().toLowerCase();
        }
        if(input2.equals("return"))//Goes back to while loop in turn()
        {
          redoTurn = true;
          return false;
        }else if(!input2.equals(""))
        {
          String input3 = input2.substring(0, 1).toUpperCase() + input2.substring(1, input2.length()).toLowerCase();//Converts string to proper case (This is the best method apparently)
          int indexOfPlayer = 0;
          for(String s: currentLocation.getLivePlayerNamesInRoom())//Is this the best way to search for a player within a list? Does this INLCUDE the user?
          {
            if(s.equals(input3))
            {
              break;
            }else
            {
              indexOfPlayer++;
            }
          }
          if(indexOfPlayer >= currentLocation.getLivePlayerNamesInRoom().size())//Determines if the previous for loop (which might not be very efficient) has looped through all the available players to kill and did not stop on one.
          {
            System.out.println("That player is not in this room!");
            redoTurn = true;
            return false;
          }
          currentLocation.getPlayersInRoom().get(indexOfPlayer).kill();
          String colorKilled = currentLocation.getPlayersInRoom().get(indexOfPlayer).getColor();
          System.out.println(GREEN+"You have successfully killed " + colorCodeList.get(colorKilled) + colorKilled + GREEN + "!"+ANSI_RESET);
          killCoolDown = standardKCD;
          justKilled = true;
        }
      }else
      {
        System.out.println(RED+"Kill cooldown is not ready! " + killCoolDown + "/" + standardKCD + ANSI_RESET);
        redoTurn = true;
        return false;
      }
      return justKilled;
  }

  //Performs a user imposter turn. Returns true if killed someone
  public boolean turn()
  {
    if(justKilled)
    {
      justKilled = false;//User cannot have just killed someone before the turn has started
    }
    if(currentLocation.getLivePlayersInRoom().size() > 1)//Checks if the user is not alone in the room that the user starts the turn in
    {
      System.out.println("\nThere are players in this room: ");
      int indexCounter = 0;
      for(String s: currentLocation.getLivePlayerNamesInRoom())
      {
        if(!s.toLowerCase().equals(color.toLowerCase()))
        {
          System.out.println(colorCodeList.get(s) + s);
        }
      }
    }

    System.out.println(YELLOW+"\nYou are in " + currentLocation.getLocationName() + ". What would you like to do next?"+ANSI_RESET);
    redoTurn = true;
    while(redoTurn)
    {
      redoTurn = false;
      String nextMove = printOptions(); //Prints available options for user (why is kill an option even when cooldown is not ready?). User input is handled in printOptions and converted to lower case for analysis below
      if(nextMove.contains("move"))
      {
        move();
      }else if(nextMove.contains("kill"))
      {
        killTime();//Why is this called kill'Time'()? Just for fun?
        //return false;//Why do we return false right after attempting to kill? If the kill cooldown is not ready, should loop back to options
      }else if(nextMove.contains("vent"))
      {
        vent();
      }else if(!redoTurn && nextMove.equals(""))
      {
        System.out.println("Skip! You are still in " + currentLocation.getLocationName());
      }else
      {
        System.out.println("Invalid turn option. Please try again.");
        redoTurn=true;
      }
    }
    if(currentLocation.getLivePlayersInRoom().size() > 1)
    {
      System.out.println("\nThere are players in this room: ");
      for(String s: currentLocation.getPlayerNamesInRoom())
      {
        if(!s.toLowerCase().equals(color.toLowerCase()))
        {
          System.out.println(colorCodeList.get(s) + s);
        }
      }
      if(!justKilled){
        System.out.println(YELLOW+"Would you like to kill? (y/n)"+ANSI_RESET);
        String input = OBJ.nextLine().toLowerCase();
        if(input.contains("y"))
        {
          killTime();
        }
      }
    }
    killCoolDown--;
    if(killCoolDown<0)
    {
      killCoolDown = 0;
    }
    return false;
  }
}