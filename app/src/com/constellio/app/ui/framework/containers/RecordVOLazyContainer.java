package com.constellio.app.ui.framework.containers;

import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.data.DataProvider.DataRefreshListener;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.framework.items.RecordVOItem;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.vaadin.data.Item;
import org.vaadin.addons.lazyquerycontainer.*;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
public class RecordVOLazyContainer extends LazyQueryContainer implements RefreshableContainer {

	private List<RecordVODataProvider> dataProviders;

	public RecordVOLazyContainer(RecordVODataProvider dataProvider) {
		this(Arrays.asList(dataProvider));
	}

	public RecordVOLazyContainer(List<RecordVODataProvider> dataProviders) {
		this(dataProviders, 100);
	}

	public RecordVOLazyContainer(List<RecordVODataProvider> dataProviders, int batchSize) {
		super(new RecordVOLazyQueryDefinition(dataProviders, isOnlyTableMetadatasShown(), batchSize),
				new RecordVOLazyQueryFactory(dataProviders));
		this.dataProviders = dataProviders;
		for (RecordVODataProvider dataProvider : dataProviders) {
			dataProvider.setBatchSize(batchSize);
		}
		for (RecordVODataProvider dataProvider : dataProviders) {
			dataProvider.addDataRefreshListener(new DataRefreshListener() {
				@Override
				public void dataRefresh() {
					RecordVOLazyContainer.this.refresh();
				}
			});
		}
	}

	public List<RecordVODataProvider> getDataProviders() {
		return dataProviders;
	}

	private static boolean isOnlyTableMetadatasShown() {
		ConstellioFactories constellioFactories = ConstellioFactories.getInstance();
		ConstellioEIMConfigs configs = new ConstellioEIMConfigs(
				constellioFactories.getModelLayerFactory().getSystemConfigurationsManager());
		return !configs.isTableDynamicConfiguration();
	}

	public List<MetadataSchemaVO> getSchemas() {
		List<MetadataSchemaVO> schemas = new ArrayList<>();
		for (RecordVODataProvider dataProvider : dataProviders) {
			schemas.add(dataProvider.getSchema());
		}
		return schemas;
	}

	private static RecordVODataProviderAndRecordIndex forRecordIndex(List<RecordVODataProvider> dataProviders, int index) {
		RecordVODataProviderAndRecordIndex result = null;
		int lastSize = 0;
		for (RecordVODataProvider dataProvider : dataProviders) {
			int dataProviderSize = dataProvider.size();
			if ((lastSize + dataProviderSize) > index) {
				int actualIndex = index - lastSize;
				result = new RecordVODataProviderAndRecordIndex(dataProvider, actualIndex);
				break;
			}
			lastSize += dataProviderSize;
		}
		return result;
	}

	public RecordVO getRecordVO(int index) {
		RecordVODataProviderAndRecordIndex dataProviderAndRecordIndex = forRecordIndex(dataProviders, index);
		int recordIndexForDataProvider = dataProviderAndRecordIndex.recordIndex;

		return dataProviderAndRecordIndex.dataProvider.getRecordVO(recordIndexForDataProvider);
	}

	private static class RecordVODataProviderAndRecordIndex implements Serializable {

		private RecordVODataProvider dataProvider;

		private int recordIndex;

		public RecordVODataProviderAndRecordIndex(RecordVODataProvider dataProvider, int recordIndex) {
			this.dataProvider = dataProvider;
			this.recordIndex = recordIndex;
		}

	}

	public static class RecordVOLazyQueryDefinition extends LazyQueryDefinition {

		List<RecordVODataProvider> dataProviders;

