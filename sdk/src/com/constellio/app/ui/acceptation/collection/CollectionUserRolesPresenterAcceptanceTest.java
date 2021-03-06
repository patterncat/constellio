package com.constellio.app.ui.acceptation.collection;

import static com.constellio.model.entities.security.global.AuthorizationAddRequest.authorizationInCollection;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.modules.rm.constants.RMRoles;
import com.constellio.app.ui.entities.RoleAuthVO;
import com.constellio.app.ui.pages.collection.CollectionUserRolesPresenter;
import com.constellio.app.ui.pages.collection.CollectionUserRolesView;
import com.constellio.model.entities.records.wrappers.Group;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.global.AuthorizationAddRequest;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup;
import com.constellio.sdk.tests.setups.Users;

public class CollectionUserRolesPresenterAcceptanceTest extends ConstellioTest {

	TestsSchemasSetup setup = new TestsSchemasSetup(zeCollection);

	Users users = new Users();
	String legends;
	String aliceId;
	String legendsId;

	private UserServices userServices;
	private RecordServices recordServices;

	String zeConcept = "zeConcept";

	CollectionUserRolesPresenter presenter;
	@Mock CollectionUserRolesView collectionUserRolesView;

	@Before
	public void setUp()
			throws Exception {
		prepareSystem(withCollection(zeCollection).withConstellioRMModule().withAllTestUsers());

		when(collectionUserRolesView.getSessionContext()).thenReturn(FakeSessionContext.adminInCollection(zeCollection));
		defineSchemasManager().using(setup);

		userServices = getModelLayerFactory().newUserServices();
		recordServices = getModelLayerFactory().newRecordServices();

		recordServices.add(recordServices.newRecordWithSchema(setup.zeDefaultSchema(), zeConcept));

		users.setUp(userServices);
		aliceId = users.aliceIn(zeCollection).getId();
		legendsId = users.legendsIn(zeCollection).getId();
		legends = users.legends().getCode();

		presenter = new CollectionUserRolesPresenter(collectionUserRolesView);
		presenter.forRequestParams(aliceId);
	}

	@Test
	public void givenUserWithInheritedRolesAndAuthsWhenGettingInheritedRolesThenAllRolesThere()
			throws Exception {
		givenAliceIsInLegendsGroup();
		add(givenAuthorizationFor(legendsId).on(zeConcept).giving(RMRoles.USER));
		givenLegendsAreManagerGlobally();
		waitForBatchProcess();

		List<RoleAuthVO> aliceInheritedRoles = presenter.getInheritedRoles();
		assertThat(aliceInheritedRoles).hasSize(2);
		verifyThat(aliceInheritedRoles).containsGlobalRole(RMRoles.MANAGER);
		verifyThat(aliceInheritedRoles).containsRoleAuths(RMRoles.USER).onTarget(zeConcept);
	}

	@Test
	public void givenUserWithSpecificRolesAndAuthsWhenGettingSpecificRolesThenAllRolesThere()
			throws Exception {
		add(givenAuthorizationFor(aliceId).on(zeConcept).giving(RMRoles.USER));
		givenAliceIsManagerGlobally();
		waitForBatchProcess();

		List<RoleAuthVO> aliceSpecificRoles = presenter.getSpecificRoles();
		assertThat(aliceSpecificRoles).hasSize(3);
		verifyThat(aliceSpecificRoles).containsGlobalRole(RMRoles.MANAGER);
		verifyThat(aliceSpecificRoles).containsRoleAuths(RMRoles.USER).onTarget(zeConcept);
	}

	@Test
	public void givenUserWithGlobalRoleWhenDeletingItThenRoleIsDeleted()
			throws Exception {
		add(givenAuthorizationFor(aliceId).on(zeConcept).giving(RMRoles.USER));
		givenAliceIsManagerGlobally();
		waitForBatchProcess();

		presenter.deleteRoleButtonClicked(
				new RoleAuthVO(null, null, asList(RMRoles.MANAGER)));

		List<RoleAuthVO> aliceSpecificRoles = presenter.getSpecificRoles();
		verifyThat(aliceSpecificRoles).doesNotContainGlobalRole(RMRoles.MANAGER);
		verifyThat(aliceSpecificRoles).containsRoleAuths(RMRoles.USER).onTarget(zeConcept);
	}

	@Test
	public void givenUserWithRoleAuthWhenDeletingItThenAuthDeleted()
			throws Exception {
		AuthorizationAddRequest authorization = givenAuthorizationFor(aliceId).on(zeConcept).giving(RMRoles.USER);
		String id = add(authorization);
		waitForBatchProcess();

		presenter.deleteRoleButtonClicked(new RoleAuthVO(id, authorization.getTarget(), asList(RMRoles.USER)));
		waitForBatchProcess();

		List<RoleAuthVO> aliceSpecificRoles = presenter.getSpecificRoles();
		assertThat(aliceSpecificRoles).hasSize(1);
		verifyThat(aliceSpecificRoles).containsGlobalRole(RMRoles.USER);
		verifyThat(aliceSpecificRoles).doesNotContainRoleAuths(RMRoles.USER).onTarget(zeConcept);
	}

