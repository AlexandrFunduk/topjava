package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
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

import javax.annotation.PostConstruct;
import java.time.LocalTime;
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
        perform(MockMvcRequestBuilders.get("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MealTestData.MEAL_MATCHER.contentJson(MealTestData.meal7));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete("/rest/meals/100008"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll() throws Exception {
        Iterable<MealTo> expectedValue = MealsUtil.getTos(MealTestData.meals, SecurityUtil.authUserCaloriesPerDay());
        perform(MockMvcRequestBuilders.get("/rest/meals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MealTestData.MEALTO_MATCHER.contentJson(expectedValue));
    }

    @Test
    void create() throws Exception {
        String json = "{\"dateTime\":\"2020-02-01T18:00:00\",\"description\":\"Созданный ужин\",\"calories\":300,\"user\":null}";
        perform(MockMvcRequestBuilders.post("/rest/meals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated());
        Meal expected = MealTestData.getNew();
        expected.setId(100011);
        perform(MockMvcRequestBuilders.get("/rest/meals/100011"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MealTestData.MEAL_MATCHER.contentJson(expected));

    }

    @Test
    void update() throws Exception {
        Meal expected = MealTestData.getUpdated();
        String json = "{\"id\":\"100002\",\"dateTime\":\"2020-01-30T10:02:00\",\"description\":\"Обновленный завтрак\",\"calories\":200}";
        perform(MockMvcRequestBuilders.put("/rest/meals/100002").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isNoContent());
        perform(MockMvcRequestBuilders.get("/rest/meals/100002"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MealTestData.MEAL_MATCHER.contentJson(expected));
    }

    @Test
    void getBetween() throws Exception {
        List<Meal> mealsFor31Jan = Arrays.asList(MealTestData.meal7, MealTestData.meal6, MealTestData.meal5, MealTestData.meal4);
        List<MealTo> expectedValue = MealsUtil.getFilteredTos(mealsFor31Jan, SecurityUtil.authUserCaloriesPerDay(), LocalTime.of(11, 0), LocalTime.of(22, 0));
        perform(MockMvcRequestBuilders.get("/rest/meals//filter?startDate=2020-01-31&endDate=2020-01-31&startTime=11:00&endTime=22:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MealTestData.MEALTO_MATCHER.contentJson(expectedValue));
    }
}