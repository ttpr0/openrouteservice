/*  This file is part of Openrouteservice.
 *
 *  Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.

 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License along with this library;
 *  if not, see <https://www.gnu.org/licenses/>.
 */
package org.heigit.ors.isorasters;

import org.locationtech.jts.geom.Coordinate;

import org.heigit.ors.common.TravelRangeType;
import org.heigit.ors.routing.RouteSearchParameters;

public class IsoRasterSearchParameters {
    private final int travellerId;
    private Coordinate location;
    private Boolean reverseDirection = false;
    private TravelRangeType rangeType = TravelRangeType.TIME;
    private double[] ranges;
    private RouteSearchParameters parameters;
    private String units;
    private double precession;
    private String crs;
    private String consumerType;

    public IsoRasterSearchParameters(int travellerId, Coordinate location, double[] ranges) {
        this.travellerId = travellerId;
        this.location = location;
        this.ranges = ranges;
    }

    public int getTravellerId() {
        return travellerId;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public Boolean getReverseDirection() {
        return reverseDirection;
    }

    public void setReverseDirection(Boolean value) {
        reverseDirection = value;
    }

    public void setRangeType(TravelRangeType rangeType) {
        this.rangeType = rangeType;
    }

    public TravelRangeType getRangeType() {
        return rangeType;
    }

    public void setRanges(double[] values) {
        ranges = values;
    }

    public double[] getRanges() {
        return ranges;
    }

    public double getMaximumRange() {
        if (ranges.length == 1)
            return ranges[0];
        else {
            double maxValue = Double.MIN_VALUE;
            for (int i = 0; i < ranges.length; ++i) {
                double v = ranges[i];
                if (v > maxValue)
                    maxValue = v;
            }

            return maxValue;
        }
    }

    public RouteSearchParameters getRouteParameters() {
        return parameters;
    }

    public void setRouteParameters(RouteSearchParameters parameters) {
        this.parameters = parameters;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    public boolean isTimeDependent() {
        return (getRouteParameters().isTimeDependent());
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
}
