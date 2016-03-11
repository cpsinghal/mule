/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.metadata;

import org.mule.api.metadata.FailureType;
import org.mule.api.metadata.MetadataKey;
import org.mule.api.metadata.OperationMetadataDescriptor;
import org.mule.api.metadata.ParameterMetadataDescriptor;
import org.mule.api.metadata.Result;
import org.mule.extension.api.introspection.OperationModel;
import org.mule.extension.api.introspection.ParameterModel;
import org.mule.extension.api.introspection.metadata.MetadataResolverFactory;
import org.mule.extension.api.metadata.MetadataContext;
import org.mule.extension.api.metadata.MetadataResolver;
import org.mule.extension.api.metadata.MetadataResolvingException;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.NullType;
import org.mule.util.metadata.ResultFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetadataMediator
{

    private static final String RETURN_PARAM_NAME = "output";
    private final OperationModel operationModel;

    public MetadataMediator(OperationModel operationModel)
    {
        this.operationModel = operationModel;
    }

    /**
     *
     * @param context
     * @return
     */
    public Result<List<MetadataKey>> getMetadataKeys(MetadataContext context)
    {
        try
        {
            Optional<MetadataResolverFactory> resolverFactory = operationModel.getMetaDataResolverFactory();
            if (!operationModel.getMetaDataKeyParameter().isPresent() || !resolverFactory.isPresent())
            {
                return ResultFactory.failure(Optional.empty(), "No Dynamic Keys available",
                                             FailureType.NO_DYNAMIC_TYPE_AVAILABLE, Optional.empty());
            }

            return ResultFactory.success(resolverFactory.get().getResolver().getMetadataKeys(context));
        }
        catch (Exception e)
        {
            return ResultFactory.failure(Optional.empty(), e.getMessage(), e);
        }
    }

    /**
     *
     * @return
     */
    public Result<OperationMetadataDescriptor> getMetadata()
    {
        List<ParameterMetadataDescriptor> paramDescriptors = new ArrayList<>(operationModel.getParameterModels().size());
        paramDescriptors.addAll(operationModel.getParameterModels().stream()
                                        .map(model -> new ImmutableParameterMetadataDescriptor(model.getName(), model.getType(), false))
                                        .collect(Collectors.toList()));

        ParameterMetadataDescriptor outputDescriptor = new ImmutableParameterMetadataDescriptor(RETURN_PARAM_NAME, operationModel.getReturnType(), false);

        return ResultFactory.success(new ImmutableOperationMetadataDescriptor(operationModel.getName(), paramDescriptors, outputDescriptor));
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public Result<OperationMetadataDescriptor> getMetadata(MetadataContext context, MetadataKey key)
    {
        if (!(operationModel.hasDynamicContentType() || operationModel.hasDynamicOutputType()))
        {
            return getMetadata();
        }

        List<ParameterMetadataDescriptor> paramDescriptors = new ArrayList<>(operationModel.getParameterModels().size());

        paramDescriptors.addAll(getStaticTypedParameters()
                                        .map(model -> new ImmutableParameterMetadataDescriptor(model.getName(), model.getType(), false))
                                        .collect(Collectors.toList()));

        getContentMetadataDescriptor(context, key).ifPresent(paramDescriptors::add);

        ParameterMetadataDescriptor outputDescriptor = getOutputMetadataDescriptor(context, key);

        // TODO fix result propagation when either content or output are dynamic and fail on fetch
        return ResultFactory.success(new ImmutableOperationMetadataDescriptor(operationModel.getName(), paramDescriptors, outputDescriptor));
    }

    private Stream<ParameterModel> getStaticTypedParameters()
    {
        if (!operationModel.getContentParameter().isPresent())
        {
            return operationModel.getParameterModels().stream();
        }

        return operationModel.getParameterModels().stream()
                                .filter(p -> !p.equals(operationModel.getContentParameter().get()));
    }

    private ParameterMetadataDescriptor getOutputMetadataDescriptor(MetadataContext context, MetadataKey key)
    {
        if (operationModel.hasDynamicOutputType())
        {
            Result<MetadataType> outputMetadata = getOutputMetadata(context, key);
            return new ImmutableParameterMetadataDescriptor(RETURN_PARAM_NAME, outputMetadata.get(), outputMetadata.isSucess());
        }

        return new ImmutableParameterMetadataDescriptor(RETURN_PARAM_NAME, operationModel.getReturnType(), false);
    }

    private Optional<ParameterMetadataDescriptor> getContentMetadataDescriptor(MetadataContext context, MetadataKey key)
    {
        if (!operationModel.hasDynamicContentType())
        {
            Optional.empty();
        }

        ParameterModel content = operationModel.getContentParameter().get();
        Result<MetadataType> contentMetadata = getContentMetadata(context, key);

        return Optional.of(new ImmutableParameterMetadataDescriptor(content.getName(), contentMetadata.get(), contentMetadata.isSucess()));
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public Result<MetadataType> getContentMetadata(MetadataContext context, MetadataKey key)
    {
        Optional<ParameterModel> contentParameter = operationModel.getContentParameter();
        if (!contentParameter.isPresent()){
            return ResultFactory.failure(Optional.<MetadataType>empty(), "No @Content parameter found",
                                         FailureType.NO_DYNAMIC_TYPE_AVAILABLE, Optional.empty());
        }

        if (operationModel.hasDynamicContentType())
        {
            return getDynamicMetadata(contentParameter.get().getType(), resolver -> resolver.getContentMetadata(context, key));
        }

        return ResultFactory.success(contentParameter.get().getType());
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public Result<MetadataType> getOutputMetadata(final MetadataContext context, final MetadataKey key)
    {
        if (operationModel.hasDynamicOutputType())
        {
            return getDynamicMetadata(operationModel.getReturnType(), resolver -> resolver.getOutputMetadata(context, key));
        }

        return ResultFactory.success(operationModel.getReturnType());
    }


    private Result<MetadataType> getDynamicMetadata(MetadataType javaType, MetadataDelegate delegate)
    {
        try
        {
            Optional<MetadataResolverFactory> resolverFactory = operationModel.getMetaDataResolverFactory();
            if (resolverFactory.isPresent())
            {
                MetadataType type = delegate.resolve(resolverFactory.get().getResolver());
                if(!(type instanceof NullType)){
                    return ResultFactory.success(type);
                }

            }
            return ResultFactory.failure(Optional.of(javaType), "No Dynamic Type available, defaulting to Java type",
                                         FailureType.NO_DYNAMIC_TYPE_AVAILABLE, Optional.empty());
        }
        catch (Exception e)
        {
            return ResultFactory.failure(Optional.of(javaType), e.getMessage(), e);
        }
    }

    private interface MetadataDelegate
    {
        MetadataType resolve(MetadataResolver resolver) throws MetadataResolvingException;

    }
}
