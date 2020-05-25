// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   *
   */
  private static final long serialVersionUID = 5770012060147035495L;
  private ArrayList<String> comments = new ArrayList<String>();

  public void populateCommentsDefault(ArrayList<String> Comments) {
    Comments.add("Quam lacus suspendisse faucibus.");
    Comments.add("Vitae et leo duis ut diam quam nulla porttitor.");
    Comments.add("Ut tristique et egestas quis ipsum. Et sollicitudin ac orci phasellus.");
    Comments.add("Accumsan in nisl nisi scelerisque. Eget magna fermentum iaculis eu non diam phasellus.");
    Comments.add("Augue ut lectus arcu bibendum at varius vel.");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    String jsonArray = new Gson().toJson(comments);
    response.getWriter().println(jsonArray);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment-content");
    comments.add(comment);
    response.sendRedirect("/index.html");
  }
}
