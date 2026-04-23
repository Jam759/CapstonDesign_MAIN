package com.Hoseo.CapstoneDesign;

import com.Hoseo.CapstoneDesign.notification.listener.NotificationQueueListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CapstoneDesignApplicationTests {

	@MockBean
	private NotificationQueueListener notificationQueueListener;

	@Test
	void contextLoads() {
	}

}
