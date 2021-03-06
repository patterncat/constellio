package com.constellio.model.services.search.query.logical.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.constellio.model.services.search.query.logical.LogicalOperator;
import com.constellio.model.services.search.query.logical.LogicalSearchValueCondition;

public class CompositeLogicalSearchCondition extends LogicalSearchCondition {

	final LogicalOperator logicalOperator;

	final List<LogicalSearchCondition> nestedSearchConditions;

	public CompositeLogicalSearchCondition(DataStoreFilters filters, LogicalOperator logicalOperator,
			List<LogicalSearchCondition> nestedSearchConditions) {
		super(filters);
		this.logicalOperator = logicalOperator;
		this.nestedSearchConditions = Collections.unmodifiableList(nestedSearchConditions);
	}

	public LogicalOperator getLogicalOperator() {
		return logicalOperator;
	}

	public List<LogicalSearchCondition> getNestedSearchConditions() {
		return nestedSearchConditions;
	}

	@Override
	public String toString() {
		return filters + ":" + logicalOperator.name() + nestedSearchConditions;
	}

	@Override
	public LogicalSearchCondition withFilters(DataStoreFilters filters) {
		List<LogicalSearchCondition> searchConditionsWithSchema = new ArrayList<>();
		for (LogicalSearchCondition condition : nestedSearchConditions) {
			searchConditionsWithSchema.add(condition.withFilters(filters));
		}
		return new CompositeLogicalSearchCondition(filters, logicalOperator, searchConditionsWithSchema);
	}

	@Override
	public LogicalSearchCondition withOrValueConditions(List<LogicalSearchValueCondition> conditions) {
		throw new UnsupportedOperationException("Cannot add value conditions on a compocollection");
	}

	@Override
	public LogicalSearchCondition withAndValueConditions(List<LogicalSearchValueCondition> conditions) {
		throw new UnsupportedOperationException("Cannot add value conditions on a compocollection");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public void validate() {
	}

	@Override
	public String getSolrQuery(SolrQueryBuilderParams params) {

		if (nestedSearchConditions.isEmpty()) {
			throw new IllegalStateException("No conditions");
		}

		String query = "(";

		for (int i = 0; i < nestedSearchConditions.size() - 1; i++) {
			query += " " + nestedSearchConditions.get(i).getSolrQuery(params) + " " + logicalOperator;
		}

		query += " " + nestedSearchConditions.get(nestedSearchConditions.size() - 1).getSolrQuery(params);

		query += " )";

		return query;
	}

}
