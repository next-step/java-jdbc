package camp.nextstep.controller;

import camp.nextstep.domain.User;
import camp.nextstep.service.TxUserService;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Controller;
import com.interface21.web.bind.annotation.RequestMapping;
import com.interface21.web.bind.annotation.RequestMethod;
import com.interface21.webmvc.servlet.ModelAndView;
import com.interface21.webmvc.servlet.view.JspView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final TxUserService userService;

    @Autowired
    public LoginController(final TxUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView view(final HttpServletRequest request, final HttpServletResponse response) {
        return UserSession.getUserFrom(request.getSession())
            .map(user -> {
                log.info("logged in {}", user.getAccount());
                return redirect("/index.jsp");
            })
            .orElse(new ModelAndView(new JspView("/login.jsp")));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(final HttpServletRequest request, final HttpServletResponse response) {
        if (UserSession.isLoggedIn(request.getSession())) {
            return redirect("/index.jsp");
        }

        final var user = userService.findByAccount(request.getParameter("account"));
        if (user == null) {
            return redirect("/401.jsp");
        }

        log.info("User : {}", user);
        return login(request, user);
    }

    private ModelAndView login(final HttpServletRequest request, final User user) {
        if (user.checkPassword(request.getParameter("password"))) {
            final var session = request.getSession();
            session.setAttribute(UserSession.SESSION_KEY, user);
            return redirect("/index.jsp");
        } else {
            return redirect("/401.jsp");
        }
    }

    private ModelAndView redirect(final String path) {
        return new ModelAndView(new JspView(JspView.REDIRECT_PREFIX + path));
    }
}
