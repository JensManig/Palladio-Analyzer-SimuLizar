package org.palladiosimulator.simulizar.runconfig;

import org.palladiosimulator.simframework.SimulatedResourceContainerRegistry;
import org.palladiosimulator.simulizar.interpreter.ComposedStructureInnerSwitchFactory;
import org.palladiosimulator.simulizar.interpreter.EventNotificationHelper;
import org.palladiosimulator.simulizar.interpreter.ExplicitDispatchComposedSwitchFactory;
import org.palladiosimulator.simulizar.interpreter.ExplicitDispatchComposedSwitchFactoryImpl;
import org.palladiosimulator.simulizar.interpreter.InterpreterDefaultContextFactory;
import org.palladiosimulator.simulizar.interpreter.InterpreterDefaultContext;
import org.palladiosimulator.simulizar.interpreter.InterpreterDefaultContextFactoryImpl;
import org.palladiosimulator.simulizar.interpreter.RDSeffSwitchFactory;
import org.palladiosimulator.simulizar.interpreter.RepositoryComponentSwitchFactory;
import org.palladiosimulator.simulizar.interpreter.UsageScenarioSwitchFactory;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEventFactory;
import org.palladiosimulator.simulizar.interpreter.listener.ModelElementPassedEventFactoryImpl;
import org.palladiosimulator.simulizar.modelobserver.AllocationLookupSyncer;
import org.palladiosimulator.simulizar.reconfiguration.ReconfigurationProcess;
import org.palladiosimulator.simulizar.reconfiguration.Reconfigurator;
import org.palladiosimulator.simulizar.runtimestate.AbstractSimuLizarRuntimeState;
import org.palladiosimulator.simulizar.runtimestate.ComponentInstanceRegistry;
import org.palladiosimulator.simulizar.runtimestate.FQComponentIDFactory;
import org.palladiosimulator.simulizar.runtimestate.SimuComModelFactory;
import org.palladiosimulator.simulizar.runtimestate.SimulatedBasicComponentInstanceFactory;
import org.palladiosimulator.simulizar.runtimestate.SimulatedCompositeComponentInstanceFactory;
import org.palladiosimulator.simulizar.runtimestate.SimulationCancelationDelegate;
import org.palladiosimulator.simulizar.usagemodel.ClosedWorkloadDriverFactory;
import org.palladiosimulator.simulizar.usagemodel.LoopingUsageEvolverFactory;
import org.palladiosimulator.simulizar.usagemodel.OpenWorkloadDriverFactory;
import org.palladiosimulator.simulizar.usagemodel.ScenarioRunnerFactory;
import org.palladiosimulator.simulizar.usagemodel.SimulatedUsageModels;
import org.palladiosimulator.simulizar.usagemodel.StretchedUsageEvolverFactory;
import org.palladiosimulator.simulizar.usagemodel.UsageEvolverFacade;
import org.palladiosimulator.simulizar.usagemodel.WorkloadDriverFactory;
import org.palladiosimulator.simulizar.utils.DefaultTransitionDeterminerFactory;
import org.palladiosimulator.simulizar.utils.PCMPartitionManager;
import org.palladiosimulator.simulizar.utils.TransitionDeterminerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import de.uka.ipd.sdq.identifier.Identifier;
import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.resources.AbstractSimulatedResourceContainer;
import de.uka.ipd.sdq.simucomframework.resources.IAssemblyAllocationLookup;
import de.uka.ipd.sdq.simucomframework.resources.ISimulatedModelEntityAccess;
import de.uka.ipd.sdq.simucomframework.usage.ClosedWorkload;
import de.uka.ipd.sdq.simucomframework.usage.OpenWorkload;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
/**
 * Module for dependency injection for RuntimeState
 * @author Jens Manig
 *
 */
public class SimuLizarModule extends AbstractModule{
	private final SimuLizarWorkflowConfiguration configuration;
	private final SimulationCancelationDelegate cancelationDelegate;
	private final MDSDBlackboard blackboard;

	
	public SimuLizarModule(final SimuLizarWorkflowConfiguration configuration,
            final MDSDBlackboard blackboard, final SimulationCancelationDelegate cancelationDelegate) {
		this.configuration = configuration;
		this.cancelationDelegate = cancelationDelegate;
		this.blackboard = blackboard;
	}
	
