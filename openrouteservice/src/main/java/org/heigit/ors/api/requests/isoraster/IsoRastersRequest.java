package org.heigit.ors.api.requests.isoraster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Coordinate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.heigit.ors.api.requests.common.APIEnums;
import org.heigit.ors.api.requests.common.APIRequest;
import org.heigit.ors.api.requests.routing.RouteRequestOptions;
import org.heigit.ors.common.DistanceUnit;
import org.heigit.ors.common.StatusCode;
import org.heigit.ors.common.TravelRangeType;
import org.heigit.ors.common.TravellerInfo;
import org.heigit.ors.exceptions.InternalServerException;
import org.heigit.ors.exceptions.ParameterOutOfRangeException;
import org.heigit.ors.exceptions.ParameterValueException;
import org.heigit.ors.exceptions.StatusCodeException;
import org.heigit.ors.isochrones.*;
import org.heigit.ors.isorasters.GeoJsonFeature;
import org.heigit.ors.isorasters.GeoJsonPoint;
import org.heigit.ors.isorasters.GeoJsonPolygon;
import org.heigit.ors.isorasters.IsoRaster;
import org.heigit.ors.isorasters.IsoRasterMap;
import org.heigit.ors.isorasters.IsoRasterRequest;
import org.heigit.ors.isorasters.IsoRasterSearchParameters;
import org.heigit.ors.isorasters.QuadNode;
import org.heigit.ors.isorasters.QuadTree;
import org.heigit.ors.isorasters.Rasterizer;
import org.heigit.ors.isorasters.Utility;
import org.heigit.ors.routing.RouteSearchParameters;
import org.heigit.ors.routing.RoutingProfileManager;
import org.heigit.ors.routing.RoutingProfileType;
import org.heigit.ors.config.IsochronesServiceSettings;
import org.heigit.ors.util.DistanceUnitUtil;
import org.springframework.http.ResponseEntity;
import org.heigit.ors.api.requests.isochrones.IsochronesRequestEnums;
import org.heigit.ors.api.responses.isoraster.IsoRasterGeoJSONResponse;
import org.heigit.ors.api.responses.isoraster.IsoRasterGridResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import static org.heigit.ors.api.requests.isochrones.IsochronesRequestEnums.CalculationMethod.CONCAVE_BALLS;
import static org.heigit.ors.api.requests.isochrones.IsochronesRequestEnums.CalculationMethod.FASTISOCHRONE;

