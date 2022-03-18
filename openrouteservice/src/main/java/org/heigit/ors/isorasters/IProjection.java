package org.heigit.ors.isorasters;

public interface IProjection {
    
    public void proj(double[] point);

    public void reproj(double[] point);
}
