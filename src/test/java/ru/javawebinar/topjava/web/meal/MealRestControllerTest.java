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
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

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
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Meal meal = JsonUtil.readValue(result.getResponse().getContentAsString(), Meal.class);
        Assertions.assertEquals(meal, MealTestData.meal7);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<MealTo> meals = JsonUtil.readValues(result.getResponse().getContentAsString(), MealTo.class);
        List<MealTo> expectedValue = MealsUtil.getTos(MealTestData.meals, SecurityUtil.authUserCaloriesPerDay());
        Assertions.assertEquals(meals, expectedValue);
    }

    @Test
    void restCreate() throws Exception {
        String json = "{\"dateTime\":\"2020-01-10T10:11:00\",\"description\":\"Созданный ужин\",\"calories\":300,\"user\":null}";
        perform(MockMvcRequestBuilders.post("/rest/meals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated());
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals/100011"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Meal meal = JsonUtil.readValue(result.getResponse().getContentAsString(), Meal.class);
        Meal expected = MealTestData.getNew();
        expected.setId(100011);
        Assertions.assertEquals(meal, expected);
    }

    @Test
    void update() throws Exception {
        String json = "{\"id\":\"100002\",\"dateTime\":\"2020-07-30T10:02:00\",\"description\":\"Обновленный завтрак\",\"calories\":200}";
        perform(MockMvcRequestBuilders.put("/rest/meals/100002").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isNoContent());
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals/100002"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Meal meal = JsonUtil.readValue(result.getResponse().getContentAsString(), Meal.class);
        Meal expected = MealTestData.getUpdated();
        Assertions.assertEquals(meal, expected);
    }

    @Test
    void getBetween() throws Exception {
        MvcResult result = perform(MockMvcRequestBuilders.get("/rest/meals//filter?endDate=2020-01-30&endTime=22:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<MealTo> meals = JsonUtil.readValues(result.getResponse().getContentAsString(), MealTo.class);
        List<Meal> mealsExpected = Arrays.asList(MealTestData.meal3, MealTestData.meal2, MealTestData.meal1);
        List<MealTo> expectedValue = MealsUtil.getTos(mealsExpected, SecurityUtil.authUserCaloriesPerDay());
        Assertions.assertEquals(meals, expectedValue);
    }
}