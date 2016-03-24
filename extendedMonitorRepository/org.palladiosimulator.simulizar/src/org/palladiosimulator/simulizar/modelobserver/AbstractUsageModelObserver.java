package org.palladiosimulator.simulizar.modelobserver;

import java.util.Objects;

import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.simulizar.runtimestate.SimuLizarRuntimeState;

public abstract class AbstractUsageModelObserver extends AbstractModelObserver<UsageModel> {

    public AbstractUsageModelObserver() {
        super();
    }

    @Override
    public void initialize(final SimuLizarRuntimeState runtimeState) {
        super.initialize(runtimeState.getModelAccess().getGlobalPCMModel().getUsageModel(),
                Objects.requireNonNull(runtimeState));
    }
}
