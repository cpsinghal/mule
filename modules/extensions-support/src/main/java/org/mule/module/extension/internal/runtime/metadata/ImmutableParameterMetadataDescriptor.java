/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.metadata;

import org.mule.api.metadata.ParameterMetadataDescriptor;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.metadata.api.model.MetadataType;

public final class ImmutableParameterMetadataDescriptor implements ParameterMetadataDescriptor
{

    private final String name;
    private final MetadataType type;
    private final boolean isDynamic;

    public ImmutableParameterMetadataDescriptor(ParameterModel param, boolean isDynamic)
    {
        this.name = param.getName();
        this.type = param.getType();
        this.isDynamic = isDynamic;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isDynamic()
    {
        return isDynamic;
    }

    @Override
    public MetadataType getType()
    {
        return type;
    }
}
