package org.heigit.ors.isorasters;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class Rasterizer {
    private double precession;
    private MathTransform projector;
    private MathTransform revprojector;
    private int[] intpoint = new int[2];
    private float[] floatpoint = new float[2];
    private double[] doublepoint = new double[2];

    public Rasterizer(double precession, String crs, boolean rev)
    {
        this.precession = precession;
        try 
        {
            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326", true);
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + crs, true);
            this.projector = CRS.findMathTransform(sourceCRS, targetCRS);
            if (rev)
            { this.revprojector = projector.inverse(); }
        }
        catch (Exception ex)
        {
            this.projector = new NullProjection();
            this.precession = 0.01;
        }
    }

    public int[] transform(double[] point)
    {
        try 
        {
            projector.transform(point, 0, floatpoint, 0, 1);
            intpoint[0] = (int)(floatpoint[0]/precession);
            intpoint[1] = (int)(floatpoint[1]/precession);
            return intpoint;
        }
        catch (Exception ex)
        {
            intpoint[0] = (int)(point[0]/precession);
            intpoint[1] = (int)(point[1]/precession);
            return intpoint;
        }
    }

    public double[] revtransform(int[] point)
    {
        try 
        {   
            floatpoint[0] = (float)(point[0]*precession);
            floatpoint[1] = (float)(point[1]*precession);
            revprojector.transform(floatpoint, 0, doublepoint, 0, 1);
            return doublepoint;
        }
        catch (Exception ex)
        {
            doublepoint[0] = (double)(point[0]*precession);
            doublepoint[1] = (double)(point[1]*precession);
            return doublepoint;
        }
    }
}
