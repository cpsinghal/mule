/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.util.FileUtils;

import java.io.File;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SmallTest
public class PluginClassLoaderTestCase extends AbstractMuleTestCase
{

    @Rule
    public TemporaryFolder classesFolder = new TemporaryFolder();

    @Test
    public void acceptsNullClassesFolder() throws Exception
    {
        new PluginClassLoader(null, new URL[0]);
    }

    @Test
    public void resolvesParentClass() throws Exception
    {
        ClassLoader parentClassLoader = getContextClassLoader();

        PluginClassLoader pluginClassLoader = new PluginClassLoader(parentClassLoader, new URL[0]);
        Class<?> aClass = pluginClassLoader.loadClass(this.getClass().getName());
        assertEquals(this.getClass(), aClass);
    }

    @Test
    public void addsURLsToClassLoaderUrls() throws Exception
    {
        URL expectedUrl = classesFolder.getRoot().toURI().toURL();
        PluginClassLoader pluginClassLoader = new PluginClassLoader(null, new URL[] {expectedUrl});
        assertThat(pluginClassLoader.getURLs().length, equalTo(1));
        assertThat(pluginClassLoader.getURLs()[0], equalTo(expectedUrl));
    }

    @Test
    public void resolvesClassFromClassesFolder() throws Exception
    {
        File orgFolder = classesFolder.newFolder("com");
        File dummyFolder = new File(orgFolder, "dummy");
        dummyFolder.mkdirs();
        File classFile = new File(dummyFolder, "DummyPlugin.class");
        FileUtils.copyStreamToFile(getClass().getResourceAsStream("/DummyPlugin.class"), classFile);

        PluginClassLoader pluginClassLoader = new PluginClassLoader(null, new URL[] {classesFolder.getRoot().toURI().toURL()});

        final String className = "com.dummy.DummyPlugin";
        Class<?> aClass = pluginClassLoader.loadClass(className);
        assertThat(aClass.getName(), equalTo(className));
    }

    private ClassLoader getContextClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
