package org.heigit.ors.shortestpathtree;

import static org.heigit.ors.shortestpathtree.ShortestPathTree.IsoLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.graphhopper.storage.NodeAccess;
import com.vividsolutions.jts.geom.Coordinate;

public class ShortestPathTreeConsumer<T> implements Consumer{

    private QuadTree tree;
    private NodeAccess access;

    public ShortestPathTreeConsumer()
    {
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
        this.tree.insert(Utility.coordToIndex(access.getLon(label.node)), Utility.coordToIndex(access.getLat(label.node)), (int)label.time);
    }
    
    public QuadTree getTree()
    {
        return this.tree;
    }
}
