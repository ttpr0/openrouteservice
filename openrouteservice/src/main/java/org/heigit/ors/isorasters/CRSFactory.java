package org.heigit.ors.isorasters;

public class CRSFactory {
    public static IProjection getProjection(String crs)
    {
        if (crs.equals("4326"))
        {
            return new NullProjection();
        }
        else
        {
            return new WebMercatorProjection();
        }
    }
}
