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

/**
 * Adds a random greeting to the page.
 */
google.charts.load('current', {'packages':['timeline']});

window.onload = function(e){
  this.drawChart();
}

function createDivElement(className=null){
  let divElement = document.createElement("div");
  if (className) divElement.className = className;
  return divElement;
}

function getBadgeColour(mood){
  if (mood === "Like") return "primary";

  else if (mood === "Love") return "danger";

  else if (mood === "Wow") return "success";

  else if (mood === "Haha") return "warning";

  else return "secondary";
}

function createNameMoodSectionComment(name, mood){
  let nameMoodDiv = createDivElement("card-header");
  let nameParagraph = createTextHTMLElement("P", name);
  if (mood) nameParagraph.appendChild(createTextHTMLElement("span", mood, "badge badge-pill badge-" + getBadgeColour(mood)));
  nameMoodDiv.appendChild(nameParagraph);
  return nameMoodDiv;
}

function createCommentCard(content, name=null, mood=null){
  let outerDiv = createDivElement("card");
  let contentDiv = createDivElement("card-body");
  contentDiv.appendChild(createTextHTMLElement("P", content, "card-text"));
  outerDiv.appendChild(contentDiv);
  if (name) outerDiv.appendChild(createNameMoodSectionComment(name, mood));
  document.getElementById("comments-container").appendChild(outerDiv);
}

function createTextHTMLElement(elementName, content, className=null){
  let element = document.createElement(elementName);
  if (className) element.className = className;
  element.innerText = content;
  return element;
}

function getCommentObject(comment){
  try {
    return JSON.parse(comment);
  } catch (error) {
    return null;
  }
}

function appendParagraphToDOM(element,container){
  document.getElementById(container).appendChild(createTextHTMLElement("P", element));
}

function fetchComments(){
  fetch('/data').then(response => response.json()).then((comments)=>{
    comments.forEach((comment) => {
      comment = getCommentObject(comment);
      if (comment!=null && comment["content"]) createCommentCard(comment["content"], comment["name"], comment["mood"]);
    });
  });
}

function addDataColumns(dataTable){
  dataTable.addColumn({ type: 'string', id: 'Organization' });
  dataTable.addColumn({ type: 'string', id: 'Position' });
  dataTable.addColumn({ type: 'date', id: 'Start' });
  dataTable.addColumn({ type: 'date', id: 'End' });
}

function getDate(date){
  let dateList = date.split("-");
  return new Date(dateList[0], dateList[1], dateList[2]);
}

function getDataRow(data,key){
  let row = [];
  row.push(key);
  row.push(data[key][0]);
  row.push(getDate(data[key][1]));
  row.push(getDate(data[key][2]));
  return row;
}

function getChartData(timelineData){
  let data = [];
  Object.keys(timelineData).forEach(function(key){
    data.push(getDataRow(timelineData, key));
  });
  return data;
}

function drawChart(){
  fetch('/timeline').then(response => response.json()).then((timelineData) =>{
    const dataTable = new google.visualization.DataTable();
    addDataColumns(dataTable);
    let data = getChartData(timelineData);
    dataTable.addRows(data);
    var options = {
      timeline: { colorByRowLabel: true }
    };
    var container = document.getElementById("timeline-chart-container");
    var chart = new google.visualization.Timeline(container);
    chart.draw(dataTable, options);
  });
}