/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.metadata;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.context.MuleContextAware;
import org.mule.api.processor.MessageProcessor;
import org.mule.construct.Flow;
import org.mule.extension.api.metadata.MetadataResolvingException;
import org.mule.metadata.api.model.MetadataType;
import org.mule.util.metadata.ResultFactory;

import java.util.List;
import java.util.Optional;

public class MuleMetadataManager implements MetadataManager, MuleContextAware
{

    public static final String MANAGER_REGISTRY_ID = "core.metadata.manager.1";
    private static final String OPERATION_NOT_METADATA_AWARE = "Operation is not MetadataAware, no information available";

    private MuleContext muleContext;

    @Override
    public Result<List<MetadataKey>> getMetadataKeys(Object event, ProcessorId processorId)
    {
        MessageProcessor messageProcessor = findMessageProcessor(processorId);
        if (!MetadataAware.class.isAssignableFrom(messageProcessor.getClass()))
        {
            return ResultFactory.failure(Optional.empty(), OPERATION_NOT_METADATA_AWARE, FailureType.RESOURCE_UNAVAILABLE, Optional.empty());
        }

        try
        {
            return ((MetadataAware)messageProcessor).getMetadataKeys((MuleEvent) event);
        }
        catch (Exception e)
        {
            return ResultFactory.failure(Optional.empty(), "An exception occurred while resolving Operation MetadataKeys", e);
        }
    }

    @Override
    public Result<MetadataType> getContentMetadata(Object event, ProcessorId processorId, MetadataKey key)
    {
        MessageProcessor messageProcessor = findMessageProcessor(processorId);
        if (!MetadataAware.class.isAssignableFrom(messageProcessor.getClass()))
        {
            return ResultFactory.failure(Optional.empty(), OPERATION_NOT_METADATA_AWARE, FailureType.RESOURCE_UNAVAILABLE, Optional.empty());
        }

        try
        {
            return ((MetadataAware)messageProcessor).getContentMetadata((MuleEvent) event, key);
        }

        catch (Exception e)
        {
            return ResultFactory.failure(Optional.empty(), "An exception occurred while resolving Content metadata", e);
        }
    }

    @Override
    public Result<MetadataType> getOutputMetadata(Object event, ProcessorId processorId, MetadataKey key)
    {
        MessageProcessor messageProcessor = findMessageProcessor(processorId);
        if (!MetadataAware.class.isAssignableFrom(messageProcessor.getClass()))
        {
            return ResultFactory.failure(Optional.empty(), OPERATION_NOT_METADATA_AWARE, FailureType.RESOURCE_UNAVAILABLE, Optional.empty());
        }

        try
        {
            return ((MetadataAware)messageProcessor).getOutputMetadata((MuleEvent) event, key);
        }
        catch (Exception e)
        {
            return ResultFactory.failure(Optional.empty(), "An exception occurred while resolving Output metadata", e);
        }
    }

    @Override
    public Result<OperationMetadataDescriptor> getOperationMetadata(Object event, ProcessorId processorId, MetadataKey key)
    {
        MessageProcessor messageProcessor = findMessageProcessor(processorId);
        if (!MetadataAware.class.isAssignableFrom(messageProcessor.getClass()))
        {
            return ResultFactory.failure(Optional.empty(), OPERATION_NOT_METADATA_AWARE, FailureType.RESOURCE_UNAVAILABLE, Optional.empty());
        }

        try
        {
            return ((MetadataAware)messageProcessor).getMetadata((MuleEvent) event, key);
        }
        catch (Exception e)
        {
            return ResultFactory.failure(Optional.empty(), "An exception occurred while resolving Operation " + processorId + " metadata", e);
        }
    }

    @Override
    public Result<OperationMetadataDescriptor> getOperationMetadata(ProcessorId processorId)
    {
        MessageProcessor messageProcessor = findMessageProcessor(processorId);
        if (!MetadataAware.class.isAssignableFrom(messageProcessor.getClass()))
        {
            return ResultFactory.failure(Optional.empty(), OPERATION_NOT_METADATA_AWARE, FailureType.RESOURCE_UNAVAILABLE, Optional.empty());
        }
        try
        {
            return ((MetadataAware)messageProcessor).getMetadata();
        }
        catch (Exception e)
        {
            return ResultFactory.failure(Optional.empty(), "An exception occurred while resolving Operation "+ processorId + " metadata", e);
        }
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    private MessageProcessor findMessageProcessor(ProcessorId processorId)
    {
        Flow flow = (Flow) muleContext.getRegistry().lookupFlowConstruct(processorId.getFlowName());
        return flow.getMessageProcessors().get(processorId.getProcessorIndex());
    }
}
