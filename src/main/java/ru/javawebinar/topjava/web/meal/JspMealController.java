package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;
@RequestMapping("/meals")
@Controller
public class JspMealController {

    @Autowired
    MealService service;

    @PostMapping("/set")
    public String setMeal(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (!StringUtils.hasText(request.getParameter("id"))) {
            service.create(meal, authUserId());
        } else {
            meal.setId(getId(request));
            service.update(meal, authUserId());
        }
        return "redirect:meals";
    }

    @PostMapping("/delete")
    public String deleteMeal(HttpServletRequest request) {
        service.delete(getId(request), authUserId());
        return "redirect:/meals";
    }

    @PostMapping("/update")
    public String updateMeal(HttpServletRequest request, Model model) {
        Meal meal = service.get(getId(request), authUserId());
        model.addAttribute("meal", meal);
        request.setAttribute("meal", meal);
        request.setAttribute("action", "create");
        return "mealForm";
    }

    @PostMapping("/create")
    public String createMeal() {
        return "mealForm";
    }

    @GetMapping()
    public String getMeals(Model model) {
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(authUserId()), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    /*
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String action = request.getParameter("action");

            switch (action == null ? "all" : action) {
                case "delete" -> {
                    int id = getId(request);
                    mealController.delete(id);
                    response.sendRedirect("meals");
                }
                case "create", "update" -> {
                    final Meal meal = "create".equals(action) ?
                            new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                            mealController.get(getId(request));
                    request.setAttribute("meal", meal);
                    request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                }
                case "filter" -> {
                    LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
                    LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
                    LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
                    LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
                    request.setAttribute("meals", mealController.getBetween(startDate, startTime, endDate, endTime));
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                }
                default -> {
                    request.setAttribute("meals", mealController.getAll());
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                }
            }
        }*/
    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
