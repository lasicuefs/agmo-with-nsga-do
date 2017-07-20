write("", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex",append=FALSE)
resultDirectory<-"./dataset-test/Learn Experiment/data"
latexHeader <- function() {
  write("\\documentclass{article}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\title{StandardStudy}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\usepackage{amssymb}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{document}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\maketitle", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\section{Tables}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\caption{", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write(problem, "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write(".HV.}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)

  write("\\label{Table:", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write(problem, "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write(".HV.}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)

  write("\\centering", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{scriptsize}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{tabular}{", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write(tabularString, "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write(latexTableFirstLine, "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\hline ", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
}

printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { 
  file1<-paste(resultDirectory, algorithm1, sep="/")
  file1<-paste(file1, problem, sep="/")
  file1<-paste(file1, indicator, sep="/")
  data1<-scan(file1)
  file2<-paste(resultDirectory, algorithm2, sep="/")
  file2<-paste(file2, problem, sep="/")
  file2<-paste(file2, indicator, sep="/")
  data2<-scan(file2)
  if (i == j) {
    write("--", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) >= median(data2)) {
        write("$\\blacktriangle$", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
      }
      else {
        write("$\\triangledown$", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE) 
      }
    }
    else {
      write("$-$", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE) 
    }
  }
  else {
    write(" ", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  }
}

latexTableTail <- function() { 
  write("\\hline", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{tabular}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{scriptsize}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{table}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
}

### START OF SCRIPT 
# Constants
problemList <-c("Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances", "Learn Select Instances") 
algorithmList <-c("NSGAII") 
tabularString <-c("l") 
latexTableFirstLine <-c("\\hline \\\\ ") 
indicator<-"HV"

 # Step 1.  Writes the latex header
latexHeader()
tabularString <-c("| l | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} \\\\") 

# Step 3. Problem loop 
latexTableHeader("Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances Learn Select Instances ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "NSGAII") {
    write(i , "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
    write(" & ", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
          } 
          if (problem == "Learn Select Instances") {
            if (j == "NSGAII") {
              write(" \\\\ ", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
            } 
            else {
              write(" & ", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
            }
          }
     else {
    write("&", "./dataset-test/Learn Experiment/R/HV.Wilcoxon.tex", append=TRUE)
     }
        }
      }
      jndx = jndx + 1
    }
    indx = indx + 1
  }
} # for algorithm

  latexTableTail()

#Step 3. Writes the end of latex file 
latexTail()

