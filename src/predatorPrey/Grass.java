package predatorPrey;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;



public class Grass 
{
	private Grid <Object> grid;
	private int respawntimer;
	private boolean alive;
	private int regrowthtime;
	
	Grass()
	{
		Parameters params = RunEnvironment.getInstance().getParameters();  //set regrowth time through paramters
		regrowthtime =  (Integer)params.getValue("grassRegrowth");
		
		if(Math.random() <= 0.5) // random chance of each grass patch of being alive
			alive=true;
		else
		{
			alive=false;
			respawntimer = (int)(Math.random()*regrowthtime);
		}
	}
	
	@ScheduledMethod (start=1, interval=1)
	public void step()
	{
		if(!alive)
		{
			if(respawntimer<=0)  //set patch to alive once timer has run out or decrease timer
			{
				respawntimer = regrowthtime;
				alive = true;
			}
			else
			{
				respawntimer--;
			}
		}
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public void die()
	{
		this.alive = false;
	}
	
	public int noAliveGrass() //returns no. of alive grass patches
	{
		Context<Object> context = ContextUtils.getContext(this);
		int count = 0;
		for ( Object obj : context ) 
		{
			if(obj instanceof Grass && ((Grass) obj).isAlive() == true)
			{
				count++;
			}
		}
		return count;
	}
}
