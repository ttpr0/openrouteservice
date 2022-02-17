package org.heigit.ors.shortestpathtree;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.graphhopper.coll.GHIntObjectHashMap;
import com.graphhopper.routing.AbstractRoutingAlgorithm;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.profiles.BooleanEncodedValue;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.GHUtility;
import com.vividsolutions.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.function.Consumer;

public class Utility {
    
    public static double calcWeightWithTurnWeightWithAccess(Weighting weighting, EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        BooleanEncodedValue accessEnc = weighting.getFlagEncoder().getAccessEnc();
        if (edgeState.getBaseNode() == edgeState.getAdjNode()) {
            if (!edgeState.get(accessEnc) && !edgeState.getReverse(accessEnc))
                return Double.POSITIVE_INFINITY;
        } else if ((!reverse && !edgeState.get(accessEnc)) || (reverse && !edgeState.getReverse(accessEnc))) {
            return Double.POSITIVE_INFINITY;
        }
        return calcWeightWithTurnWeight(weighting, edgeState, reverse, prevOrNextEdgeId);
    }

    public static double calcWeightWithTurnWeight(Weighting weighting, EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        final double edgeWeight = weighting.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        if (!EdgeIterator.Edge.isValid(prevOrNextEdgeId)) {
            return edgeWeight;
        }
        return edgeWeight;
    }
    /**
     * @see #calcWeightWithTurnWeight(Weighting, EdgeIteratorState, boolean, int)
     */
    public static long calcMillisWithTurnMillis(Weighting weighting, EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        long edgeMillis = weighting.calcMillis(edgeState, reverse, prevOrNextEdgeId);
        if (!EdgeIterator.Edge.isValid(prevOrNextEdgeId)) {
            return edgeMillis;
        }
        // should we also separate weighting vs. time for turn? E.g. a fast but dangerous turn - is this common?
        // todo: why no first/last orig edge here as in calcWeight ?
//        final int origEdgeId = reverse ? edgeState.getOrigEdgeLast() : edgeState.getOrigEdgeFirst();
        return edgeMillis;
    }

    public static int coordToIndex(double x)
    {
        return (int)(x*200);
    }

    public static double indexToCoord(int x)
    {
        return ((double)x)/200;
    }
}
