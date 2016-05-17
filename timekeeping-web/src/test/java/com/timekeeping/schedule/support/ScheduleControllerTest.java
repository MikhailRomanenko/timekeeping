package com.timekeeping.schedule.support;

import com.timekeeping.NonSecuredWebTestConfig;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(NonSecuredWebTestConfig.class)
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
                .alwaysDo(print())
                .build();
    }

    @Test
    public void getExistingScheduleShouldReturnRightJsonView() throws Exception {
        mvc.perform(get("/api/schedule/1/2016-03-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.shop.id").value(1))
                .andExpect(jsonPath("$.shop.employees").doesNotExist())
                .andExpect(jsonPath("$.items", IsCollectionWithSize.hasSize(3)));
    }

    @Test
    public void getNotExistingScheduleShouldReturnRightJsonView() throws Exception {
        mvc.perform(get("/api/schedule/1/2016-03-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.shop.id").value(1))
                .andExpect(jsonPath("$.shop.employees").doesNotExist())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
