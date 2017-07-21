write("", "./test/ZDTStudy/R/EP.Wilcoxon.tex",append=FALSE)
resultDirectory<-"./test/ZDTStudy/data"
latexHeader <- function() {
  write("\\documentclass{article}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\title{StandardStudy}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\usepackage{amssymb}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\begin{document}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\maketitle", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\section{Tables}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\caption{", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write(problem, "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write(".EP.}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)

  write("\\label{Table:", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write(problem, "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write(".EP.}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)

  write("\\centering", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\begin{scriptsize}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\begin{tabular}{", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write(tabularString, "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write(latexTableFirstLine, "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\hline ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
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
    write("-- ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) <= median(data2)) {
        write("$\\blacktriangle$", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
      }
      else {
        write("$\\triangledown$", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE) 
      }
    }
    else {
      write("--", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE) 
    }
  }
  else {
    write(" ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  }
}

latexTableTail <- function() { 
  write("\\hline", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\end{tabular}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\end{scriptsize}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
  write("\\end{table}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
}

### START OF SCRIPT 
# Constants
problemList <-c("ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6") 
algorithmList <-c("SMPSO", "NSGAII", "SPEA2") 
tabularString <-c("lcc") 
latexTableFirstLine <-c("\\hline  & NSGAII & SPEA2\\\\ ") 
indicator<-"EP"

 # Step 1.  Writes the latex header
latexHeader()
tabularString <-c("| l | p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm } | p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm } | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} & \\multicolumn{5}{c|}{NSGAII} & \\multicolumn{5}{c|}{SPEA2} \\\\") 

# Step 3. Problem loop 
latexTableHeader("ZDT1 ZDT2 ZDT3 ZDT4 ZDT6 ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "SPEA2") {
    write(i , "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
    write(" & ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
          } 
          if (problem == "ZDT6") {
            if (j == "SPEA2") {
              write(" \\\\ ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
            } 
            else {
              write(" & ", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
            }
          }
     else {
    write("&", "./test/ZDTStudy/R/EP.Wilcoxon.tex", append=TRUE)
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

