package org.amidukr.software.vnf.server;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dmytro Brazhnyk on 6/18/2017.
 */
@SuppressWarnings("serial")
public class ClassLoaderResourceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try(InputStream input = classLoader.getResourceAsStream("html/" + req.getPathInfo() + ".html")) {
            if(input == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            IOUtils.copy(input, resp.getOutputStream());
        }

    }
}
