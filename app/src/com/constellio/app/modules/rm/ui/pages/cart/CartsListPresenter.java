package com.constellio.app.modules.rm.ui.pages.cart;

import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Cart;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SchemaPresenterUtils;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.schemas.builders.CommonMetadataBuilder;
import com.constellio.model.services.search.StatusFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import java.util.Arrays;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

public class CartsListPresenter extends SingleSchemaBasePresenter<CartsListView> {

	private RecordToVOBuilder recordToVOBuilder = new RecordToVOBuilder();
	private final MetadataSchemaVO schemaVO;
	private RMSchemasRecordsServices rm;

	public CartsListPresenter(CartsListView view) {
		super(view,Cart.DEFAULT_SCHEMA);
		schemaVO =  new MetadataSchemaToVOBuilder().build(defaultSchema(), RecordVO.VIEW_MODE.TABLE, view.getSessionContext());
		rm = new RMSchemasRecordsServices(collection,appLayerFactory);
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	public void displayButtonClicked(RecordVO recordVO) {
		view.navigate().to(RMViews.class).cart(recordVO.getId());
	}

	public void deleteButtonClicked(RecordVO recordVO) {
		delete(toRecord(recordVO));
		view.navigate().to(RMViews.class).listCarts();
	}

	public RecordVODataProvider getOwnedCartsDataProvider() {
		return new RecordVODataProvider(schemaVO, recordToVOBuilder, modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return new LogicalSearchQuery(from(defaultSchema()).where(getMetadata(Cart.OWNER))
						.isEqualTo(getCurrentUser().getId())).sortAsc(Schemas.TITLE);
			}
		};
	}

	public RecordVODataProvider getSharedCartsDataProvider() {
		return new RecordVODataProvider(schemaVO, recordToVOBuilder, modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return new LogicalSearchQuery(from(defaultSchema()).where(getMetadata(Cart.SHARED_WITH_USERS))
						.isContaining(Arrays.asList(getCurrentUser().getId()))).sortAsc(Schemas.TITLE);
			}
		};
	}

	public void saveButtonClicked(String title) {
		Cart cart = rm.newCart();
		cart.setTitle(title);
		cart.setOwner(getCurrentUser());
		try {
			recordServices().execute(new Transaction(cart.getWrappedRecord()).setUser(getCurrentUser()));
			view.navigate().to(RMViews.class).listCarts();
		} catch (RecordServicesException e) {
			e.printStackTrace();
		}
	}
}
