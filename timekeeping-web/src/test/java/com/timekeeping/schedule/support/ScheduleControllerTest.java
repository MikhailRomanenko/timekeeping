package com.timekeeping.schedule.support;

import com.timekeeping.TimekeepingWebApplication;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TimekeepingWebApplication.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class ScheduleControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Ignore
    @Test
    @WithUserDetails("user1")
    public void getExistingScheduleShouldReturnRightJsonView() throws Exception {
        mvc.perform(get("/api/schedule/1/2016-03-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shop.id").value(1))
                .andExpect(jsonPath("$.items", IsCollectionWithSize.hasSize(2)))
                .andExpect(jsonPath("$.items[0].department").value("Sales"))
                .andExpect(jsonPath("$.items[0].employees").value(1))
                .andExpect(jsonPath("$.items[0].employees[0].employee.id").value(3))
                .andExpect(jsonPath("$.items[0].employees[0].startTime").value(720));
    }

    @Test
    @WithUserDetails("user1")
    public void getNotExistingScheduleShouldReturnRightJsonView() throws Exception {
        mvc.perform(get("/api/schedule/1/2016-03-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shop.id").value(1))
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.version").value(0));
    }
}
