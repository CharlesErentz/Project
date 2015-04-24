package predatorPrey;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace ;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid ;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.context.Context;
import repast.simphony.util.SimUtilities;


public class Predator
{
	private ContinuousSpace<Object> space;
	private Grid <Object> grid;
	private boolean moved;
	private int energy;
	private int age;
	private int reproductionrate;
	
	public Predator(ContinuousSpace<Object> space, Grid<Object> grid, int energy, int age)
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		reproductionrate =  (Integer)params.getValue("predatorReproduce");
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
			System.out.println("old age");
			return;
		}
		
		age++;
		
		GridPoint pt = grid.getLocation (this);
		
		GridCellNgh<Prey> nghCreator = new GridCellNgh <Prey>(grid, pt, Prey.class, 5 , 5);
		List <GridCell<Prey>> gridCells = nghCreator.getNeighborhood (true);
		SimUtilities.shuffle (gridCells, RandomHelper.getUniform ());
		
		GridPoint pointWithMostPrey = null;
		int maxCount = -1;
		for (GridCell<Prey> cell : gridCells) 
		{
			if (cell.size () > maxCount ) 
			{
				pointWithMostPrey = cell.getPoint();
				maxCount = cell.size();
			}
		}
		
		moveTowardsPrey(pointWithMostPrey);
		
		energy--;
		
		eat();
		
		System.out.println(energy);
		
		if(age>= 300)   //only agents of over 300 tick age may reproduce
		{
			if(RandomHelper.nextIntFromTo(1, 1000) <= reproductionrate) //small chance each tick to reproduce
			{
				energy = energy/2; //energy split between mother and child
				context.add(new Predator(space, grid, energy/2, 0)); //new predator age 0
				System.out.println("birth");
				
				for ( Object obj : context ) 
				{
					if( obj instanceof Predator && ((Predator) obj).getAge()==0)
					{
						NdPoint point = space.getLocation(obj);
						grid.moveTo(obj, (int)point.getX() +1, (int)point.getY()+1);
					}
				}
			}
		}
		
		if(energy<=0)
		{
			die();
			System.out.println("hunger");
		}
		
	}
	
	public void moveTowardsPrey(GridPoint pt)
	{
		if (!pt.equals(grid.getLocation(this)));
		{
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY ());
			
			moved = true;
		}
	}
	
	public void eat()
	{
		GridPoint pt = grid.getLocation(this);
		List<Object> prey = new ArrayList < Object >();
		
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) 
		{
			if ( obj instanceof Prey) 
			{
				prey.add(obj);
			}
		}
		
		if (prey.size() > 0)
		{
			int index = RandomHelper.nextIntFromTo(0 ,prey.size()-1);
			Object obj = prey.get(index);
			
			NdPoint spacePt = space.getLocation (obj);
			Context<Object> context = ContextUtils.getContext(obj);
			context.remove(obj);
			
			System.out.println("dinner time");
			this.energy = 100;
		}
	}
	
	public void die()
	{
		Context<Object> context = ContextUtils.getContext(this);
		if(context.size() > 1)
			context.remove(this);
	}
	
	public int getAge()
	{
		return age;
	}
	

}