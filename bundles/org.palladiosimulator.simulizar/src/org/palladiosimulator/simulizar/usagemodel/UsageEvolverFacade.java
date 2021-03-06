package org.palladiosimulator.simulizar.usagemodel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.palladiosimulator.simulizar.utils.PCMPartitionManager;
import org.scaledl.usageevolution.Usage;
import org.scaledl.usageevolution.UsageEvolution;
import org.scaledl.usageevolution.UsageevolutionPackage;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;

public class UsageEvolverFacade {

    protected Map<Usage, PeriodicallyTriggeredUsageEvolver> usageEvolvers;
    
    /** Runtime state of the simulation. Required to start evolution(s). */
    private final PCMPartitionManager pcmManager;
    private final SimuComModel model;
    
    private final LoopingUsageEvolverFactory loopingUsageEvolerFactory;
    private final StretchedUsageEvolverFactory stretchedUsageEvolverFactory;

    @Inject
    public UsageEvolverFacade(final PCMPartitionManager pcmManager, final SimuComModel model, 
    		final LoopingUsageEvolverFactory loopingUsageEvolerFactory, final StretchedUsageEvolverFactory stretchedUsageEvolverFactory) {
       this.model = model;
       this.pcmManager = pcmManager;
       this.usageEvolvers = new HashMap<Usage, PeriodicallyTriggeredUsageEvolver>();
       this.loopingUsageEvolerFactory = loopingUsageEvolerFactory;
       this.stretchedUsageEvolverFactory = stretchedUsageEvolverFactory;
    }

    public void start() {
        // TODO: add check on duration of evolutions for work parameters.
        // For now, assume that the duration of these are the same as for the load evolution
        
    	UsageEvolution ueModel = this.pcmManager.findModel(UsageevolutionPackage.eINSTANCE.getUsageEvolution());
        ueModel.getUsages().forEach(this::startUsageEvolution);
    }
    
    public void startUsageEvolution(Usage usage) {
        this.usageEvolvers.put(usage, createUsageEvolver(usage));
    }
    
    public void stopUsageEvolution(Usage usage) {
        this.usageEvolvers.remove(usage).stop();
    }
    
    protected PeriodicallyTriggeredUsageEvolver createUsageEvolver(Usage usage) {
        double timePerStep = 1d;
        if (usage.getEvolutionStepWidth() != 0d) {
            timePerStep = usage.getEvolutionStepWidth();
        }
        
        double simulationTimeOffset = 0d; 
        if (this.model.getSimulationControl().isRunning()) {
            simulationTimeOffset = this.model.getSimulationControl().getCurrentSimulationTime();
        }
        
        if (usage.isRepeatingPattern()) {
            return this.loopingUsageEvolerFactory.create(0d, timePerStep, usage.getScenario(), simulationTimeOffset);
        } else {
            // TODO remove this line once 'legacy' support is no longer needed.
            timePerStep = this.model.getConfiguration().getSimuTime()
                    / (usage.getLoadEvolution().getFinalDuration() + 1);
            return this.stretchedUsageEvolverFactory.create(0d, timePerStep, usage.getScenario());
        }
    }
}
