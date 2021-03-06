package com.constellio.app.ui.pages.login;

import static com.constellio.app.ui.i18n.i18n.$;

import javax.servlet.http.Cookie;

import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.handlers.OnEnterKeyHandler;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.app.ui.pages.base.LogoUtils;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class LoginViewImpl extends BaseViewImpl implements LoginView {

	private static final String USERNAME_COOKIE = "Constellio.username";

	private String initialUsername;

	private VerticalLayout loginFormLayout;

	private CssLayout labelsLayout;
	private Label welcomeLabel;

	private HorizontalLayout fieldsLayout;
	private TextField usernameField;
	private PasswordField passwordField;
	private CheckBox rememberMeField;
	private Button signInButton;

	private LoginPresenter presenter;

	public LoginViewImpl() {
		presenter = new LoginPresenter(this);

		setSizeFull();

		loginFormLayout = buildLoginForm();
		addComponent(loginFormLayout);
		setComponentAlignment(loginFormLayout, Alignment.MIDDLE_CENTER);
	}

	private VerticalLayout buildLoginForm() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeUndefined();
		mainLayout.setSpacing(true);
		Responsive.makeResponsive(mainLayout);
		mainLayout.addStyleName("login-panel");

		labelsLayout = buildLabels();
		fieldsLayout = buildFields();
		rememberMeField = new CheckBox($("LoginView.rememberMe"), true);

		mainLayout.addComponent(labelsLayout);
		mainLayout.addComponent(fieldsLayout);
		mainLayout.addComponent(rememberMeField);
		return mainLayout;
	}

	private CssLayout buildLabels() {
		CssLayout labelsLayout = new CssLayout();
		labelsLayout.addStyleName("labels");
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSizeFull();

		welcomeLabel = new Label($("LoginView.welcome"));
		welcomeLabel.setSizeUndefined();
		welcomeLabel.addStyleName(ValoTheme.LABEL_H2);
		welcomeLabel.addStyleName(ValoTheme.LABEL_COLORED);
		//		hLayout.addComponent(welcomeLabel);

		String linkTarget = presenter.getLogoTarget();
		Link logo = new Link(null, new ExternalResource(linkTarget));
		ModelLayerFactory modelLayerFactory = getConstellioFactories().getModelLayerFactory();
		logo.setIcon(LogoUtils.getLogoResource(modelLayerFactory));
		logo.addStyleName("login-logo");
		logo.setSizeUndefined();
		hLayout.addComponent(logo);
		labelsLayout.addComponent(hLayout);

		return labelsLayout;
	}

	private HorizontalLayout buildFields() {
		HorizontalLayout fieldsLayout = new HorizontalLayout();
		fieldsLayout.setSpacing(true);
		fieldsLayout.addStyleName("fields");

		usernameField = new TextField($("LoginView.username"));
		usernameField.setIcon(FontAwesome.USER);
		usernameField.setMaxLength(100);
		usernameField.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		if (initialUsername != null) {
			usernameField.setValue(initialUsername);
		} else {
			usernameField.focus();
		}

		passwordField = new PasswordField($("LoginView.password"));
		passwordField.setIcon(FontAwesome.LOCK);
		passwordField.setMaxLength(100);
		passwordField.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		if (initialUsername != null) {
			passwordField.focus();
		}

		signInButton = new Button($("LoginView.signIn"));
		signInButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		signInButton.setClickShortcut(KeyCode.ENTER);

		fieldsLayout.addComponents(usernameField, passwordField, signInButton);
		fieldsLayout.setComponentAlignment(signInButton, Alignment.BOTTOM_LEFT);

		signInButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				attemptSignIn();
			}
		});

		OnEnterKeyHandler onEnterHandler = new OnEnterKeyHandler() {
			@Override
			public void onEnterKeyPressed() {
				attemptSignIn();
			}
		};
		onEnterHandler.installOn(usernameField);
		onEnterHandler.installOn(passwordField);

		return fieldsLayout;
	}

	private void attemptSignIn() {
		presenter.signInAttempt(usernameField.getValue(), passwordField.getValue(), rememberMeField.getValue());
	}

	@Override
	public void updateUIContent() {
		ConstellioUI.getCurrent().updateContent();
	}

	@Override
	public SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}

	@Override
	protected Component buildMainComponent(ViewChangeListener.ViewChangeEvent event) {
		return loginFormLayout;
	}

	@Override
	public void showUserHasNoCollectionMessage() {
		Notification.show($("LoginView.userHasNoCollection"));
	}

	@Override
	public void showBadLoginMessage() {
		Notification.show($("LoginView.badLoginMessage"));
	}

	@Override
	public void setUsername(String username) {
		if (usernameField != null) {
			usernameField.setValue(username);
		} else {
			initialUsername = username;
		}
	}

	@Override
	public String getUsernameCookieValue() {
		String usernameCookieValue = null;
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(USERNAME_COOKIE)) {
				usernameCookieValue = cookie.getValue();
				break;
			}
		}
		return usernameCookieValue;
	}

	@Override
	public void setUsernameCookie(String username) {
		Cookie usernameCookie = new Cookie(USERNAME_COOKIE, username);
		if (username != null) {
			// Make cookie expire in 2 minutes
			usernameCookie.setMaxAge(Integer.MAX_VALUE);
		} else {
			// Delete the cookie
			usernameCookie.setMaxAge(0);
		}

		// Set the cookie path.
		usernameCookie.setPath(VaadinService.getCurrentRequest().getContextPath());

		// Save cookie
		VaadinService.getCurrentResponse().addCookie(usernameCookie);
	}

}
