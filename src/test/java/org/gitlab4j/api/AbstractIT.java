package org.gitlab4j.api;

import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractIT {


	@BeforeAll
	public static void testSetup() {
		System.out.println("AbstractIT#testSetup");
	}

}
