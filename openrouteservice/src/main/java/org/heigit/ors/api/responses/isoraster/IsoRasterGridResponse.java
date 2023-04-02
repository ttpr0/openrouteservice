package org.heigit.ors.api.responses.isoraster;

import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.kdtree.*;
import org.heigit.ors.isorasters.GeoJsonFeature;
import org.heigit.ors.isorasters.GeoJsonPolygon;
import org.heigit.ors.isorasters.IsoRaster;
import org.heigit.ors.isorasters.NullProjection;
import org.heigit.ors.isorasters.QuadNode;
import org.heigit.ors.isorasters.QuadTree;
import org.heigit.ors.isorasters.Rasterizer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class IsoRasterGridResponse {
    public String type = "Raster";
    public double precession;
    public String crs;
    public double[] extend;
    public int[] size;
    public double[][] envelope;
    public List<GridFeature> features;

    public IsoRasterGridResponse(KdTree tree, String crs, double precession, boolean intersect) {
        Rasterizer rasterizer = new Rasterizer(precession, crs, true);

        List<KdNode> nodelist = tree
                .query(new Envelope(Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE));
        int[] bb = new int[4];
        bb[0] = (int) nodelist.get(0).getX();
        bb[1] = (int) nodelist.get(0).getX();
        bb[2] = (int) nodelist.get(0).getY();
        bb[3] = (int) nodelist.get(0).getY();
        for (KdNode node : nodelist) {
            if (node.getX() < bb[0])
                bb[0] = (int) node.getX();
            if (node.getX() > bb[1])
                bb[1] = (int) node.getX();
            if (node.getY() < bb[2])
                bb[2] = (int) node.getY();
            if (node.getY() > bb[3])
                bb[3] = (int) node.getY();
        }

        this.precession = precession;
        this.crs = crs;
        this.extend = new double[] { bb[0] * precession, bb[2] * precession, (bb[1] + 1) * precession,
                (bb[3] + 1) * precession };
        this.envelope = new double[4][2];
        double[] ll = rasterizer.revtransform(new int[] { bb[0], bb[2] });
        this.envelope[0][0] = ll[0];
        this.envelope[0][1] = ll[1];
        double[] lr = rasterizer.revtransform(new int[] { bb[0], bb[3] + 1 });
        this.envelope[1][0] = lr[0];
        this.envelope[1][1] = lr[1];
        double[] ul = rasterizer.revtransform(new int[] { bb[1] + 1, bb[2] });
        this.envelope[2][0] = ul[0];
        this.envelope[2][1] = ul[1];
        double[] ur = rasterizer.revtransform(new int[] { bb[1] + 1, bb[3] + 1 });
        this.envelope[3][0] = ur[0];
        this.envelope[3][1] = ur[1];
        this.size = new int[] { bb[1] - bb[0] + 1, bb[3] - bb[2] + 1 };
        this.features = new ArrayList<GridFeature>(nodelist.size());
        for (int i = 0; i < nodelist.size(); i++) {
            KdNode node = nodelist.get(i);
            double x = (node.getX() + 0.5) * precession;
            double y = (node.getY() + 0.5) * precession;
            features.add(new GridFeature((float) x, (float) y, node.getData()));
        }
    }

    public IsoRasterGridResponse(List<IsoRaster> rasters, String crs, double precession, boolean intersect) {
        Rasterizer rasterizer = new Rasterizer(precession, crs, true);

        QuadTree tree = rasters.get(0).tree;
        for (int i = 1; i < rasters.size(); i++) {
            tree.mergeQuadNodes(rasters.get(i).tree.toList());
        }
        List<QuadNode> nodelist = tree.toList();
        int[] bb = tree.getBoundingBox();
        this.precession = precession;
        this.crs = crs;
        this.extend = new double[] { bb[0] * precession, bb[2] * precession, (bb[1] + 1) * precession,
                (bb[3] + 1) * precession };
        this.envelope = new double[4][2];
        double[] ll = rasterizer.revtransform(new int[] { bb[0], bb[2] });
        this.envelope[0][0] = ll[0];
        this.envelope[0][1] = ll[1];
        double[] lr = rasterizer.revtransform(new int[] { bb[0], bb[3] + 1 });
        this.envelope[1][0] = lr[0];
        this.envelope[1][1] = lr[1];
        double[] ul = rasterizer.revtransform(new int[] { bb[1] + 1, bb[2] });
        this.envelope[2][0] = ul[0];
        this.envelope[2][1] = ul[1];
        double[] ur = rasterizer.revtransform(new int[] { bb[1] + 1, bb[3] + 1 });
        this.envelope[3][0] = ur[0];
        this.envelope[3][1] = ur[1];
        this.size = new int[] { bb[1] - bb[0] + 1, bb[3] - bb[2] + 1 };
        this.features = new ArrayList<GridFeature>(nodelist.size());
        for (int i = 0; i < nodelist.size(); i++) {
            QuadNode node = nodelist.get(i);
            double x = (node.x + 0.5) * precession;
            double y = (node.y + 0.5) * precession;
            features.add(new GridFeature((float) x, (float) y, new TempValue(node.value)));
        }
    }
}

class TempValue {
    public int range;

    public TempValue(int value) {
        this.range = value;
    }
}

class GridFeature {
    public float x;
    public float y;
    public Object value;

    public GridFeature(float x, float y, Object value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}