package com.ticketdaata.ticketservice;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// A context-load smoke test shouldn't depend on live external infrastructure:
// switch to the in-memory messaging mode the app already supports instead of
// a real RabbitMQ broker, and mock out MongoClient so MongoConfig's eager
// connectivity probe (listCollectionNames().first()) never runs against a
// real (and here, absent) MongoDB instance.
@SpringBootTest
@TestPropertySource(properties = "messaging.mode=inmemory")
class TicketServiceApplicationTests {

	@MockitoBean
	private MongoClient mongoClient;

	@Test
	void contextLoads() {
	}

}
