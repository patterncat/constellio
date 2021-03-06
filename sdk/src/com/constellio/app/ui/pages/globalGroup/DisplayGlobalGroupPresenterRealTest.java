package com.constellio.app.ui.pages.globalGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constellio.sdk.tests.MockedNavigation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.ui.application.CoreViews;
import com.constellio.app.ui.application.NavigatorConfigurationService;
import com.constellio.app.ui.entities.GlobalGroupVO;
import com.constellio.app.ui.entities.UserCredentialVO;
import com.constellio.app.ui.framework.data.GlobalGroupVODataProvider;
import com.constellio.model.entities.security.global.GlobalGroup;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.users.UserCredentialsManager;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedFactories;

public class DisplayGlobalGroupPresenterRealTest extends ConstellioTest {

	public static final String HEROES_GLOBAL_GROUP = "Heroes Global Group";
	public static final String HEROES = "heroes";
	public static final String LEGENDS = "legends";

	@Mock DisplayGlobalGroupViewImpl globalGroupView;
	@Mock UserServices userServices;
	@Mock GlobalGroupVO heroesGlobalGroupVO;
	MockedNavigation navigator;
    @Mock CoreViews coreView;
	@Mock UserCredentialsManager userCredentialsManager;
	@Mock UserCredential dakotaCredential, newDakotaCredential;
	@Mock GlobalGroup heroes, legends;
	@Mock GlobalGroupVODataProvider globalGroupVODataProvider;
	@Mock UserCredentialVO dakotaCredentialVO;

	DisplayGlobalGroupPresenter presenter;
	MockedFactories mockedFactories = new MockedFactories();
	List<GlobalGroup> globalGroups;

	@Before
	public void setUp()
			throws Exception {

		globalGroups = new ArrayList<>();
		globalGroups.add(heroes);

        navigator = new MockedNavigation();

		when(globalGroupView.getConstellioFactories()).thenReturn(mockedFactories.getConstellioFactories());
		when(globalGroupView.getSessionContext()).thenReturn(FakeSessionContext.dakotaInCollection(zeCollection));
		when(globalGroupView.navigate()).thenReturn(navigator);


		when(heroes.getCode()).thenReturn(HEROES);
		when(heroes.getName()).thenReturn(HEROES_GLOBAL_GROUP);

		when(heroesGlobalGroupVO.getCode()).thenReturn(HEROES);
		when(heroesGlobalGroupVO.getName()).thenReturn(HEROES_GLOBAL_GROUP);

		when(mockedFactories.getModelLayerFactory().newUserServices()).thenReturn(userServices);
		when(mockedFactories.getModelLayerFactory().getUserCredentialsManager()).thenReturn(userCredentialsManager);
		when(userServices.getGroup(HEROES)).thenReturn(heroes);

		when(dakotaCredentialVO.getUsername()).thenReturn(dakota);

		presenter = spy(new DisplayGlobalGroupPresenter(globalGroupView));

		givenBreadCrumbAndParameters();

	}

	private void givenBreadCrumbAndParameters() {
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("globalGroupCode", HEROES);
		presenter.setParamsMap(paramsMap);
		presenter.setBreadCrumb("url1/url2/url3");
	}

	@Test
	public void givenCodeWhenGetGlobalGroupVOThenReturnVO()
			throws Exception {

		GlobalGroupVO globalGroupVO = presenter.getGlobalGroupVO(HEROES);

		assertThat(globalGroupVO.getCode()).isEqualTo(heroes.getCode());
		assertThat(globalGroupVO.getName()).isEqualTo(heroes.getName());
	}

	@Test
	public void whenBackButtonClickedThenNavigateToLastBreadCrumb()
			throws Exception {

		presenter.backButtonClicked();

		verify(globalGroupView.navigate().to(), times(1)).url("url3/url1/url2/" + URLEncoder.encode("globalGroupCode=heroes",
				"UTF-8"));
	}

	@Test
	public void whenEditButtonClickedThenNavigateToEditGlobalGroupWithRightParams()
			throws Exception {

		presenter.editButtonClicked(heroesGlobalGroupVO);

		verify(globalGroupView.navigate().to(), times(1))
				.editGlobalGroup("url1/url2/url3/" + NavigatorConfigurationService.GROUP_DISPLAY + "/" + URLEncoder
						.encode("globalGroupCode=heroes", "UTF-8"));
	}

