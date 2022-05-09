package org.heigit.ors.isorasters;

import static org.heigit.ors.isorasters.ShortestPathTree.IsoLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.vividsolutions.jts.geom.Coordinate;

public interface IsoRasterConsumer extends Consumer<IsoLabel> {
    
    public void setGraphAccess(Graph graph);

    public void accept(IsoLabel label);

    public QuadTree getTree();
}
