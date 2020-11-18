package com.baml.eops.csw_eureka_server;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


class EurekaServerTest {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	public void testEurekaStartup() throws Exception {
		testFolder.create();
		File logsDir = testFolder.newFolder();
		System.setProperty("LOGS_DIR", logsDir.getAbsolutePath());
		EurekaServer.main(new String[] {"--spring.profiles.active=test"});
	}
}
