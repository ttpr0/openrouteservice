package org.heigit.ors.isorasters;

import static org.heigit.ors.isorasters.ShortestPathTree.IsoLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import org.locationtech.jts.geom.Coordinate;

public class IsoRasterNodeConsumer implements IsoRasterConsumer {

    private QuadTree tree;
    private NodeAccess access;
    private Rasterizer rasterizer;

    public IsoRasterNodeConsumer(Rasterizer rasterizer)
    {
        this.rasterizer = rasterizer;
        this.tree = new QuadTree();
    }

    public void setGraphAccess(Graph graph)
    {
        this.access = graph.getNodeAccess();
    }

    @Override
    public void accept(IsoLabel label) {
        // TODO Auto-generated method stub
        double[] point = {access.getLon(label.node), access.getLat(label.node)};
        int[] indexes = rasterizer.transform(point);
        this.tree.insert(indexes[0], indexes[1], (int)label.time);
    }
    
    public QuadTree getTree()
    {
        return this.tree;
    }
}
