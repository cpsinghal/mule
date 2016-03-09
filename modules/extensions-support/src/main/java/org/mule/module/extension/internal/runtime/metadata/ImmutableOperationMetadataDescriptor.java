/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.metadata;

import org.mule.api.metadata.OperationMetadataDescriptor;
import org.mule.api.metadata.ParameterMetadataDescriptor;

import java.util.List;
import java.util.Optional;

public final class ImmutableOperationMetadataDescriptor implements OperationMetadataDescriptor
{
    private final String name;

    public ImmutableOperationMetadataDescriptor(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public List<ParameterMetadataDescriptor> getParametersMetadata()
    {
        return null;
    }

    @Override
    public Optional<ParameterMetadataDescriptor> getOutputMetadata()
    {
        return null;
    }
}
