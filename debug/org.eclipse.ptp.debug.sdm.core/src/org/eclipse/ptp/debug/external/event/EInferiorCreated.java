package org.eclipse.ptp.debug.external.event;

import java.util.Hashtable;

public class EInferiorCreated extends DebugEvent {
	public EInferiorCreated(Hashtable s, int[] p) {
		super("inferiorExit", s, p);
	}
}
