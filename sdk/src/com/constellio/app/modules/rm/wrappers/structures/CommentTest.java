package com.constellio.app.modules.rm.wrappers.structures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.data.utils.TimeProvider;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.sdk.tests.ConstellioTest;

public class CommentTest extends ConstellioTest {

	@Mock User bob;
	CommentFactory factory;

	LocalDateTime nowDateTime = TimeProvider.getLocalDateTime();

	@Before
	public void setUp()
			throws Exception {
		factory = spy(new CommentFactory());

		when(bob.getId()).thenReturn("bobId");
		when(bob.getUsername()).thenReturn("bob");

	}

	@Test
	public void whenSetAttributeValueThenBecomeDirty() {
		Comment comment = new Comment();
		assertThat(comment.isDirty()).isFalse();

		comment = new Comment();
		comment.setUser(bob);
		assertThat(comment.isDirty()).isTrue();

		comment = new Comment();
		comment.setDateTime(nowDateTime);
		assertThat(comment.isDirty()).isTrue();

		comment = new Comment();
		comment.setMessage("Message");
		assertThat(comment.isDirty()).isTrue();
	}

	@Test
	public void whenConvertingStructureWithAllValuesThenRemainsEqual()
			throws Exception {

		Comment comment = new Comment();
		comment.setUser(bob);
		comment.setDateTime(nowDateTime);

		comment.setMessage("Message");

		String stringValue = factory.toString(comment);
		Comment builtComment = (Comment) factory.build(stringValue);
		String stringValue2 = factory.toString(builtComment);

		assertThat(builtComment).isEqualTo(comment);
		assertThat(stringValue2).isEqualTo(stringValue);
		assertThat(builtComment.isDirty()).isFalse();

	}

	@Test
	public void whenConvertingStructureWithNullValuesThenRemainsEqual()
			throws Exception {

		Comment comment = new Comment();
		comment.setUser(null);
		comment.setDateTime(null);
		comment.setMessage(null);

		String stringValue = factory.toString(comment);
		Comment builtComment = (Comment) factory.build(stringValue);
		String stringValue2 = factory.toString(builtComment);

		assertThat(builtComment).isEqualTo(comment);
		assertThat(stringValue2).isEqualTo(stringValue);
		assertThat(builtComment.isDirty()).isFalse();
	}

	@Test
	public void whenConvertingStructureWithoutSetValuesThenRemainsEqual()
			throws Exception {

		Comment comment = new Comment();

		String stringValue = factory.toString(comment);
		Comment builtComment = (Comment) factory.build(stringValue);
		String stringValue2 = factory.toString(builtComment);

		assertThat(builtComment).isEqualTo(comment);
		assertThat(stringValue2).isEqualTo(stringValue);
		assertThat(builtComment.isDirty()).isFalse();
	}
}
