package com.constellio.app.ui.pages.management.labels;

import com.constellio.app.ui.application.CoreViews;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.components.breadcrumb.BreadcrumbTrail;
import com.constellio.app.ui.framework.components.breadcrumb.TitleBreadcrumbTrail;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.app.ui.pages.events.EventCategory;
import com.constellio.app.ui.pages.viewGroups.AdminViewGroup;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import static com.constellio.app.ui.i18n.i18n.$;

public class ReportConfigViewImpl extends BaseViewImpl implements AdminViewGroup {
    public static final String CATEGORY_BUTTON = "seleniumCategoryButton";

    @Override
    protected BaseBreadcrumbTrail buildBreadcrumbTrail() {
        return super.buildBreadcrumbTrail();
    }

    @Override
    protected Component buildMainComponent(ViewChangeListener.ViewChangeEvent event) {
        CssLayout layout = new CssLayout();
        Button manageLabels = newLabelManagementLink();
        Button managePrintableReport = newPrintableReportManagementLink();
        Button manageExcelReport = newExcelReportManagementLink();
        layout.addComponents(manageLabels, manageExcelReport, managePrintableReport);
        return layout;
    }

    private Button newLabelManagementLink() {
        return createLink($("LabelViewImpl.title"), new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigate().to().manageLabels();
            }
        }, "labels");
    }

    private Button newPrintableReportManagementLink() {
        return createLink($("PrintableReport.title"), new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigate().to().managePrintableReport();
            }
        }, "report-print");
    }

    private Button newExcelReportManagementLink() {
        return createLink($("ExcelReport.title"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigate().to().manageExcelReport();
            }
        }, "excel-templates");
    }

    private Button createLink(String caption, final Button.ClickListener listener, String iconName) {
        Button returnLink = new Button(caption, new ThemeResource("images/icons/" + iconName + ".png"));
        returnLink.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
        returnLink.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        returnLink.addStyleName(CATEGORY_BUTTON);
        returnLink.addClickListener(listener);
        return returnLink;
    }

    @Override
    public String getTitle() {
        return $("ReportConfig.title");
    }
}
