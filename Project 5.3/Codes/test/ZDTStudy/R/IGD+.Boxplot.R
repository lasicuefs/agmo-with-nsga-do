postscript("IGD+.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
fileSMPSO<-paste(resultDirectory, "SMPSO", sep="/")
fileSMPSO<-paste(fileSMPSO, problem, sep="/")
fileSMPSO<-paste(fileSMPSO, indicator, sep="/")
SMPSO<-scan(fileSMPSO)

fileNSGAII<-paste(resultDirectory, "NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, problem, sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileSPEA2<-paste(resultDirectory, "SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, problem, sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

algs<-c("SMPSO","NSGAII","SPEA2")
boxplot(SMPSO,NSGAII,SPEA2,names=algs, notch = TRUE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(3,3))
indicator<-"IGD+"
qIndicator(indicator, "ZDT1")
qIndicator(indicator, "ZDT2")
qIndicator(indicator, "ZDT3")
qIndicator(indicator, "ZDT4")
qIndicator(indicator, "ZDT6")
