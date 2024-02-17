


/*
 *    Copyright (c) 2020, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
*    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

 package io.supertokens.anomalydetection;

 import com.google.gson.JsonObject;
 import io.supertokens.Main;
 import io.supertokens.pluginInterface.Storage;
 import io.supertokens.pluginInterface.exceptions.StorageQueryException;
 import io.supertokens.pluginInterface.exceptions.StorageTransactionLogicException;
 import io.supertokens.pluginInterface.multitenancy.AppIdentifierWithStorage;
 import io.supertokens.pluginInterface.multitenancy.TenantIdentifier;
 import io.supertokens.pluginInterface.multitenancy.TenantIdentifierWithStorage;
 import io.supertokens.pluginInterface.multitenancy.exceptions.TenantOrAppNotFoundException;
 import io.supertokens.pluginInterface.anomalydetection.sqlStorage.AnomalyDetectionSQLStorage;
 import io.supertokens.storageLayer.StorageLayer;
 import io.supertokens.utils.MetadataUtils;
 import org.jetbrains.annotations.TestOnly;
 
 import javax.annotation.Nonnull;

 public class AnomalyDetection {
    @TestOnly
    public static String getLastIPAddress(Main main, @Nonnull String userId) throws StorageQueryException {
        Storage storage = StorageLayer.getStorage(main);
        try {
            return getLastIPAddress(new AppIdentifierWithStorage(null, null, storage), userId);
        } catch (TenantOrAppNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getLastIPAddress(AppIdentifierWithStorage appIdentifierWithStorage,
                                            @Nonnull String userId)
            throws StorageQueryException, TenantOrAppNotFoundException {
        AnomalyDetectionSQLStorage storage = appIdentifierWithStorage.getAnomalyDetectionStorage();

        String lastIPAddress = storage.getLastIPAddress(appIdentifierWithStorage, userId);

        if (lastIPAddress == null) {
            return new String();
        }

        return lastIPAddress;
    }

    @TestOnly
    public static void insertNewIPAddress(Main main, @Nonnull String userId, @Nonnull String newIPAddress) throws StorageQueryException {
        Storage storage = StorageLayer.getStorage(main);
        try {
            insertNewIPAddress(new AppIdentifierWithStorage(null, null, storage), userId, newIPAddress);
        } catch (TenantOrAppNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void insertNewIPAddress(AppIdentifierWithStorage appIdentifierWithStorage,
                                        @Nonnull String userId, @Nonnull String newIPAddress)
            throws StorageQueryException, TenantOrAppNotFoundException {
        appIdentifierWithStorage.getAnomalyDetectionStorage().insertNewIPAddress(appIdentifierWithStorage, userId, newIPAddress);
    }
 }
 