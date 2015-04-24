package predatorPrey;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Prey
{
	private ContinuousSpace<Object> space ;
	private Grid<Object> grid ;
	private int energy;
	private int age;
	private int reproductionrate;
	
	public Prey (ContinuousSpace< Object > space , Grid < Object > grid , int energy, int age)
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		reproductionrate =  (Integer)params.getValue("preyReproduce");
		this.space = space;
		this.grid = grid;
		this.energy = energy;
		this.age = age;
	}
	
	
	@ScheduledMethod (start=1, interval=1)
	public void step ()
	{	
		Context context = ContextUtils.getContext(this); // create context so new agents can be added
		
		if(age >= 1000)
		{
			die();
			return; //break out of method if agent dies
		}
		
		age++; //age each tick
		
		if(energy<70) //attempt to eat
			eat();
		
		if(age>= 300)   //only prey of over 300 tick age may reproduce
		{
			if(RandomHelper.nextIntFromTo(1, 1000) <= reproductionrate) //small chance each tick to reproduce
			{
				energy = energy/2;
				context.add(new Prey(space, grid, energy/2, 0));
				
				for ( Object obj : context )    //Find the new prey and move it to grid space next to mother
				{
					if( obj instanceof Prey && ((Prey) obj).getAge()==0)
					{
						NdPoint point = space.getLocation(obj);
						grid.moveTo(obj, (int)point.getX() +1, (int)point.getY()+1);
					}
				}
			}
		}
		
		move();
		
		energy--;
		
		if(energy<=0)
		{
			die();
		}
	}
	
	private int getAge() 
	{
		return age;
	}
	
	public void die()
	{
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
	}
	
	public void move()
	{
		space.moveByVector(this, 1, RandomHelper.nextIntFromTo(1, 360), 0);
		NdPoint myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY ());
	}
	
	public void eat()
	{
		NdPoint pt = space.getLocation(this);
		
		List<Object> grass = new ArrayList <Object>();
		
		for (Object obj : grid.getObjectsAt((int)pt.getY(), (int)pt.getX()))
		{
			if ( obj instanceof Grass && ((Grass) obj).isAlive() == true) 
			{
				((Grass) obj).die();				
				
				this.energy += 5;
			}
		}
	}
	
}

