/**
 * Copyright (c) 2013 - 2015, Coveo Solutions Inc.
 */
package com.github.glo2003;

import static com.google.common.truth.Truth.*;

import org.junit.Test;

public class DummyTest
{
    @Test
    public void aDumbTest()
    {
        assertThat(true).isTrue();
        assert_().that(true).isTrue();
    }
}
