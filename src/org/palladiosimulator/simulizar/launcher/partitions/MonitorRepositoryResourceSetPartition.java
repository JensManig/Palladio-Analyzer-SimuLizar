package org.palladiosimulator.simulizar.launcher.partitions;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.simulizar.monitorrepository.MonitorRepository;
import org.palladiosimulator.simulizar.monitorrepository.MonitorrepositoryPackage;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;
import de.uka.ipd.sdq.workflow.pcm.blackboard.PCMResourceSetPartition;

/**
 * Special ResourceSetPartition for the MonitorRepository, with the functionality to resolve cross
 * references from the PRM to PCM.
 * 
 * @author Joachim Meyer
 * 
 */
public class MonitorRepositoryResourceSetPartition extends ResourceSetPartition {

    private static final Logger LOGGER = Logger.getLogger(MonitorRepositoryResourceSetPartition.class);
    private MonitorRepository monitorRepositoryModel;

    /**
     * Constructor
     * 
     * @param pcmResourceSetPartition
     *            the pcm resource set partition to resolve cross references from prm to pcm.
     */
    public MonitorRepositoryResourceSetPartition(final PCMResourceSetPartition pcmResourceSetPartition) {
        super();
        this.monitorRepositoryModel = null;
    }

    public MonitorRepository getMonitorRepositoryModel() {
        if (this.monitorRepositoryModel == null) {
            this.monitorRepositoryModel = loadMonitorRepositoryModel();
        }
        return this.monitorRepositoryModel;
    }

    /**
     * @return return the MonitorRepository element
     */
    private MonitorRepository loadMonitorRepositoryModel() {
        try {
            LOGGER.debug("Retrieving Monitor Repository Model from blackboard partition");
            List<MonitorRepository> result = getElement(MonitorrepositoryPackage.eINSTANCE.getMonitorRepository());
            return result.get(0);
        } catch (Exception e) {
            LOGGER.warn("No Monitor Repository found, no requests will be measured.");
            return null;
        }
    }

}