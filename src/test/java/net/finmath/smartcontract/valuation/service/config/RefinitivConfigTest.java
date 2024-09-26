package net.finmath.smartcontract.valuation.service.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefinitivConfigTest {
	private RefinitivConfig connectionDetails;

	@BeforeEach
	public void setUp() {
		connectionDetails = new RefinitivConfig();
	}

	@Test
	void testGetUser() {
		assertNull(connectionDetails.getUser());
	}

	@Test
	void testSetUser() {
		connectionDetails.setUser("testUser");
		assertEquals("testUser", connectionDetails.getUser());
	}

	@Test
	void testGetPassword() {
		assertNull(connectionDetails.getPassword());
	}

	@Test
	void testSetPassword() {
		connectionDetails.setPassword("testPassword");
		assertEquals("testPassword", connectionDetails.getPassword());
	}

	@Test
	void testGetClientId() {
		assertNull(connectionDetails.getClientId());
	}

	@Test
	void testSetClientId() {
		connectionDetails.setClientId("client123");
		assertEquals("client123", connectionDetails.getClientId());
	}

	@Test
	void testGetHostName() {
		assertNull(connectionDetails.getHostName());
	}

	@Test
	void testSetHostName() {
		connectionDetails.setHostName("localhost");
		assertEquals("localhost", connectionDetails.getHostName());
	}

	@Test
	void testGetPort() {
		assertEquals(0, connectionDetails.getPort());
	}

	@Test
	void testSetPort() {
		connectionDetails.setPort(8080);
		assertEquals(8080, connectionDetails.getPort());
	}

	@Test
	void testGetAuthUrl() {
		assertNull(connectionDetails.getAuthUrl());
	}

	@Test
	void testSetAuthUrl() {
		connectionDetails.setAuthUrl("http://auth.example.com");
		assertEquals("http://auth.example.com", connectionDetails.getAuthUrl());
	}

	@Test
	void testGetUseProxy() {
		assertNull(connectionDetails.getUseProxy());
	}

	@Test
	void testSetUseProxy() {
		connectionDetails.setUseProxy("true");
		assertEquals("true", connectionDetails.getUseProxy());
	}

	@Test
	void testGetProxyHost() {
		assertNull(connectionDetails.getProxyHost());
	}

	@Test
	void testSetProxyHost() {
		connectionDetails.setProxyHost("proxy.example.com");
		assertEquals("proxy.example.com", connectionDetails.getProxyHost());
	}

	@Test
	void testGetProxyPort() {
		assertEquals(0, connectionDetails.getProxyPort());
	}

	@Test
	void testSetProxyPort() {
		connectionDetails.setProxyPort(8081);
		assertEquals(8081, connectionDetails.getProxyPort());
	}

	@Test
	void testGetProxyUser() {
		assertNull(connectionDetails.getProxyUser());
	}

	@Test
	void testSetProxyUser() {
		connectionDetails.setProxyUser("proxyUser");
		assertEquals("proxyUser", connectionDetails.getProxyUser());
	}

	@Test
	void testGetProxyPassword() {
		assertNull(connectionDetails.getProxyPassword());
	}

	@Test
	void testSetProxyPassword() {
		connectionDetails.setProxyPassword("proxyPassword");
		assertEquals("proxyPassword", connectionDetails.getProxyPassword());
	}
}