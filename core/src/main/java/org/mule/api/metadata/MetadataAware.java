/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.metadata;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.metadata.api.model.MetadataType;

import java.util.List;
import java.util.Optional;

public interface MetadataAware
{

    Optional<List<MetadataKey>> getMetadataKeys(MuleEvent event) throws MuleException;

    Result<MetadataType> getContentMetadata(MuleEvent event, MetadataKey key) throws MuleException;

    Result<MetadataType> getOutputMetadata(MuleEvent event, MetadataKey key) throws MuleException;

    Result<OperationMetadataDescriptor> getMetadata();

    Result<OperationMetadataDescriptor> getMetadata(MuleEvent event, MetadataKey key);
}

