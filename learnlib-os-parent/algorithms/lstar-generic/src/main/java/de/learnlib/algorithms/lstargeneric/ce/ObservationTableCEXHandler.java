package de.learnlib.algorithms.lstargeneric.ce;

import java.util.List;

import de.learnlib.algorithms.lstargeneric.table.ObservationTable;
import de.learnlib.algorithms.lstargeneric.table.Row;
import de.learnlib.api.MembershipOracle;
import de.learnlib.api.Query;

public interface ObservationTableCEXHandler<I, O> {
	public List<List<Row<I>>> handleCounterexample(Query<I,O> ceQuery,
			ObservationTable<I,O> table, MembershipOracle<I,O> oracle);
	
	public boolean needsConsistencyCheck();
}