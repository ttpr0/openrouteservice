package org.heigit.ors.isorasters;
import com.vividsolutions.jts.geom.Coordinate;
import org.heigit.ors.common.TravelRangeType;
import org.heigit.ors.common.TravellerInfo;
import org.heigit.ors.routing.RoutingProfileType;
import org.heigit.ors.routing.WeightingMethod;

import springfox.documentation.spring.web.readers.parameter.ParameterRequiredReader;

import org.heigit.ors.common.ServiceRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IsoRasterRequest extends ServiceRequest {
    private final List<TravellerInfo> travellers;
    private String calcMethod;
    private String units = null;
    private String areaUnits = null;
    private boolean includeIntersections = false;
    private String[] attributes;
    private float smoothingFactor = -1.0f;
    private Rasterizer rasterizer;

    public IsoRasterRequest() {
        travellers = new ArrayList<>();
    }

    public String getCalcMethod() {
        return calcMethod;
    }

    public void setCalcMethod(String calcMethod) {
        this.calcMethod = calcMethod;
    }

    public String getUnits() {
        return units;
    }

    public String getAreaUnits() {
        return areaUnits;
    }

    public void setUnits(String units) {
        this.units = units.toLowerCase();
    }

    public void setAreaUnits(String areaUnits) {
        this.areaUnits = areaUnits.toLowerCase();
    }

    public boolean isValid() {
        return !travellers.isEmpty();
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public boolean hasAttribute(String attr) {
        if (attributes == null || attr == null)
            return false;

        for (String attribute : attributes)
            if (attr.equalsIgnoreCase(attribute))
                return true;

        return false;
    }

    public boolean getIncludeIntersections() {
        return includeIntersections;
    }

    public void setIncludeIntersections(boolean value) {
        includeIntersections = value;
    }

    public Coordinate[] getLocations() {
        return travellers.stream().map(TravellerInfo::getLocation).toArray(Coordinate[]::new);
    }

    public void setSmoothingFactor(float smoothingFactor) {
        this.smoothingFactor = smoothingFactor;
    }

    public void setRasterizer(Rasterizer rasterizer)
    {
        this.rasterizer = rasterizer;
    }

    public Rasterizer getRasterizer()
    {
        return this.rasterizer;
    }

    public IsoRasterSearchParameters getSearchParameters(int travellerIndex) {
        TravellerInfo traveller = travellers.get(travellerIndex);
        double[] ranges = traveller.getRanges();

        // convert ranges in units to meters or seconds
        if (!(units == null || "m".equalsIgnoreCase(units))) {
            double scale = 1.0;
            if (traveller.getRangeType() == TravelRangeType.DISTANCE) {
                switch (units) {
                    default:
                    case "m":
                        break;
                    case "km":
                        scale = 1000;
                        break;
                    case "mi":
                        scale = 1609.34;
                        break;
                }
            }

            if (scale != 1.0) {
                for (int i = 0; i < ranges.length; i++)
                    ranges[i] = ranges[i] * scale;
            }
        }

        IsoRasterSearchParameters parameters = new IsoRasterSearchParameters(travellerIndex, traveller.getLocation(), ranges);
        parameters.setLocation(traveller.getLocation());
        parameters.setRangeType(traveller.getRangeType());
        parameters.setCalcMethod(calcMethod);
        parameters.setAttributes(attributes);
        parameters.setUnits(units);
        parameters.setAreaUnits(areaUnits);
        parameters.setRouteParameters(traveller.getRouteSearchParameters());
        if ("destination".equalsIgnoreCase(traveller.getLocationType()))
            parameters.setReverseDirection(true);
        parameters.setSmoothingFactor(smoothingFactor);
        parameters.setRasterizer(this.rasterizer);
        return parameters;
    }

    public List<TravellerInfo> getTravellers() {
        return travellers;
    }

    public void addTraveller(TravellerInfo traveller) throws Exception {
        if (traveller == null)
            throw new Exception("'traveller' argument is null.");

        travellers.add(traveller);
    }

    public Set<String> getProfilesForAllTravellers() {
        Set<String> ret = new HashSet<>();
        for (TravellerInfo traveller : travellers)
            ret.add(RoutingProfileType.getName(traveller.getRouteSearchParameters().getProfileType()));
        return ret;
    }

    public Set<String> getWeightingsForAllTravellers() {
        Set<String> ret = new HashSet<>();
        for (TravellerInfo traveller : travellers)
            ret.add(WeightingMethod.getName(traveller.getRouteSearchParameters().getWeightingMethod()));
        return ret;
    }
}
