package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import ru.javawebinar.topjava.AllActiveProfileResolver;

import javax.annotation.PostConstruct;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-mvc.xml",
        "classpath:spring/spring-db.xml"
})
@Transactional
@ActiveProfiles(resolver = AllActiveProfileResolver.class)
class MealRestControllerTest {

    private static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER = new CharacterEncodingFilter();

    static {
        CHARACTER_ENCODING_FILTER.setEncoding("UTF-8");
        CHARACTER_ENCODING_FILTER.setForceEncoding(true);
    }

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    private void postConstruct() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(CHARACTER_ENCODING_FILTER)
                .build();
    }

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @Test
    void get() throws Exception {
        String responseJson = "{\"id\":100008,\"dateTime\":\"2020-01-31T20:00:00\",\"description\":\"Ужин\",\"calories\":510,\"user\":null}";
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getContentAsString(), responseJson);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll() throws Exception {
        String responseJson = "[" +
                "{\"id\":100008,\"dateTime\":\"2020-01-31T20:00:00\",\"description\":\"Ужин\",\"calories\":510,\"excess\":true}," +
                "{\"id\":100007,\"dateTime\":\"2020-01-31T13:00:00\",\"description\":\"Обед\",\"calories\":1000,\"excess\":true}," +
                "{\"id\":100006,\"dateTime\":\"2020-01-31T10:00:00\",\"description\":\"Завтрак\",\"calories\":500,\"excess\":true}," +
                "{\"id\":100005,\"dateTime\":\"2020-01-31T00:00:00\",\"description\":\"Еда на граничное значение\",\"calories\":100,\"excess\":true}," +
                "{\"id\":100004,\"dateTime\":\"2020-01-30T20:00:00\",\"description\":\"Ужин\",\"calories\":500,\"excess\":false}," +
                "{\"id\":100003,\"dateTime\":\"2020-01-30T13:00:00\",\"description\":\"Обед\",\"calories\":1000,\"excess\":false}," +
                "{\"id\":100002,\"dateTime\":\"2020-01-30T10:00:00\",\"description\":\"Завтрак\",\"calories\":500,\"excess\":false}]";
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getContentAsString(), responseJson);
    }

    @Test
    void restCreate() throws Exception {
        String json = "{\"dateTime\":\"2020-01-10T10:11:00\",\"description\":\"Обед 2\",\"calories\":1111}";
        perform(MockMvcRequestBuilders.post("/rest/meals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        String responseJson = "{\"id\":100011,\"dateTime\":\"2020-01-10T10:11:00\",\"description\":\"Обед 2\",\"calories\":1111,\"user\":null}";
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals/100011"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getContentAsString(), responseJson);
    }

    @Test
    void update() throws Exception {
        String json = "{\"id\":\"100008\",\"dateTime\":\"2020-02-10T10:21:00\",\"description\":\"Обед 3\",\"calories\":1331}";
        perform(MockMvcRequestBuilders.put("/rest/meals/100008").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isNoContent());

        String responseJson = "{\"id\":100008,\"dateTime\":\"2020-02-10T10:21:00\",\"description\":\"Обед 3\",\"calories\":1331,\"user\":null}";
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getContentAsString(), responseJson);
    }

    @Test
    void getBetween() throws Exception {
        String responseJson = "[" +
                "{\"id\":100004,\"dateTime\":\"2020-01-30T20:00:00\",\"description\":\"Ужин\",\"calories\":500,\"excess\":false}," +
                "{\"id\":100003,\"dateTime\":\"2020-01-30T13:00:00\",\"description\":\"Обед\",\"calories\":1000,\"excess\":false}," +
                "{\"id\":100002,\"dateTime\":\"2020-01-30T10:00:00\",\"description\":\"Завтрак\",\"calories\":500,\"excess\":false}]";
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals//filter?endDate=2020-01-30&endTime=22:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getContentAsString(), responseJson);

    }
}