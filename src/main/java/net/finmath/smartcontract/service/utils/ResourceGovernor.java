package net.finmath.smartcontract.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that provides all resource access to the rest of the services.
 */
@Service
@Profile("!test")
public class ResourceGovernor {

	private static final Logger logger = LoggerFactory.getLogger(ResourceGovernor.class);
	@Autowired
	private ResourcePatternResolver resourcePatternResolver;
	@Value("${storage.basedir}")
	private String storageBaseDir;
	@Value("${storage.importdir}")
	private String importDir;
	@Value("${storage.internals.marketDataProviderConnectionPropertiesFile}")
	private String refinitivConnectionPropertiesFile;

	@Value("${storage.internals.databaseConnectionPropertiesFile}")
	private String databaseConnectionPropertiesFile;

	public Resource getActiveDatasetAsResourceInReadMode(String username) {

		return resourcePatternResolver.getResource(
				"file:///" + Objects.requireNonNull(storageBaseDir) + RoleFolders.MARKET_DATA_FOLDER.toString()
						.formatted(
								username) + "active_dataset.json");
	}

	public WritableResource getActiveDatasetAsResourceInWriteMode(String username) {

		return (WritableResource) resourcePatternResolver.getResource(
				"file:///" + Objects.requireNonNull(storageBaseDir) + RoleFolders.MARKET_DATA_FOLDER.toString()
						.formatted(
								username) + "active_dataset.json");
	}

	public Resource getImportCandidateAsResourceInReadMode() {
		return resourcePatternResolver.getResource(
				"file:///" + Objects.requireNonNull(importDir) + "/import_candidate.json");
	}

	public WritableResource getImportCandidateAsResourceInWriteMode() {
		return (WritableResource) resourcePatternResolver.getResource(
				"file:///" + Objects.requireNonNull(importDir) + "/import_candidate.json");
	}

	public Resource getRefinitivPropertiesAsResourceInReadMode() {
		return resourcePatternResolver.getResource("file:///" + refinitivConnectionPropertiesFile);
	}

	public Resource getDatabasePropertiesAsResourceInReadMode() {
		return resourcePatternResolver.getResource("file:///" + databaseConnectionPropertiesFile);
	}

	public Resource getReadableResource(String username, RoleFolders roleFolder, String filename) {
		return resourcePatternResolver.getResource(
				"file:///" + Objects.requireNonNull(storageBaseDir) + roleFolder.toString()
						.formatted(username) + filename);
	}

	public WritableResource getWritableResource(String username, RoleFolders roleFolder, String filename) {
		return (WritableResource) resourcePatternResolver.getResource(
				"file:///" + Objects.requireNonNull(storageBaseDir) + roleFolder.toString()
						.formatted(username) + filename);
	}

	public Resource[] listContentsOfUserFolder(String username, RoleFolders roleFolder) throws IOException {
		return resourcePatternResolver.getResources(
				"file:///" + storageBaseDir + roleFolder.toString().formatted(username) + "*");
	}

	public enum RoleFolders {
		MARKET_DATA_FOLDER("/%s.marketdata/"), SAVED_CONTRACTS_FOLDER("/%s.savedcontracts/");

		private final String folderTemplate;

		RoleFolders(String s) {
			this.folderTemplate = s;
		}

		@Override
		public String toString() {
			return folderTemplate;
		}
	}
}
