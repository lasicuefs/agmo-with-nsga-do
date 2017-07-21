write("", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex",append=FALSE)
resultDirectory<-"./dataset-test/The Experiment 3/data"
latexHeader <- function() {
  write("\\documentclass{article}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\title{StandardStudy}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\usepackage{amssymb}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\begin{document}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\maketitle", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\section{Tables}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\caption{", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write(problem, "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write(".IGD+.}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)

  write("\\label{Table:", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write(problem, "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write(".IGD+.}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)

  write("\\centering", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\begin{scriptsize}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\begin{tabular}{", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write(tabularString, "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write(latexTableFirstLine, "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\hline ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
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
    write("-- ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) <= median(data2)) {
        write("$\\blacktriangle$", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
      }
      else {
        write("$\\triangledown$", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE) 
      }
    }
    else {
      write("--", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE) 
    }
  }
  else {
    write(" ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  }
}

latexTableTail <- function() { 
  write("\\hline", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\end{tabular}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\end{scriptsize}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
  write("\\end{table}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
}

### START OF SCRIPT 
# Constants
problemList <-c("zoo-1", "zoo-2", "zoo-3", "zoo-4", "zoo-5", "zoo-6", "zoo-7", "zoo-8", "zoo-9", "zoo-10") 
algorithmList <-c("NSGAII") 
tabularString <-c("l") 
latexTableFirstLine <-c("\\hline \\\\ ") 
indicator<-"IGD+"

 # Step 1.  Writes the latex header
latexHeader()
tabularString <-c("| l | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} \\\\") 

# Step 3. Problem loop 
latexTableHeader("zoo-1 zoo-2 zoo-3 zoo-4 zoo-5 zoo-6 zoo-7 zoo-8 zoo-9 zoo-10 ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "NSGAII") {
    write(i , "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
    write(" & ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
          } 
          if (problem == "zoo-10") {
            if (j == "NSGAII") {
              write(" \\\\ ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
            } 
            else {
              write(" & ", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
            }
          }
     else {
    write("&", "./dataset-test/The Experiment 3/R/IGD+.Wilcoxon.tex", append=TRUE)
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