	@Test
	public void whenAddingGlobalRoleThenRoleAddedToUser()
			throws Exception {
		presenter.addRoleButtonClicked(new RoleAuthVO(null, null, asList(RMRoles.MANAGER)));
		waitForBatchProcess();

		List<RoleAuthVO> aliceSpecificRoles = presenter.getSpecificRoles();
		assertThat(aliceSpecificRoles).hasSize(2);
		verifyThat(aliceSpecificRoles).containsGlobalRole(RMRoles.USER);
		verifyThat(aliceSpecificRoles).containsGlobalRole(RMRoles.MANAGER);
	}

	@Test
	public void whenAddingRoleAuthThenAuthorizationAdded()
			throws Exception {
		presenter.addRoleButtonClicked(new RoleAuthVO(null, zeConcept, asList(RMRoles.MANAGER)));
		waitForBatchProcess();

		List<RoleAuthVO> aliceSpecificRoles = presenter.getSpecificRoles();
		assertThat(aliceSpecificRoles).hasSize(2);
		verifyThat(aliceSpecificRoles).containsRoleAuths(RMRoles.MANAGER).onTarget(zeConcept);
	}

	// ==================================================================================================
	// ==================================================================================================

	private void givenLegendsAreManagerGlobally()
			throws RecordServicesException {
		Group legendsGroup = userServices.getGroupInCollection(legends, zeCollection);
		List<String> roles = new ArrayList<>(legendsGroup.getRoles());
		roles.add(RMRoles.MANAGER);
		legendsGroup.setRoles(roles);
		recordServices.update(legendsGroup.getWrappedRecord());
	}

	private void givenAliceIsManagerGlobally()
			throws RecordServicesException {
		User aliceUser = userServices.getUserInCollection(aliceWonderland, zeCollection);
		List<String> roles = new ArrayList<>(aliceUser.getUserRoles());
		roles.add(RMRoles.MANAGER);
		aliceUser.setUserRoles(roles);
		recordServices.update(aliceUser.getWrappedRecord());
	}

	private String add(AuthorizationAddRequest authorization) {
		return getModelLayerFactory().newAuthorizationsServices().add(authorization, User.GOD);
	}

	private AuthorizationAddRequest givenAuthorizationFor(String principalId) {
		return authorizationInCollection(zeCollection).forPrincipalsIds(asList(principalId));
	}

	private void givenAliceIsInLegendsGroup() {
		UserServices userServices = getModelLayerFactory().newUserServices();
		userServices.addUpdateUserCredential(users.alice().withNewGlobalGroup(users.legends().getCode()));
	}

	private RoleAuthVOListVerifier verifyThat(List<RoleAuthVO> roleAuthVOs) {
		return new RoleAuthVOListVerifier(roleAuthVOs);
	}

	private class RoleAuthVOListVerifier {
		private List<RoleAuthVO> roleAuthVOs;
		private List<String> wantedRoles = new ArrayList<>();
		private boolean expectedToContain = true;

		public RoleAuthVOListVerifier(List<RoleAuthVO> roleAuthVOs) {
			this.roleAuthVOs = roleAuthVOs;
		}

		public RoleAuthVOListVerifier containsRoleAuths(String... roles) {
			wantedRoles.clear();
			wantedRoles.addAll(asList(roles));
			return this;
		}

		public RoleAuthVOListVerifier doesNotContainRoleAuths(String... roles) {
			wantedRoles.clear();
			wantedRoles.addAll(asList(roles));
			expectedToContain = false;
			return this;
		}

		public void onTarget(String target) {
			boolean constainsWantedRolesOnTarget = false;
			for (RoleAuthVO roleAuthVO : roleAuthVOs) {
				if (target.equals(roleAuthVO.getTarget()) && roleAuthVO.getRoles().containsAll(wantedRoles)) {
					constainsWantedRolesOnTarget = true;
					break;
				}
			}
			assertThat(constainsWantedRolesOnTarget == expectedToContain).isTrue();
		}

		public void containsGlobalRole(String role) {
			boolean constainsRole = false;
			for (RoleAuthVO roleAuthVO : roleAuthVOs) {
				if (roleAuthVO.getId() == null && roleAuthVO.getRoles().contains(role)) {
					constainsRole = true;
					break;
				}
			}
			assertThat(constainsRole).isTrue();
		}

		public void doesNotContainGlobalRole(String role) {
			boolean constainsRole = false;
			for (RoleAuthVO roleAuthVO : roleAuthVOs) {
				if (roleAuthVO.getId() == null && roleAuthVO.getRoles().contains(role)) {
					constainsRole = true;
					break;
				}
			}
			assertThat(constainsRole).isFalse();
		}
	}
}
