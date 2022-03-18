package org.heigit.ors.isorasters;


public class IsoRasterMap {
    public String type = "FeatureCollection"; 
    public GeoJsonFeature[] features;

    public IsoRasterMap(GeoJsonFeature[] points)
    {
        this.features = points;
    }
}
