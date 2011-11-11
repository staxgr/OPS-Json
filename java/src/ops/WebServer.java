/**
*
* Copyright (C) 2011 Anton Gravestam.
*
* This file is part of OPS (Open Publish Subscribe).
*
* OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
*/
package ops;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.webapp.WebAppContext;

class WebServer {

    public static final String SERVER_PORT = "BACKEND_CONTAINER_PORT";

    private static Server server;
    private static Map<String, ServletHandler> servlets = new HashMap<String, ServletHandler>();
    private static HttpServlet menuServlet = new HttpServlet() {

        private static final long serialVersionUID = -9028369467594748110L;

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            PrintWriter output = beginHtmlDoc(response, null, null, "Menu@" + getHostname(), null);
            synchronized (WebServer.class) {
                output.append("<table border=1><tr><th>Context</th><th>Servlet name</th><th>Servlet mapping</th></tr>");
                for (Entry<String, ServletHandler> context : servlets.entrySet()) {
                    output.append("<tr><td><a href=\"" + context.getKey() + "\">" + context.getKey() + "</a></td><td></td><td></td></tr>");
                    for (ServletMapping mapping : context.getValue().getServletMappings())
                        for (String path : mapping.getPathSpecs()) {
                            path = path.endsWith("*") ? path.substring(0, path.length() - 1) : path;
                            if (!path.contains("*")) {
                                String fullPath = context.getKey() + path;
                                output.append("<tr><td></td><td><a href=\"" + fullPath + "\">" + mapping.getServletName() + "</a></td><td><a href=\"" + fullPath + "\">" + path
                                        + "</a></td></tr>");
                            }
                        }
                }
                output.append("</table>");
            }
            endHtmlDoc(output);
            output.close();
        }

    };

    static void init() throws Exception {
        if (server == null) {
            Integer webServerPort = 9090;//Integer.parseInt(System.getProperty(SERVER_PORT, "80"));
            server = new Server(webServerPort);
            server.start();
            addServlet("/menu", "/*", "Menu Servlet", menuServlet);
        }
    }

    public synchronized static void addWebApp(String contextPath, String warUrlString) throws Exception {
        if (servlets.containsKey(contextPath))
            throw new IllegalArgumentException("The path is already in use");
        init();
        WebAppContext webapp = new WebAppContext(server, warUrlString, contextPath);
        webapp.setParentLoaderPriority(true);
        webapp.start();        
        server.getHandler().start();
        servlets.put(contextPath, webapp.getServletHandler());
    }

    public synchronized static void addServlet(String contextPath, String servletMapping, String servletName, Servlet servlet) throws Exception {
        init();
        ServletHolder holder = new ServletHolder(servlet);
        holder.setName(servletName);
        ServletHandler handler = servlets.get(contextPath);
        if (handler != null)
            handler.addServletWithMapping(holder, servletMapping);
        else {
            Context context = new Context(server, contextPath, Context.SESSIONS);
            context.addServlet(holder, servletMapping);
            context.start();
            server.getHandler().start();
            servlets.put(contextPath, context.getServletHandler());
        }
    }

    public synchronized static void stopServer() throws Exception {
        if (server != null)
            server.stop();
    }

    public synchronized static Servlet getServlet(String contextPath, String servletName) {
        ServletHandler handler = servlets.get(contextPath);
        if (handler != null) {
            ServletHolder holder = handler.getServlet(servletName);
            if (holder != null)
                return holder.getServletInstance();
        }
        return null;
    }
    
    public synchronized static ServletHandler getServletHandler(String contextPath) {
        return servlets.get(contextPath);
    }

    public static void main(String[] args) throws Exception {
        init();
    }
    
    public static PrintWriter beginHtmlDoc(HttpServletResponse resp, String[] cssUrls, String[] jsUrls, String title, String onload) throws IOException {
        resp.setContentType("text/html");
        PrintWriter output = resp.getWriter();
        output.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\r\n");
        output.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
        output.append("<head>\r\n");
        if (cssUrls != null) {
            for (String cssUrl : cssUrls) {
                output.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(cssUrl).append("\" />\r\n");
            }
        }
        if (jsUrls != null) {
            for (String jsUrl : jsUrls) {
                output.append("<script type=\"text/javascript\" src=\"").append(jsUrl).append("\"></script>\r\n");
            }
        }
        if (title != null) {
            output.append("<title>").append(title).append("</title>\n");
        }
        output.append("</head><body");
        if (onload != null) {
            output.append(" onload=\"").append(onload).append("\"");
        }
        output.append(">\r\n");
        return output;
    }

    public static void endHtmlDoc(PrintWriter output) {
        output.append("</body></html>\r\n");
        output.flush();
    }
    
    public static String escapeHtml(String str) {
        return str == null ? null : str.replace("&", "&amp;").replace("<", "&lt;");
    }
    
    public static String getHostname() {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        } catch (java.net.UnknownHostException uhe) {
            return "<unknown host>";
        }
    }

}
