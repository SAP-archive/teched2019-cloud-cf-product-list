package com.sap.cp.cf.demoapps;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class ApplicationTests {

	@ClassRule
	public static InjectVcapServiceRule server = new InjectVcapServiceRule("");
	
	@Test
	public void contextLoads() {
	}

}
