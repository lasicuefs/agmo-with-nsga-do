postscript("SPREAD.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
fileNSGAII<-paste(resultDirectory, "NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, problem, sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

algs<-c("NSGAII")
boxplot(NSGAII,names=algs, notch = TRUE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(1,2))
indicator<-"SPREAD"
qIndicator(indicator, "zoo-1")
qIndicator(indicator, "zoo-2")
qIndicator(indicator, "zoo-3")
qIndicator(indicator, "zoo-4")
qIndicator(indicator, "zoo-5")
qIndicator(indicator, "zoo-6")
qIndicator(indicator, "zoo-7")
qIndicator(indicator, "zoo-8")
qIndicator(indicator, "zoo-9")
qIndicator(indicator, "zoo-10")
