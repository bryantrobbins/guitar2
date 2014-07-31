#!/bin/bash
if [ -z $1  ]
then
   echo "Usage: $0 [build|clean]"
   exit 1
fi

if [ $1 = "build" ] ; then
   pdflatex dozen.tex
   pdflatex dozen.tex
   rm -f dozen.out dozen.aux dozen.bbl dozen.blg dozen.dvi dozen.log
   exit 0
elif [ $1 = "clean" ] ; then 
   rm -f dozen.out dozen.aux dozen.bbl dozen.blg dozen.dvi dozen.log dozen.pdf
   exit 0
else
   echo "Usage: $0 [build|clean]"
   exit 1
fi
