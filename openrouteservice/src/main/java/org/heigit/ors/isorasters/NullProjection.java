package org.heigit.ors.isorasters;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

public class NullProjection implements MathTransform {
    @Override
    public int getSourceDimensions() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getTargetDimensions() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public DirectPosition transform(DirectPosition ptSrc, DirectPosition ptDst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        dstPts = srcPts;
    }

    @Override
    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        dstPts = srcPts;
    }

    @Override
    public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        for (int i=0; i<numPts; i++)
        {
            dstPts[dstOff + i*2] = (double)srcPts[srcOff + i*2];
            dstPts[dstOff + i*2 + 1] = (double)srcPts[srcOff + i*2 + 1];
        }
    }

    @Override
    public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        for (int i=0; i<numPts; i++)
        {
            dstPts[dstOff + i*2] = (float)srcPts[srcOff + i*2];
            dstPts[dstOff + i*2 + 1] = (float)srcPts[srcOff + i*2 + 1];
        }
    }

    @Override
    public Matrix derivative(DirectPosition point) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MathTransform inverse() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isIdentity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toWKT() {
        // TODO Auto-generated method stub
        return null;
    }
}
