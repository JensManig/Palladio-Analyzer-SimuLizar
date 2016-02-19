package org.palladiosimulator.simulizar.metrics.aggregators;

import java.util.Collection;

public class GeometricMean implements IStatisticalCharacterization {

    @Override
    public double calculateStatisticalCharaterization(final Collection<Double> measurements) {
        Double sum = 1.0;
        for (final Double m : measurements) {
            sum *= m;
        }
        return Math.pow(sum, 1.0 / measurements.size());
    }

}
