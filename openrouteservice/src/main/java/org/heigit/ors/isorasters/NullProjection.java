package org.heigit.ors.isorasters;

public class NullProjection implements IProjection {
        
    public void proj(double[] point)
    {
        point = point;
    }

    public void reproj(double[] point)
    {
        point = point;
    }
}
