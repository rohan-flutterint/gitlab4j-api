package org.gitlab4j.api;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
public class UserApiIT {

	// Launch singleton container in version X
	// Inject minimal data
	// Verify precondition
	// Run test
	// Needed data :
	// - custom user
	// - group
	// - project

	@BeforeAll
	public static void testSetup() {
		System.out.println("UserApiIT#testSetup");
	}

	@Test
	public void test() {
		System.out.println("UserApiIT#test");
		assertTrue(true);
	}
}
