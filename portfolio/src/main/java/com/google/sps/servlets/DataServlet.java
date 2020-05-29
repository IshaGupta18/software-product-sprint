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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   *
   */
  private static final long serialVersionUID = 5770012060147035495L;

  public PreparedQuery getStoredComments(){
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    return results;
  }

  public ArrayList<String> convertDataToList(){
    ArrayList<String> commentsList = new ArrayList<String>();
    PreparedQuery commentsDatastore = getStoredComments();
    for (Entity entity : commentsDatastore.asIterable()){
      String comment = (String) entity.getProperty("content");
      commentsList.add(comment);
    }
    return commentsList;
  }

  public String convertToJSON(String content,String name,String mood){
    String json = "{";
    json += "\"content\": ";
    json += "\"" + content + "\"";
    json += ", ";
    json += "\"name\": ";
    json += "\"" + name + "\"";
    json += ", ";
    json += "\"mood\": ";
    json += "\"" + mood + "\"";
    json += "}";
    return json;
  }

  public String handleHTMLInjection(String content){
    content.replace("<", "&lt;");
    content.replace(">", "&gt;");
    return content;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    String jsonArray = new Gson().toJson(convertDataToList());
    response.getWriter().println(jsonArray);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = handleHTMLInjection(request.getParameter("comment-content"));

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", comment);
    commentEntity.setProperty("timestamp", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }
}
