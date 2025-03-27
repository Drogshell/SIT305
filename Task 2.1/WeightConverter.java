package com.trevin.unitconverterapp;

import java.util.HashMap;
import java.util.Map;

public class WeightConverter extends UnitConverter{
    private static final Map<String, Double> WEIGHT_FACTORS;
    static {
        // Assuming conversion factor relative to kilograms
        WEIGHT_FACTORS = new HashMap<>();
        WEIGHT_FACTORS.put("kg", 1.0);
        WEIGHT_FACTORS.put("g", 0.001);
        WEIGHT_FACTORS.put("lb", 0.45359237);
        WEIGHT_FACTORS.put("oz", 0.02834952);
        WEIGHT_FACTORS.put("t", 1000.0);
    }

    public WeightConverter(){
        super(WEIGHT_FACTORS);
    }
}
