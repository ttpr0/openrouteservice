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

public class IsoRasterGridResponse
{
    public String type = "Raster"; 
    public double[] lowerleft; 
    public int rows;
    public int columns;
    public int[][] values;
    
    public IsoRasterGridResponse(List<IsoRaster> rasters, String crs, double precession, boolean intersect)
    {

        QuadTree tree = rasters.get(0).tree;
        for (int i=1; i<rasters.size(); i++)
        {
            tree.mergeQuadNodes(rasters.get(i).tree.toList());
        }
        List<QuadNode> nodelist = tree.toList();
        int[] bb  = tree.getBoundingBox();
        this.lowerleft = new double[] { bb[0]*precession, bb[2]*precession };
        this.rows = bb[3] - bb[2] + 1;
        this.columns = bb[1] - bb[0] + 1;
        this.values = new int[nodelist.size()][3];
        for (int i=0; i<nodelist.size(); i++) 
        {
            QuadNode node = nodelist.get(i);
            values[i][0] = bb[3] - node.y; 
            values[i][1] = node.x - bb[0]; 
            values[i][2] = node.value; 
        }
    }
} 