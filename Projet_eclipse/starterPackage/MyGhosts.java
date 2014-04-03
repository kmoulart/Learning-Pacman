package game.entries.ghosts;

import game.controllers.GhostController;
import game.core.Game;

/*
 * Ghost team controller as part of the starter package - simply upload this file as a zip called
 * MyGhosts.zip and you will be entered into the rankings - as simple as that! Controller written by
 * Philipp Rohlfshagen - feel free to modify it or to start from scratch, using the classes supplied
 * with the original software. Best of luck!
 * 
 * This ghost controller does the following:
 * 1. If edible or Ms Pac-Man is close to power pill, run away from Ms Pac-Man
 * 2. If non-edible, attack Ms Pac-Man with certain prorbability, else choose random direction
 */
public final class MyGhosts implements GhostController
{	
	private final static float CONSISTENCY=0.9f;	//attack Ms Pac-Man with this probability
	private final static int PILL_PROXIMITY=15;		//if Ms Pac-Man is this close to a power pill, back away
	
	public int[] getActions(Game game,long timeDue)
	{		
		int[] directions=new int[Game.NUM_GHOSTS];
		
		for(int i=0;i<directions.length;i++)	//for each ghost
		{			
			if(game.ghostRequiresAction(i))		//if ghost requires an action
			{
				if(game.getEdibleTime(i)>0 || closeToPower(game))	//retreat from Ms Pac-Man if edible or if Ms Pac-Man is close to power pill
					directions[i]=game.getNextGhostDir(i,game.getCurPacManLoc(),false,Game.DM.PATH);
				else if(Game.rnd.nextFloat()<CONSISTENCY)			//attack Ms Pac-Man otherwise (with certain probability)
					directions[i]=game.getNextGhostDir(i,game.getCurPacManLoc(),true,Game.DM.PATH);
				else												//else take a random legal action (to be less predictable)
				{					
					int[] possibleDirs=game.getPossibleGhostDirs(i);	
					directions[i]=possibleDirs[Game.rnd.nextInt(possibleDirs.length)];
				}
			}
		}

		return directions;
	}
	
	//This helper function checks if Ms Pac-Man is close to an available power pill
    private boolean closeToPower(Game game)
    {
    	int[] powerPills=game.getPowerPillIndices();
    	
    	for(int i=0;i<powerPills.length;i++)
    		if(game.checkPowerPill(i) && game.getPathDistance(powerPills[i],game.getCurPacManLoc())<PILL_PROXIMITY)
    			return true;

        return false;
    }
}