package org.palladiosimulator.simulizar.interpreter;

import java.util.Stack;

import javax.inject.Inject;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import de.uka.ipd.sdq.simucomframework.Context;
import de.uka.ipd.sdq.simucomframework.SimuComSimProcess;
import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.resources.AbstractSimulatedResourceContainer;
import de.uka.ipd.sdq.simucomframework.resources.IAssemblyAllocationLookup;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStack;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * Default context for the pcm interpreter.
 *
 * @author Joachim Meyer
 *
 */
public class InterpreterDefaultContext extends Context {

    /**
    *
    */
    private static final long serialVersionUID = -5027373777424401211L;

    private final Stack<AssemblyContext> assemblyContextStack = new Stack<AssemblyContext>();


    private IAssemblyAllocationLookup<AbstractSimulatedResourceContainer> assemblyAllocationLookup;

    @Inject
    public InterpreterDefaultContext(SimuComModel myModel,
            IAssemblyAllocationLookup<AbstractSimulatedResourceContainer> assemblyAllocationLookup) {
        super(myModel);
        this.stack = new SimulatedStack<Object>();
        this.assemblyAllocationLookup = assemblyAllocationLookup;
    }
    
    InterpreterDefaultContext(final Context context, final boolean copyStack) {
        super(context.getModel());
        this.assemblyAllocationLookup = context.getAssemblyAllocationLookup();
        this.setEvaluationMode(context.getEvaluationMode());
        this.setSimProcess(context.getThread());
        this.stack = new SimulatedStack<Object>();
        if (copyStack && context.getStack().size() > 0) {
            this.stack.pushStackFrame(context.getStack().currentStackFrame().copyFrame());
        } else {
            this.stack.pushStackFrame(new SimulatedStackframe<Object>());
        }
    }

    /**
     * Create interpreter default context from the given default context (model, sim process and
     * stack are set according to the given default context). The contents of the stack will be
     * copied.
     *
     * @param context
     *            the default context from which the new default context should be created.
     * @param thread
     */
    public InterpreterDefaultContext(final InterpreterDefaultContext context, final SimuComSimProcess thread) {
        this(context, true);
        this.setSimProcess(thread);
    }

    
    public Stack<AssemblyContext> getAssemblyContextStack() {
        return this.assemblyContextStack;
    }


    @Override
    public IAssemblyAllocationLookup<AbstractSimulatedResourceContainer> getAssemblyAllocationLookup() {
        return this.assemblyAllocationLookup;
    };
}
