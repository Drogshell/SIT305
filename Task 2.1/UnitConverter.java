package com.trevin.unitconverterapp;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class UnitConverter {

    // Stores conversion factors relative to the base unit
    private final Map<String, Double> conversionFactors;

    public UnitConverter(Map<String, Double> conversionFactors){
        this.conversionFactors = Collections.unmodifiableMap(conversionFactors);
    }

    public Double Convert(double value, String fromUnit, String toUnit){
        // If both units are the same then no need to convert
        if (fromUnit.equalsIgnoreCase(toUnit)){
            return value;
        }
        // Convert the input value to a base unit
        Double factorFrom = conversionFactors.get(fromUnit.toLowerCase());
        Double factorTo = conversionFactors.get(toUnit.toLowerCase());

        if (factorFrom == null || factorTo == null){
            throw new IllegalArgumentException("No conversion factor!");
        }
        double valueInBaseForm = value * factorFrom;
        return valueInBaseForm / factorTo;
    }

    public Set<String> getUnits(){
        return conversionFactors.keySet();
    }
}
