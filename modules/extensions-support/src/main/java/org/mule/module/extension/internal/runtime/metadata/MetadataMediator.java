/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.metadata;

import org.mule.api.metadata.MetadataKey;
import org.mule.api.metadata.OperationMetadataDescriptor;
import org.mule.api.metadata.ParameterMetadataDescriptor;
import org.mule.api.metadata.Result;
import org.mule.extension.api.introspection.OperationModel;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.extension.api.introspection.metadata.MetadataResolverFactory;
import org.mule.extension.api.metadata.MetadataContext;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.NullType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetadataMediator
{


    private final OperationModel operationModel;

    public MetadataMediator(OperationModel operationModel)
    {
        this.operationModel = operationModel;
    }

    public Optional<List<MetadataKey>> getMetadataKeys(MetadataContext context)
    {
        if (operationModel.getMetaDataKeyParameter().isPresent())
        {
            Optional<MetadataResolverFactory> resolverFactory = operationModel.getMetaDataResolverFactory();
            if (resolverFactory.isPresent())
            {
                return Optional.of(resolverFactory.get().createResolver().getMetadataKeys(context));
            }
        }

        return Optional.empty();
    }

    public Result<OperationMetadataDescriptor> getMetadata()
    {
        List<ParameterMetadataDescriptor> params = new ArrayList<>(operationModel.getParameterModels().size());
        Optional<ParameterModel> contentParameter = operationModel.getContentParameter();
        if (contentParameter.isPresent())
        {
            params.add(new ImmutableParameterMetadataDescriptor(contentParameter.get(), operationModel.hasDynamicContentType()));
        }

        for (ParameterModel parameterModel : operationModel.getParameterModels())
        {
            //TODO
        }

        return null;
    }

    public Result<OperationMetadataDescriptor> getMetadata(MetadataContext context, MetadataKey key)
    {
        return null;
    }

    public Result<MetadataType> getContentMetadata(MetadataContext context, MetadataKey key)
    {
        Optional<ParameterModel> contentParameter = operationModel.getContentParameter();
        if (contentParameter.isPresent())
        {
            Optional<MetadataResolverFactory> resolverFactory = operationModel.getMetaDataResolverFactory();
            if (resolverFactory.isPresent())
            {
                MetadataType dynamicMetadata = resolverFactory.get().createResolver().getContentMetadata(context, key);
                if(!(dynamicMetadata instanceof NullType)){
                    //return Optional.of(dynamicMetadata);
                }
            }
            //return Optional.of(contentParameter.get().getType());
        }

        //return Optional.empty();
        return null;
    }

    public Result<MetadataType> getOutputMetadata(MetadataContext context, MetadataKey key)
    {
        Optional<MetadataResolverFactory> resolverFactory = operationModel.getMetaDataResolverFactory();
        if (resolverFactory.isPresent())
        {
            MetadataType outputMetadata = resolverFactory.get().createResolver().getOutputMetadata(context, key);
            if(!(outputMetadata instanceof NullType)){
                //return Optional.of(outputMetadata);
            }
        }

        //return Optional.of(operationModel.getReturnType());
        return null;
    }
}
