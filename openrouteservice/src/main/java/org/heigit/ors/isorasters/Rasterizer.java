package org.heigit.ors.isorasters;

public class Rasterizer {
    double factor;
    IProjection projection;

    public Rasterizer(double precession, String crs)
    {
        this.factor = precession;
        this.projection = CRSFactory.getProjection(crs);
    }

    public void coordToIndex(double[] point)
    {
        projection.proj(point);
        point[0] = (int)(point[0]/factor);
        point[1] = (int)(point[1]/factor);
    }

    public void indexToCoord(double[] point)
    {
        point[0] = ((double)point[0])*factor;
        point[1] = ((double)point[1])*factor;
        projection.reproj(point);
    } 
}
