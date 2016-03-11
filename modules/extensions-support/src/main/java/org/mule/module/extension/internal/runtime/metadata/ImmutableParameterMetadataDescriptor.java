/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.metadata;

import org.mule.api.metadata.ParameterMetadataDescriptor;
import org.mule.metadata.api.model.MetadataType;

public final class ImmutableParameterMetadataDescriptor implements ParameterMetadataDescriptor
{

    private final String name;
    private final MetadataType type;
    private final boolean hasDynamicType;

    public ImmutableParameterMetadataDescriptor(String name, MetadataType type, boolean hasDynamicType)
    {
        this.name = name;
        this.type = type;
        this.hasDynamicType = hasDynamicType;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean hasDynamicType()
    {
        return hasDynamicType;
    }

    @Override
    public MetadataType getType()
    {
        return type;
    }
}