		/**
		 * final boolean compositeItems, final int batchSize, final Object idPropertyId
		 *
		 * //@param dataProviders
		 * //@param compositeItems
		 * //@param batchSize
		 * //@param idPropertyId
		 */
		public RecordVOLazyQueryDefinition(List<RecordVODataProvider> dataProviders, boolean tableMetadatasOnly, int batchSize) {
			super(true, batchSize, null);
			this.dataProviders = dataProviders;

			List<MetadataVO> propertyMetadataVOs = new ArrayList<>();
			List<MetadataVO> tablePropertyMetadataVOs = new ArrayList<>();
			List<MetadataVO> extraPropertyMetadataVOs = new ArrayList<>();
			List<MetadataVO> queryMetadataVOs = new ArrayList<>();

			for (RecordVODataProvider dataProvider : dataProviders) {
				MetadataSchemaVO schema = dataProvider.getSchema();
				List<MetadataVO> dataProviderTableMetadataVOs = schema.getTableMetadatas();
				tablePropertyMetadataVOs.addAll(dataProviderTableMetadataVOs);
				List<MetadataVO> dataProviderQueryMetadataVOs = new ArrayList<>(dataProviderTableMetadataVOs);
				if (!tableMetadatasOnly) {
					List<MetadataVO> dataProviderDisplayMetadataVOs = schema.getDisplayMetadatas();
					for (MetadataVO metadataVO : dataProviderDisplayMetadataVOs) {
						if (!dataProviderQueryMetadataVOs.contains(metadataVO)) {
							dataProviderQueryMetadataVOs.add(metadataVO);
						}
					}
				}
				for (MetadataVO metadataVO : dataProviderQueryMetadataVOs) {
					if (!queryMetadataVOs.contains(metadataVO)) {
						if (dataProviderTableMetadataVOs.contains(metadataVO)) {
							tablePropertyMetadataVOs.add(metadataVO);
						} else {
							extraPropertyMetadataVOs.add(metadataVO);
						}
					}
				}
			}

			Collections.sort(extraPropertyMetadataVOs, new Comparator<MetadataVO>() {
				@Override
				public int compare(MetadataVO o1, MetadataVO o2) {
					if(o1.getLabel() == null || o2.getLabel() == null) {
						return -1;
					}
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
			propertyMetadataVOs.addAll(tablePropertyMetadataVOs);
			propertyMetadataVOs.addAll(extraPropertyMetadataVOs);

			for (MetadataVO metadataVO : propertyMetadataVOs) {
				super.addProperty(metadataVO, metadataVO.getJavaType(), null, true, true);
			}
		}
	}

	public static class RecordVOLazyQueryFactory implements QueryFactory, Serializable {

		List<RecordVODataProvider> dataProviders;

		public RecordVOLazyQueryFactory(List<RecordVODataProvider> dataProviders) {
			this.dataProviders = dataProviders;
		}

		@Override
		public Query constructQuery(final QueryDefinition queryDefinition) {
			Object[] sortPropertyIds = queryDefinition.getSortPropertyIds();
			if (sortPropertyIds != null && sortPropertyIds.length > 0) {
				List<MetadataVO> sortMetadatas = new ArrayList<MetadataVO>();
				for (int i = 0; i < sortPropertyIds.length; i++) {
					MetadataVO sortMetadata = (MetadataVO) sortPropertyIds[i];
					sortMetadatas.add(sortMetadata);
				}
				for (RecordVODataProvider dataProvider : dataProviders) {
					dataProvider.sort(sortMetadatas.toArray(new MetadataVO[0]), queryDefinition.getSortPropertyAscendingStates());
				}
			}
			return new SerializableQuery() {
				@Override
				public int size() {
					int totalSizes = 0;
					for (RecordVODataProvider dataProvider : dataProviders) {
						totalSizes += dataProvider.size();
					}
					return totalSizes;
				}

				@Override
				public void saveItems(List<Item> addedItems, List<Item> modifiedItems, List<Item> removedItems) {
					throw new UnsupportedOperationException("Query is read-only");
				}

				@Override
				public List<Item> loadItems(int startIndex, int count) {
					List<Item> items = new ArrayList<Item>();

					RecordVODataProviderAndRecordIndex dataProviderAndRecordIndex = forRecordIndex(dataProviders, startIndex);
					RecordVODataProvider firstDataProvider = dataProviderAndRecordIndex.dataProvider;
					int startIndexForFirstDataProvider = dataProviderAndRecordIndex.recordIndex;

					List<RecordVO> recordVOsFromFirstDataProvider = firstDataProvider
							.listRecordVOs(startIndexForFirstDataProvider, count);
					for (RecordVO recordVO : recordVOsFromFirstDataProvider) {
						Item item = new RecordVOItem(recordVO);
						items.add(item);
					}

					if (items.size() < count) {
						// We need to add results from extra dataProviders
						boolean firstDataProviderFound = false;
						for (RecordVODataProvider dataProvider : dataProviders) {
							if (dataProvider.equals(firstDataProvider)) {
								firstDataProviderFound = true;
							} else if (firstDataProviderFound) {
								// Only records belonging to dataProviders after the first are relevant
								int startIndexForDataProvider = 0;
								int countForDataProvider = count - items.size();
								List<RecordVO> recordVOsFromDataProvider = dataProvider
										.listRecordVOs(startIndexForDataProvider, countForDataProvider);
								for (RecordVO recordVO : recordVOsFromDataProvider) {
									Item item = new RecordVOItem(recordVO);
									items.add(item);
								}
								if (items.size() >= count) {
									break;
								}
							}
						}
					}

					return items;
				}

				@Override
				public boolean deleteAllItems() {
					throw new UnsupportedOperationException("Query is read-only");
				}

				@Override
				public Item constructItem() {
					throw new UnsupportedOperationException("Query is read-only");
				}
			};
		}

		private interface SerializableQuery extends Query, Serializable {

		}
	}
}
