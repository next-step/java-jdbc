package camp.nextstep.controller;

import camp.nextstep.dto.UserDto;
import camp.nextstep.service.AppUserService;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Controller;
import com.interface21.web.bind.annotation.RequestMapping;
import com.interface21.web.bind.annotation.RequestMethod;
import com.interface21.webmvc.servlet.ModelAndView;
import com.interface21.webmvc.servlet.view.JspView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class RegisterController {

    private final AppUserService appUserService;

    @Autowired
    public RegisterController(final AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(final UserDto userDto) {
        final var user = userDto.toEntity();
        appUserService.save(user);
        return new ModelAndView(new JspView("redirect:/index.jsp"));
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView view(final HttpServletRequest request, final HttpServletResponse response) {
        return new ModelAndView(new JspView("/register.jsp"));
    }
}
