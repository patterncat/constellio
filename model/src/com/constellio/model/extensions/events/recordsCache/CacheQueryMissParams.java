package com.constellio.model.extensions.events.recordsCache;

import com.constellio.model.services.search.query.logical.LogicalSearchQuerySignature;

public class CacheQueryMissParams {

	LogicalSearchQuerySignature signature;

	long duration;

	public CacheQueryMissParams(LogicalSearchQuerySignature signature, long duration) {
		this.signature = signature;
		this.duration = duration;
	}

	public LogicalSearchQuerySignature getSignature() {
		return signature;
	}

	public long getDuration() {
		return duration;
	}
}
