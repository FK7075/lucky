package com.lucky.thymeleaf.core;

import com.lucky.web.core.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/1 5:20 下午
 */
public abstract class ThymeleafWrite {

    public static void write(Model model, String returnFile)  {
        try {
            HttpServletRequest request = model.getRequest();
            HttpServletResponse response = model.getResponse();
            request.setCharacterEncoding("utf8");
            response.setCharacterEncoding("utf8");
            TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(model.getServletContext());
            WebContext context = new WebContext(request, response, request.getServletContext());
            engine.process(returnFile, context, new PrintWriter(model.getOutputStream()));
        }catch (IOException e){
            throw new ThymeleafWriteException(e);
        }

    }
}

class ThymeleafWriteException extends RuntimeException{

    public ThymeleafWriteException(Throwable e){
        super("",e);
    }
}
