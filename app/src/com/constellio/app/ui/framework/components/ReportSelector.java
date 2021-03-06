package com.constellio.app.ui.framework.components;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.List;

import com.constellio.app.modules.rm.reports.model.search.UnsupportedReportException;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.framework.buttons.WindowButton.WindowConfiguration;
import com.constellio.app.ui.framework.reports.NewReportWriterFactory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ReportSelector extends HorizontalLayout {
	
	private final NewReportPresenter presenter;
	private AbstractSelect selector;
	private final Button button;

	public ReportSelector(NewReportPresenter presenter) {
		setSpacing(true);
		this.presenter = presenter;
		button = buildActivationButton();
		try {
			selector = buildSelector();
			addComponents(selector, button);
		} catch (UnsupportedReportException e) {
			setVisible(false);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		selector.setEnabled(enabled);
		button.setEnabled(enabled && selector.getValue() != null);
	}

	public String getSelectedReport() {
		return (String) selector.getValue();
	}

	private AbstractSelect buildSelector() {
		ComboBox comboBox = new ComboBox();
		List<String> supportedReports = presenter.getSupportedReports();
		if (supportedReports.isEmpty()) {
			setVisible(false);
		} else {
			for (String report : supportedReports) {
				comboBox.addItem(report);
				comboBox.setItemCaption(report, $(report));
			}
			comboBox.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
			comboBox.setNullSelectionAllowed(false);
			comboBox.setValue(supportedReports.get(0));
			comboBox.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					button.setEnabled(true);
				}
			});
			comboBox.addValidator(new Validator() {
				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value == null) {
						throw new InvalidValueException($("ReportTabButton.invalidReportType"));
					}
				}
			});
		}
		return comboBox;
	}

	private Button buildActivationButton() {
		return new WindowButton($("ReportSelector.go"), null, WindowConfiguration.modalDialog("75%", "75%")) {
			@Override
			protected Component buildWindowContent() {
				NewReportWriterFactory factory = presenter.getReport(getSelectedReport());
				if (factory == null) {
					return new Label($("ReportViewer.noReportFactoryAvailable"));
				} else {
					return new ReportViewer(factory.getReportBuilder(presenter.getReportParameters(getSelectedReport())),
							factory.getFilename(presenter.getReportParameters(getSelectedReport())));
				}
			}

			@Override
			protected String getWindowCaption() {
				return $(getSelectedReport());
			}
		};
	}

}
