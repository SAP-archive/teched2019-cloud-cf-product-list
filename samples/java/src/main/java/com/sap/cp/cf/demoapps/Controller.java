package com.sap.cp.cf.demoapps;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
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
public class Controller extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
    private ProductService productService = new ProductService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productsJson;
        if (request.getParameter("name") != null) {
            String name = request.getParameter("name");
            productsJson = this.gson.toJson(productService.getProductByName(name));

        } else {
            productsJson = this.gson.toJson(productService.getProducts());
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(productsJson);
        out.flush();

    }
}
