package com.constellio.app.modules.rm.wrappers;

import java.util.List;

import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.CopyRetentionRuleInRule;
import com.constellio.app.modules.rm.wrappers.structures.Comment;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.RecordWrapper;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;

public class Category extends RecordWrapper {

	public static final String SCHEMA_TYPE = "category";

	public static final String DEFAULT_SCHEMA = SCHEMA_TYPE + "_default";

	public static final String CODE = "code";
	public static final String DESCRIPTION = "description";

	public static final String PARENT = "parent";

	public static final String KEYWORDS = "keywords";

	public static final String DEACTIVATE = "deactivate";

	public static final String COMMENTS = "comments";
	public static final String DEFAULT_COPY_RULE_ID = "defaultCopyRuleIdentifier";
	public static final String DEFAULT_COPY_RULE = "defaultCopyRule";
	public static final String DEFAULT_RETENTION_RULE = "defaultRetentionRule";
	public static final String RETENTION_RULES = "retentionRules";
	public static final String LEVEL = "level";
	public static final String COPY_RETENTION_RULES_ON_DOCUMENT_TYPES = "copyRetentionRulesOnDocumentTypes";

	public Category(Record record,
			MetadataSchemaTypes types) {
		super(record, types, SCHEMA_TYPE);
	}

	public Category setTitle(String title) {
		super.setTitle(title);
		return this;
	}

	public String getParent() {
		return get(PARENT);
	}

	public Category setParent(Category category) {
		set(PARENT, category);
		return this;
	}

	public Category setParent(Record category) {
		set(PARENT, category);
		return this;
	}

	public Category setParent(String category) {
		set(PARENT, category);
		return this;
	}

	public String getDefaultCopyRuleId() {
		return get(DEFAULT_COPY_RULE_ID);
	}

	public Category setDefaultCopyRuleId(CopyRetentionRule copy) {
		set(DEFAULT_COPY_RULE_ID, copy == null ? null : copy.getId());
		return this;
	}

	public Category setDefaultCopyRuleId(String id) {
		set(DEFAULT_COPY_RULE_ID, id);
		return this;
	}

	public String getDefaultRetentionRule() {
		return get(DEFAULT_RETENTION_RULE);
	}

	public CopyRetentionRule getDefaultCopyRule() {
		return get(DEFAULT_COPY_RULE);
	}

	public String getCode() {
		return get(CODE);
	}

	public Category setCode(String code) {
		set(CODE, code);
		return this;
	}

	public String getDescription() {
		return get(DESCRIPTION);
	}

	public Category setDescription(String description) {
		set(DESCRIPTION, description);
		return this;
	}

	public List<String> getKeywords() {
		return getList(KEYWORDS);
	}

	public Category setKeywords(List<String> keywords) {
		set(KEYWORDS, keywords);
		return this;
	}

	public List<Comment> getComments() {
		return getList(COMMENTS);
	}

	public Category setComments(List<Comment> comments) {
		set(COMMENTS, comments);
		return this;
	}

	public Category setRetentionRules(List<?> retentionRules) {
		set(RETENTION_RULES, retentionRules);
		return this;
	}

	public List<String> getRententionRules() {
		return getList(RETENTION_RULES);
	}

	public boolean isLinkable() {
		return getBooleanWithDefaultValue(Schemas.LINKABLE.getLocalCode(), true);
	}

	public int getLevel() {
		return getInteger(LEVEL);
	}

	public List<CopyRetentionRuleInRule> getCopyRetentionRulesOnDocumentTypes() {
		return getList(COPY_RETENTION_RULES_ON_DOCUMENT_TYPES);
	}

	public static Category wrap(Record record, MetadataSchemaTypes types) {
		return record == null ? null : new Category(record, types);
	}
}
