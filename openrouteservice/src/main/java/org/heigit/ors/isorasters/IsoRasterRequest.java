package org.heigit.ors.isorasters;

import org.locationtech.jts.geom.Coordinate;
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
    private String units = null;
    private boolean includeIntersections = false;
    private double precession;
    private String crs;
    private String consumerType;

    public IsoRasterRequest() {
        travellers = new ArrayList<>();
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units.toLowerCase();
    }

    public boolean isValid() {
        return !travellers.isEmpty();
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

    public void setPrecession(double precession)
    {
        this.precession = precession;
    }

    public double getPrecession()
    {
        return this.precession;
    }

    public void setCrs(String crs)
    {
        this.crs = crs;
    }

    public String getCrs()
    {
        return this.crs;
    }

    public void setConsumerType(String consumerType)
    {
        this.consumerType = consumerType;
    }

    public String getConsumerType()
    {
        return this.consumerType;
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
        parameters.setUnits(units);
        parameters.setRouteParameters(traveller.getRouteSearchParameters());
        if ("destination".equalsIgnoreCase(traveller.getLocationType()))
            parameters.setReverseDirection(true);
        parameters.setCrs(crs);
        parameters.setPrecession(precession);
        parameters.setConsumerType(consumerType);
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
