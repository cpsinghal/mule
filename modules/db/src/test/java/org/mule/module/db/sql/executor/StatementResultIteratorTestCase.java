/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.sql.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mule.module.db.internal.domain.autogeneratedkey.NoAutoGeneratedKeyStrategy;
import org.mule.module.db.internal.domain.connection.DbConnection;
import org.mule.module.db.internal.domain.param.DefaultOutputQueryParam;
import org.mule.module.db.internal.domain.param.QueryParam;
import org.mule.module.db.internal.domain.query.QueryTemplate;
import org.mule.module.db.internal.domain.query.QueryType;
import org.mule.module.db.internal.domain.type.UnknownDbType;
import org.mule.module.db.internal.result.resultset.NullResultSetHandler;
import org.mule.module.db.internal.result.statement.OutputParamResult;
import org.mule.module.db.internal.result.statement.ResultSetResult;
import org.mule.module.db.internal.result.statement.StatementResultIterator;
import org.mule.module.db.internal.result.statement.UpdateCountResult;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

@SmallTest
public class StatementResultIteratorTestCase extends AbstractMuleTestCase
{

    private final CallableStatement statement = mock(CallableStatement.class);
    private final QueryTemplate queryTemplate = new QueryTemplate("dummy", QueryType.STORE_PROCEDURE_CALL, Collections.EMPTY_LIST);
    private final DbConnection connection = mock(DbConnection.class);
    private final StatementResultIterator iterator = new StatementResultIterator(connection, statement, queryTemplate, new NoAutoGeneratedKeyStrategy(), new NullResultSetHandler());

    @Before
    public void configureConnectionMetadata() throws Exception
    {
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metaData);
    }

    @Test
    public void detectHasResultSet() throws Exception
    {
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.getResultSet()).thenReturn(resultSet);
        boolean hasNext = iterator.hasNext();

        assertThat(hasNext, equalTo(true));
    }

    @Test
    public void detectHasUpdateCount() throws Exception
    {
        int updateCount = 10;
        when(statement.getUpdateCount()).thenReturn(updateCount);
        boolean hasNext = iterator.hasNext();

        assertThat(hasNext, equalTo(true));
    }

    @Test
    public void detectHasOutputParam() throws Exception
    {
        when(statement.getResultSet()).thenReturn(null);
        when(statement.getUpdateCount()).thenReturn(StatementResultIterator.NO_UPDATE_COUNT);

        DefaultOutputQueryParam param1 = new DefaultOutputQueryParam(0, UnknownDbType.getInstance(), "param1");
        List<QueryParam> params = new LinkedList<QueryParam>();
        params.add(param1);

        QueryTemplate queryTemplate = new QueryTemplate("dummy", QueryType.STORE_PROCEDURE_CALL, params);
        StatementResultIterator iterator = new StatementResultIterator(null, statement, queryTemplate, null, null);

        boolean hasNext = iterator.hasNext();

        assertThat(hasNext, equalTo(true));
    }

    @Test
    public void returnsResultSetInNextResult() throws Exception
    {
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.getResultSet()).thenReturn(resultSet);

        Object nextResult = iterator.next();

        assertThat(nextResult, is(instanceOf(ResultSetResult.class)));
        ResultSetResult resultSetResult = (ResultSetResult) nextResult;
        assertThat(resultSetResult.getName(), equalTo("resultSet1"));
        assertThat((ResultSet) resultSetResult.getResult(), equalTo(resultSet));
    }

    @Test
    public void returnsUpdateCountInNextResult() throws Exception
    {
        int updateCount = 10;
        when(statement.getUpdateCount()).thenReturn(updateCount);

        Object nextResult = iterator.next();

        assertThat(nextResult, is(instanceOf(UpdateCountResult.class)));
        UpdateCountResult updateCountResult = (UpdateCountResult) nextResult;
        assertThat(updateCountResult.getName(), equalTo("updateCount1"));
        assertThat((Integer) updateCountResult.getResult(), equalTo(updateCount));
    }

    @Test
    public void returnsOutputParamInNextResult() throws Exception
    {
        int paramValue = 7;
        when(statement.getResultSet()).thenReturn(null);
        when(statement.getUpdateCount()).thenReturn(StatementResultIterator.NO_UPDATE_COUNT);
        when(statement.getObject(1)).thenReturn(7);

        DefaultOutputQueryParam param1 = new DefaultOutputQueryParam(1, UnknownDbType.getInstance(), "param1");
        List<QueryParam> params = new LinkedList<QueryParam>();
        params.add(param1);

        QueryTemplate queryTemplate = new QueryTemplate("dummy", QueryType.STORE_PROCEDURE_CALL, params);
        StatementResultIterator iterator = new StatementResultIterator(null, statement, queryTemplate, null, null);

        Object nextResult = iterator.next();

        assertThat(nextResult, is(instanceOf(OutputParamResult.class)));
        OutputParamResult outputParamResult = (OutputParamResult) nextResult;
        assertThat(outputParamResult.getName(), equalTo("param1"));
        assertThat((Integer) outputParamResult.getResult(), equalTo(paramValue));
    }

    @Test
    public void processResultSetOnce() throws Exception
    {
        when(statement.getResultSet()).thenReturn(mock(ResultSet.class));

        iterator.next();
        iterator.next();

        InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).getResultSet();
        inOrder.verify(statement).getMoreResults();
    }

    @Test
    public void processUpdateCountOnce() throws Exception
    {
        when(statement.getUpdateCount()).thenReturn(10);

        iterator.next();
        iterator.next();

        InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).getUpdateCount();
        inOrder.verify(statement).getMoreResults();
    }

    @Test
    public void processOutputParamOnce() throws Exception
    {
        when(statement.getResultSet()).thenReturn(null);
        when(statement.getUpdateCount()).thenReturn(StatementResultIterator.NO_UPDATE_COUNT);
        when(statement.getObject(1)).thenReturn(7);

        DefaultOutputQueryParam param1 = new DefaultOutputQueryParam(1, UnknownDbType.getInstance(), "param1");
        DefaultOutputQueryParam param2 = new DefaultOutputQueryParam(2, UnknownDbType.getInstance(), "param2");
        List<QueryParam> params = new LinkedList<QueryParam>();
        params.add(param1);
        params.add(param2);

        QueryTemplate queryTemplate = new QueryTemplate("dummy", QueryType.STORE_PROCEDURE_CALL, params);
        StatementResultIterator iterator = new StatementResultIterator(connection, statement, queryTemplate, new NoAutoGeneratedKeyStrategy(), null);

        iterator.next();
        iterator.next();

        InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).getObject(1);
        inOrder.verify(statement).getObject(2);
    }

    @Test
    public void cachesHasNext() throws Exception
    {
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.getResultSet()).thenReturn(resultSet);
        iterator.hasNext();

        boolean hasNext = iterator.hasNext();

        assertThat(hasNext, equalTo(true));
        verify(statement).getResultSet();
    }

    @Test
    public void clearsCacheOnNextInvocation() throws Exception
    {
        ResultSet resultSet = mock(ResultSet.class);
        when(statement.getResultSet()).thenReturn(resultSet);
        iterator.hasNext();
        iterator.next();

        boolean hasNext = iterator.hasNext();

        assertThat(hasNext, equalTo(true));
        verify(statement, times(2)).getResultSet();
    }


    @Test(expected = NoSuchElementException.class)
    public void throwsExceptionWhenNoElementExist() throws Exception
    {
        when(statement.getUpdateCount()).thenReturn(StatementResultIterator.NO_UPDATE_COUNT);
        when(statement.getResultSet()).thenReturn(null);

        iterator.next();
    }
}
