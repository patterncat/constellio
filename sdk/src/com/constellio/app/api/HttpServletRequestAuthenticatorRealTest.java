package com.constellio.app.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.security.authentification.AuthenticationService;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.setups.Users;

public class HttpServletRequestAuthenticatorRealTest extends ConstellioTest {
	
	public static final String USER_SERVICE_KEY = "serviceKey";
	public static final String USER_TOKEN = "token";
	private Users usersRecords = new Users();
	
	private String bobServiceKey;
	private String bobToken;

	@Before
	public void setup() {
		givenBackgroundThreadsEnabled();
		prepareSystem(withZeCollection().withAllTestUsers());
		
		//startApplication();

		// User/Security setup
		UserServices userServices = getModelLayerFactory().newUserServices();
		usersRecords.setUp(userServices);
		User bob = usersRecords.bobIn(zeCollection);
		UserCredential userCredentialBob = usersRecords.bob();
		userServices.givenSystemAdminPermissionsToUser(userCredentialBob);
		String bobPassword = updateBobPassword(userCredentialBob);

		bobServiceKey = getServiceKey(userServices, userCredentialBob);
		bobToken = getToken(bob, bobServiceKey, bobPassword);
	}

	private String updateBobPassword(UserCredential userCredentialBob) {
		AuthenticationService authService = getModelLayerFactory().newAuthenticationService();
		String bobPassword = "p2";
		authService.changePassword(userCredentialBob.getUsername(), bobPassword);
		return bobPassword;
	}

	private String getServiceKey(UserServices userServices, UserCredential userCredentialBob) {
		return userServices.giveNewServiceToken(userCredentialBob);
	}

	private String getToken(User bob, String bobServiceKey, String bobPassword) {
		return getModelLayerFactory().newUserServices()
				.getToken(bobServiceKey, bob.getUsername(), bobPassword);
	}

	@Test
	public void givenEmpyRequestThenNoCredentials() {
		HttpServletRequestAuthenticator authenticator = new HttpServletRequestAuthenticator(getModelLayerFactory());
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession()).thenReturn(mock(HttpSession.class));
		
		UserCredential credentials = authenticator.authenticate(request);
		
		assertThat(credentials).isNull();
	}
	
	@Test
	public void givenInvalidTokenThenNoCredentials() {
		HttpServletRequestAuthenticator authenticator = new HttpServletRequestAuthenticator(getModelLayerFactory());
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession()).thenReturn(mock(HttpSession.class));
		
		when(request.getParameter(USER_SERVICE_KEY)).thenReturn(this.bobServiceKey);
		when(request.getParameter(USER_TOKEN)).thenReturn(this.bobToken + "invalid");
		
		UserCredential credentials = authenticator.authenticate(request);
		
		assertThat(credentials).isNull();
	}
	
	@Test
	public void givenValidTokenThenredentials() {
		HttpServletRequestAuthenticator authenticator = new HttpServletRequestAuthenticator(getModelLayerFactory());
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession()).thenReturn(mock(HttpSession.class));
		
		when(request.getParameter(USER_SERVICE_KEY)).thenReturn(this.bobServiceKey);
		when(request.getParameter(USER_TOKEN)).thenReturn(this.bobToken);
		
		UserCredential credentials = authenticator.authenticate(request);
		
		assertThat(credentials.getUsername()).isEqualTo("bob");
	}

	//TODO Vincent - @Test
	public void givenSsoThenredentials() {
		HttpServletRequestAuthenticator authenticator = new HttpServletRequestAuthenticator(getModelLayerFactory());
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession()).thenReturn(session);
		Principal userPrincipal = mock(Principal.class);
		when(request.getUserPrincipal()).thenReturn(userPrincipal);
		when(userPrincipal.getName()).thenReturn("bob");

		UserCredential credentials = authenticator.authenticate(request);
		assertThat(credentials.getUsername()).isEqualTo("bob");
	}
}