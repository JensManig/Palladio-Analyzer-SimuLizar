package org.palladiosimulator.simulizar.interpreter;

import de.uka.ipd.sdq.simucomframework.Context;
/**
 * Factory interface for InterpreterDefaultContext
 * @author Jens Manig
 */
import de.uka.ipd.sdq.simucomframework.SimuComSimProcess;
/**
 * Factory interface for InterpreterDefaultContext
 * @author Jens Manig
 *
 */
public interface InterpreterDefaultContextFactory {

    public  InterpreterDefaultContext create(final Context context, final boolean copyStack);
    
    public  InterpreterDefaultContext create(final InterpreterDefaultContext context, final SimuComSimProcess thread); 
}