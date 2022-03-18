package org.heigit.ors.isorasters;

import static org.heigit.ors.isorasters.ShortestPathTree.IsoLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.graphhopper.storage.NodeAccess;
import com.vividsolutions.jts.geom.Coordinate;

public class IsoRasterConsumer<T> implements Consumer{

    private QuadTree tree;
    private NodeAccess access;
    private Rasterizer rasterizer;

    public IsoRasterConsumer(Rasterizer rasterizer)
    {
        this.rasterizer = rasterizer;
        this.tree = new QuadTree();
    }

    public void setNodeAcess(NodeAccess nodeaccess)
    {
        this.access = nodeaccess;
    }

    @Override
    public void accept(Object t) {
        // TODO Auto-generated method stub
        IsoLabel label = (IsoLabel)t;
        double[] point = {access.getLon(label.node), access.getLat(label.node)};
        rasterizer.coordToIndex(point);
        this.tree.insert((int)point[0], (int)point[1], (int)label.time);
    }
    
    public QuadTree getTree()
    {
        return this.tree;
    }
}
