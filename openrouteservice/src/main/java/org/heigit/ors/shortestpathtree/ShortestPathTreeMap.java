package org.heigit.ors.shortestpathtree;

import java.util.List;

public class ShortestPathTreeMap {
    public String type = "FeatureCollection"; 
    public GeoJsonPoint[] features;

    public ShortestPathTreeMap(GeoJsonPoint[] points)
    {
        this.features = points;
    }
}
