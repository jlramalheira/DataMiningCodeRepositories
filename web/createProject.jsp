<%-- 
    Document   : createProject
    Created on : Jul 2, 2016, 12:39:55 AM
    Author     : joao
--%>

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
                            <li class="active"><a href="#">Analisar</a></li>
                            <li><a href="Controller?action=results">Resultado</a></li>
                            <li><a href="#contact">Sobre</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </div>
        <div class="container margn-top">
            <div class="row margn-top">
                <form name="form" action="Project" method="POST" class="form-horizontal">
                    <div class="form-group">
                        <div class="col-sm-6">
                            <h4 class="text-center">Projeto</h4>

                            <div class="form-group">
                                <label class="col-sm-1 control-label">Projeto: </label>
                                <div class="col-sm-11">
                                    <input type="text" class="form-control" name="project" placeholder="Projeto" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-1 control-label">URI: </label>
                                <div class="col-sm-11">
                                    <input type="text" class="form-control" name="uri" placeholder="URI git" />
                                </div>
                            </div>
                            <hr/>
                            <h4 class="text-center">Métricas de código fonte</h4>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" value="AAC" checked="checked">
                                    Complexidade Ciclomática de McCabe
                                </label>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" value="NOM" checked="checked">
                                    Número de Métodos
                                </label>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" value="LOC" checked="checked">
                                    Linhas de Código
                                </label>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" value="DAM" checked="checked">
                                    Métricas de Acesso a Dados
                                </label>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" value="CIS" checked="checked">
                                    Tamanho da Interface da Classe
                                </label>
                            </div>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" value="AvgLOCNOM" checked="checked">
                                    Densidade de Linhas por Método
                                </label>
                            </div>
                            <br/>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Discretização: </label>
                                <div class="col-sm-10">
                                    <label class="radio-inline">
                                        <input type="radio" name="discretizationMode" value="0" checked="checked"> Nenhuma
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="discretizationMode" value="1"> Baldes
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" name="discretizationMode" value="2"> Categorias
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <h4 class="text-center">Parâmetros do Apriori</h4>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Delta: </label>
                                <div class="col-sm-9">
                                    <input type="number" class="form-control" name="delta" placeholder="Delta" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label ">Lower Bound:</label>
                                <div class="col-sm-9">
                                    <input type="number" class="form-control" name="lowerBoundMinSupport" placeholder="Limite Inferior Suporte Mínimo" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label ">Metric Type:</label>
                                <div class="col-sm-9">
                                    <select class="form-control" name="metricType">
                                        <option value="0">Confidence</option>
                                        <option value="1">Lift</option>
                                        <option value="2">Leverage</option>
                                        <option value="3">Conviction</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label ">Min Metric:</label>
                                <div class="col-sm-9">
                                    <input type="number" class="form-control" name="minMetric" placeholder="Confiança Mínimo" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label ">Num Rules:</label>
                                <div class="col-sm-9">
                                    <input type="number" class="form-control" name="numRules" placeholder="Número de Regras" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label ">Significance Level:</label>
                                <div class="col-sm-9">
                                    <input type="number" class="form-control" name="significanceLevel" placeholder="Significância Estatística" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label ">Upper Bound:</label>
                                <div class="col-sm-9">
                                    <input type="number" class="form-control" name="upperBoundMinSupport" placeholder="Limite Superior Suporte Minimo" />
                                </div>
                            </div>
                            <div class="form-group text-right">
                                <button type="submit" class="btn btn-success" name="submit" value="create">
                                    Analisar
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
    </body>
</html>