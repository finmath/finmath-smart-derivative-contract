package net.finmath.smartcontract.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Profile("test")
public class MockResourceGovernor extends ResourceGovernor{

    @Override
    public Resource getActiveDatasetAsResourceInReadMode(String username) {
        return new ClassPathResource("net.finmath.smartcontract.client/md_testset_newformat_1.json");
    }

    @Override
    public WritableResource getActiveDatasetAsResourceInWriteMode(String username) {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public Resource getImportCandidateAsResourceInReadMode() {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public WritableResource getImportCandidateAsResourceInWriteMode() {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public Resource getRefinitivPropertiesAsResourceInReadMode() {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public Resource getDatabasePropertiesAsResourceInReadMode() {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public Resource getReadableResource(String username, RoleFolders roleFolder, String filename) {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public WritableResource getWritableResource(String username, RoleFolders roleFolder, String filename) {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public Resource[] listContentsOfUserFolder(String username, RoleFolders roleFolder) throws IOException {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }
}
