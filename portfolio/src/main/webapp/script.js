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

function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
function returnParagraphTag(content){
  let pElement = document.createElement("P");
  pElement.innerText = content;
  return pElement;
}
function fetchComments(){
  fetch('/data').then(response => response.json()).then((comments)=>{
    console.log(comments);
    comments.forEach((comment) => {
      document.getElementById("comments-container").appendChild(returnParagraphTag(comment));
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
  return new Date(dateList[0],dateList[1],dateList[2]);
}
function getDataRow(data,key){
  let row = [];
  row.push(key);
  row.push(data[key][0]);
  row.push(getDate(data[key][1]));
  row.push(getDate(data[key][2]));
  return row;
}
function drawChart(){
  fetch('/timeline').then(response => response.json()).then((timelineData) =>{
    const dataTable = new google.visualization.DataTable();
    addDataColumns(dataTable);
    let data = [];
    Object.keys(timelineData).forEach(function(key){
      data.push(getDataRow(timelineData,key));
    });
    dataTable.addRows(data);
    var options = {
      timeline: { colorByRowLabel: true }
    };
    var container = document.getElementById("timeline-chart-container");
    var chart = new google.visualization.Timeline(container);
    chart.draw(dataTable,options);
  });
}