	@Test
	public void whenDisplayButtonClickedThenNavigateToEditGlobalGroupWithRightParams()
			throws Exception {

		presenter.editButtonClicked(heroesGlobalGroupVO);

		verify(globalGroupView.navigate().to(), times(1))
				.editGlobalGroup("url1/url2/url3/" + NavigatorConfigurationService.GROUP_DISPLAY + "/" + URLEncoder
						.encode("globalGroupCode=heroes", "UTF-8"));
	}

	@Test
	public void whenDeleteButtonClickedThenCleanBreadCrumbRemoveGroupAndNavigateToBackPage()
			throws Exception {

		presenter.setBreadCrumb("url1/groupDisplay/groupAddEdit");

		when(userCredentialsManager.getUserCredential(dakota)).thenReturn(dakotaCredential);

		presenter.deleteButtonClicked(heroesGlobalGroupVO);

		verify(userServices).logicallyRemoveGroupHierarchy(dakotaCredential, heroes);
		verify(presenter).cleanInvalidBackPages();
		verify(globalGroupView.navigate().to(), times(1)).url("url1/");
	}

	@Test
	public void whenAddUserCredentialButtonClickedThenOk()
			throws Exception {

		List<String> dakotaGlobalGroups = new ArrayList<>();
		List<String> newDakotaGlobalGroups = new ArrayList<>();
		dakotaGlobalGroups.add(HEROES);
		newDakotaGlobalGroups.addAll(dakotaGlobalGroups);
		newDakotaGlobalGroups.add(LEGENDS);
		when(dakotaCredential.getGlobalGroups()).thenReturn(dakotaGlobalGroups);
		when(dakotaCredential.withGlobalGroups(newDakotaGlobalGroups)).thenReturn(newDakotaCredential);
		when(userServices.getUserCredential(dakota)).thenReturn(dakotaCredential);

		presenter.addUserCredentialButtonClicked(LEGENDS, dakota);

		verify(userServices).addUpdateUserCredential(newDakotaCredential);
		verify(globalGroupView).refreshTable();
	}

	@Test
	public void whenDisplayUserCredentialButtonClickedThenNavigateToDisplayUserCredential()
			throws Exception {

		presenter.displayUserCredentialButtonClicked(dakotaCredentialVO, HEROES);

		verify(globalGroupView.navigate().to(), times(1))
				.displayUserCredential("url1/url2/url3/" + NavigatorConfigurationService.GROUP_DISPLAY + "/" + URLEncoder
						.encode("globalGroupCode=heroes;username=dakota", "UTF-8"));
	}

	@Test
	public void whenEditUserCredentialButtonClickedThenNavigateToEditUserCredential()
			throws Exception {

		presenter.editUserCredentialButtonClicked(dakotaCredentialVO, HEROES);

		verify(globalGroupView.navigate().to(), times(1))
				.editUserCredential("url1/url2/url3/" + NavigatorConfigurationService.GROUP_DISPLAY + "/" + URLEncoder
						.encode("globalGroupCode=heroes;username=dakota", "UTF-8"));
	}

	@Test
	public void whenDeleteUserCredentialButtonClickedThenDeleteUser()
			throws Exception {

		presenter.deleteUserCredentialButtonClicked(dakotaCredentialVO, heroesGlobalGroupVO.getCode());

		verify(userServices).removeUserFromGlobalGroup(dakota, heroesGlobalGroupVO.getCode());
		verify(globalGroupView).refreshTable();
	}

	@Test
	public void givenGlobalGroupCodeWhenGetUserDataProviderThenOk()
			throws Exception {

		presenter.getUserCredentialVODataProvider(HEROES);

		verify(presenter).newUserCredentialVODataProvider(HEROES, presenter.newUserCredentialVOBuilder());
	}

	//TODO fix test
	@Test
	@Ignore
	public void whenAddSubGroupClickedThenOk()
			throws Exception {

		presenter.addSubGroupClicked(heroesGlobalGroupVO);

		verify(globalGroupView.navigateTo()).url(NavigatorConfigurationService.GROUP_ADD_EDIT
				+ "/groupList/groupDisplay/username%253Dcharles%253BparentGlobalGroupCode%253Dheroes%253BglobalGroupCode%253Dheroes");
	}
}
