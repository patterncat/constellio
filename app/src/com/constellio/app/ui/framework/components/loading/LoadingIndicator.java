package com.constellio.app.ui.framework.components.loading;

import java.io.Serializable;

import com.vaadin.ui.JavaScript;

public class LoadingIndicator implements Serializable {

    public void setVisible(boolean visible) {
        if (visible) {
            StringBuilder js = new StringBuilder();
            js.append("var div = document.createElement('div');");
            js.append("div.className = 'custom-loader';");
            js.append("document.body.appendChild(div);");
            JavaScript.getCurrent().execute(js.toString());
        } else {
            StringBuilder js = new StringBuilder();
            js.append("var divs = document.getElementsByClassName('custom-loader');");
            js.append("while (divs.length > 0) {");
            js.append("divs[0].parentNode.removeChild(divs[0]);");
            js.append("}");
            JavaScript.getCurrent().execute(js.toString());
        }
    }
}
