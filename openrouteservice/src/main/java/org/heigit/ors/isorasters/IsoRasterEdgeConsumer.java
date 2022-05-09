package org.heigit.ors.isorasters;

import static org.heigit.ors.isorasters.ShortestPathTree.IsoLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.vividsolutions.jts.geom.Coordinate;

public class IsoRasterEdgeConsumer implements IsoRasterConsumer {

    private QuadTree tree;
    private Graph graph;
    private Rasterizer rasterizer;

    public IsoRasterEdgeConsumer(Rasterizer rasterizer)
    {
        this.rasterizer = rasterizer;
        this.tree = new QuadTree();
    }

    public void setGraphAccess(Graph graph)
    {
        this.graph = graph;
    }

    @Override
    public void accept(IsoLabel label) {
        if (label.edge == -1)
            return;

        EdgeIteratorState egde = graph.getEdgeIteratorState(label.edge, label.node);

        PointList points = egde.fetchWayGeometry(2);

        for (int i=0; i< points.getSize(); i++)
        {
            double[] point = {points.getLon(i), points.getLat(i)};
            int[] indexes = rasterizer.transform(point);
            this.tree.insert(indexes[0], indexes[1], (int)label.time);
        }
    }
    
    public QuadTree getTree()
    {
        return this.tree;
    }
}

