package org.heigit.ors.api.requests.isoraster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.heigit.ors.exceptions.ParameterValueException;

import static org.heigit.ors.isochrones.IsochronesErrorCodes.INVALID_PARAMETER_VALUE;

public class IsoRastersRequestEnums {

    public enum ConsumerType {
        NODE_BASED("node_based"),
        EDGE_BASED("edge_based");

        private final String value;

        ConsumerType(String value) {
            this.value = value;
        }

        @JsonCreator
        public static ConsumerType forValue(String v) throws ParameterValueException {
            for (ConsumerType enumItem : ConsumerType.values()) {
                if (enumItem.value.equals(v))
                    return enumItem;
            }
            throw new ParameterValueException(INVALID_PARAMETER_VALUE, "consumer_type", v);
        }

        @Override
        @JsonValue
        public String toString() {
            return value;
        }
    }

}
