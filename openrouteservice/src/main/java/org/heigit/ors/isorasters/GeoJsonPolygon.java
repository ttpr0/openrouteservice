package org.heigit.ors.isorasters;

import org.locationtech.jts.geom.Coordinate;

public class GeoJsonPolygon extends GeoJsonFeature {
    public String type = "Feature";
    public properties properties;
    public geometry geometry;

    public class geometry
    {
        public String type = "Polygon";
        public Double[][][] coordinates;

        public geometry(Double[][][] points)
        {
            this.coordinates = points;
        }
    }

    public class properties
    {
        public int value;

        public properties(int value)
        {
            this.value = value;
        }
    }

    public GeoJsonPolygon(int value, Double[][][] points)
    {
        this.properties = new properties(value);
        this.geometry = new geometry(points);
    }
}