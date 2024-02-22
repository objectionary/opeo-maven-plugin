package org.eolang.opeo.decompilation.handlers;

import org.eolang.opeo.decompilation.DecompilerState;
import org.eolang.opeo.decompilation.InstructionHandler;

/**
 * Invoke Interface instruction handler.
 * <p>
 *   Other bytes: 4: indexbyte1, indexbyte2, count, 0
 * </p>
 * <p>
 *   Stack: objectref, [arg1, arg2, ...] → result
 * </p>
 * <p>
 *   Invokes an interface method on object objectref and puts the result on the stack (might be void);
 *   the interface method is identified by method reference index in constant
 *   pool (indexbyte1 << 8 | indexbyte2)
 * </p>
 * @since 0.2
 */
public final class InvokeInterfaceHandler implements InstructionHandler {
    @Override
    public void handle(final DecompilerState state) {

    }
}
