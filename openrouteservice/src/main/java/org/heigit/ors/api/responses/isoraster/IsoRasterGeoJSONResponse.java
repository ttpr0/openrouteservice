package org.heigit.ors.api.responses.isoraster;

import java.util.List;

import org.geotools.referencing.CRS;
import org.heigit.ors.isorasters.GeoJsonFeature;
import org.heigit.ors.isorasters.GeoJsonPolygon;
import org.heigit.ors.isorasters.IsoRaster;
import org.heigit.ors.isorasters.NullProjection;
import org.heigit.ors.isorasters.QuadNode;
import org.heigit.ors.isorasters.QuadTree;
import org.heigit.ors.isorasters.Rasterizer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class IsoRasterGeoJSONResponse {

    public String type = "FeatureCollection"; 
    public GeoJsonFeature[] features;
    
    public IsoRasterGeoJSONResponse(List<IsoRaster> rasters, String crs, double precession, boolean intersect)
    {
        Rasterizer rasterizer = new Rasterizer(precession, crs, true);

        QuadTree tree = rasters.get(0).tree;
        for (int i=1; i<rasters.size(); i++)
        {
            tree.mergeQuadNodes(rasters.get(i).tree.toList());
        }
        List<QuadNode> nodelist = tree.toList();
        // GeoJsonPoint[] points = new GeoJsonPoint[nodelist.size()];
        // for (int i=0; i<points.length; i++) 
        // {
        //     QuadNode node = nodelist.get(i);
        //     points[i] = new GeoJsonPoint(node.value, new Coordinate(Utility.indexToCoord(node.x), Utility.indexToCoord(node.y)));
        // }
        int[] intpoint = new int[2];
        GeoJsonFeature[] points = new GeoJsonPolygon[nodelist.size()];
        for (int i=0; i<points.length; i++) 
        {
            QuadNode node = nodelist.get(i);
            Double[][][] p = new Double[1][5][2];
            intpoint[0] = node.x;
            intpoint[1] = node.y;
            double[] r = rasterizer.revtransform(intpoint);
            p[0][0][0] = r[0];
            p[0][0][1] = r[1];
            p[0][1][1] = r[1];
            p[0][3][0] = r[0];
            p[0][4][0] = r[0];
            p[0][4][1] = r[1];
            intpoint[0] = node.x + 1;
            intpoint[1] = node.y + 1;
            double[] or = rasterizer.revtransform(intpoint);
            p[0][1][0] = or[0];
            p[0][2][0] = or[0];
            p[0][2][1] = or[1];
            p[0][3][1] = or[1];
            points[i] = new GeoJsonPolygon(node.value, p);
        }
        this.features = points;
    }

}
