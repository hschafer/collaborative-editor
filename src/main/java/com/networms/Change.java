package com.networms;

import java.util.List;

public interface Change {
	public void applyOT(List<Change> pendingChanges);
}