	@Provides @Singleton
	protected SimuComModel provideSimuComModel(SimuLizarWorkflowConfiguration configuration) {
		return SimuComModelFactory.createSimuComModel(configuration);
	}
	
	@Provides
	protected SimulatedResourceContainerRegistry provideSimulatedResourceContainerRegistry(SimuComModel model){
		return model.getResourceRegistry();
	}
	
	@Provides
	protected ISimulatedModelEntityAccess<Identifier, AbstractSimulatedResourceContainer> providesResourceContainerLookup(SimulatedResourceContainerRegistry registry){
		return registry::getResourceContainer;
	}
	
	@Provides @Singleton
	protected  IAssemblyAllocationLookup<AbstractSimulatedResourceContainer> provideAllocationLookupSyner(SimulatedResourceContainerRegistry registry) {
		return new AllocationLookupSyncer(registry::getResourceContainer);
	}
	@Provides
	protected Reconfigurator provideReconfigurator(AbstractSimuLizarRuntimeState state) {
		return state.getReconfigurator();
	}
	
	@Provides ReconfigurationProcess provideReconfigurationProcess(Reconfigurator config) {
		return config.getReconfigurationProcess();	
	}
	
	@Override
	protected void configure() {
		bind(SimuLizarWorkflowConfiguration.class).toInstance(this.configuration);
		bind(SimulationCancelationDelegate.class).toInstance(this.cancelationDelegate);
		bind(MDSDBlackboard.class).toInstance(this.blackboard);
		bind(PCMPartitionManager.class).in(Singleton.class);
		bind(InterpreterDefaultContext.class).annotatedWith(Names.named("RootContext")).to(InterpreterDefaultContext.class).in(Singleton.class);
		bind(EventNotificationHelper.class).in(Singleton.class);
		bind(ComponentInstanceRegistry.class).in(Singleton.class);
		bind(InterpreterDefaultContextFactory.class).to(InterpreterDefaultContextFactoryImpl.class);
		bind(UsageEvolverFacade.class).in(Singleton.class);
		bind(SimulatedUsageModels.class).in(Singleton.class);
		bind(IAssemblyAllocationLookup.class).to(AllocationLookupSyncer.class).in(Singleton.class);
		bind(TransitionDeterminerFactory.class).to(DefaultTransitionDeterminerFactory.class);
		bind(SimulatedStackframe.class);
		bind(ExplicitDispatchComposedSwitchFactory.class).to(ExplicitDispatchComposedSwitchFactoryImpl.class);
		bind(ModelElementPassedEventFactory.class).to(ModelElementPassedEventFactoryImpl.class);
		bind(new TypeLiteral<WorkloadDriverFactory<OpenWorkload>>() {}).to(OpenWorkloadDriverFactory.class);
		bind(new TypeLiteral<WorkloadDriverFactory<ClosedWorkload>>() {}).to(ClosedWorkloadDriverFactory.class);

		
		install(new FactoryModuleBuilder()
			     .build(RepositoryComponentSwitchFactory.class));
		install(new FactoryModuleBuilder()
			     .build(RDSeffSwitchFactory.class));
		install(new FactoryModuleBuilder()
				.build(UsageScenarioSwitchFactory.class));
		install(new FactoryModuleBuilder()
				.build(ComposedStructureInnerSwitchFactory.class));
		install(new FactoryModuleBuilder()
				.build(SimulatedBasicComponentInstanceFactory.class));
		install(new FactoryModuleBuilder()
				.build(SimulatedCompositeComponentInstanceFactory.class));
		install(new FactoryModuleBuilder()
				.build(FQComponentIDFactory.class));
		install(new FactoryModuleBuilder()
				.build(LoopingUsageEvolverFactory.class));
		install(new FactoryModuleBuilder()
				.build(StretchedUsageEvolverFactory.class));
		install(new FactoryModuleBuilder()
				.build(ScenarioRunnerFactory.class));
		
	}
}
