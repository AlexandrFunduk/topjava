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
    private static final Logger log = getLogger(MealServlet.class);
    private final MealService mealService = new MealServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            switch (pathInfo.toLowerCase()) {
                case "/delete":
                    delete(request, response);
                    return;
                case "/edit":
                    edit(request, response);
                    return;
                case "/create":
                    create(request, response);
                    return;
            }
        }
        List<MealTo> mealTos = mealService.findAll();
        log.debug("Show all meals. Count = {}", mealTos.size());
        request.setAttribute("meals", mealTos);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Forward to create new meal");
        request.setAttribute("formType", "Create");
        request.setAttribute("meal", null);
        request.getRequestDispatcher("/createupdatemeal.jsp").forward(request, response);
    }

    private void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = getId(request);
        log.debug("Forward to edit meal with id = {}", id);
        request.setAttribute("formType", "Edit");
        request.setAttribute("meal", mealService.findById(id));
        request.getRequestDispatcher("/createupdatemeal.jsp").forward(request, response);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = getId(request);
        log.debug("Delete meal with id = {}", id);
        mealService.deleteById(id);
        response.sendRedirect(request.getContextPath() + "/meals");
    }

    private Long getId(HttpServletRequest request) {
        String idParam = request.getParameter("id");
        return idParam == null || idParam.equals("") ? null : Long.parseLong(idParam);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        Meal meal = new Meal(dateTime, description, calories);
        Long id = getId(request);
        if (id != null) {
            meal.setId(id);
        }
        log.debug("Save meal. id = {} date = {} description = {} calories = {}", meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        mealService.save(meal);
        response.sendRedirect(request.getContextPath() + "/meals");
    }
}
