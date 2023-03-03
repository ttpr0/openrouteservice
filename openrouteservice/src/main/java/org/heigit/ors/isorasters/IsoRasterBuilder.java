package org.heigit.ors.isorasters;


import org.heigit.ors.isochrones.IsochroneSearchParameters;
import org.heigit.ors.routing.AvoidFeatureFlags;
import org.heigit.ors.routing.RouteSearchContext;
import org.heigit.ors.routing.graphhopper.extensions.ORSEdgeFilterFactory;
import org.heigit.ors.routing.graphhopper.extensions.ORSWeightingFactory;
import org.heigit.ors.routing.graphhopper.extensions.edgefilters.AvoidFeaturesEdgeFilter;
import org.heigit.ors.routing.graphhopper.extensions.edgefilters.EdgeFilterSequence;
import org.heigit.ors.isochrones.builders.concaveballs.PointItemVisitor;
import org.heigit.ors.isorasters.ShortestPathTree;

import scala.annotation.meta.param;
import com.graphhopper.util.*;
import org.locationtech.jts.geom.*;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.Snap;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class IsoRasterBuilder {
    private static DistanceCalc dcFast = new DistancePlaneProjection();
    private double searchWidth = 0.0007;
    private double pointWidth = 0.0005;
    private double visitorThreshold = 0.0013;
    private Envelope searchEnv = new Envelope();
    private List<Coordinate> prevIsoPoints = null;
    private PointItemVisitor visitor = null;
    private TreeSet<Coordinate> treeSet;
    private RouteSearchContext searchcontext;

    public IsoRasterBuilder(RouteSearchContext searchContext)
    {
        this.searchcontext = searchContext;
    }

    public IsoRaster compute(IsoRasterSearchParameters parameters) throws Exception {

        Graph graph = searchcontext.getGraphHopper().getGraphHopperStorage().getBaseGraph();
        Weighting weighting = ORSWeightingFactory.createIsochroneWeighting(searchcontext, parameters.getRangeType());

        ORSEdgeFilterFactory edgeFilterFactory = new ORSEdgeFilterFactory();
        Coordinate loc = parameters.getLocation();
        EdgeFilterSequence edgeFilterSequence = getEdgeFilterSequence(edgeFilterFactory);
        Snap res = searchcontext.getGraphHopper().getLocationIndex().findClosest(loc.y, loc.x, edgeFilterSequence);
        int from = res.getClosestNode();
        double[] ranges = parameters.getRanges();

        IsoRasterConsumer consumer;
        if (parameters.getConsumerType() == "edge_based")
            consumer = new IsoRasterEdgeConsumer(new Rasterizer(parameters.getPrecession(), parameters.getCrs(), false));
        else
            consumer = new IsoRasterNodeConsumer(new Rasterizer(parameters.getPrecession(), parameters.getCrs(), false));
        consumer.setGraphAccess(graph);

        ShortestPathTree alg = new ShortestPathTree(graph, weighting, true, TraversalMode.EDGE_BASED);
        alg.setTimeLimit(ranges[ranges.length-1]);
        alg.search(from, consumer);
        
        return new IsoRaster(consumer.getTree(), parameters.getCrs(), parameters.getPrecession());
    }

    private EdgeFilterSequence getEdgeFilterSequence(ORSEdgeFilterFactory edgeFilterFactory) throws Exception {
        EdgeFilterSequence edgeFilterSequence = new EdgeFilterSequence();
        EdgeFilter edgeFilter = edgeFilterFactory.createEdgeFilter(searchcontext.getProperties(), searchcontext.getEncoder(), searchcontext.getGraphHopper().getGraphHopperStorage());
        edgeFilterSequence.add(edgeFilter);
        edgeFilterSequence.add(new AvoidFeaturesEdgeFilter(AvoidFeatureFlags.FERRIES, searchcontext.getGraphHopper().getGraphHopperStorage()));
        return edgeFilterSequence;
    }
}
