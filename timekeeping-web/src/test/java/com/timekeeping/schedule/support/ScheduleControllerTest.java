package com.timekeeping.schedule.support;

import com.timekeeping.NonSecuredWebTestConfig;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @Transactional
    public void shouldSaveNewSchedule() throws Exception {
        String scheduleJson = "{\"id\":2," +
                "\"date\":{" +
                    "\"year\":2016," +
                    "\"month\":\"MARCH\"," +
                    "\"dayOfMonth\":8," +
                    "\"dayOfWeek\":\"TUESDAY\"," +
                    "\"era\":\"CE\"," +
                    "\"dayOfYear\":68," +
                    "\"leapYear\":true," +
                    "\"monthValue\":3," +
                    "\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}}," +
                "\"shop\":{" +
                    "\"id\":1," +
                    "\"name\":\"Shop1\"," +
                    "\"employees\":null," +
                    "\"location\":{\"city\":\"City1\",\"street\":\"Street1\"}}," +
                "\"version\":1," +
                "\"items\":[" +
                    "{\"employee\":" +
                        "{\"id\":2," +
                        "\"firstName\":\"Name2\"," +
                        "\"lastName\":\"Surname2\"," +
                        "\"position\":{\"id\":2,\"name\":\"Admin\",\"department\":\"Managenement\"}," +
                        "\"active\":true," +
                        "\"employment\":\"FULL_TIME\"}," +
                    "\"startTime\":720," +
                    "\"duration\":780," +
                    "\"type\":\"WORK\"}," +
                    "{\"employee\":" +
                        "{\"id\":1," +
                        "\"firstName\":\"Name1\"," +
                        "\"lastName\":\"Surname1\"," +
                        "\"position\":{\"id\":1,\"name\":\"Manager\",\"department\":\"Managenement\"}," +
                        "\"active\":true," +
                        "\"employment\":\"FULL_TIME\"}," +
                    "\"startTime\":720," +
                    "\"duration\":240," +
                    "\"type\":\"WORK\"}," +
                    "{\"employee\":" +
                        "{\"id\":3," +
                        "\"firstName\":\"Name3\"," +
                        "\"lastName\":\"Surname3\"," +
                        "\"position\":{\"id\":3,\"name\":\"Saler\",\"department\":\"Sales\"}," +
                        "\"active\":true," +
                        "\"employment\":\"PART_TIME\"}," +
                    "\"startTime\":720," +
                    "\"duration\":900," +
                    "\"type\":\"WORK\"}]}";
        mvc.perform(post("/api/schedule").content(scheduleJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void shouldUpdateExistingSchedule() throws Exception {
        String scheduleJson = "{\"id\":2," +
                "\"date\":{" +
                    "\"year\":2016," +
                    "\"month\":\"MARCH\"," +
                    "\"dayOfMonth\":1," +
                    "\"dayOfWeek\":\"TUESDAY\"," +
                    "\"era\":\"CE\"," +
                    "\"dayOfYear\":61," +
                    "\"leapYear\":true," +
                    "\"monthValue\":3," +
                    "\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}}," +
                "\"shop\":{" +
                    "\"id\":1," +
                    "\"name\":\"Shop1\"," +
                    "\"employees\":null," +
                    "\"location\":{\"city\":\"City1\",\"street\":\"Street1\"}}," +
                "\"version\":1," +
                "\"items\":[" +
                    "{\"employee\":" +
                        "{\"id\":3," +
                        "\"firstName\":\"Name3\"," +
                        "\"lastName\":\"Surname3\"," +
                        "\"position\":{\"id\":3,\"name\":\"Saler\",\"department\":\"Sales\"}," +
                        "\"active\":true," +
                        "\"employment\":\"PART_TIME\"}," +
                    "\"startTime\":600," +
                    "\"duration\":480," +
                    "\"type\":\"WORK\"}]}";

        mvc.perform(post("/api/schedule").content(scheduleJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"version\": 1 }"));
    }

    @Test
    @Transactional
    public void concurrentSaveShouldRaiseException() throws Exception {
        String scheduleJson = "{\"id\":2," +
                "\"date\":{" +
                    "\"year\":2016," +
                    "\"month\":\"MARCH\"," +
                    "\"dayOfMonth\":1," +
                    "\"dayOfWeek\":\"TUESDAY\"," +
                    "\"era\":\"CE\"," +
                    "\"dayOfYear\":61," +
                    "\"leapYear\":true," +
                    "\"monthValue\":3," +
                    "\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}}," +
                "\"shop\":{" +
                    "\"id\":1," +
                    "\"name\":\"Shop1\"," +
                    "\"employees\":null," +
                    "\"location\":{\"city\":\"City1\",\"street\":\"Street1\"}}," +
                "\"version\":0," +
                "\"items\":[" +
                    "{\"employee\":" +
                        "{\"id\":3," +
                        "\"firstName\":\"Name3\"," +
                        "\"lastName\":\"Surname3\"," +
                        "\"position\":{\"id\":3,\"name\":\"Saler\",\"department\":\"Sales\"}," +
                        "\"active\":true," +
                        "\"employment\":\"PART_TIME\"}," +
                    "\"startTime\":600," +
                    "\"duration\":480," +
                    "\"type\":\"WORK\"}]}";
        mvc.perform(post("/api/schedule").content(scheduleJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is5xxServerError());
    }

}
