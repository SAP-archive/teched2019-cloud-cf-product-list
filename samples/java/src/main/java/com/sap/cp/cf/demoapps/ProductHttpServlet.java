package com.sap.cp.cf.demoapps;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.HttpConstraint;

import com.google.gson.Gson;

@WebServlet({ "/products/*", "/productsByParam" })
// configure servlet to check against scope "$XSAPPNAME.read"
@ServletSecurity(@HttpConstraint(rolesAllowed = { "read" }))
public class ProductHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson GSON = new Gson();
    private static final ProductService PRODUCT_SERVICE = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String productsJson;
        if (request.getParameter("name") != null) {
            String name = request.getParameter("name");
            productsJson = GSON.toJson(PRODUCT_SERVICE.getProductByName(name));
        } else {
            productsJson = GSON.toJson(PRODUCT_SERVICE.getProducts());
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(productsJson);
        out.flush();
    }
}
