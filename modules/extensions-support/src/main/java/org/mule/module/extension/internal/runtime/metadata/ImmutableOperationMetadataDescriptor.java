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

public final class ImmutableOperationMetadataDescriptor implements OperationMetadataDescriptor
{
    private final String name;
    private final List<ParameterMetadataDescriptor> parameters;
    private final ParameterMetadataDescriptor outputParameter;

    public ImmutableOperationMetadataDescriptor(String name, List<ParameterMetadataDescriptor> parameters,
                                                ParameterMetadataDescriptor outputParameter)
    {
        this.name = name;
        this.parameters = parameters;
        this.outputParameter = outputParameter;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public List<ParameterMetadataDescriptor> getParametersMetadata()
    {
        return this.parameters;
    }

    @Override
    public ParameterMetadataDescriptor getOutputMetadata()
    {
        return this.outputParameter;
    }
}