@ApiModel(value = "IsochronesRequest", description = "The JSON body request sent to the isochrones service which defines options and parameters regarding the isochrones to generate.")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class IsoRastersRequest extends APIRequest {
    public static final String PARAM_LOCATIONS = "locations";
    public static final String PARAM_LOCATION_TYPE = "location_type";
    public static final String PARAM_RANGE = "range";
    public static final String PARAM_RANGE_TYPE = "range_type";
    public static final String PARAM_RANGE_UNITS = "units";
    public static final String PARAM_INTERSECTIONS = "intersections";
    public static final String PARAM_TIME = "time";
    public static final String PARAM_PRECESSION = "precession";
    public static final String PARAM_CRS = "crs";
    public static final String PARAM_CONSUMER_TYPE = "consumer_type";


    @ApiModelProperty(name = PARAM_LOCATIONS, value = "The locations to use for the route as an array of `longitude/latitude` pairs",
            example = "[[8.681495,49.41461],[8.686507,49.41943]]",
            required = true)
    @JsonProperty(PARAM_LOCATIONS)
    private Double[][] locations = new Double[][]{};
    @JsonIgnore
    private boolean hasLocations = false;

    @ApiModelProperty(name = PARAM_LOCATION_TYPE, value = "`start` treats the location(s) as starting point, `destination` as goal. CUSTOM_KEYS:{'apiDefault':'start'}",
            example = "start")
    @JsonProperty(value = PARAM_LOCATION_TYPE)
    private IsochronesRequestEnums.LocationType locationType;
    @JsonIgnore
    private boolean hasLocationType = false;

    @ApiModelProperty(name = PARAM_RANGE, value = "Maximum range value of the analysis in **seconds** for time and **metres** for distance." +
            "Alternatively a comma separated list of specific range values. Ranges will be the same for all locations.",
            example = "[ 300, 200 ]",
            required = true)
    @JsonProperty(PARAM_RANGE)
    private List<Double> range;
    @JsonIgnore
    private boolean hasRange = false;

    @ApiModelProperty(name = PARAM_RANGE_TYPE,
            value = "Specifies the isochrones reachability type. CUSTOM_KEYS:{'apiDefault':'time'}", example = "time")
    @JsonProperty(value = PARAM_RANGE_TYPE, defaultValue = "time")
    private IsochronesRequestEnums.RangeType rangeType;
    @JsonIgnore
    private boolean hasRangeType = false;

    // unit only valid for range_type distance, will be ignored for range_time time
    @ApiModelProperty(name = PARAM_RANGE_UNITS,
            value = "Specifies the distance units only if `range_type` is set to distance.\n" +
                    "Default: m. " +
                    "CUSTOM_KEYS:{'apiDefault':'m','validWhen':{'ref':'range_type','value':'distance'}}",
            example = "m")
    @JsonProperty(value = PARAM_RANGE_UNITS)
    private APIEnums.Units rangeUnit;
    @JsonIgnore
    private boolean hasRangeUnits = false;

    @ApiModelProperty(hidden = true)
    private String responseType = "GRID";

    @ApiModelProperty(name = PARAM_INTERSECTIONS,
            value = "Specifies whether to return intersecting polygons. " +
                    "CUSTOM_KEYS:{'apiDefault':false}")
    @JsonProperty(value = PARAM_INTERSECTIONS)
    private boolean intersections;
    @JsonIgnore
    private boolean hasIntersections = false;

    @ApiModelProperty(name = PARAM_TIME, value = "Departure date and time provided in local time zone" +
            "CUSTOM_KEYS:{'validWhen':{'ref':'arrival','valueNot':['*']}}",
            example = "2020-01-31T12:45:00")
    @JsonProperty(PARAM_TIME)
    private LocalDateTime time;
    @JsonIgnore
    private boolean hasTime = false;

    @JsonIgnore
    private IsochroneMapCollection isoMaps;
    @JsonIgnore
    private IsoRasterRequest rasterRequest;

    @JsonProperty(PARAM_PRECESSION)
    private double precession;
    @JsonIgnore
    private boolean hasPrecession = false;

    @JsonProperty(PARAM_CRS)
    private String crs;
    @JsonIgnore
    private boolean hasCrs = false;

    @JsonProperty(value = PARAM_CONSUMER_TYPE)
    private IsoRastersRequestEnums.ConsumerType consumerType;
    @JsonIgnore
    private boolean hasConsumerType = false;

    @JsonIgnore
    Rasterizer rasterizer;

    @JsonCreator
    public IsoRastersRequest() {
    }

    static String[] convertAttributes(IsochronesRequestEnums.Attributes[] attributes) {
        return convertAPIEnumListToStrings(attributes);
    }

    protected static int convertToIsochronesProfileType(APIEnums.Profile profile) throws ParameterValueException {
        try {
            int profileFromString = RoutingProfileType.getFromString(profile.toString());
            if (profileFromString == 0) {
                throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, "profile");
            }
            return profileFromString;
        } catch (Exception e) {
            throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, "profile");
        }
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public boolean getIntersections() {
        return intersections;
    }

    public void setIntersections(Boolean intersections) {
        this.intersections = intersections;
        hasIntersections = true;
    }

    public boolean hasIntersections() {
        return hasIntersections;
    }

    public APIEnums.Units getRangeUnit() {
        return rangeUnit;
    }

    public void setRangeUnit(APIEnums.Units rangeUnit) {
        this.rangeUnit = rangeUnit;
        hasRangeUnits = true;
    }

    public boolean hasRangeUnits() {
        return hasRangeUnits;
    }

    public Double[][] getLocations() {
        return locations;
    }

    public void setLocations(Double[][] locations) {
        this.locations = locations;
        hasLocations = true;
    }

    public boolean hasLocations() {
        return hasLocations;
    }

    public IsochronesRequestEnums.LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(IsochronesRequestEnums.LocationType locationType) {
        this.locationType = locationType;
        hasLocationType = true;
    }

    public boolean hasLocationType() {
        return hasLocationType;
    }

    public List<Double> getRange() {
        return range;
    }

    public void setRange(List<Double> range) {
        this.range = range;
        hasRange = true;
    }

    public boolean hasRange() {
        return hasRange;
    }

    public IsochronesRequestEnums.RangeType getRangeType() {
        return rangeType;
    }

    public void setRangeType(IsochronesRequestEnums.RangeType rangeType) {
        this.rangeType = rangeType;
        hasRangeType = true;
    }

    public boolean hasRangeType() {
        return hasRangeType;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
        hasTime = true;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public void setPrecession(double precession) {
        this.precession = precession;
        hasPrecession = true;
    }

    public boolean hasPrecession() {
        return hasPrecession;
    }

    public void setCrs(String crs) {
        this.crs = crs;
        hasCrs = true;
    }

    public boolean hasCrs() {
        return hasCrs;
    }

    public void setConsumerType(IsoRastersRequestEnums.ConsumerType consumerType) {
        this.consumerType = consumerType;
        hasConsumerType = true;
    }

    public boolean hasConsumerType() {
        return hasConsumerType;
    }

    public Object generateIsoRasterFromRequest() throws Exception {
        this.rasterRequest = this.convertIsoRasterRequest();
        // request object is built, now check if ors config allows all settings
        List<TravellerInfo> travellers = this.rasterRequest.getTravellers();
    
        // TODO where should we put the validation code?
        validateAgainstConfig(this.rasterRequest, travellers);
    
        List<IsoRaster> rasters = new LinkedList<IsoRaster>();
        if (!travellers.isEmpty()) {
    
            for (int i = 0; i < travellers.size(); ++i) {
                IsoRasterSearchParameters searchParams = this.rasterRequest.getSearchParameters(i);
                IsoRaster raster = RoutingProfileManager.getInstance().buildIsoRaster(searchParams);
                rasters.add(raster);
            }
        }

        if (responseType == "GRID")
            return new IsoRasterGridResponse(rasters, this.crs, this.precession, true);
        else
            return new IsoRasterGeoJSONResponse(rasters, this.crs, this.precession, true);
    }

    String convertLocationType(IsochronesRequestEnums.LocationType locationType) throws ParameterValueException {
        IsochronesRequestEnums.LocationType value;

        switch (locationType) {
            case DESTINATION:
                value = IsochronesRequestEnums.LocationType.DESTINATION;
                break;
            case START:
                value = IsochronesRequestEnums.LocationType.START;
                break;
            default:
                throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_LOCATION_TYPE, locationType.toString());
        }

        return value.toString();
    }

    TravelRangeType convertRangeType(IsochronesRequestEnums.RangeType rangeType) throws ParameterValueException {
        TravelRangeType travelRangeType;

        switch (rangeType) {
            case DISTANCE:
                travelRangeType = TravelRangeType.DISTANCE;
                break;
            case TIME:
                travelRangeType = TravelRangeType.TIME;
                break;
            default:
                throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_RANGE_TYPE, rangeType.toString());
        }

        return travelRangeType;

    }

    String convertRangeUnit(APIEnums.Units unitsIn) throws ParameterValueException {

        DistanceUnit units;
        try {
            units = DistanceUnitUtil.getFromString(unitsIn.toString(), DistanceUnit.UNKNOWN);
            if (units == DistanceUnit.UNKNOWN)
                throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_RANGE_UNITS, unitsIn.toString());
        } catch (Exception e) {
            throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_RANGE_UNITS, unitsIn.toString());
        }
        return DistanceUnitUtil.toString(units);

    }

    Coordinate convertSingleCoordinate(Double[] coordinate) throws ParameterValueException {
        Coordinate realCoordinate;
        if (coordinate.length != 2) {
            throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_LOCATIONS);
        }
        try {
            realCoordinate = new Coordinate(coordinate[0], coordinate[1]);
        } catch (Exception e) {
            throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_LOCATIONS);
        }
        return realCoordinate;
    }

    IsoRasterRequest convertIsoRasterRequest() throws Exception {
        IsoRasterRequest convertedIsochroneRequest = new IsoRasterRequest();


        for (int i = 0; i < locations.length; i++) {
            Double[] location = locations[i];
            TravellerInfo travellerInfo = this.constructTravellerInfo(location);
            travellerInfo.setId(Integer.toString(i));
            try {
                convertedIsochroneRequest.addTraveller(travellerInfo);
            } catch (Exception ex) {
                throw new InternalServerException(IsochronesErrorCodes.UNKNOWN, IsoRastersRequest.PARAM_RANGE);
            }
        }
        if (this.hasId())
            convertedIsochroneRequest.setId(this.getId());
        if (this.hasRangeUnits())
            convertedIsochroneRequest.setUnits(convertRangeUnit(rangeUnit));
        if (this.hasIntersections())
            convertedIsochroneRequest.setIncludeIntersections(intersections);
        if (this.hasPrecession())
            convertedIsochroneRequest.setPrecession(this.precession);
        else
            convertedIsochroneRequest.setPrecession(0.01);
        if (this.hasCrs())
            convertedIsochroneRequest.setCrs(this.crs);
        else
            convertedIsochroneRequest.setCrs("4326");
        if (this.hasConsumerType())
            convertedIsochroneRequest.setConsumerType(this.consumerType.toString());
        else
            convertedIsochroneRequest.setConsumerType("node_based");
        return convertedIsochroneRequest;

    }

    TravellerInfo constructTravellerInfo(Double[] coordinate) throws Exception {
        TravellerInfo travellerInfo = new TravellerInfo();

        RouteSearchParameters routeSearchParameters = this.constructRouteSearchParameters();
        travellerInfo.setRouteSearchParameters(routeSearchParameters);
        if (this.hasRangeType())
            travellerInfo.setRangeType(convertRangeType(rangeType));
        if (this.hasLocationType())
            travellerInfo.setLocationType(convertLocationType(locationType));
        travellerInfo.setLocation(convertSingleCoordinate(coordinate));
        travellerInfo.getRanges();
        //range + interval
        if (range == null) {
            throw new ParameterValueException(IsochronesErrorCodes.MISSING_PARAMETER, IsoRastersRequest.PARAM_RANGE);
        }
        List<Double> rangeValues = range;
        setRangeAndIntervals(travellerInfo, rangeValues);
        return travellerInfo;
    }

    RouteSearchParameters constructRouteSearchParameters() throws Exception {
        RouteSearchParameters routeSearchParameters = new RouteSearchParameters();
        int profileType;
        try {
            profileType = convertToIsochronesProfileType(this.getProfile());
        } catch (Exception e) {
            throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_PROFILE);
        }

        if (profileType == RoutingProfileType.UNKNOWN)
            throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, IsoRastersRequest.PARAM_PROFILE);
        routeSearchParameters.setProfileType(profileType);

        if (this.hasTime()) {
            routeSearchParameters.setDeparture(this.getTime());
            routeSearchParameters.setArrival(this.getTime());
        }
        routeSearchParameters.setConsiderTurnRestrictions(false);
        return routeSearchParameters;
    }

    void validateAgainstConfig(IsoRasterRequest isochroneRequest, List<TravellerInfo> travellers) throws StatusCodeException {
        if (travellers.size() > IsochronesServiceSettings.getMaximumLocations())
            throw new ParameterOutOfRangeException(IsochronesErrorCodes.PARAMETER_VALUE_EXCEEDS_MAXIMUM, IsoRastersRequest.PARAM_LOCATIONS, Integer.toString(travellers.size()), Integer.toString(IsochronesServiceSettings.getMaximumLocations()));

        for (TravellerInfo traveller : travellers) {
            int maxAllowedRange = IsochronesServiceSettings.getMaximumRange(traveller.getRouteSearchParameters().getProfileType(), "fastisochrone", traveller.getRangeType());
            double maxRange = traveller.getMaximumRange();
            if (maxRange > maxAllowedRange)
                throw new ParameterOutOfRangeException(IsochronesErrorCodes.PARAMETER_VALUE_EXCEEDS_MAXIMUM, IsoRastersRequest.PARAM_RANGE, Double.toString(maxRange), Integer.toString(maxAllowedRange));
        }

    }

    void setRangeAndIntervals(TravellerInfo travellerInfo, List<Double> rangeValues) throws ParameterValueException, ParameterOutOfRangeException {
        double rangeValue = -1;
        if (rangeValues.size() == 1) {
            try {
                rangeValue = rangeValues.get(0);
                travellerInfo.setRanges(new double[]{rangeValue});
            } catch (NumberFormatException ex) {
                throw new ParameterValueException(IsochronesErrorCodes.INVALID_PARAMETER_VALUE, "range");
            }
        } else {
            double[] ranges = new double[rangeValues.size()];
            double maxRange = Double.MIN_VALUE;
            for (int i = 0; i < ranges.length; i++) {
                double dv = rangeValues.get(i);
                if (dv > maxRange)
                    maxRange = dv;
                ranges[i] = dv;
            }
            Arrays.sort(ranges);
            travellerInfo.setRanges(ranges);
        }
    }

    public IsoRasterRequest getRasterRequest() {
        return rasterRequest;
    }
}