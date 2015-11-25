/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.model.property;

import org.mule.extension.api.introspection.ParameterModel;
import org.mule.module.extension.internal.introspection.ParameterGroup;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A custom model property that specifies that a certain group of {@link ParameterModel parameterModels}
 * are grouped. This grouping is transparent and is not reflected on the introspection
 * model because it's implementation specific.
 * <p/>
 * This property provides the necessary metadata for the runtime to handle those parameters accordingly.
 * <p/>
 * It gives access to a list of {@link ParameterGroup} instances through the {@link #getGroups()} method.
 * <p/>
 * This class is immutable
 *
 * @since 4.0
 */
public final class ParameterGroupModelProperty
{

    /**
     * A unique key that identifies this property type
     */
    public static final String KEY = ParameterGroupModelProperty.class.getName();

    private final List<ParameterGroup> groups;

    public ParameterGroupModelProperty(List<ParameterGroup> groups)
    {
        this.groups = ImmutableList.copyOf(groups);
    }

    public List<ParameterGroup> getGroups()
    {
        return groups;
    }
}