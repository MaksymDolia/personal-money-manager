package me.dolia.pmm;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml", "classpath*:spring/dispatcher-servlet.xml"})
@ActiveProfiles("dev")
public class ApplicationTest {

  @Autowired
  private ApplicationContext context;

  @Test
  public void contextLoads() {
    assertNotNull(context);
  }
}
