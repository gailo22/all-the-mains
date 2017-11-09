package com.gailo22;

import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

public class HelloTest {

	@Test
	public void test() {
		Assert.assertThat(1, is(1));
	}
}
