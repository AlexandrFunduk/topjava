package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MealServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);
    private final MealService mealService = new MealServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            if (pathInfo.equalsIgnoreCase("/delete")) {
                Long id = Long.parseLong(request.getParameterMap().get("id")[0]);
                log.debug("Delete meal with id = {}", id);
                mealService.deleteById(id);
                response.sendRedirect(request.getContextPath() + "/meals");
                return;
            }
            if (pathInfo.equalsIgnoreCase("/edit")) {
                Long id = Long.parseLong(request.getParameterMap().get("id")[0]);
                log.debug("Forward to edit meal with id = {}", id);
                request.setAttribute("meal", mealService.findById(id));
                request.getRequestDispatcher("/updatemeal.jsp").forward(request, response);
                return;
            }
        }
        List<MealTo> mealTos = mealService.findAll();
        log.debug("Show all meals. Count = {}", mealTos.size());
        request.setAttribute("meals", mealTos);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        Meal meal = new Meal(dateTime, description, calories);
        String idParam = request.getParameter("id");
        if (idParam != null) {
            meal.setId(Long.parseLong(idParam));
        }
        mealService.save(meal);
        log.debug("Save meal. id = {} date = {} description = {} calories = {}", meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        response.sendRedirect(request.getContextPath() + "/meals");
    }
}
