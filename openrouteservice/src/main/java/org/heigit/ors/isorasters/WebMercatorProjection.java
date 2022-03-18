package org.heigit.ors.isorasters;

public class WebMercatorProjection implements IProjection {
    static int a = 6378137;

    public void proj(double[] point)
    {
        point[0] = a * point[0] * Math.PI / 180;
        point[1] = a * Math.log(Math.tan(Math.PI / 4 + point[1] * Math.PI / 360));
    }

    public void reproj(double[] point)
    {
        point[0] = point[0] * 180 / (a * Math.PI);
        point[1] = 360 * (Math.atan(Math.exp(point[1] / a)) - Math.PI / 4) / Math.PI;
    }
}
