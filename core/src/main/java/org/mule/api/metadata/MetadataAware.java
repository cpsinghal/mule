/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.metadata;

import org.mule.api.MuleEvent;
import org.mule.extension.api.metadata.MetadataResolvingException;
import org.mule.metadata.api.model.MetadataType;

import java.util.List;

public interface MetadataAware
{

    Result<List<MetadataKey>> getMetadataKeys(MuleEvent event) throws MetadataResolvingException;

    Result<MetadataType> getContentMetadata(MuleEvent event, MetadataKey key) throws MetadataResolvingException;

    Result<MetadataType> getOutputMetadata(MuleEvent event, MetadataKey key) throws MetadataResolvingException;

    Result<OperationMetadataDescriptor> getMetadata() throws MetadataResolvingException;

    Result<OperationMetadataDescriptor> getMetadata(MuleEvent event, MetadataKey key) throws MetadataResolvingException;
}

