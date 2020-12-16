package ru.javawebinar.topjava.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileUIController extends AbstractUserController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String profile() {
        return "profile";
    }

    @PostMapping
    public String updateProfile(@Valid UserTo userTo, BindingResult result, SessionStatus status, HttpServletRequest req) {
        if (result.hasErrors()) {
            return "profile";
        } else {
            try {
                super.update(userTo, SecurityUtil.authUserId());
                SecurityUtil.get().update(userTo);
                status.setComplete();
                return "redirect:/meals";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("email", "user.duplicateEmail");
                return "profile";
            }
        }
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("userTo", new UserTo());
        model.addAttribute("register", true);
        return "profile";
    }

    //  https://stackoverflow.com/questions/12107503/adding-error-message-to-spring-3-databinder-for-custom-object-fields
    @PostMapping("/register")
    public String saveRegister(@Valid UserTo userTo, BindingResult result, SessionStatus status, ModelMap model, HttpServletRequest req) {
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        } else {
            try {
                super.create(userTo);
                status.setComplete();
                return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("email", "user.duplicateEmail");
                model.addAttribute("register", true);
                return "profile";
            }
        }
    }
}