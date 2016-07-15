<%-- 
    Document   : results
    Created on : Jul 2, 2016, 12:39:55 AM
    Author     : joao
--%>
<%@page import="java.util.List"%>
<%@page import="model.Project"%>
<%
    Project project = (Project) request.getAttribute("project");
    List<Double> aacs = (List<Double>) request.getAttribute("aacs");
    List<Integer> qts = (List<Integer>) request.getAttribute("qtsCommit");
    List<String> authors = (List<String>) request.getAttribute("authors");
    int pre = (Integer) request.getAttribute("totPreProcessing");
    if (project != null) {
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Title</title>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
    </head>
    <body>
        <div>
            <nav class="navbar navbar-inverse navbar-fixed-top">
                <div class="container">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand" href="#">Project</a>
                    </div>
                    <div id="navbar" class="collapse navbar-collapse">
                        <ul class="nav navbar-nav">
                            <li><a href="#">Analisar</a></li>
                            <li class="active"><a href="#about">Resultado</a></li>
                            <li><a href="#contact">Sobre</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </div>
        <div class="container margn-top">
            <div class="row margn-top">
                <div class="col-sm-6">
                    <h3><%=project.getName()%> - Resultado</h3>
                    <p><strong>Total de Commits do Projeto: </strong><%=request.getAttribute("totCommits")%></p>
                    <p><strong>Total de Commits do Processados: </strong><%=pre%></p>
                    <p><strong>Total de Arquivos Processados: </strong><%=request.getAttribute("totFiles")%></p>

                </div>
                <div class="col-sm-2">
                    <br/>
                    <br/>
                    <p><a href="#" class="btn btn-info btn-block">Alterar Parâmetros</a></p>
                    <p><a href="Controller?action=downloadARFF" class="btn btn-primary btn-block">Download ARFF</a></p>
                    <p><a href="Controller?action=downloadRules" class="btn btn-success btn-block">Download Rules</a></p>
                </div>
                <div class="col-sm-4"></div>
            </div>
            <hr/>
            <div class="row">
                <div class="col-sm-6">
                    <div id="chart_aacs"></div>
                </div>
                <div class="col-sm-6">
                    <div id="chart_devs"></div>
                </div>
            </div>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            google.charts.load('current', {'packages': ['corechart']});
            google.charts.setOnLoadCallback(drawChartAAC);

            function drawChartAAC() {
                var data = google.visualization.arrayToDataTable([
                    ['Commit Processado', 'AAC'],
            <%
                      int size = aacs.size();
                      double sumAcc = 0;
                      for (int i = 0; i < size - 1; i++) {
                          sumAcc += aacs.get(i);
                          out.println("[" + (i + 1) + "," + sumAcc + "],");
                      }
                      out.println("[" + (size) + "," + (sumAcc + aacs.get(size - 1)) + "],");
            %>
                ]);

                var options = {
                    title: 'Complexidade Ciclomática de McCabe',
                    height: 300
                };

                // Instantiate and draw our chart, passing in some options.
                var chart = new google.visualization.AreaChart(document.getElementById('chart_aacs'));
                chart.draw(data, options);
                
                drawChartDevs();
            }
            
            function drawChartDevs() {
                var data = google.visualization.arrayToDataTable([
                    ['Desenvolvedor', 'Qtd'],
                    <%
                      int sizeauthor = authors.size();
                      for (int i = 0; i < sizeauthor; i++) {
                          out.println("[\'Desenv. "+ (i+1) + "\'," + qts.get(i) + "],");
                      }
                    %>
                ]);
                
                var options = {
                    title: 'Maiores contribuidores',
                    height: 300
                };
                
                var chart = new google.visualization.ColumnChart(document.getElementById('chart_devs'));
                chart.draw(data, options);
            }
        </script>
    </body>
</html>
<%} else {
        response.sendRedirect("index.html");
    }
%>