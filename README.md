# Algoritmos Evolutivos Multiobjetivo

Este trabalho consistiu no estudo sobre a aplicação de Algoritmos Genéticos Multiobjetivo (AGMO) para a redução de grandes bases de dados de problemas de classificação. Mais precisamente, foram avaliados três AGMO (NSGA-II, NSGA-III, NSGA-DO) e dois algoritmos determinísticos (RNG e PBIL).

Para a realização dos experimentos foram utilizadas 37 bases de dados extraídas do repositório KEEL (http://sci2s.ugr.es/keel/datasets.php). Elas foram separadas em bases grandes e pequenas, sendo 17 delas consideradas grandes e 20 pequenas. Todas as bases de dados foram divididas em conjuntos de treinamento e teste, utilizando a abordagem 10-fold cross validation. Além disso, cada fold foi testado três vezes, logo, para cada base de dados foram efetuadas 30 execuções.

O classificador utilizado para o cálculo da acurácia foi o k-Nearest Neighbor (KNN) disponível no WEKA, com o valor de k igual a 5.
