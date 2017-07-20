postscript("IGD+.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
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
indicator<-"IGD+"
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
qIndicator(indicator, "Learn Select Instances")
