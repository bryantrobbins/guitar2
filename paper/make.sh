#!/bin/bash

pdflatex amalga.tex
pdflatex amalga.tex
rm -f amalga.out amalga.aux amalga.bbl amalga.blg amalga.dvi amalga.log
exit 0
