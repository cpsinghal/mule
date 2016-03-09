/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.metadata;

import org.mule.api.metadata.FailureType;
import org.mule.api.metadata.MetadataDescriptor;
import org.mule.api.metadata.MetadataException;
import org.mule.api.metadata.Result;

public abstract class ResultFactory
{
    public static <T extends MetadataDescriptor> Result<T> success(T metadataDescriptor)
    {
        return null;
    }
    public static <T extends MetadataDescriptor> Result<T> success(T metadataDescriptor, String message)
    {
        return null;
    }
    public static <T extends MetadataDescriptor> Result<T> success(T metadataDescriptor, String message, FailureType failure)
    {
        return null;
    }
    public static <T extends MetadataDescriptor> Result<T> success(T metadataDescriptor, String message, FailureType failure, MetadataException e)
    {
        return new Result<T>(){
            @Override
            public T get()
            {
                return null;
            }

            @Override
            public Status getStatus()
            {
                return null;
            }

            @Override
            public String getMessage()
            {
                return null;
            }

            @Override
            public FailureType getFailureType()
            {
                return null;
            }

            @Override
            public String getStacktrace()
            {
                return null;
            }
        };
    }
}
