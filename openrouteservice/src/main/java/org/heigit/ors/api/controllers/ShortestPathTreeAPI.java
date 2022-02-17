package org.heigit.ors.api.controllers;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.heigit.ors.api.errors.CommonResponseEntityExceptionHandler;
import org.heigit.ors.api.requests.common.APIEnums;
import org.heigit.ors.api.requests.shortestpathtree.ShortestPathTreeRequest;
import org.heigit.ors.api.responses.isochrones.geojson.GeoJSONIsochronesResponse;
import org.heigit.ors.exceptions.*;
import org.heigit.ors.isochrones.IsochroneMapCollection;
import org.heigit.ors.isochrones.IsochronesErrorCodes;
import org.heigit.ors.shortestpathtree.ShortestPathTreeMap;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/v2/shortestpathtree")
public class ShortestPathTreeAPI {
    static final CommonResponseEntityExceptionHandler errorHandler = new CommonResponseEntityExceptionHandler(IsochronesErrorCodes.BASE);

    // generic catch methods - when extra info is provided in the url, the other methods are accessed.
    @GetMapping
    public void getGetMapping() throws MissingParameterException {
        throw new MissingParameterException(IsochronesErrorCodes.MISSING_PARAMETER, "profile");
    }

    @PostMapping
    public String getPostMapping(@RequestBody ShortestPathTreeRequest request) throws MissingParameterException {
        throw new MissingParameterException(IsochronesErrorCodes.MISSING_PARAMETER, "profile");
    }

    // Matches any response type that has not been defined
    @PostMapping(value="/{profile}/*")
    public void getInvalidResponseType() throws StatusCodeException {
        throw new StatusCodeException(HttpServletResponse.SC_NOT_ACCEPTABLE, IsochronesErrorCodes.UNSUPPORTED_EXPORT_FORMAT, "This response format is not supported");
    }

    // Functional request methods
    @PostMapping(value = "/{profile}", produces = "application/geo+json;charset=UTF-8")
    public ShortestPathTreeMap getDefaultIsochrones(
            @PathVariable APIEnums.Profile profile,
            @RequestBody ShortestPathTreeRequest request) throws Exception {
        return getGeoJsonIsochrones(profile, request);
    }

    @PostMapping(value = "/{profile}/geojson", produces = "application/geo+json;charset=UTF-8")
    public ShortestPathTreeMap getGeoJsonIsochrones(
            @PathVariable APIEnums.Profile profile,
            @RequestBody ShortestPathTreeRequest request) throws Exception {
        request.setProfile(profile);
        request.setResponseType(APIEnums.RouteResponseType.GEOJSON);

        return request.generateMultiGraphFromRequest();
        //IsochroneMapCollection isoMaps = request.getIsoMaps();
        //return profile;
    }
}
