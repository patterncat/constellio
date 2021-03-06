package com.constellio.data.dao.managers.config.values;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import com.constellio.data.dao.managers.config.ConfigManagerRuntimeException;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLConfiguration implements Serializable {

	private final String version;

	private final String hash;

	private final String xml;

	private transient Document document;

	public XMLConfiguration(String version, String hash, Document document) {
		super();
		this.version = version == null || version.equals("-1") ? hash : version;
		this.hash = hash;
		this.document = document;
		this.xml = document == null ? null : toXml(document);
	}

	private String toXml(Document document) {
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		return xmlOutput.outputString(document);
	}

	private Document toDocument(String xml) {
		SAXBuilder builder = new SAXBuilder();
		Reader reader = new StringReader(xml);
		try {
			return builder.build(reader);
		} catch (JDOMException e) {
			throw new ConfigManagerRuntimeException("JDOM2 Exception", e);
		} catch (IOException e) {
			throw new ConfigManagerRuntimeException.CannotCompleteOperation("build Document JDOM2 from file", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public String getHash() {
		return version;
	}

	public String getRealHash() {
		return hash;
	}

	public Document getDocument() {
		return document.clone();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
