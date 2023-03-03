package org.heigit.ors.api.controllers;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.heigit.ors.api.errors.CommonResponseEntityExceptionHandler;
import org.heigit.ors.api.requests.common.APIEnums;
import org.heigit.ors.api.requests.isoraster.IsoRastersRequest;
import org.heigit.ors.api.responses.isochrones.geojson.GeoJSONIsochronesResponse;
import org.heigit.ors.exceptions.*;
import org.heigit.ors.isochrones.IsochroneMapCollection;
import org.heigit.ors.isochrones.IsochronesErrorCodes;
import org.heigit.ors.isorasters.IsoRasterMap;
import org.heigit.ors.api.responses.isoraster.IsoRasterGridResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/v2/isoraster")
public class IsoRastersAPI {
    static final CommonResponseEntityExceptionHandler errorHandler = new CommonResponseEntityExceptionHandler(IsochronesErrorCodes.BASE);

    // generic catch methods - when extra info is provided in the url, the other methods are accessed.
    @GetMapping
    public void getGetMapping() throws MissingParameterException {
        throw new MissingParameterException(IsochronesErrorCodes.MISSING_PARAMETER, "profile");
    }

    @PostMapping
    public String getPostMapping(@RequestBody IsoRastersRequest request) throws MissingParameterException {
        throw new MissingParameterException(IsochronesErrorCodes.MISSING_PARAMETER, "profile");
    }

    // Matches any response type that has not been defined
    @PostMapping(value="/{profile}/*")
    public void getInvalidResponseType() throws StatusCodeException {
        throw new StatusCodeException(HttpServletResponse.SC_NOT_ACCEPTABLE, IsochronesErrorCodes.UNSUPPORTED_EXPORT_FORMAT, "This response format is not supported");
    }

    // Functional request methods
    @PostMapping(value = "/{profile}", produces = "application/geo+json;charset=UTF-8")
    public Object getDefaultIsoRaster(
            @PathVariable APIEnums.Profile profile,
            @RequestBody IsoRastersRequest request) throws Exception {
        return getGridIsoRaster(profile, request);
    }

    @PostMapping(value = "/{profile}/geojson", produces = "application/geo+json;charset=UTF-8")
    public Object getGeoJSONIsoRaster(
            @PathVariable APIEnums.Profile profile,
            @RequestBody IsoRastersRequest request) throws Exception {
        request.setProfile(profile);
        request.setResponseType("GEOJSON");

        return request.generateIsoRasterFromRequest();
        //IsochroneMapCollection isoMaps = request.getIsoMaps();
        //return profile;
    }

    @PostMapping(value = "/{profile}/raster", produces = "application/geo+json;charset=UTF-8")
    public Object getGridIsoRaster(
            @PathVariable APIEnums.Profile profile,
            @RequestBody IsoRastersRequest request) throws Exception {
        request.setProfile(profile);
        request.setResponseType("GRID");

        return request.generateIsoRasterFromRequest();
    }
}
