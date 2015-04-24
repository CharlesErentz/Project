package predatorPrey;

import java.awt.Color; 
import java.awt.Paint; 
import java.awt.Stroke; 



import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;


public class AgentStyle2D extends DefaultStyleOGL2D
{

	@Override 
    public Color getColor(Object o) 
    { 
            if(o instanceof Grass) { 
                    Grass agent = (Grass) o; 
                    if(agent.isAlive()) { 
                            return Color.GREEN; 
                    } 
                    else { 
                            return new Color(165, 42, 42);
                    } 
            } 
            else { 
                    return null;
            } 
    }
	
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
	    if (spatial == null) {
	      spatial = shapeFactory.createRectangle(15, 15);
	    }
	    return spatial;
	  }
	
	
}
