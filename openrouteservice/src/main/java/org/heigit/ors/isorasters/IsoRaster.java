package org.heigit.ors.isorasters;

public class IsoRaster {
    public QuadTree tree;
    public String crs;
    public double precession;

    public IsoRaster(QuadTree tree, String crs, double precession)
    {
        this.tree = tree;
        this.crs = crs;
        this.precession = precession;
    }
}
