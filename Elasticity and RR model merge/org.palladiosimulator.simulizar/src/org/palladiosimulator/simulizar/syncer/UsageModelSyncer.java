package org.palladiosimulator.simulizar.syncer;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.core.CorePackage;
import org.palladiosimulator.pcm.parameter.ParameterPackage;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;
import org.palladiosimulator.pcm.usagemodel.Workload;
import org.palladiosimulator.simulizar.runtimestate.SimuLizarRuntimeStateAbstract;

import de.uka.ipd.sdq.stoex.StoexPackage;

public class UsageModelSyncer extends AbstractSyncer<UsageModel>implements IModelSyncer {

    private static final Logger LOGGER = Logger.getLogger(UsageModelSyncer.class);

    public UsageModelSyncer(final SimuLizarRuntimeStateAbstract runtimeModel) {
        super(runtimeModel, runtimeModel.getModelAccess().getGlobalPCMModel().getUsageModel());
    }

    @Override
    public void initializeSyncer() {
    }

    @Override
    protected void synchronizeSimulationEntities(final Notification notification) {
        LOGGER.debug("Usage model changed... Resync needed");
        switch (notification.getEventType()) {
        case Notification.REMOVING_ADAPTER:
        case Notification.RESOLVE:
            break;
        case Notification.SET:
            if (UsagemodelPackage.eINSTANCE.getClosedWorkload().isInstance(notification.getNotifier())) {
                this.syncClosedWorkload(notification);
            } else if (CorePackage.eINSTANCE.getPCMRandomVariable().isInstance(notification.getNotifier())
                    && ((EObject) notification.getNotifier()).eContainer() instanceof OpenWorkload
                    && notification.getFeature() == StoexPackage.eINSTANCE.getRandomVariable_Specification()) {
                this.syncOpenWorkload(notification);
            } else if (CorePackage.eINSTANCE.getPCMRandomVariable().isInstance(notification.getNotifier())
                    && ParameterPackage.eINSTANCE.getVariableCharacterisation()
                            .isInstance(((EObject) notification.getNotifier()).eContainer())) {
                /*
                 * Nothing needs to happen in this case as the new variable char. is used for the
                 * next user
                 */
            } else {
                LOGGER.error(
                        "Usage model changed...But no resync strategy is known. Simulation results most likely are wrong.");
            }
            break;
        default:
            LOGGER.error(
                    "Usage model changed...But no resync strategy is known. Simulation results most likely are wrong.");
            break;
        }
    }

    /**
     * @param notification
     */
    private void syncClosedWorkload(final Notification notification) {
        final ClosedWorkload workload = (ClosedWorkload) notification.getNotifier();
        this.closedWorkloadPopulationChanged(workload, notification.getNewIntValue());
    }

    /**
     * @param notification
     */
    private void syncOpenWorkload(final Notification notification) {
        final OpenWorkload workload = (OpenWorkload) ((EObject) notification.getNotifier()).eContainer();
        this.openWorkloadInterarrivalChange(workload, notification.getNewStringValue());
    }

    private void openWorkloadInterarrivalChange(final Workload workload, final String newInterarrivalTime) {
        LOGGER.debug("Setting open workload interarrival time to " + newInterarrivalTime);
        this.runtimeModel.getUsageModels().getOpenWorkloadDriver((OpenWorkload) workload)
                .setInterarrivalTime(newInterarrivalTime);
    }

    private void closedWorkloadPopulationChanged(final Workload workload, final int newPopulation) {
        LOGGER.debug("Setting closed workload population to " + newPopulation);
        this.runtimeModel.getUsageModels().getClosedWorkloadDriver((ClosedWorkload) workload)
                .setPopulation(newPopulation);
    }
}