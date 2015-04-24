package predatorPrey;

import java.util.Random;

import predatorPrey.Prey;
import predatorPrey.Predator;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class PredatorPreyBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) 
	{
		int xdim = 50;
		int ydim = 50;
		
		context.setId ("PredatorPrey");
		
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders() ,xdim, ydim);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid ("grid", context, new GridBuilderParameters<Object>(new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, xdim, ydim));
		
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int predatorCount = (Integer)params.getValue("noPredators");
		for ( int i = 0; i < predatorCount ; i ++) 
		{
			int energy = RandomHelper.nextIntFromTo(40, 100);
			int age = RandomHelper.nextIntFromTo(1, 999);
			context.add(new Predator(space, grid, energy, age));
		}
		
		int preyCount = (Integer)params.getValue("noPrey");
		for ( int i = 0; i < preyCount ; i ++)
		{
			int energy = RandomHelper.nextIntFromTo(40, 100);
			int age = RandomHelper.nextIntFromTo(1, 999);
			context.add(new Prey(space, grid, energy, age));
		}
		
		for ( Object obj : context ) 
		{
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		
		for(int x=1; x<xdim; x++)
		{
			for(int y=1; y<ydim; y++)
			{
				Grass grass = new Grass();
				context.add(grass);
				grid.moveTo(grass, y, x);
				space.moveTo(grass, x, y);
			}
		}
		
		return context;
	}

}
