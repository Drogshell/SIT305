package com.trevin.unitconverterapp;

import java.util.HashMap;
import java.util.Map;

public class LengthConverter extends UnitConverter{
    private static final Map<String, Double> LENGTH_FACTORS;
    static {
        // Assuming conversion factor relative to meters
        LENGTH_FACTORS = new HashMap<>();
        LENGTH_FACTORS.put("m", 1.0);
        LENGTH_FACTORS.put("km", 1000.0);
        LENGTH_FACTORS.put("in", 0.0254);
        LENGTH_FACTORS.put("cm", 0.01);
        LENGTH_FACTORS.put("ft", 0.3048);
        LENGTH_FACTORS.put("yd", 0.9144);
        LENGTH_FACTORS.put("mi", 1609.344);
    }

    public LengthConverter(){
        super(LENGTH_FACTORS);
    }
}
